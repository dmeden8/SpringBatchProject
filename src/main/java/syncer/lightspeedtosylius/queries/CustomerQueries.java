package syncer.lightspeedtosylius.queries;

public class CustomerQueries {
	
	public static final String INSERT_CUSTOMERS = "INSERT INTO sylius_customer "
    		+ "(email, email_canonical, first_name, last_name, phone_number, code, created_at, subscribed_to_newsletter) "
    		+ "(SELECT email, email_canonical, first_name, last_name, phone_number, CONCAT('ls-',customer_id), NOW(), 0 FROM ls_staging_customers)"
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "first_name = VALUES(first_name), "
    		+ "last_name = VALUES(last_name), "
    		+ "phone_number = VALUES(phone_number)";
	
	
	public static final String INSERT_CUSTOMER_ADDRESSES = "INSERT INTO sylius_address "
    		+ "(customer_id, first_name, last_name, phone_number, street, city, postcode, code, created_at, country_code, province_code) "
    		+ "(SELECT b.id, a.first_name, a.last_name, a.phone_number, c.address_1, c.city, c.zip, CONCAT('ls-',a.customer_id), NOW(), c.country, c.state "
    		+ "FROM ls_staging_customers a INNER JOIN sylius_customer b ON CONCAT('ls-',a.customer_id) = b.code "
    		+ "INNER JOIN ls_staging_customer_address c ON a.customer_id = c.customer_id) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "first_name = VALUES(first_name), "
    		+ "last_name = VALUES(last_name), "
    		+ "phone_number = VALUES(phone_number),"
			+ "street = VALUES(street)";
	
	
	public static final String INSERT_SHOP_USERS = "INSERT INTO sylius_shop_user "
    		+ "(customer_id, username, username_canonical, enabled, salt, password, email, email_canonical, created_at, code, locked, roles) "
    		+ "(SELECT b.id, a.email, a.email_canonical, ABS(a.archived-1) AS enabled, md5(customer_id), md5(customer_id), a.email, a.email_canonical, NOW(), CONCAT('ls-',customer_id), 0, 'a:1:{i:0;s:9:\"ROLE_USER\";}' "
    		+ "FROM ls_staging_customers a INNER JOIN sylius_customer b ON CONCAT('ls-',customer_id) = b.code)"
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "enabled = VALUES(enabled)";
	
}
