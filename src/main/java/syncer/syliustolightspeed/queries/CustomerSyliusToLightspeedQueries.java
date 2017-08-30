package syncer.syliustolightspeed.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerSyliusToLightspeedQueries {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
		
	public void updateCustomerCode(String customerSyliusId, Long customerId) {
				
			String query = "UPDATE sylius_customer SET code = 'ls-" + customerSyliusId + "' where id = " + customerId;   		
			jdbcTemplate.execute(query);				
	}
			
	public static final String SELECT_SYLIUS_CUSTOMERS = "SELECT a.id, a.email, a.first_name, a.last_name, a.phone_number, "
			+ "b.street, b.city, b.postcode, b.country_code, b.province_code "
			+ "FROM sylius_customer a LEFT JOIN sylius_address b ON a.default_address_id = b.id "
			+ "WHERE a.code IS NULL";	

}
