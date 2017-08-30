package syncer.syliustoshipstation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingDetailDto {
	
	private Long orderId;
	private String orderNumber;
	private Long priceTotal;
	private Long itemQuantity;
	private String manufacturerSku;
	private String itemName;
	private String phoneNumber;
	private String addressStreet;
	private String addressCity;
	private String addressPostcode;
	private String addressCountry;
	private String addressProvince;
	private String firstName;
	private String lastName;
	private Long width;
	private Long height;
	private Long depth;
	private Long weight;
	private String createdAt;
	private String email;

}
