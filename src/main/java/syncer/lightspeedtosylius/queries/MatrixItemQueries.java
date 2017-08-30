package syncer.lightspeedtosylius.queries;

public class MatrixItemQueries {
	
    public static final String INSERT_ITEM_ATTRIBUTE_SETS = "INSERT INTO syncer_attribute_set "
    		+ "(code, name, attribute_name1, attribute_name2, attribute_name3) "
    		+ "(SELECT CONCAT('ls-as-',item_attribute_set_id),name,attribute_name1,attribute_name2,attribute_name3 "
    		+ "FROM ls_staging_item_attribute_sets) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "code = VALUES(code),"
    		+ "name = VALUES(name),"
    		+ "attribute_name1 = VALUES(attribute_name1),"
    		+ "attribute_name2 = VALUES(attribute_name2),"
    		+ "attribute_name3 = VALUES(attribute_name3)";
    
    public static final String INSERT_ITEM_ATTRIBUTE_VALUES = "INSERT INTO syncer_attribute "
   		    + "(attribute_value1, attribute_value2, attribute_value3, code) "
    		+ "(SELECT a.attribute_1, a.attribute_2, a.attribute_3, CONCAT('ls-at-',a.item_id) FROM ls_staging_item_attributes a) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "attribute_value1 = VALUES(attribute_value1),"
    		+ "attribute_value2 = VALUES(attribute_value2),"
    		+ "attribute_value3 = VALUES(attribute_value3)";
	  
    public static final String INSERT_PRODUCT_CONFIGURABLE = "INSERT INTO sylius_product "
    		+ "(code,enabled, created_at, variant_selection_method, item_attribute_set_id) "
    		+ "(SELECT CONCAT('ls-m-',item_matrix_id),ABS(archived-1) AS enabled,NOW(),'choice',b.id FROM ls_staging_item_matrices a "
    		+ "INNER JOIN syncer_attribute_set b ON b.code = CONCAT('ls-as-', a.item_attribute_set_id)) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "enabled = VALUES(enabled), "
    		+ "item_attribute_set_id = VALUES(item_attribute_set_id)";

    public static final String INSERT_PRODUCT_CONFIGURABLE_TRANSLATION = "INSERT INTO sylius_product_translation "
    		+ "(translatable_id, name,slug,locale) "
    		+ "(SELECT b.id, a.description AS name,CONCAT('ls-m-',a.item_matrix_id),'en_US' FROM ls_staging_item_matrices a "
    		+ "INNER JOIN sylius_product b ON b.code = CONCAT('ls-m-', a.item_matrix_id)) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "name = VALUES(name)";

    public static final String INSERT_PRODUCT_VARIANTS_CONFIGURABLE = "INSERT INTO sylius_product_variant "
    		+ "(product_id, tax_category_id, code, created_at, position, on_hold, on_hand, tracked, shipping_required, item_attributes_id,upc,ean,customer_sku,manufacturer_sku) "
    		+ "(SELECT b.id, "
    		+ "  (CASE WHEN d.id IS NULL THEN @taxCategoryId "
    		+ "        ELSE d.id "
    		+ "	  END) AS tax_category_id, "
    		+ "CONCAT('ls-',a.item_id),NOW(),0,0,0,0,1,c.id,a.upc,a.ean,a.custom_sku,a.manufacturer_sku FROM ls_staging_items a "
    		+ "INNER JOIN sylius_product b ON CONCAT('ls-m-', a.item_matrix_id) = b.code "
    		+ "INNER JOIN syncer_attribute c ON c.code = CONCAT('ls-at-', a.item_id) "
    		+ "LEFT JOIN sylius_tax_category d ON CONCAT('ls-',a.tax_class_id) = d.code "
    		+ "WHERE a.item_matrix_id>0) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "tax_category_id = VALUES(tax_category_id), "
    		+ "upc = VALUES(upc), "
    		+ "ean = VALUES(ean), "
    		+ "customer_sku = VALUES(customer_sku), "
			+ "manufacturer_sku = VALUES(manufacturer_sku), "
    		+ "item_attributes_id = VALUES(item_attributes_id)";
    
    public static final String INSERT_PRODUCT_VARIANTS_CONFIGURABLE_TRANSLATION = "INSERT INTO sylius_product_variant_translation "
    		+ "(translatable_id, name, locale) "
    		+ "(SELECT b.id, a.description AS name, 'en_US' FROM ls_staging_items a "
    		+ "INNER JOIN sylius_product_variant b ON b.code = CONCAT('ls-', a.item_id) WHERE a.item_matrix_id>0) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "name = VALUES(name)";
    
    
}
