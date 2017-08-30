package syncer.syliustoshipstation.help;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import syncer.syliustoshipstation.dto.ShippingDetailDto;

public class ShippingDetailRowMapper  implements RowMapper<ShippingDetailDto> {
	
	@Override
	public ShippingDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ShippingDetailDto shippingDetail = new ShippingDetailDto();
			
		shippingDetail.setOrderId(rs.getLong("id"));
		shippingDetail.setOrderNumber(rs.getString("number"));
		shippingDetail.setPriceTotal(rs.getLong("total"));
		shippingDetail.setItemQuantity(rs.getLong("quantity"));
		shippingDetail.setManufacturerSku(rs.getString("manufacturer_sku"));
		shippingDetail.setItemName(rs.getString("name"));
		shippingDetail.setPhoneNumber(rs.getString("phone_number"));
		shippingDetail.setAddressStreet(rs.getString("street"));
		shippingDetail.setAddressCity(rs.getString("city"));
		shippingDetail.setAddressCountry(rs.getString("country_code"));
		shippingDetail.setAddressPostcode(rs.getString("postcode"));
		shippingDetail.setAddressProvince(rs.getString("province_code"));
		shippingDetail.setFirstName(rs.getString("first_name"));
		shippingDetail.setLastName(rs.getString("last_name"));
		shippingDetail.setWeight(rs.getLong("weight"));
		shippingDetail.setWidth(rs.getLong("width"));
		shippingDetail.setDepth(rs.getLong("depth"));
		shippingDetail.setHeight(rs.getLong("height"));
		shippingDetail.setCreatedAt(rs.getString("created_at"));
		shippingDetail.setEmail(rs.getString("email"));
		
		return shippingDetail;
		
	}

}
