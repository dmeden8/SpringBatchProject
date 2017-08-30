package syncer.syliustolightspeed.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleLine {
	
    public Long itemID;
    public Long unitQuantity;
    public Double unitPrice;
    public Double normalUnitPrice;
    public Boolean tax;
    public Long taxCategoryID;
    public Double discountAmount;
    public Long saleLineID;

//    public Long saleLineID;
//    public String createTime;
//    public String timeStamp;
//    public Long taxClassID;
//    public Long customerID;
//    public String discountID;
//    public String employeeID;
//    public String noteID;
//    public String parentSaleLineID;
//    public String shopID;
//    public String saleID;
//    public Note note;

}
