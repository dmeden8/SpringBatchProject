package syncer.lightspeedtosylius.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import syncer.batch.config.AppProperties;
import syncer.batch.core.DatabaseItemWriter;
import syncer.lightspeedtosylius.classes.Customer;
import syncer.lightspeedtosylius.classes.DatabaseObject;
import syncer.lightspeedtosylius.classes.Item;
import syncer.lightspeedtosylius.classes.ItemAttributeSet;
import syncer.lightspeedtosylius.classes.ItemMatrix;
import syncer.lightspeedtosylius.classes.Manufacturer;
import syncer.lightspeedtosylius.classes.Sale;
import syncer.lightspeedtosylius.classes.SaleLine;
import syncer.lightspeedtosylius.classes.Shop;
import syncer.lightspeedtosylius.classes.TaxClass;
import syncer.lightspeedtosylius.queries.ItemQueries;
import syncer.lightspeedtosylius.queries.OrderQueries;

@Configuration
@EnableBatchProcessing
public class LightspeedToSyliusConfiguration {
		
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private AppProperties config;
	
	@Autowired
	private ItemQueries itemQueries;
	
	@Autowired
	private OrderQueries orderQueries;
	
	@Bean
    public ItemReader<ItemAttributeSet> readerItemAttributeSet() {
        return new LightspeedRestXmlReader<ItemAttributeSet>(ItemAttributeSet.class, false, config);
    }
	
    @Bean
    public ItemReader<ItemMatrix> readerItemMatrix() {
        return new LightspeedRestXmlReader<ItemMatrix>(ItemMatrix.class, "&archived=true", true, config);
    }
	
    @Bean
    public ItemReader<Item> readerItem() {
        return new LightspeedRestXmlReader<Item>(Item.class, "&archived=true&load_relations=[\"ItemAttributes\",\"ItemShops\",\"ItemComponents\"]", true, config);
    }
    
    @Bean
    public ItemReader<Customer> readerCustomer() {
        return new LightspeedRestXmlReader<Customer>(Customer.class, "&archived=true&load_relations=[\"Contact\"]", true, config);
    }
    
    @Bean
    public ItemReader<Shop> readerShop() {
        return new LightspeedRestXmlReader<Shop>(Shop.class, "&load_relations=[\"Contact\"]", true, config);
    }
    
    @Bean
    public ItemReader<Sale> readerSale() {
        return new LightspeedRestXmlReader<Sale>(Sale.class, "&or=voided=true|completed=true", true, config);
    }

    @Bean
    public ItemReader<SaleLine> readerSaleLine() {
        return new LightspeedRestXmlReader<SaleLine>(SaleLine.class, true, config);
    }
    
    @Bean
    public ItemReader<Manufacturer> readerManufacturer() {
        return new LightspeedRestXmlReader<Manufacturer>(Manufacturer.class, false, config);
    }
    
    @Bean
    public ItemReader<TaxClass> readerTaxClass() {
        return new LightspeedRestXmlReader<TaxClass>(TaxClass.class, false, config);
    }
    
    @Bean
    public ItemProcessor<Customer, Customer> processor() {
    	return new CustomerProcessor();
    }
    
    @Bean
    public ItemWriter<DatabaseObject> writerToDatabase(DataSource dataSource) {
    	return new DatabaseItemWriter();
    }
    
    @Bean
    public Job lightSpeedToSyliusSyncer(JobBuilderFactory jobs, 
    		Step truncateStagingTables, 
    		Step getItemMatrices,
    		Step getItems, 
    		Step getItemAttributeSets,
    		Step getCustomers,
    		Step getShops, 
    		Step getSales,
    		Step getSaleLines,
    		Step getManufacturers,
    		Step getTaxClasses,
    		Step moveToLiveTables) {
    	return jobs.get("lightSpeedToSyliusSyncer")
                .incrementer(new RunIdIncrementer())
                .start(truncateStagingTables)
                .next(getItemMatrices)
                .next(getItems)
                .next(getItemAttributeSets)
                .next(getCustomers)
                .next(getShops)
                .next(getSales)
                .next(getSaleLines)
                .next(getManufacturers)
                .next(getTaxClasses)
                .next(moveToLiveTables)
                .build();
    }

    @Bean
    public Step truncateStagingTables(StepBuilderFactory stepBuilderFactory) {
      return stepBuilderFactory.get("truncateStagingTables")
          .tasklet(new TruncateStagingTables(jdbcTemplate))
          .build();
    } 
    
    @Bean
    public Step getItemMatrices(StepBuilderFactory stepBuilderFactory, ItemReader<ItemMatrix> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getItemMatrices")
                .<ItemMatrix, ItemMatrix> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Step getItems(StepBuilderFactory stepBuilderFactory, ItemReader<Item> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getItems")
                .<Item, Item> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }  
    
    @Bean
    public Step getItemAttributeSets(StepBuilderFactory stepBuilderFactory, ItemReader<ItemAttributeSet> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getItemAttributeSets")
                .<ItemAttributeSet, ItemAttributeSet> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }  
    
    @Bean
    public Step getCustomers(StepBuilderFactory stepBuilderFactory, ItemReader<Customer> reader, 
    		ItemProcessor<Customer, Customer> processor, ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getCustomers")
                .<Customer, Customer> chunk(config.getChunkSize())
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    
    @Bean
    public Step getShops(StepBuilderFactory stepBuilderFactory, ItemReader<Shop> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getShops")
                .<Shop, Shop> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    } 
    
    @Bean
    public Step getSales(StepBuilderFactory stepBuilderFactory, ItemReader<Sale> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getSales")
                .<Sale, Sale> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }
    
    @Bean
    public Step getSaleLines(StepBuilderFactory stepBuilderFactory, ItemReader<SaleLine> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getSaleLines")
                .<SaleLine, SaleLine> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }
    
    @Bean
    public Step getManufacturers(StepBuilderFactory stepBuilderFactory, ItemReader<Manufacturer> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getManufacturers")
                .<Manufacturer, Manufacturer> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }
    
    @Bean
    public Step getTaxClasses(StepBuilderFactory stepBuilderFactory, ItemReader<TaxClass> reader,
            ItemWriter<DatabaseObject> writer) {
        return stepBuilderFactory.get("getTaxClasses")
                .<TaxClass, TaxClass> chunk(config.getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Step moveToLiveTables(StepBuilderFactory stepBuilderFactory) {
      return stepBuilderFactory.get("moveToLiveTables")
          .tasklet(new MoveToLiveTables(jdbcTemplate, orderQueries, itemQueries))
          .build();
    } 
    
    
}