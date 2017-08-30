package syncer.shipstationtosylius.classes;

import org.springframework.jdbc.core.JdbcTemplate;

import lombok.Getter;
import lombok.Setter;
import syncer.lightspeedtosylius.classes.DatabaseObject;
import syncer.shipstationtosylius.queries.ShipstationStagingQueries;

@Getter
@Setter
public class Order implements DatabaseObject {
	
	public String orderId;
    public String orderStatus;
    
    @Override
	public void save(JdbcTemplate jdbcTemplate) {
   	
    	jdbcTemplate.update(ShipstationStagingQueries.INSERT_SHIPPING_INFO,
    			this.orderId, 
            	this.orderStatus
		);    	
	}
}
