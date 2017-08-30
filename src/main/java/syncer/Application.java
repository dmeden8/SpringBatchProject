package syncer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import syncer.batch.config.AppScheduledTasks;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    	
    	context.getBean(AppScheduledTasks.class).startAllJobs();;
    }
}