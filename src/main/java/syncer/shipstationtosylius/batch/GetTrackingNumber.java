package syncer.shipstationtosylius.batch;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import syncer.batch.config.AppProperties;
import syncer.exceptions.JobFailedException;
import syncer.shipstationtosylius.classes.Shipment;
import syncer.shipstationtosylius.classes.Shipments;
import syncer.shipstationtosylius.queries.OrderShipstationToSyliusQueries;

@Component
public class GetTrackingNumber implements Tasklet, InitializingBean {
	
	private JdbcTemplate jdbcTemplate;
	private HttpHeaders headers;
	private AppProperties config;
	private String baseApiUrl;
	
	private OrderShipstationToSyliusQueries orderShipstationToSyliusQueries;
	
	public GetTrackingNumber (AppProperties config, JdbcTemplate jdbcTemplate, OrderShipstationToSyliusQueries orderShipstationToSyliusQueries) {
		this.config = config;
		this.baseApiUrl = config.getShipstationAPIUrl();
		this.jdbcTemplate = jdbcTemplate;
		this.orderShipstationToSyliusQueries = orderShipstationToSyliusQueries;
	}
	
	private void setHeaders(){      
	    this.headers = new HttpHeaders();
	    headers.add("Authorization", "Basic " + config.getShipstationAccessToken());
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
	 	
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception { 
                   
        System.out.println("SELECT_SHIPSTATION_SHIPPED_ORDERS");
        
        List<String> shipstationShippedOrders = jdbcTemplate.queryForList(OrderShipstationToSyliusQueries.SELECT_SHIPSTATION_SHIPPED_ORDERS, String.class);
        
        setHeaders();
        
        for (String orderId : shipstationShippedOrders) {
        	
        	HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectionRequestTimeout(config.getRestTimeout());
            httpRequestFactory.setConnectTimeout(config.getRestTimeout());
            httpRequestFactory.setReadTimeout(config.getRestTimeout());
            
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
                        
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            
            ResponseEntity<Shipments> response = null;
            
            try {    
                String url = baseApiUrl + "/shipments?orderId=" + orderId;
            	System.out.println(url); 
            	response = restTemplate.exchange(url, HttpMethod.GET, entity, Shipments.class);
    		} catch (RestClientException e1) {
    			throw new JobFailedException("Job aborted, rest problems");			
    		}
            
            Shipment shipment = null;
            if (response.getBody().getShipments().size() > 0 ) {
            	shipment = response.getBody().getShipments().get(0);
            }
            
            if (shipment != null) {
            	orderShipstationToSyliusQueries.updateTrackingNumber(shipment.getTrackingNumber(), shipment.getOrderId());
            }
                  	
        }
        		        
    	return RepeatStatus.FINISHED;
    }

	@Override
	public void afterPropertiesSet() throws Exception {}

}
