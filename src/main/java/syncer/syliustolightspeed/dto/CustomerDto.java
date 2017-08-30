package syncer.syliustolightspeed.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {
	
	private Long customerSyliusId;
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String addressStreet;
	private String addressCity;
	private String addressPostcode;
	private String addressCountry;
	private String addressProvince;

}
