package syncer.batch.core;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import syncer.batch.config.AppProperties;
import syncer.batch.core.classes.NewAccessTokenRequest;
import syncer.batch.core.classes.NewAccessTokenResponse;
import syncer.exceptions.JobFailedException;

@Component
@Getter
@Setter
public class LightspeedTokenRefresher {
	
	@Autowired
	AppProperties config;
	
	private volatile String accessToken;
	private volatile boolean refreshed = false;
	
	private HttpHeaders headers;
		
	private void setRequest() {      
	    this.headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType((MediaType.APPLICATION_JSON));
    }
	
	public synchronized String refreshToken() {
		
		if(refreshed) {
			refreshed = false;
			return accessToken;
		}
		
		setRequest();
		
		NewAccessTokenRequest newAccessTokenRequest = new NewAccessTokenRequest();
		newAccessTokenRequest.setRefresh_token(config.getLightspeedRefreshToken());
		newAccessTokenRequest.setClient_id(config.getLightspeedClientId());
		newAccessTokenRequest.setClient_secret(config.getLightspeedClientSecret());
		
		Gson gson = new Gson();
        String newAccessTokenRequestJson = gson.toJson(newAccessTokenRequest);
        System.out.println(newAccessTokenRequestJson);
		
		HttpEntity<String> entity = new HttpEntity<String>(newAccessTokenRequestJson, headers);
    	
    	HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(config.getRestTimeout());
        httpRequestFactory.setConnectTimeout(config.getRestTimeout());
        httpRequestFactory.setReadTimeout(config.getRestTimeout());
        
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
    	
    	ResponseEntity<NewAccessTokenResponse> response = null;
		
		try {       				       			
			String url = config.getLightspeedTokenUrl();
			System.out.println("URL: " + url);
			response = restTemplate.exchange(url, HttpMethod.POST, entity, NewAccessTokenResponse.class);
		} catch (RestClientException e1) {
			throw new JobFailedException("Job aborted, rest problems");			
		} 
		
		if (response.getStatusCode() != HttpStatus.OK) {
			System.out.println("HTTP CODE: " + response.getStatusCode());
			throw new JobFailedException("Job aborted, rest problems");		
		}
		
		NewAccessTokenResponse newAccessTokenResponse = response.getBody();
		
		if(newAccessTokenResponse.getAccess_token() != null) {
			System.out.println("New access token is: " + newAccessTokenResponse.getAccess_token());
			accessToken = newAccessTokenResponse.getAccess_token();
			refreshed = true;
			
			return accessToken;
		}
		
		return accessToken;
	}
	

}
