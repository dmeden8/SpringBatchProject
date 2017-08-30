package syncer.shipstationtosylius.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

public class TruncateStagingTablesShipstation implements Tasklet, InitializingBean {
	
	JdbcTemplate jdbcTemplate;
	
	public TruncateStagingTablesShipstation (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    	
    	System.out.println("TRUNCATE ss_staging_shipping_info");  	
   		jdbcTemplate.update("truncate table ss_staging_shipping_info");

    	return RepeatStatus.FINISHED;
    }

	@Override
	public void afterPropertiesSet() throws Exception {}

}