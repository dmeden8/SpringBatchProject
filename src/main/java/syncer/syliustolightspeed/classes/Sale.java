package syncer.syliustolightspeed.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sale {
	
	public Long saleID;
    public Long referenceNumber;
    public Long customerID;
    public Long registerID;
    public Long employeeID;
    public Long shipToID;
    public Long shopID;
    public Long taxCategoryID;
    public Long quoteID;
    public Boolean completed;
    public Boolean enablePromotions;
    public String receiptPreference;
    public String referenceNumberSource;
    public SaleLines SaleLines;
    public SalePayments SalePayments;

}
