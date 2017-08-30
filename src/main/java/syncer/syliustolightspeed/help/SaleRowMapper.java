package syncer.syliustolightspeed.help;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import syncer.syliustolightspeed.dto.OrderDto;

public class SaleRowMapper implements RowMapper<OrderDto> {
	
	@Override
	public OrderDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		OrderDto order = new OrderDto();
		
		order.setOrderId(rs.getLong("id"));
		order.setItemId(rs.getLong("item_id"));
		order.setState(rs.getString("state"));
		order.setItemQuantity(rs.getLong("quantity"));
		order.setItemAllPrice(rs.getLong("units_total"));
		order.setCustomerId(rs.getLong("customer_id"));
		order.setPriceTotal(rs.getLong("total"));
		order.setAdjustPrice(rs.getLong("adjust_amount"));
		order.setAdjustType(rs.getString("adjust_type"));
		order.setTaxCategoryId(rs.getLong("tax_id"));
		
		return order;
		
	}

}
