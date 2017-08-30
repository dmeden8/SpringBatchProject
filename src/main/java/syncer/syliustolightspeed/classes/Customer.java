package syncer.syliustolightspeed.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {
	
    public Long customerID;
    public String firstName;
    public String lastName;
    public Long customerTypeID;
    public Long discountID;
    public Long taxCategoryID;    
    public Contact Contact;
    
	public Long referenceID;

}
