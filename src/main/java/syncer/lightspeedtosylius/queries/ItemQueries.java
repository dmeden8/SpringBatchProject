package syncer.lightspeedtosylius.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import syncer.batch.config.AppProperties;

@Component
public class ItemQueries {
	
	@Autowired
	private AppProperties config;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public void insertProductSingle() {
		
		String query = "INSERT INTO sylius_product "
	    		+ "(code,enabled,created_at,variant_selection_method) "
	    		+ "(SELECT CONCAT('ls-',item_id),ABS(archived-1) AS enabled,NOW(),'choice' FROM ls_staging_items "
	    		+ "WHERE item_matrix_id=0 "
	    		+ "AND item_id NOT IN(" + config.getShippingItemId() + ")) "
	    		+ "ON DUPLICATE KEY UPDATE "
	    		+ "enabled = VALUES(enabled)";
						
		jdbcTemplate.execute(query);				
	}
	   
	public static final String INSERT_PRODUCT_SINGLE_TRANSLATION = "INSERT INTO sylius_product_translation "
    		+ "(translatable_id,name,slug,locale) "
    		+ "(SELECT b.id,a.description AS name,CONCAT('ls-',item_id),'en_US' FROM ls_staging_items a "
    		+ "INNER JOIN sylius_product b ON b.code = CONCAT('ls-',a.item_id) WHERE a.item_matrix_id=0) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "name = VALUES(name)";
    
	public static final String INSERT_PRODUCT_VARIANTS_SINGLE = "INSERT INTO sylius_product_variant "
    		+ "(product_id,tax_category_id,code,created_at,position,on_hold,on_hand,tracked,shipping_required,upc,ean,customer_sku,manufacturer_sku) "
    		+ "(SELECT b.id, "
    		+ "  (CASE WHEN c.id IS NULL THEN @taxCategoryId "
    		+ "        ELSE c.id "
    		+ "	  END) AS tax_category_id, "
    		+ "CONCAT('ls-',item_id),NOW(),0,0,0,0,1,a.upc,a.ean,a.custom_sku,a.manufacturer_sku FROM ls_staging_items a "
    		+ "INNER JOIN sylius_product b ON CONCAT('ls-',item_id) = b.code "
    		+ "LEFT JOIN sylius_tax_category c ON CONCAT('ls-',a.tax_class_id) = c.code WHERE a.item_matrix_id=0) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "on_hand = VALUES(on_hand), "
    		+ "tax_category_id = VALUES(tax_category_id), "
    		+ "upc = VALUES(upc), "
    		+ "ean = VALUES(ean), "
    		+ "customer_sku = VALUES(customer_sku), "
			+ "manufacturer_sku = VALUES(manufacturer_sku)";
	
    public static final String INSERT_PRODUCT_VARIANTS_SINGLE_TRANSLATION = "INSERT INTO sylius_product_variant_translation "
    		+ "(translatable_id, name, locale) "
    		+ "(SELECT b.id, a.description AS name, 'en_US' FROM ls_staging_items a "
    		+ "INNER JOIN sylius_product_variant b ON b.code = CONCAT('ls-', a.item_id) WHERE a.item_matrix_id=0) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "name = VALUES(name)";	
	
	public static final String UPDATE_QUANTITY_PRODUCT_VARIANTS = "UPDATE sylius_product_variant spv "
            + "INNER JOIN ("
	               + "SELECT a.item_id, b.qoh FROM ls_staging_items a "
	               + "INNER JOIN ls_staging_item_shops b ON a.item_id = b.item_id	"
	               + "WHERE b.shop_id = 0 "
            + ") AS d ON spv.code = CONCAT('ls-',d.item_id) "
            + "SET spv.on_hand = d.qoh";
    
	public static final String INSERT_PRODUCT_CHANNEL = "INSERT IGNORE INTO sylius_product_channels "
    		+ "(product_id,channel_id) "
    		+ "(SELECT id,1 FROM sylius_product)";

	public static final String INSERT_VARIANT_PRICES = "INSERT INTO sylius_channel_pricing "
    		+ "(product_variant_id, price, channel_code) "
    		+ "(SELECT a.id,b.amount * 100,'default' FROM sylius_product_variant a "
    		+ "INNER JOIN ls_staging_item_prices b ON CONCAT('ls-',b.item_id) = a.code WHERE use_type='Online') "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "price = VALUES(price)";
		
	public static final String INSERT_BUNDLE_RELATIONS = "INSERT INTO syncer_bundle_component "
    		+ "(parent_variant_id, child_variant_id, quantity, code) "
    		+ "(SELECT b.id,c.id,a.quantity,CONCAT('ls-',item_component_id) FROM ls_staging_item_components a "
    		+ "INNER JOIN sylius_product_variant b ON CONCAT('ls-',a.assembly_item_id) = b.`code` "
    		+ "INNER JOIN sylius_product_variant c ON CONCAT('ls-',a.component_item_id) = c.`code`) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "quantity = VALUES(quantity)";
	
	public static final String DELETE_BUNDLE_RELATION = "DELETE a FROM syncer_bundle_component a "
			+ "LEFT JOIN ls_staging_item_components b ON a.`code` = CONCAT('ls-',b.item_component_id) "
    		+ "INNER JOIN sylius_product_variant c ON a.parent_variant_id = c.id "
    		+ "INNER JOIN ls_staging_items d ON c.`code` = CONCAT('ls-', d.item_id) "
			+ "WHERE b.id IS NULL";
	
	public static final String INSERT_SHOPS = "INSERT INTO syncer_shop "
    		+ "(name, code, enabled) "
    		+ "(SELECT a.name, CONCAT('ls-',shop_id), ABS(archived-1) AS enabled FROM ls_staging_shops a) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "enabled = VALUES(enabled)";
	
	public static final String INSERT_SHOP_ITEMS = "INSERT INTO syncer_shop_product_variant "
    		+ "(shop_id, product_variant_id, quantity, code) "
    		+ "(SELECT b.id, c.id, a.qoh, CONCAT('ls-',item_shop_id) FROM ls_staging_item_shops a "
    		+ "INNER JOIN syncer_shop b ON CONCAT('ls-',a.shop_id) = b.`code` "
    		+ "INNER JOIN sylius_product_variant c ON CONCAT('ls-',item_id) = c.`code`) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "quantity = VALUES(quantity)";
	
}
