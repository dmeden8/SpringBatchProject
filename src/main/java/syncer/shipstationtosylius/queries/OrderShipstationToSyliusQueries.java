package syncer.shipstationtosylius.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderShipstationToSyliusQueries {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
		
	public static final String UPDATE_ORDER_SHIPPING_INFO = "UPDATE sylius_order a " 
			+ "INNER JOIN ss_staging_shipping_info b "
			+ "ON a.shipstation_id = b.order_id "
			+ "SET a.shipping_state = b.order_status";
	
	public static final String SELECT_SHIPSTATION_SHIPPED_ORDERS = "SELECT a.shipstation_id FROM sylius_order a "
			+ "INNER JOIN sylius_shipment b "
			+ "ON b.order_id = a.id "
			+ "WHERE a.shipping_state = 'shipped' "
			+ "AND a.shipstation_id IS NOT NULL "
			+ "AND b.tracking IS NULL";		
	
	public void updateTrackingNumber(String trackingNumber, String shipstationOrderId) {
		
		String query = "UPDATE sylius_shipment a " 
				+ "INNER JOIN sylius_order b "
				+ "ON a.order_id = b.id "
				+ "SET a.tracking = '" + trackingNumber + "' "
				+ "WHERE shipstation_id = '" + shipstationOrderId + "'";
								
		jdbcTemplate.execute(query);				
	}
}
