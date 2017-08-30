package syncer.syliustolightspeed.queries;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import syncer.batch.config.AppProperties;
import syncer.syliustolightspeed.classes.SaleLine;

@Component
public class OrderSyliusToLightspeedQueries {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private AppProperties config;	
	
	public void updateOrderCode(Long saleId, Long orderId) {
				
			String query = "UPDATE sylius_order SET code = 'ls-" + saleId + "' where id = " + orderId;   		
			jdbcTemplate.execute(query);				
	}
	
	@Transactional
	public void updateOrderItemsCodes(List<SaleLine> saleLines, Long orderId) {
				
		for (SaleLine saleLine : saleLines) {
			
			if ((long)saleLine.getItemID() == (long)config.getShippingItemId())
				continue;
			
			String query = "UPDATE sylius_order_item a INNER JOIN sylius_product_variant b "
					+ "ON a.variant_id = b.id "
					+ "SET a.`code` = 'ls-" + saleLine.getSaleLineID() + "' "
					+ "WHERE order_id = " + orderId + " "
					+ "AND b.code = CONCAT('ls-'," + saleLine.getItemID() + ")";   		
		    jdbcTemplate.execute(query);
		    
		    if (saleLine.getUnitQuantity() > 1) {
		    	
		    	for (int i=1 ; i<=saleLine.getUnitQuantity(); i++) {
		    		
		    		Integer offset = i - 1;
		    		query = "SELECT a.id FROM sylius_order_item_unit a INNER JOIN sylius_order_item b "
		    				+ "ON a.order_item_id = b.id "
		    				+ "WHERE b.order_id = " + orderId + " "
		    				+ "AND b.code = CONCAT('ls-',"+saleLine.itemID+") "
		    				+ "AND a.code is null "
		    				+ "ORDER BY a.id LIMIT 1 OFFSET "+ offset +"";
		    		Integer orderItemUnitId = jdbcTemplate.queryForObject(query, Integer.class);
			        
			        String code = "CONCAT('ls-', " + saleLine.getSaleLineID() +  ", '/', " + i + ")";
			        query = "UPDATE sylius_order_item_unit a INNER JOIN sylius_order_item b "
			    			+ "ON a.order_item_id = b.id "
			    			+ "SET a.`code` = " + code + " "
			    			+ "WHERE b.order_id = " + orderId + " "
			    			+ "AND a.id = " + orderItemUnitId;
			    	
			    	jdbcTemplate.execute(query);
			    }		    	
		    }
		    else {
		    	query = "UPDATE sylius_order_item_unit a INNER JOIN sylius_order_item b "
		    			+ "ON a.order_item_id = b.id "
		    			+ "SET a.`code` = 'ls-" + saleLine.getSaleLineID() + "/1' " 
		    			+ "WHERE b.order_id = " + orderId + " "
		    			+ "AND b.code = CONCAT('ls-'," + saleLine.getSaleLineID() + ")";
		    	
		    	jdbcTemplate.execute(query);

		    }
		    		    
		}						
	}
			
	public String selectSyliusOrders() {
	
		String query = "(SELECT a.id as id, a.payment_state, a.state, substring(c.code,4) as item_id, " 
			+ "COALESCE(h.`code`, 0) as tax_id, substring(d.code,4) as customer_id, b.quantity, b.unit_price as units_total, " 
			+ "a.total, COALESCE(sum(f.amount), 0) as adjust_amount, COALESCE(f.type, 'none') as adjust_type "
			+ "FROM sylius_order a "
			+ "INNER JOIN sylius_order_item b ON a.id = b.order_id " 
			+ "INNER JOIN sylius_product_variant c ON b.variant_id = c.id " 
			+ "INNER JOIN sylius_customer d ON a.customer_id = d.id " 
			+ "INNER JOIN sylius_order_item_unit e ON b.id = e.order_item_id " 
			+ "LEFT JOIN sylius_adjustment f ON e.id = f.order_item_unit_id AND f.type != 'tax' "
			+ "INNER JOIN sylius_address g ON g.id = a.shipping_address_id " 
	        + "LEFT JOIN sylius_zone_member k ON g.province_code = k.`code` " 
			+ "LEFT JOIN sylius_zone h ON h.id = k.belongs_to " 
	        + "WHERE a.code IS NULL " 
			+ "AND a.payment_state = 'paid' "
			+ "GROUP BY id,item_id "
			+ ") "
			+ "UNION "
			+ "(SELECT a.id as id, a.payment_state, a.state, " + config.getShippingItemId() + " as item_id, " 
			+ "COALESCE(h.`code`, 0) as tax_id, substring(d.code,4) as customer_id, 1, f.amount as units_total, " 
			+ "a.total, COALESCE(sum(f.amount), 0) as adjust_amount, COALESCE(f.type, 'none') as adjust_type "
			+ "FROM sylius_order a " 
			+ "INNER JOIN sylius_customer d ON a.customer_id = d.id " 
			+ "LEFT JOIN sylius_adjustment f ON a.id = f.order_id AND f.type != 'tax' "
			+ "INNER JOIN sylius_address g ON g.id = a.shipping_address_id " 
	        + "LEFT JOIN sylius_zone_member k ON g.province_code = k.`code` " 
			+ "LEFT JOIN sylius_zone h ON h.id = k.belongs_to " 
	        + "WHERE a.code IS NULL " 
			+ "AND a.payment_state = 'paid' "
			+ "GROUP BY id,item_id "
			+ ") "
			+ "ORDER BY id";
		
		return query;
	}

}
	