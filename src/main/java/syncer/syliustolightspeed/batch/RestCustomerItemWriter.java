package syncer.syliustolightspeed.batch;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import syncer.batch.config.AppProperties;
import syncer.batch.core.LightspeedHelper;
import syncer.batch.core.LightspeedTokenRefresher;
import syncer.exceptions.JobFailedException;
import syncer.syliustolightspeed.classes.Customer;
import syncer.syliustolightspeed.dto.CustomerDto;
import syncer.syliustolightspeed.help.CustomerDtoToCustomerMapper;
import syncer.syliustolightspeed.queries.CustomerSyliusToLightspeedQueries;

public class RestCustomerItemWriter implements ItemWriter<CustomerDto> {
	
	@Autowired
	CustomerSyliusToLightspeedQueries customerSyliusToLightSpeedQueries;
	
	@Autowired
	CustomerDtoToCustomerMapper customerDtoToSaleMapper;
	
	@Autowired
	LightspeedTokenRefresher lightspeedTokenRefresher;
	
	private AppProperties config;
	private String baseApiUrl;
	private HttpHeaders headers;
	
	int timeToWait;
	
	public RestCustomerItemWriter (AppProperties config) {
		this.config = config;
		this.baseApiUrl = config.getLightspeedAPIUrl() + config.getLightspeedAccountID();
	}
	
    private void setHeaders(){      
	    this.headers = new HttpHeaders();
	    headers.add("Authorization", "Bearer " + lightspeedTokenRefresher.getAccessToken());
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType((MediaType.APPLICATION_JSON));
    }
    
    @Override
	public void write(List<? extends CustomerDto> customersSylius) throws Exception {
    	
    	setHeaders();
    	List<Customer> customers = customerDtoToSaleMapper.prepareCustomersForRestCall(customersSylius);
    					
		for (Customer customer : customers) {
			
			//String customerId = checkIfCustomerAlreadyExistOnLightspeed(customer.getContact().getEmails().getContactEmail().getAddress());
        	
        	Gson gson = new Gson();
            String customerJson = gson.toJson(customer);
            System.out.println(customerJson);
        	
        	HttpEntity<String> entity = new HttpEntity<String>(customerJson, headers);
        	
        	HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectionRequestTimeout(config.getRestTimeout());
            httpRequestFactory.setConnectTimeout(config.getRestTimeout());
            httpRequestFactory.setReadTimeout(config.getRestTimeout());
            
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        	
        	ResponseEntity<String> response = null;
        	
        	try {
	        	if(timeToWait > 0) { 
					System.out.println("Waiting " + (double)timeToWait/1000 + " seconds before next request");				
	    			Thread.sleep(timeToWait); 
	        	}
			} catch (InterruptedException e) {
				throw new JobFailedException("Job aborted, thread problem");	
			}
        	String customerId = null;
        	if (customerId != null) {   
        		String url = baseApiUrl + "/Customer/" + customerId + ".json";
        		try {       				       			      			
        			System.out.println("URL: " + url);
        			response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        		} catch (HttpClientErrorException e) {
        			//if access token expires						
        			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        				refreshToken(url,restTemplate);
        			}
        			else {
        				throw new JobFailedException("Job aborted, rest problems");	
        			}			
        		}        		
        	}
        	else {
    			String url = baseApiUrl + "/Customer.json";
        		try {			       			
        			System.out.println("URL: " + url);
        			response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        		} catch (HttpClientErrorException e) {
        			//if access token expires						
        			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        				refreshToken(url,restTemplate);
        			}
        			else {
        				throw new JobFailedException("Job aborted, rest problems");	
        			}		
        		}           	
        	}
        	
        	if(response != null && response.getBody() != null) {
        		String body = response.getBody();
        		customerId = body.substring(body.indexOf("customerID")+13,body.indexOf("\",\"create"));
        		customerSyliusToLightSpeedQueries.updateCustomerCode(customerId, customer.getReferenceID());
        	}
        	
        	if (response.getHeaders().get("X-LS-API-Bucket-Level") != null && response.getHeaders().get("X-LS-API-Bucket-Level").size() > 0) {
            	timeToWait = LightspeedHelper.getTimeToWaitBeforeSendingRequets(response.getHeaders().get("X-LS-API-Bucket-Level").get(0), 10);
        	}
        	      	
        	if (response.getStatusCode() != HttpStatus.OK) {
    			System.out.println("HTTP CODE: " + response.getStatusCode());
    			throw new JobFailedException("Job aborted, rest problems");		
    		}
        	
		}
    	
    }
    
    private String checkIfCustomerAlreadyExistOnLightspeed(String email) {
    	
    	String url = baseApiUrl + "/Customer.json?limit=1&load_relations=[\"Contact\"]&Contact.email=" + email;
    	
    	HttpEntity<String> entity = new HttpEntity<String>(headers);
    	
    	HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(config.getRestTimeout());
        httpRequestFactory.setConnectTimeout(config.getRestTimeout());
        httpRequestFactory.setReadTimeout(config.getRestTimeout());
        
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
    	
    	ResponseEntity<String> response = null;
    	      	
    	try {
			response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		} catch (RestClientException e1) {
			throw new JobFailedException("Job aborted, rest problems");			
		}
    	
    	if (response.getStatusCode() != HttpStatus.OK) {
			System.out.println("HTTP CODE: " + response.getStatusCode());
			throw new JobFailedException("Job aborted, rest problems");		
		}
    	
    	String body = "";
    	if(response != null && response.getBody() != null) {
    		body = response.getBody();	   		
    	}
    	
    	if(body.contains("Customer")) {
    		String customerId = body.substring(body.indexOf("customerID")+13,body.indexOf("\",\"create"));
    		return customerId;
		}
    	else 
    		return null;
    	
    }
    
    private ResponseEntity<String> refreshToken(String url, RestTemplate restTemplate) {
		
		ResponseEntity<String> response = null;
    	
		headers.remove("Authorization");
        headers.add("Authorization", "Bearer " + lightspeedTokenRefresher.refreshToken());
        HttpEntity<String> request = new HttpEntity<String>(headers);
        try {
			response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		} catch (RestClientException e) {
			throw new JobFailedException("Job aborted, rest problems after refreshing access token, " + e.getMessage());	
		}
        
        return response;    	
    }

}
