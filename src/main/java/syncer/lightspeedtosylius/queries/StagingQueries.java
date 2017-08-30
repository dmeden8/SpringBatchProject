package syncer.lightspeedtosylius.queries;

import org.springframework.stereotype.Component;

@Component
public class StagingQueries {
	
	 public static final String INSERT_STAGING_CUSTOMER_ADDRESS = "INSERT INTO ls_staging_customer_address "
	    		+ "(customer_id,address_1,city,state,zip,country) "
	    		+ "VALUES (?,?,?,?,?,?)";
	 		
	 public static final String INSERT_STAGING_CUSTOMER = "INSERT INTO ls_staging_customers "
	    		+ "(customer_id, email, email_canonical, first_name, last_name, birthday, phone_number, archived)"
	    		+ "VALUES "
	    		+ "(?,?,?,?,?,?,?,?)";
	 
	 public static final String INSERT_STAGING_ITEM = "INSERT INTO ls_staging_items "
	    		+ "(item_id,system_sku,default_cost,avg_cost,discountable,tax,archived,item_type,description,model_year,upc,ean,custom_sku,manufacturer_sku,"
	    		+ "create_time,timestamp,category_id,tax_class_id,department_id,"
	    		+ "item_matrix_id,manufacturer_id,season_id,default_vendor_id,long_description,short_description,weight,width,height,length,list_on_store) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	 public static final String INSERT_STAGING_ITEM_PRICE = "INSERT INTO ls_staging_item_prices "
	    		+ "(item_id,amount,use_type) "
	    		+ "VALUES "
	    		+ "(?,?,?)";

	 public static final String INSERT_STAGING_ITEM_ATTRIBUTE = "INSERT INTO ls_staging_item_attributes "
	    		+ "(item_attribute_set_id,item_id,attribute_1,attribute_2,attribute_3) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?)";

	 public static final String INSERT_STAGING_ITEM_MATRIX = "INSERT INTO ls_staging_item_matrices "
	    		+ "(item_matrix_id,description,tax,default_cost,item_type,model_year,archived,timestamp,item_attribute_set_id,manufacturer_id,"
	    		+ "category_id,default_vendor_id,tax_class_id,season_id,department_id,long_description,short_description,weight,width,height,length,list_on_store) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	 
	 public static final String INSERT_STAGING_SHOP = "INSERT INTO ls_staging_shops "
	    		+ "(shop_id, name, service_rate,time_zone,tax_labor,label_title,label_msrp,archived,timestamp,contact_id,tax_category_id,receipt_setup_id,cc_gateway_id,price_level_id) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	 
	 public static final String INSERT_STAGING_ITEM_SHOP = "INSERT INTO ls_staging_item_shops "
	    		+ "(item_shop_id,qoh,backorder,component_qoh,component_backorder,reorder_point,reorder_level,timestamp,item_id,shop_id) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?,?,?,?,?,?) ";
	 
	 public static final String INSERT_STAGING_SALE = "INSERT INTO ls_staging_sales "
	    		+ "(sale_id,timestamp,discount_percent,completed,archived,voided,enable_promotions,reference_number,reference_number_source,tax1_rate,tax2_rate,"
	    		+ "`change`,receipt_preference,ticket_number,calc_discount,calc_total,calc_subtotal,calc_taxable,calc_non_taxable,calc_avg_cost,calc_fifo_cost,calc_tax1,"
	    		+ "calc_tax2,calc_payments,total,total_due,balance,customer_id,discount_id,employee_id,quote_id,register_id,ship_to_id,shop_id,tax_category_id,tax_total) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    
	 public static final String INSERT_STAGING_SALE_ITEM = "INSERT INTO ls_staging_sale_lines "
	    		+ "(sale_line_id,create_time,timestamp,unit_quantity,unit_price,normal_unit_price,discount_amount,discount_percent,avg_cost,fifo_cost,tax,"
	    		+ "tax1_rate,tax2_rate,is_layaway,is_workorder,is_special_order,calc_line_discount,calc_transaction_discount,calc_total,calc_subtotal,"
	    		+ "calc_tax1,calc_tax2,tax_class_id,customer_id,discount_id,employee_id,item_id,note_id,parent_sale_line_id,shop_id,tax_category_id,sale_id) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";   
	 
	 public static final String INSERT_ITEM_ATTRIBUTE_SET = "INSERT INTO ls_staging_item_attribute_sets "
	    		+ "(item_attribute_set_id,name,attribute_name1,attribute_name2,attribute_name3) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?)";
	 
	 public static final String INSERT_ITEM_COMPONENT = "INSERT INTO ls_staging_item_components "
	    		+ "(item_component_id,quantity,component_group,assembly_item_id,component_item_id) "
	    		+ "VALUES "
	    		+ "(?,?,?,?,?)";
	 
	 public static final String INSERT_MANUFACTURER = "INSERT INTO ls_staging_manufacturers "
	    		+ "(manufacturer_id,name,timestamp) "
	    		+ "VALUES "
	    		+ "(?,?,?)";
	 	 
	 public static final String INSERT_TAX_CLASSES = "INSERT INTO ls_staging_tax_classes "
	    		+ "(tax_class_id,name,timestamp) "
	    		+ "VALUES "
	    		+ "(?,?,?)";
	 
}
