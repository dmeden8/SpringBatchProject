package syncer.syliustolightspeed.batch;

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
import syncer.syliustolightspeed.dto.CustomerDto;
import syncer.syliustolightspeed.dto.OrderDto;
import syncer.syliustolightspeed.help.CustomerRowMapper;
import syncer.syliustolightspeed.help.SaleRowMapper;
import syncer.syliustolightspeed.queries.CustomerSyliusToLightspeedQueries;
import syncer.syliustolightspeed.queries.OrderSyliusToLightspeedQueries;

@Configuration
@EnableBatchProcessing
public class SyliusToLightspeedConfiguration {
	
	@Autowired
	private AppProperties config;
	
	private OrderSyliusToLightspeedQueries orderSyliusToLightspeedQueries;
	
	public SyliusToLightspeedConfiguration(OrderSyliusToLightspeedQueries orderSyliusToLightspeedQueries) {
		this.orderSyliusToLightspeedQueries = orderSyliusToLightspeedQueries;
	}
	
	
	@Bean
    ItemReader<OrderDto> syliusOrderReader(DataSource dataSource) {
        JdbcCursorItemReader<OrderDto> databaseReader = new JdbcCursorItemReader<>();
 
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(orderSyliusToLightspeedQueries.selectSyliusOrders());
        databaseReader.setRowMapper(new SaleRowMapper());
 
        return databaseReader;
    }
	
	@Bean
    ItemReader<CustomerDto> syliusCustomerReader(DataSource dataSource) {
        JdbcCursorItemReader<CustomerDto> databaseReader = new JdbcCursorItemReader<>();
 
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(CustomerSyliusToLightspeedQueries.SELECT_SYLIUS_CUSTOMERS);
        databaseReader.setRowMapper(new CustomerRowMapper());
 
        return databaseReader;
    }
	
	@Bean
    public ItemWriter<OrderDto> writeSaleToLightSpeed() {
    	return new RestSaleItemWriter(config);
    }
	
	@Bean
    public ItemWriter<CustomerDto> writeCustomerToLightSpeed() {
    	return new RestCustomerItemWriter(config);
    }
	
	@Bean
    public Job syliusToLightSpeedSyncer(JobBuilderFactory jobs, 
    		Step moveOrdersToLightSpeed,
    		Step moveCustomersToLightSpeed) {
    	return jobs.get("syliusToLightSpeedSyncer")
                .incrementer(new RunIdIncrementer())
                .start(moveCustomersToLightSpeed)
                .next(moveOrdersToLightSpeed)
                .build();
    }
	
	@Bean
    public Step moveOrdersToLightSpeed(StepBuilderFactory stepBuilderFactory, ItemReader<OrderDto> syliusOrderReader,  
            ItemWriter<OrderDto> writeSaleToLightSpeed) {
        return stepBuilderFactory.get("moveOrdersToLightSpeed")
                .<OrderDto, OrderDto> chunk(config.getChunkSize())
                .reader(syliusOrderReader)
                .writer(writeSaleToLightSpeed)
                .build();
    }
	
	@Bean
    public Step moveCustomersToLightSpeed(StepBuilderFactory stepBuilderFactory, ItemReader<CustomerDto> syliusCustomerReader,  
            ItemWriter<CustomerDto> writeCustomerToLightSpeed) {
        return stepBuilderFactory.get("moveCustomersToLightSpeed")
                .<CustomerDto, CustomerDto> chunk(config.getChunkSize())
                .reader(syliusCustomerReader)
                .writer(writeCustomerToLightSpeed)
                .build();
    }

}
