package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "SaleLine")
public class SaleLine implements DatabaseObject {
	
	public int saleLineID;
	public Date createTime;
	public Date timeStamp;
	public int unitQuantity;
	public float unitPrice;
	public float normalUnitPrice;
	public float discountAmount;
	public float discountPercent;
	public float avgCost;
	public float fifoCost;
	public boolean tax;
	public float tax1Rate;
	public float tax2Rate;
	public boolean isLayaway;
	public boolean isWorkorder;
	public boolean isSpecialOrder;
	public float calcLineDiscount;
	public float calcTransactionDiscount;
	public float calcTotal;
	public float calcSubtotal;
	public float calcTax1;
	public float calcTax2;
	public int taxClassID;
	public int customerID;
	public int discountID;
	public int employeeID;
	public int itemID;
	public int noteID;
	public int parentSaleLineID;
	public int shopID;
	public int taxCategoryID;
	public int saleID;

	@Override
	public void save(JdbcTemplate jdbcTemplate) {
    	jdbcTemplate.update(StagingQueries.INSERT_STAGING_SALE_ITEM,
    			this.saleLineID,
    			this.createTime,
    			this.timeStamp,
    			this.unitQuantity,
    			this.unitPrice,
				this.normalUnitPrice,
				this.discountAmount,
				this.discountPercent,
				this.avgCost,
				this.fifoCost,
				this.tax,
				this.tax1Rate,
				this.tax2Rate,
				this.isLayaway,
				this.isWorkorder,
				this.isSpecialOrder,
				this.calcLineDiscount,
				this.calcTransactionDiscount,
				this.calcTotal,
				this.calcSubtotal,
				this.calcTax1,
				this.calcTax2,
				this.taxClassID,
				this.customerID,
				this.discountID,
				this.employeeID,
				this.itemID,
				this.noteID,
				this.parentSaleLineID,
				this.shopID,
				this.taxCategoryID,
				this.saleID);
	}

}
