package syncer.syliustoshipstation.batch;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import syncer.batch.config.AppProperties;
import syncer.exceptions.JobFailedException;
import syncer.syliustoshipstation.classes.ShipStationOrder;
import syncer.syliustoshipstation.dto.ShippingDetailDto;
import syncer.syliustoshipstation.help.ShippingDetailDtoToShipStationOrderMapper;
import syncer.syliustoshipstation.queries.ShippingOrderQueries;

public class RestShippingDetailtemWriter implements ItemWriter<ShippingDetailDto> {
	
	@Autowired
	ShippingOrderQueries shippingOrderQueries;
	
	@Autowired
	ShippingDetailDtoToShipStationOrderMapper shippingDetailDtoToShipstationOrderMapper;
	
	private AppProperties config;
	private String baseApiUrl;
	private HttpHeaders headers;
	
	int timeToWait;
	
	public RestShippingDetailtemWriter (AppProperties config) {
		this.config = config;
		this.baseApiUrl = config.getShipstationAPIUrl();
		this.setHeaders();
	}
	
    private void setHeaders(){      
	    this.headers = new HttpHeaders();
	    headers.add("Authorization", "Basic " + config.getShipstationAccessToken());
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
    
    @Override
	public void write(List<? extends ShippingDetailDto> shippingDetails) throws Exception {

		List<ShipStationOrder> shipStationOrders = shippingDetailDtoToShipstationOrderMapper.prepareShippingDetailsForRestCall(shippingDetails);
		
		String url = baseApiUrl + "orders/createorder";
		System.out.println("URL: " + url);
		               
        for (ShipStationOrder order : shipStationOrders) {
        	
        	try {
            	if(timeToWait > 0) { 
    				System.out.println("Waiting " + (double)timeToWait/1000 + " seconds before next request");				
        			Thread.sleep(timeToWait); 
            	}
    		} catch (InterruptedException e) {
    			throw new JobFailedException("Job aborted, thread problem");	
    		}
        	        	
        	Gson gson = new Gson();
            String orderJson = gson.toJson(order);
            System.out.println(orderJson);
        	
        	HttpEntity<String> entity = new HttpEntity<String>(orderJson, headers);
        	
        	HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectionRequestTimeout(config.getRestTimeout());
            httpRequestFactory.setConnectTimeout(config.getRestTimeout());
            httpRequestFactory.setReadTimeout(config.getRestTimeout());
            
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        	        	
        	ResponseEntity<String> response = null;
        	       	      	
        	try {
    			response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    		} catch (RestClientException e1) {
    			throw new JobFailedException("Job aborted, rest problems");			
    		}
        	
        	if(response != null && response.getBody() != null) {
      		
        		String body = transformBodyFromUtf16toUtf8(response.getBody());
        		String orderId = body.substring(body.indexOf("orderId")+9, body.indexOf(",\"orderNumber") );
        		
        		shippingOrderQueries.updateShipStationOrderId(orderId, order.getOrderNumber());
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

        	if (response.getStatusCode() != HttpStatus.OK) {
    			System.out.println("HTTP CODE: " + response.getStatusCode());
    			throw new JobFailedException("Job aborted, rest problems");		
    		}
        	
		}
	              
	}
    
    
    private String transformBodyFromUtf16toUtf8 (String bodyUTF16) throws UnsupportedEncodingException {
    	
    	byte[] bytes = bodyUTF16.getBytes("UTF-16");      		
		String bodyUTF8 = new String(bytes, StandardCharsets.UTF_8);
		
		StringBuilder sb = new StringBuilder();
		for (int i=0 ; i<bodyUTF8.length(); i++) {
			Character c = bodyUTF8.charAt(i);
			if ((int)c  != 0 ) {
				sb.append(c);
			}
		}
		
		return sb.toString();
    	
    }
    
}
