package syncer.syliustoshipstation.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShippingOrderQueries {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
		
	public void updateShipStationOrderId(String orderShipstationId, String orderNumber) {
				
			String query = "UPDATE sylius_order SET shipstation_id = '" + orderShipstationId + "' where number = '" + orderNumber + "'";   		
			jdbcTemplate.execute(query);				
	}
		
	public static final String SELECT_SYLIUS_SHIPPING_DETAILS =  "SELECT a.id, a.number, b.quantity, a.total, c.manufacturer_sku, e.name, c.width, c.height, c.depth, c.weight, "
			+ "f.street, f.city, f.postcode, f.country_code, SUBSTRING(f.province_code,4) as province_code, f.phone_number, f.first_name, f.last_name, a.created_at, g.email "
			+ "FROM sylius_order a "
			+ "INNER JOIN sylius_order_item b ON a.id = b.order_id "
			+ "INNER JOIN sylius_product_variant c ON b.variant_id = c.id "
			+ "INNER JOIN sylius_product_variant_translation e ON e.translatable_id = c.id "
			+ "INNER JOIN sylius_address f ON f.id = a.shipping_address_id "
			+ "INNER JOIN sylius_customer g ON a.customer_id = g.id "
			+ "WHERE a.shipstation_id IS NULL "
			//+ "AND SUBSTRING(a.number,1,3) != 'ls-' "
			+ "AND a.shipping_state = 'awaiting_shipment' "
			+ "AND a.shipping_address_id IS NOT NULL "
			+ "ORDER BY a.id"; 
	
}

	