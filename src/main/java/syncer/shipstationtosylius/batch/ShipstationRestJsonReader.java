package syncer.shipstationtosylius.batch;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import syncer.batch.config.AppProperties;
import syncer.batch.core.JobExecutionDetails;
import syncer.exceptions.JobFailedException;
import syncer.shipstationtosylius.classes.Order;
import syncer.shipstationtosylius.classes.Orders;


public class ShipstationRestJsonReader extends AbstractPagingItemReader<Order> {
	
	@Autowired
	JobExecutionDetails jobExecutionDetails;
	
	private AppProperties config;
	private String baseApiUrl;
	private boolean useTimeIntervals;
	private HttpHeaders headers;
	 
    private List<Order> orderData;    
    
    private boolean initialized = false;
    private int current;    
    private int page = 1;
    private int pageSize;
    
    int timeToWait;
    
    int i;
    
	public ShipstationRestJsonReader(AppProperties config, boolean useTimeIntervals) {
		this.config = config;
		this.baseApiUrl = config.getShipstationAPIUrl();
		this.useTimeIntervals = useTimeIntervals;
		this.page = 1;
		this.pageSize = config.getPageSize();
		this.setHeaders();
	}
	
    private void setHeaders(){      
	    this.headers = new HttpHeaders();
	    headers.add("Authorization", "Basic " + config.getShipstationAccessToken());
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
 
	@Override
	protected void doReadPage() {
		
		Date dateFrom = jobExecutionDetails.getLastSuccessfulJobExecutionStartTime("shipStationToSyliusSyncer");
        Date dateUntil = jobExecutionDetails.getLastStartedJobExecutionStartTime("shipStationToSyliusSyncer");
        String dateParams = "";
        
        SimpleDateFormat sdfAmerica = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfAmerica.setTimeZone(TimeZone.getTimeZone(config.getShipstationTimeZone()));
               
        try {
        	if(timeToWait > 0) { 
				System.out.println("Waiting " + (double)timeToWait/1000 + " seconds before next request");				
    			Thread.sleep(timeToWait); 
        	}
		} catch (InterruptedException e) {
			throw new JobFailedException("Job aborted, thread problem");	
		}
               
        if (!config.getShipstationGlobalUseTimeInterval())
        	this.useTimeIntervals = false;
        	       
        if(this.useTimeIntervals) {
	        if(dateFrom != null && dateUntil != null && dateFrom.before(dateUntil)) {
	        	dateParams = "&modifyDateStart=" + sdfAmerica.format(dateFrom) + "&modifyDateEnd=" + sdfAmerica.format(dateUntil);
	        } else if(dateUntil != null) {
	        	dateParams = "&modifyDateEnd=" + sdfAmerica.format(dateUntil);
	        }
        }
    	
    	HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(config.getRestTimeout());
        httpRequestFactory.setConnectTimeout(config.getRestTimeout());
        httpRequestFactory.setReadTimeout(config.getRestTimeout());
        
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
        ResponseEntity<Orders> response = null;
        
        try {    
            String url = baseApiUrl + "/orders?page=" + page + "&pageSize=" + pageSize + dateParams;
        	System.out.println(url); 
        	response = restTemplate.exchange(url, HttpMethod.GET, entity, Orders.class);
		} catch (RestClientException e1) {
			throw new JobFailedException("Job aborted, rest problems");			
		}
                
        if (response.getHeaders().get("X-Rate-Limit-Remaining") != null 
        		&& response.getHeaders().get("X-Rate-Limit-Remaining").size() > 0
        		&& Integer.parseInt(response.getHeaders().get("X-Rate-Limit-Remaining").get(0)) == 0
				&& response.getHeaders().get("X-Rate-Limit-Reset") != null 
				&& response.getHeaders().get("X-Rate-Limit-Reset").size() > 0) {
        	timeToWait = Integer.parseInt(response.getHeaders().get("X-Rate-Limit-Reset").get(0)) * 1000;
    	}
        else {
        	timeToWait = 0;
        }
        
                      
        Orders orders = response.getBody();
        
        orderData = orders.getOrders();
        		
	}
	
	@Override
	protected Order doRead() throws Exception {
		
		if(initialized == false) {
			System.out.println("Reading page " + page);
			doReadPage();
			page++;
			initialized = true;
		}
			
		if(current >= config.getPageSize()) {
			System.out.println("Reading page " + page);
			doReadPage();
			page++;
			current = 0;
		}
		
		if (orderData.size() == current) {
			return null;
		}
		Order order = orderData.get(current);
		current++;
							
		return order;
	}
	

	@Override
	protected void doJumpToPage(int itemIndex) {}
	
	@Override
	protected void doOpen() throws Exception {
		initialized = false;
		page = 1;
		current = 0;
	 }	
	
}
