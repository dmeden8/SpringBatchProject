package syncer.lightspeedtosylius.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import syncer.batch.config.AppProperties;

@Component
public class OrderQueries {
	
	@Autowired
	private AppProperties config;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void insertOrderItemUnit() {
		
		List<Integer> quantites = new ArrayList<Integer>();
		String query = "SELECT DISTINCT unit_quantity FROM ls_staging_sale_lines a "
				+ "INNER JOIN ls_staging_sales b ON a.sale_id = b.sale_id "
				+ "WHERE unit_quantity>0 AND b.reference_number_source != 'web'";
					
		List<Map<String,Object>> list = jdbcTemplate.queryForList(query);
		
		for (Map<String,Object> map : list) {
			quantites.add((Integer)map.get("unit_quantity"));
		}
			    		    	
    	for (int i=0; i<quantites.size(); i++) {
	    	for(int j=1; j<=quantites.get(i); j++) {
	    		
	    		String code = "CONCAT('ls-', a.sale_line_id, '/','" + j + "')";
	    		
	    		StringBuilder sb = new StringBuilder();
	    		sb.append("INSERT INTO sylius_order_item_unit ");
	    		sb.append("(order_item_id, adjustments_total, created_at, code) ");
	    		sb.append("(SELECT b.id, ROUND(100*(a.calc_tax1 - a.calc_line_discount)/a.unit_quantity), NOW(), ").append(code).append(" as `code` ");
	    		sb.append("FROM ls_staging_sale_lines a ");
	    		sb.append("INNER JOIN sylius_order_item b ON CONCAT('ls-',a.sale_line_id) = b.`code` ");
	    		sb.append("WHERE a.unit_quantity=").append(quantites.get(i)).append(") ");
	    		sb.append("ON DUPLICATE KEY UPDATE ");
	    		sb.append("code = VALUES(code)");
	    		
	    		jdbcTemplate.execute(sb.toString());
	    	}
	    }
				
	}
	
	public void insertOrderItem() {
		
		String query = "INSERT INTO sylius_order_item "
				+ "(order_id, variant_id, quantity, unit_price, units_total, adjustments_total, total, is_immutable, code) "
				+ "(SELECT b.id, c.id, a.unit_quantity, a.unit_price*100, a.calc_total*100, 0, a.calc_total*100, 0, CONCAT('ls-',a.sale_line_id) as code "
				+ "FROM ls_staging_sale_lines a "
				+ "INNER JOIN sylius_order b ON CONCAT('ls-',a.sale_id) = b.code "
				+ "INNER JOIN sylius_product_variant c ON CONCAT('ls-',a.item_id) = c.code  "
				+ "INNER JOIN ls_staging_sales d ON a.sale_id = d.sale_id "
				+ "WHERE a.item_id NOT IN(" + config.getShippingItemId() + ") AND d.reference_number_source != 'web' ) "
				+ "ON DUPLICATE KEY UPDATE "
				+ "total = VALUES(total)";
								
		jdbcTemplate.execute(query);				
	}
		
	public void insertOrder() { 
		
		String query = "INSERT INTO sylius_order "	
    		+ "(channel_id, customer_id, number, code, created_at, updated_at, checkout_completed_at, locale_code, currency_code, items_total, adjustments_total, total, checkout_state, shipping_state, state, payment_state) "
    		+ "(SELECT 1, b.id, CONCAT('ls-',a.sale_id), CONCAT('ls-',a.sale_id), a.`timestamp`, a.`timestamp`, a.`timestamp`, 'en_US', 'USD', a.calc_total*100, 0, a.calc_total*100, 'completed', 'ready', "
    		+ "  (CASE WHEN a.reference_number_source = 'web' AND a.completed=1 THEN '" + config.getLightspeedSendAsCompleteState() + "' "
    		+ "		   WHEN a.reference_number_source != 'web' AND a.completed=1 THEN 'fulfilled' "
    		+ "        ELSE 'new' "
    		+ "	  END) AS state, " 
    		+ "  (CASE WHEN a.voided=1 THEN 'cancelled' "
    		+ "		   WHEN a.completed=1 THEN 'paid' "
    		+ "        ELSE 'new' "
    		+ "	  END) AS payment_state "
    		+ "FROM ls_staging_sales a LEFT JOIN sylius_customer b ON CONCAT('ls-',a.customer_id) = b.code) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "customer_id = VALUES(customer_id), "
    		+ "state = VALUES(state), "
    		+ "updated_at = VALUES(updated_at), "
    		+ "payment_state = VALUES(payment_state)";
		
		jdbcTemplate.execute(query);
	}
	
	
	public static final String INSERT_ORDER_SEQUENCE = "INSERT INTO sylius_order_sequence (idx) "
			+ "(SELECT COALESCE(MAX(id),1) FROM sylius_order)" 
			+ "ON DUPLICATE KEY UPDATE "
			+ "idx = VALUES(idx)";
	
	
	public static final String INSERT_ORDER_ADJUSTMENT_TAX = "INSERT INTO sylius_adjustment "
			+ "(order_item_unit_id, type, amount, created_at, is_neutral, is_locked, code) "
			+ "(SELECT b.id, 'tax', ROUND(a.calc_tax1*100/a.unit_quantity), NOW(), 0, 0, CONCAT('t-',b.code) as code "
			+ "FROM ls_staging_sale_lines a "
			+ "LEFT JOIN sylius_order_item_unit b ON CONCAT('ls-',a.sale_line_id) = SUBSTRING_INDEX(b.code, '/', 1) "
			+ "INNER JOIN ls_staging_sales c ON a.sale_id = c.sale_id "
			+ "WHERE a.calc_tax1 > 0 AND c.reference_number_source != 'web') "
			+ "ON DUPLICATE KEY UPDATE "
			+ "amount = VALUES(amount), "
			+ "type = VALUES(type)";
	
	public static final String INSERT_ORDER_ADJUSTMENT_DISCOUNT = "INSERT INTO sylius_adjustment "
			+ "(order_item_unit_id, type, amount, created_at, is_neutral, is_locked, code) "
			+ "(SELECT b.id, 'order_promotion', -1*ROUND(a.calc_line_discount*100/a.unit_quantity), NOW(), 0, 0, CONCAT('d-',b.code) as code "
			+ "FROM ls_staging_sale_lines a "
			+ "LEFT JOIN sylius_order_item_unit b ON CONCAT('ls-',a.sale_line_id) = SUBSTRING_INDEX(b.code, '/', 1) "
			+ "INNER JOIN ls_staging_sales c ON a.sale_id = c.sale_id "
			+ "WHERE a.calc_line_discount > 0 AND c.reference_number_source != 'web') "
			+ "ON DUPLICATE KEY UPDATE "
			+ "amount = VALUES(amount), "
			+ "type = VALUES(type)";
	
	
	public void insertOrderPayment() {
		
		String query = "INSERT INTO sylius_payment "
				+ "(method_id, order_id, currency_code, amount, created_at, details, code, state) "
				+ "(SELECT " + config.getLightspeedPaymentMethodId() + ", b.id, 'USD', a.calc_total*100, NOW(), '[]', CONCAT('ls-',a.sale_id) as code, "
				+ "(CASE WHEN a.voided=1 THEN 'cancelled' "
	    		+ "		   WHEN a.completed=1 THEN 'completed' "
	    		+ "        ELSE 'new' "
	    		+ "	  END) AS state "
				+ "FROM ls_staging_sales a "
				+ "INNER JOIN sylius_order b ON CONCAT('ls-',a.sale_id) = b.code "
				+ "WHERE a.reference_number_source != 'web') "
				+ "ON DUPLICATE KEY UPDATE "
				+ "amount = VALUES(amount)";
		
		jdbcTemplate.execute(query);
		
	}
	    
		
}

	