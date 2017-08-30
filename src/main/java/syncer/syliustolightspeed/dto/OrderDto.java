package syncer.syliustolightspeed.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
	
	private Long orderId;
	private Long priceTotal;
	private String adjustType;
	private Long adjustPrice;
	private Long itemId;
	private Long itemQuantity;
	private Long itemAllPrice;
	private Long taxCategoryId;
	private Long customerId;
	private String state;
	
}
