package syncer.lightspeedtosylius.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import syncer.lightspeedtosylius.queries.CustomerQueries;
import syncer.lightspeedtosylius.queries.ItemQueries;
import syncer.lightspeedtosylius.queries.MatrixItemQueries;
import syncer.lightspeedtosylius.queries.OrderQueries;
import syncer.lightspeedtosylius.queries.TaxQueries;
import syncer.lightspeedtosylius.queries.TaxonQueries;

@Component
public class MoveToLiveTables implements Tasklet, InitializingBean {
	
	private JdbcTemplate jdbcTemplate;
		
	private ItemQueries itemQueries;
	
	private OrderQueries orderQueries;
	
	public MoveToLiveTables (JdbcTemplate jdbcTemplate, OrderQueries orderQueries, ItemQueries itemQueries) {
		this.jdbcTemplate = jdbcTemplate;
		this.orderQueries = orderQueries;
		this.itemQueries = itemQueries;
	}
	 	
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception { 
                   
	        System.out.println("INSERT_TAX_CATEGORIES");
	        jdbcTemplate.execute(TaxQueries.INSERT_TAX_CATEGORIES);
            System.out.println("SET_DEFAULT_TAX_CATEGORY_VARIABLE");
            jdbcTemplate.execute(TaxQueries.SET_DEFAULT_TAX_CATEGORY_VARIABLE);	        

            System.out.println("INSERT_ITEM_ATTRIBUTE_SETS");
            jdbcTemplate.execute(MatrixItemQueries.INSERT_ITEM_ATTRIBUTE_SETS);  
            System.out.println("INSERT_ITEM_ATTRIBUTE_VALUES");
            jdbcTemplate.execute(MatrixItemQueries.INSERT_ITEM_ATTRIBUTE_VALUES);  
            
    		System.out.println("INSERT_PRODUCT_SINGLE");
            itemQueries.insertProductSingle();
            System.out.println("INSERT_PRODUCT_SINGLE_TRANSLATION");
            jdbcTemplate.execute(ItemQueries.INSERT_PRODUCT_SINGLE_TRANSLATION);
            System.out.println("INSERT_PRODUCT_VARIANTS_SINGLE");
            jdbcTemplate.execute(ItemQueries.INSERT_PRODUCT_VARIANTS_SINGLE); 
            System.out.println("INSERT_PRODUCT_VARIANTS_SINGLE_TRANSLATION");
            jdbcTemplate.execute(ItemQueries.INSERT_PRODUCT_VARIANTS_SINGLE_TRANSLATION); 
            
            System.out.println("INSERT_SHOPS");
            jdbcTemplate.execute(ItemQueries.INSERT_SHOPS); 
            System.out.println("INSERT_SHOP_ITEMS");
            jdbcTemplate.execute(ItemQueries.INSERT_SHOP_ITEMS); 

            System.out.println("INSERT_PRODUCT_CONFIGURABLE");
            jdbcTemplate.execute(MatrixItemQueries.INSERT_PRODUCT_CONFIGURABLE);
            System.out.println("INSERT_PRODUCT_CONFIGURABLE_TRANSLATION");
            jdbcTemplate.execute(MatrixItemQueries.INSERT_PRODUCT_CONFIGURABLE_TRANSLATION);
            System.out.println("INSERT_PRODUCT_VARIANTS_CONFIGURABLE");
            jdbcTemplate.execute(MatrixItemQueries.INSERT_PRODUCT_VARIANTS_CONFIGURABLE);         
            System.out.println("INSERT_PRODUCT_VARIANTS_CONFIGURABLE_TRANSLATION");
            jdbcTemplate.execute(MatrixItemQueries.INSERT_PRODUCT_VARIANTS_CONFIGURABLE_TRANSLATION);         
            
            
            System.out.println("UPDATE_QUANTITY_PRODUCT_VARIANTS");
            jdbcTemplate.execute(ItemQueries.UPDATE_QUANTITY_PRODUCT_VARIANTS);
            System.out.println("INSERT_PRODUCT_CHANNEL");
            jdbcTemplate.execute(ItemQueries.INSERT_PRODUCT_CHANNEL);
            System.out.println("INSERT_VARIANT_PRICES");
            jdbcTemplate.execute(ItemQueries.INSERT_VARIANT_PRICES);
            System.out.println("INSERT_BUNDLE_RELATIONS");
            jdbcTemplate.execute(ItemQueries.INSERT_BUNDLE_RELATIONS); 
            System.out.println("DELETE_BUNDLE_RELATION");
            jdbcTemplate.execute(ItemQueries.DELETE_BUNDLE_RELATION);

            System.out.println("INSERT_CUSTOMERS");
            jdbcTemplate.execute(CustomerQueries.INSERT_CUSTOMERS); 
            System.out.println("INSERT_SHOP_USERS");
            jdbcTemplate.execute(CustomerQueries.INSERT_SHOP_USERS);
//          System.out.println("INSERT_CUSTOMER_ADDRESSES");
//          jdbcTemplate.execute(CustomerQueries.INSERT_CUSTOMER_ADDRESSES); 
            
            System.out.println("INSERT_ORDERS");
            orderQueries.insertOrder();
            System.out.println("INSERT_ORDER_ITEMS");
            orderQueries.insertOrderItem();      
            System.out.println("INSERT_ORDER_ITEM_UNITS");
            orderQueries.insertOrderItemUnit();
            
            System.out.println("INSERT_ORDER_SEQUENCE");
            jdbcTemplate.execute(OrderQueries.INSERT_ORDER_SEQUENCE);
            System.out.println("INSERT_ORDER_ADJUSTMENT_TAX");
            jdbcTemplate.execute(OrderQueries.INSERT_ORDER_ADJUSTMENT_TAX);
            System.out.println("INSERT_ORDER_ADJUSTMENT_DISCOUNT");
            jdbcTemplate.execute(OrderQueries.INSERT_ORDER_ADJUSTMENT_DISCOUNT);
            System.out.println("INSERT_ORDER_PAYMENT");
            orderQueries.insertOrderPayment();
            
            System.out.println("SET_BRAND_VARIABLES");
            jdbcTemplate.execute(TaxonQueries.SET_BRAND_VARIABLES);
            System.out.println("INSERT_BRANDS");
            jdbcTemplate.execute(TaxonQueries.INSERT_BRANDS);
            System.out.println("DELETE_BRANDS");
            jdbcTemplate.execute(TaxonQueries.DELETE_BRANDS);
            System.out.println("UPDATE_BRAND_ROOT");
            jdbcTemplate.execute(TaxonQueries.UPDATE_BRAND_ROOT);           
            System.out.println("INSERT_BRAND_TRANSLATION");
            jdbcTemplate.execute(TaxonQueries.INSERT_BRAND_TRANSLATION); 
            System.out.println("INSERT_BRAND_ON_PRODUCT_SINGLE");
            jdbcTemplate.execute(TaxonQueries.INSERT_BRAND_ON_PRODUCT_SINGLE);
            System.out.println("INSERT_BRAND_ON_PRODUCT_MATRIX");
            jdbcTemplate.execute(TaxonQueries.INSERT_BRAND_ON_PRODUCT_MATRIX);
        
    	return RepeatStatus.FINISHED;
    }

	@Override
	public void afterPropertiesSet() throws Exception {}
	
}