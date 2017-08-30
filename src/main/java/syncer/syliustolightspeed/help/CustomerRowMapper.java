package syncer.syliustolightspeed.help;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import syncer.syliustolightspeed.dto.CustomerDto;

public class CustomerRowMapper implements RowMapper<CustomerDto> {
	
	@Override
	public CustomerDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CustomerDto customer = new CustomerDto();
			
		customer.setCustomerSyliusId(rs.getLong("id"));
		customer.setEmail(rs.getString("email"));
		customer.setFirstName(rs.getString("first_name"));
		customer.setLastName(rs.getString("last_name"));
		customer.setPhoneNumber(rs.getString("phone_number"));
		customer.setAddressStreet(rs.getString("street"));
		customer.setAddressCity(rs.getString("city"));
		customer.setAddressCountry(rs.getString("postcode"));
		customer.setAddressPostcode(rs.getString("country_code"));
		customer.setAddressProvince(rs.getString("province_code"));
		
		return customer;
		
	}

}
