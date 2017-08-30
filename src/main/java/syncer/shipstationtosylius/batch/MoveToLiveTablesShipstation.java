package syncer.shipstationtosylius.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import syncer.shipstationtosylius.queries.OrderShipstationToSyliusQueries;

@Component
public class MoveToLiveTablesShipstation implements Tasklet, InitializingBean {
	
	private JdbcTemplate jdbcTemplate;
			
	public MoveToLiveTablesShipstation (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	 	
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception { 
                   
	        System.out.println("UPDATE_ORDER_SHIPPING_INFO");
	        jdbcTemplate.execute(OrderShipstationToSyliusQueries.UPDATE_ORDER_SHIPPING_INFO);
	        
    	return RepeatStatus.FINISHED;
    }

	@Override
	public void afterPropertiesSet() throws Exception {}
	
}