package syncer.shipstationtosylius.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import syncer.batch.config.AppProperties;
import syncer.batch.core.DatabaseItemWriter;
import syncer.lightspeedtosylius.classes.DatabaseObject;
import syncer.shipstationtosylius.classes.Order;
import syncer.shipstationtosylius.queries.OrderShipstationToSyliusQueries;

@Configuration
@EnableBatchProcessing
public class ShipstationToSyliusConfiguration {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	private OrderShipstationToSyliusQueries orderShipstationToSyliusQueries;

	@Autowired
	private AppProperties config;
		
	@Bean
    ItemReader<Order> readerShippingOrder() {
        return new ShipstationRestJsonReader(config, true);
    }
	
	@Bean
    public ItemWriter<DatabaseObject> writerToDatabase(DataSource dataSource) {
    	return new DatabaseItemWriter();
    }
		
	@Bean
    public Job shipStationToSyliusSyncer(JobBuilderFactory jobs, 
    		Step truncateStagingTablesShipstation,
    		Step getShippingStatus,
    		Step moveToLiveTablesShipstation,
    		Step getTrackingNumber) {
    	return jobs.get("shipStationToSyliusSyncer")
                .incrementer(new RunIdIncrementer())
                .start(truncateStagingTablesShipstation)
                .next(getShippingStatus)
                .next(moveToLiveTablesShipstation)
                .next(getTrackingNumber)
                .build();
    }
	
    @Bean
    public Step truncateStagingTablesShipstation(StepBuilderFactory stepBuilderFactory) {
      return stepBuilderFactory.get("truncateStagingTablesShipstation")
          .tasklet(new TruncateStagingTablesShipstation(jdbcTemplate))
          .build();
    } 
	
	@Bean
    public Step getShippingStatus(StepBuilderFactory stepBuilderFactory, ItemReader<Order> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getShippingStatus")
                .<Order,Order> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }
	
	@Bean
    public Step moveToLiveTablesShipstation(StepBuilderFactory stepBuilderFactory) {
      return stepBuilderFactory.get("moveToLiveTables")
          .tasklet(new MoveToLiveTablesShipstation(jdbcTemplate))
          .build();
    }
	
	@Bean
    public Step getTrackingNumber(StepBuilderFactory stepBuilderFactory) {
      return stepBuilderFactory.get("getTrackingNumber")
          .tasklet(new GetTrackingNumber(config, jdbcTemplate, orderShipstationToSyliusQueries))
          .build();
    }
	
	

}
