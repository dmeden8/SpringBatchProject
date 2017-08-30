package syncer.batch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AppProperties {

//    @Value("${lightspeed.accessToken}")
//    private String lightspeedAccessToken;   
    @Value("${lightspeed.refreshToken}")
    private String lightspeedRefreshToken; 
    @Value("${lightspeed.clientId}")
    private String lightspeedClientId;
    @Value("${lightspeed.clientSecret}")
    private String lightspeedClientSecret;
    @Value("${lightspeed.accountID}")
    private String lightspeedAccountID;   
    @Value("${lightspeed.apiUrl}")
    private String lightspeedAPIUrl;
    @Value("${lightspeed.tokenUrl}")
    private String lightspeedTokenUrl;
    @Value("${lightspeed.sendAsCompleteState}")
    private String lightspeedSendAsCompleteState;
    @Value("${lightspeed.paymentMethodId}")
    private String lightspeedPaymentMethodId;
    
    @Value("${syncer.chunkSize}")
    private Integer chunkSize;   
    @Value("${syncer.pageSize}")
    private Integer pageSize;   
    
    @Value("${syncer.lightspeed.globalUseTimeInterval}")
    private Boolean lightspeedGlobalUseTimeInterval;
    @Value("${syncer.shipstation.globalUseTimeInterval}")
    private Boolean shipstationGlobalUseTimeInterval;
    
    @Value("${syncer.restTimeout}")
    private Integer restTimeout;
    
    @Value("${lightspeed.saleField.employeeId}")
    private Long employeeId;   
    @Value("${lightspeed.saleField.registerId}")
    private Long registerId;  
    @Value("${lightspeed.saleField.shopId}")
    private Long shopId;
    
    @Value("${lightspeed.saleField.shippingItemId}")
    private Long shippingItemId;    
    
    @Value("${shipstation.accessToken}")
    private String shipstationAccessToken;       
    @Value("${shipstation.apiUrl}")
    private String shipstationAPIUrl;
    @Value("${shipstation.timeZone}")
    private String shipstationTimeZone;
    
    @Value("${scheduler.lightspeedToSylius.fixedDelay}")
    private Long lightspeedToSyliusDelay;
    @Value("${scheduler.syliusToLightspeed.fixedDelay}")
    private Long syliusToLightspeedDelay;
    @Value("${scheduler.syliusToShipstation.fixedDelay}")
    private Long syliusToShipstationDelay;
    @Value("${scheduler.shipstationToSylius.fixedDelay}")
    private Long shipstationToSyliusDelay;
    
    @Value("${scheduler.lightspeedToSylius.jobActive}")
    private Boolean lightspeedToSyliusJobActive;
    @Value("${scheduler.syliusToLightspeed.jobActive}")
    private Boolean syliusToLightspeedJobActive;
    @Value("${scheduler.syliusToShipstation.jobActive}")
    private Boolean syliusToShipstationJobActive;
    @Value("${scheduler.shipstationToSylius.jobActive}")
    private Boolean shipstationToSyliusJobActive;
         
}
