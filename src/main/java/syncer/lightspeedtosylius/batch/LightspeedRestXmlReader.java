package syncer.lightspeedtosylius.batch;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.batch.item.xml.StaxUtils;
import org.springframework.batch.item.xml.stax.DefaultFragmentEventReader;
import org.springframework.batch.item.xml.stax.FragmentEventReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import syncer.batch.config.AppProperties;
import syncer.batch.core.JobExecutionDetails;
import syncer.batch.core.LightspeedHelper;
import syncer.batch.core.LightspeedTokenRefresher;
import syncer.exceptions.JobFailedException;


public class LightspeedRestXmlReader<T> extends AbstractPagingItemReader<T> implements InitializingBean {
	
	@Autowired
	JobExecutionDetails jobExecutionDetails;
	
	@Autowired
	LightspeedTokenRefresher lightspeedTokenRefresher;
		
	private FragmentEventReader fragmentReader;
	private XMLEventReader eventReader;
	private List<QName> fragmentRootElementNames;
	
	private volatile boolean initialized = false;
	private int pageSize = 1000;
	private volatile int current = 0;
	private volatile int page = 0;
	
	private Class<T> objectClass;
	
	private HttpHeaders headers;
	private String objectToRead;
	private String baseApiUrl;
	private String additionalParams;
	private String timeStampPrefix;
	private boolean useTimeIntervals;
	private int timeToWait;	
	
	private AppProperties config;
	
	/**
	 * General contructor
	 * @param objectClass sets class of a object 
	 * @param useTimeIntervals if true make request for time interval
	 */
	public LightspeedRestXmlReader (Class<T> objectClass, boolean useTimeIntervals, AppProperties config) {
    	//System.out.println("INIT READER " + objectClass.getSimpleName());
		this.config = config;
		this.pageSize = config.getPageSize();
		this.objectClass = objectClass;
    	this.objectToRead = objectClass.getSimpleName();
    	this.baseApiUrl = config.getLightspeedAPIUrl() + config.getLightspeedAccountID();
    	this.setFragmentRootElementName(objectClass.getSimpleName());
		this.additionalParams = "";
		this.timeStampPrefix = "";
		this.useTimeIntervals = useTimeIntervals;
	}
	
	/**
	 * Contructor with additional params
	 * @param objectClass sets class of a object 
	 * @param additionalParams additional params for GET request
	 * @param useTimeIntervals if true make request for time interval
	 */
	public LightspeedRestXmlReader (Class<T> objectClass, String additionalParams, boolean useTimeIntervals, AppProperties config) {
		this(objectClass, useTimeIntervals, config);
		this.additionalParams = additionalParams;
	}
	
	/**
	 * 
	 * @param objectClass sets class of a object
	 * @param objectToRead object for request is not equal to objectClass name
	 * @param timeStampPrefix timestamp interval is set for this object
	 * @param additionalParams additional params for GET request
	 * @param useTimeIntervals if true make request for time interval
	 */
	public LightspeedRestXmlReader (Class<T> objectClass, String objectToRead, String timeStampPrefix, String additionalParams, boolean useTimeIntervals, AppProperties config) {
		this(objectClass, additionalParams, useTimeIntervals, config);
		this.objectToRead = objectToRead;
		this.timeStampPrefix = timeStampPrefix;
	}
	
    private void setHeaders(){
//	    String plainCreds = config.getLightspeedUsername() + ":" + config.getLightspeedPassword();
//	    byte[] plainCredsBytes = plainCreds.getBytes();
//	    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
//	    String base64Creds = new String(base64CredsBytes);        
	    this.headers = new HttpHeaders();
//	    headers.add("Authorization", "Basic " + base64Creds);
	    headers.add("Authorization", "Bearer " + lightspeedTokenRefresher.getAccessToken());
    }

    /**
     * get unmarshaller to convert xml to corresponding object
     * @return
     */
    public Jaxb2Marshaller xmlToObject() {
		Jaxb2Marshaller j2m = new Jaxb2Marshaller();
		j2m.setClassesToBeBound(objectClass);
		return j2m;
    }

	/**
	 * @param fragmentRootElementName name of the root element of the fragment
	 */
	private void setFragmentRootElementName(String fragmentRootElementName) {
		setFragmentRootElementNames(new String[] {fragmentRootElementName});
	}

	/**
	 * @param fragmentRootElementNames list of the names of the root element of the fragment
	 */
	public void setFragmentRootElementNames(String[] fragmentRootElementNames) {
		this.fragmentRootElementNames = new ArrayList<QName>();
		for (String fragmentRootElementName : fragmentRootElementNames) {
			this.fragmentRootElementNames.add(parseFragmentRootElementName(fragmentRootElementName));
		}
	}

	/**
	 * Ensure that all required dependencies for the ItemReader to run are provided after all properties have been set.
	 *
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 * @throws IllegalArgumentException if the Resource, FragmentDeserializer or FragmentRootElementName is null, or if
	 * the root element is empty.
	 * @throws IllegalStateException if the Resource does not exist.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(fragmentRootElementNames, "The FragmentRootElementNames must not be null");
		Assert.notNull(fragmentRootElementNames, "The FragmentRootElementNames must not be empty");
		//to do add checks for lightspeed objects
		for (QName fragmentRootElementName : fragmentRootElementNames) {
			Assert.hasText(fragmentRootElementName.getLocalPart(), "The FragmentRootElementNames must contain empty elements");
		}
	}
	
	/**
	 * Responsible for moving the cursor before the StartElement of the fragment root.
	 *
	 * This implementation simply looks for the next corresponding element, it does not care about element nesting. You
	 * will need to override this method to correctly handle composite fragments.
	 *
	 * @return <code>true</code> if next fragment was found, <code>false</code> otherwise.
	 *
	 * @throws NonTransientResourceException if the cursor could not be moved. This will be treated as fatal and
	 * subsequent calls to read will return null.
	 */
	protected boolean moveCursorToNextFragment(XMLEventReader reader) throws NonTransientResourceException {
		try {
			while (true) {
				while (reader.peek() != null && !reader.peek().isStartElement()) {
					reader.nextEvent();
				}
				if (reader.peek() == null) {
					return false;
				}
				QName startElementName = ((StartElement) reader.peek()).getName();
				if (isFragmentRootElementName(startElementName)) {
					return true;
				}
				reader.nextEvent();
			}
		}
		catch (XMLStreamException e) {
			throw new NonTransientResourceException("Error while reading from event reader", e);
		}
	}
	
	/*
	 * Read until the first StartElement tag that matches any of the provided fragmentRootElementNames. Because there may be any
	 * number of tags in between where the reader is now and the fragment start, this is done in a loop until the
	 * element type and name match.
	 */
	private QName readToStartFragment() throws XMLStreamException {
		while (true) {
			XMLEvent nextEvent = eventReader.nextEvent();
			if (nextEvent.isStartElement()
					&& isFragmentRootElementName(((StartElement) nextEvent).getName())) {
				return ((StartElement) nextEvent).getName();
			}
		}
	}
	
	/*
	 * Read until the first EndElement tag that matches the provided fragmentRootElementName. Because there may be any
	 * number of tags in between where the reader is now and the fragment end tag, this is done in a loop until the
	 * element type and name match
	 */
	private void readToEndFragment(QName fragmentRootElementName) throws XMLStreamException {
		while (true) {
			XMLEvent nextEvent = eventReader.nextEvent();
			if (nextEvent.isEndElement()
					&& fragmentRootElementName.equals(((EndElement) nextEvent).getName())) {
				return;
			}
		}
	}
	
	
	/*
	 * jumpToItem is overridden because reading in and attempting to bind an entire fragment is unacceptable in a
	 * restart scenario, and may cause exceptions to be thrown that were already skipped in previous runs.
	 */
	@Override
	protected void jumpToItem(int itemIndex) throws Exception {
		for (int i = 0; i < itemIndex; i++) {
			try {
				QName fragmentName = readToStartFragment();
				readToEndFragment(fragmentName);
			} catch (NoSuchElementException e) {
				if (itemIndex == (i + 1)) {
					// we can presume a NoSuchElementException on the last item means the EOF was reached on the last run
					return;
				} else {
					// if NoSuchElementException occurs on an item other than the last one, this indicates a problem
					throw e;
				}
			}
		}
	}


	private boolean isFragmentRootElementName(QName name) {
		for (QName fragmentRootElementName : fragmentRootElementNames) {
			if (fragmentRootElementName.getLocalPart().equals(name.getLocalPart())) {
				if (!StringUtils.hasText(fragmentRootElementName.getNamespaceURI())
						|| fragmentRootElementName.getNamespaceURI().equals(name.getNamespaceURI())) {
					return true;
				}
			}
		}
		return false;
	}

	
	private QName parseFragmentRootElementName(String fragmentRootElementName) {
		String name = fragmentRootElementName;
		String nameSpace = null;
		if (fragmentRootElementName.contains("{")) {
			nameSpace = fragmentRootElementName.replaceAll("\\{(.*)\\}.*", "$1");
			name = fragmentRootElementName.replaceAll("\\{.*\\}(.*)", "$1");
		}
		return new QName(nameSpace, name, "");
	}


	@Override
	protected void doReadPage() {
		
		setHeaders();
				
        HttpEntity<String> request = new HttpEntity<String>(headers);
        
        Date dateFrom = jobExecutionDetails.getLastSuccessfulJobExecutionStartTime("lightSpeedToSyliusSyncer");
        Date dateUntil = jobExecutionDetails.getLastStartedJobExecutionStartTime("lightSpeedToSyliusSyncer");
        String dateParams = "";
        
        if (!config.getLightspeedGlobalUseTimeInterval())
        	this.useTimeIntervals = false;
        	       
        if(this.useTimeIntervals) {
	        if(dateFrom != null && dateUntil != null && dateFrom.before(dateUntil)) {
	        	dateParams = "&timeStamp=><," + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(dateFrom) + "," + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(dateUntil);
	        } else if(dateUntil != null) {
	        	dateParams = "&timeStamp=<=," + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(dateUntil);
	        }
	        
	        if(!this.timeStampPrefix.isEmpty()) {
	        	dateParams = dateParams.replace("&timeStamp", "&" + this.timeStampPrefix + ".timeStamp");
	        }
        }
//        String url = baseApiUrl + "/" + objectToRead + "?oauth_token=" + config.getLightspeedAccessToken() + "&limit=" + pageSize + "&offset=" + page*pageSize + additionalParams + dateParams;
        String url = baseApiUrl + "/" + objectToRead + "?limit=" + pageSize + "&offset=" + page*pageSize + additionalParams + dateParams;
        System.out.println("URL: " + url);
                
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(config.getRestTimeout());
        httpRequestFactory.setConnectTimeout(config.getRestTimeout());
        httpRequestFactory.setReadTimeout(config.getRestTimeout());
                
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        ResponseEntity<String> response = null;
		try {
			if(timeToWait > 0)
				System.out.println("Waiting " + (double)timeToWait/1000 + " seconds before next request");			
			Thread.sleep(timeToWait);
			response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		} catch (RestClientException e1) {	
			//if access token expires						
			if (e1.getMessage().contains("401")) {
				headers.remove("Authorization");
		        headers.add("Authorization", "Bearer " + lightspeedTokenRefresher.refreshToken());
		        HttpEntity<String> request2 = new HttpEntity<String>(headers);
		        try {
					response = restTemplate.exchange(url, HttpMethod.GET, request2, String.class);
				} catch (RestClientException e2) {
					throw new JobFailedException("Job aborted, rest problems after refreshing access token");	
				}
			}
			else {
				throw new JobFailedException("Job aborted, rest problems");	
			}						
		} catch (InterruptedException e) {
			throw new JobFailedException("Job aborted, thread problem");	
		}
		
		if (response.getStatusCode() != HttpStatus.OK) {
			System.out.println("HTTP CODE: " + response.getStatusCode());
			throw new JobFailedException("Job aborted, rest problems");		
		}
		
		if (response.getHeaders().get("X-LS-API-Bucket-Level") != null && response.getHeaders().get("X-LS-API-Bucket-Level").size() > 0) {
        	timeToWait = LightspeedHelper.getTimeToWaitBeforeSendingRequets(response.getHeaders().get("X-LS-API-Bucket-Level").get(0), 1);
    	}
		
        StringReader reader = new StringReader(response.getBody());
		try {
			eventReader = XMLInputFactory.newInstance().createXMLEventReader(reader);
		} catch (XMLStreamException | FactoryConfigurationError e) {
			e.printStackTrace();
		}
		fragmentReader = new DefaultFragmentEventReader(eventReader);
	}
	

	
	/**
	 * Move to next fragment and map it to item.
	 */
	@Override
	protected T doRead() throws Exception {
		if(initialized == false){
			System.out.println("Reading page " + page);
			doReadPage();
			page++;
			initialized = true;
		}
		if (fragmentReader.peek() == null || current >= pageSize) {
			System.out.println("Reading page " + page);
			doReadPage();
			page++;
			if (current >= pageSize) {
				current = 0;
			}
		}
		current++;
		T item = null;
		boolean success = false;
		try {
			success = moveCursorToNextFragment(fragmentReader);
		}
		catch (NonTransientResourceException e) {
			// Prevent caller from retrying indefinitely since this is fatal
			throw e;
		}
		if (success) {
			fragmentReader.markStartFragment();
			try {
				@SuppressWarnings("unchecked")
				T mappedFragment = (T) xmlToObject().unmarshal(StaxUtils.getSource(fragmentReader));
				item = mappedFragment;
			}
			finally {
				fragmentReader.markFragmentProcessed();
			}
		}
		else {
			this.initialized = false;
			this.current = 0;
			this.page = 0;
		}
		return item;
	}
	
	
	@Override
	protected void doJumpToPage(int itemIndex) {
		// This is most likely used in restart scenarios		
	}
	

}
