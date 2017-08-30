package syncer.batch.config;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import syncer.batch.core.LightspeedTokenRefresher;

@Component
@EnableScheduling
public class AppScheduledTasks {
	
	@Autowired
	ApplicationContext context;
	
	@Autowired 
	JobLauncher jobLauncher;
	
	@Autowired 
	AppProperties config;
	
	@Autowired
	LightspeedTokenRefresher lightspeedTokenRefresher;

	public void startAllJobs(){
		
		lightspeedTokenRefresher.refreshToken();
		
		if (config.getSyliusToLightspeedJobActive()) {
			SyliusToLightspeedRunnable stl = new SyliusToLightspeedRunnable(context, jobLauncher);
		    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(stl, 0, config.getSyliusToLightspeedDelay(), TimeUnit.SECONDS);
		}
		
		if (config.getLightspeedToSyliusJobActive()) {
			LightspeedToSyliusRunnable lts = new LightspeedToSyliusRunnable(context, jobLauncher);
		    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(lts, 0, config.getLightspeedToSyliusDelay(), TimeUnit.SECONDS);
		}							   	    
	    
		if (config.getSyliusToShipstationJobActive()) {
		    SyliusToShipstationRunnable stss = new SyliusToShipstationRunnable(context, jobLauncher);
		    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(stss, 0, config.getSyliusToShipstationDelay(), TimeUnit.SECONDS);		    
		}
		
		if (config.getShipstationToSyliusJobActive()) {		    
		    ShipstationToSyliusRunnable ssts = new ShipstationToSyliusRunnable(context, jobLauncher);
		    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(ssts, 0, config.getShipstationToSyliusDelay(), TimeUnit.SECONDS);
		}
	}
	
}
