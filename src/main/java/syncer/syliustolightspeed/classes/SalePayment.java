package syncer.syliustolightspeed.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalePayment {
	
    public Long registerID;
    public Long employeeID;
    public Long creditAccountID;
    public Long salePaymentID;    
    public Double amount;
    public Long paymentTypeID;
    public Long saleID;

}
