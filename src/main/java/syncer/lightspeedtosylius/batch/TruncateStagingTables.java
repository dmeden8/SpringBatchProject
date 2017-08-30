package syncer.lightspeedtosylius.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

public class TruncateStagingTables implements Tasklet, InitializingBean {
	
	JdbcTemplate jdbcTemplate;
	
	public TruncateStagingTables (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    	System.out.println("TRUNCATE ls_staging tables"); 
    	
   		jdbcTemplate.update("truncate table ls_staging_items");
   		jdbcTemplate.update("truncate table ls_staging_item_prices");
  		jdbcTemplate.update("truncate table ls_staging_item_attribute_sets");
  		jdbcTemplate.update("truncate table ls_staging_item_attributes");
  		jdbcTemplate.update("truncate table ls_staging_shops");
   		jdbcTemplate.update("truncate table ls_staging_item_shops");
   		jdbcTemplate.update("truncate table ls_staging_item_matrices");
   		jdbcTemplate.update("truncate table ls_staging_customers");
   		jdbcTemplate.update("truncate table ls_staging_customer_address");
   		jdbcTemplate.update("truncate table ls_staging_sales");
   		jdbcTemplate.update("truncate table ls_staging_sale_lines");
   		jdbcTemplate.update("truncate table ls_staging_item_components");
  		jdbcTemplate.update("truncate table ls_staging_manufacturers");
  		jdbcTemplate.update("truncate table ls_staging_tax_classes");

    	return RepeatStatus.FINISHED;
    }

	@Override
	public void afterPropertiesSet() throws Exception {}

}