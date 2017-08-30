package syncer.syliustoshipstation.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import syncer.batch.config.AppProperties;
import syncer.syliustoshipstation.dto.ShippingDetailDto;
import syncer.syliustoshipstation.help.ShippingDetailRowMapper;
import syncer.syliustoshipstation.queries.ShippingOrderQueries;

@Configuration
@EnableBatchProcessing
public class SyliusToShipstationConfiguration {
	
	@Autowired
	private AppProperties config;
	
	@Bean
    ItemReader<ShippingDetailDto> syliusShippingDetailReader(DataSource dataSource) {
        JdbcCursorItemReader<ShippingDetailDto> databaseReader = new JdbcCursorItemReader<>();
 
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(ShippingOrderQueries.SELECT_SYLIUS_SHIPPING_DETAILS);
        databaseReader.setRowMapper(new ShippingDetailRowMapper());
 
        return databaseReader;
    }
	
	@Bean
    public ItemWriter<ShippingDetailDto> writeShippingDetailToShipStation() {
    	return new RestShippingDetailtemWriter(config);
    }
	
	@Bean
    public Job syliusToShipStationSyncer(JobBuilderFactory jobs, 
    		Step moveShippingDetailsToShipStation) {
    	return jobs.get("syliusToShipStationSyncer")
                .incrementer(new RunIdIncrementer())
                .start(moveShippingDetailsToShipStation)
                .build();
    }
	
	@Bean
    public Step moveShippingDetailsToShipStation(StepBuilderFactory stepBuilderFactory, ItemReader<ShippingDetailDto> syliusShippingDetailReader,  
            ItemWriter<ShippingDetailDto> writeShippingDetailToShipStation) {
        return stepBuilderFactory.get("moveShippingDetailsToShipStation")
                .<ShippingDetailDto, ShippingDetailDto> chunk(config.getChunkSize())
                .reader(syliusShippingDetailReader)
                .writer(writeShippingDetailToShipStation)
                .build();
    }

}
