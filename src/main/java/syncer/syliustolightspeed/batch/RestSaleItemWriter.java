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
import syncer.syliustolightspeed.classes.Sale;
import syncer.syliustolightspeed.dto.OrderDto;
import syncer.syliustolightspeed.help.OrderDtoToSaleMapper;
import syncer.syliustolightspeed.queries.OrderSyliusToLightspeedQueries;

public class RestSaleItemWriter implements ItemWriter<OrderDto> {
	
	@Autowired
	OrderSyliusToLightspeedQueries orderSyliusToLightSpeedQueries;
	
	@Autowired
	OrderDtoToSaleMapper orderDtoToSaleMapper;
	
	@Autowired
	LightspeedTokenRefresher lightspeedTokenRefresher;
	
	private AppProperties config;
	private String baseApiUrl;
	private HttpHeaders headers;
	
	int timeToWait;
	
	public RestSaleItemWriter (AppProperties config) {
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
	public void write(List<? extends OrderDto> orders) throws Exception {

		setHeaders();
		List<Sale> sales = orderDtoToSaleMapper.prepareOrdersForRestCall(orders);
		
		String url = baseApiUrl + "/Sale.json";
		System.out.println("URL: " + url);
               
        for (Sale sale : sales) {
        	
        	Gson gson = new Gson();
            String saleJson = gson.toJson(sale);
            System.out.println(saleJson);
        	
        	HttpEntity<String> entity = new HttpEntity<String>(saleJson, headers);
        	
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
        	      	
        	try {
    			response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    		} catch (HttpClientErrorException e) {
    			//if access token expires						
    			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
    				refreshToken(url,restTemplate);
    			}
    			else if (e.getStatusCode() == HttpStatus.BAD_REQUEST && e.getResponseBodyAsString().contains("Cannot complete an out of balance sale")) {
    				try {
    					System.out.println("Trying to increase total amount by 0.01");
    					response = restTemplate.exchange(getUrl(e.getResponseBodyAsString()), HttpMethod.PUT, changeSaleAmount(sale,entity,0.01), String.class);    					
    				}
    				catch (HttpClientErrorException e1) {
    	    			if (e1.getStatusCode() == HttpStatus.UNAUTHORIZED) {
    	    				response = refreshToken(url,restTemplate);
    	    			}
    	    			else if (e1.getStatusCode() == HttpStatus.BAD_REQUEST && e1.getResponseBodyAsString().contains("Cannot complete an out of balance sale")) {
    	    				try {
    	    					System.out.println("Trying to decrease total amount by 0.01");
            					response = restTemplate.exchange(getUrl(e1.getResponseBodyAsString()), HttpMethod.PUT, changeSaleAmount(sale,entity,-0.02), String.class);
    	    				}
    	    				catch (HttpClientErrorException e2) {
    	    					System.out.println(e2.getResponseBodyAsString());
    	    					throw new JobFailedException("Job aborted, rest problems, " + e2.getMessage());	
    	    				}   	    				
    	    			}   
    	    			else {
    	    				throw new JobFailedException("Job aborted, rest problems, " + e1.getMessage());	
    	    			}
    				}   				
    			}
    			else {
    				throw new JobFailedException("Job aborted, rest problems, " + e.getMessage());	
    			}			
    		}
        	
        	if(response != null && response.getBody() != null) {
        		String body = response.getBody();       		
        		saleJson = body.substring(body.indexOf("Sale")+6, body.indexOf("SalePayments")-2).concat("}");
        		        		
        		Sale returnSaleData = gson.fromJson(saleJson, Sale.class);
        		
        		orderSyliusToLightSpeedQueries.updateOrderCode(returnSaleData.getSaleID(), sale.getReferenceNumber());
        		// We don't need this since sylius originated orders are never synced back for anything but status 
        		//orderSyliusToLightSpeedQueries.updateOrderItemsCodes(returnSaleData.getSaleLines().getSaleLine(), sale.getReferenceNumber());
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
	
	private HttpEntity<String> changeSaleAmount(Sale sale, HttpEntity<String> entity, double amountChange) {
		
		Gson gson = new Gson();
		
		Double amount = sale.getSalePayments().getSalePayment().get(0).getAmount();
		sale.getSaleLines().getSaleLine().clear();
		sale.getSalePayments().getSalePayment().get(0).setAmount(amount + amountChange);
		
		String saleJson = gson.toJson(sale);
        System.out.println(saleJson);
        
    	entity = new HttpEntity<String>(saleJson, headers);
    	
    	return entity;
		
	}
	
	private String getUrl(String responseBody) {
			
		String saleId = responseBody.substring(responseBody.indexOf("saleID") + 9, responseBody.lastIndexOf("balance")-3);		
		String url = baseApiUrl + "/Sale/" + saleId + ".json";
				       
        return url; 
		
	}
	
}
