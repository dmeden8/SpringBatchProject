package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "Sale")
public class Sale implements DatabaseObject {
	
	public int saleID;
	public Date timeStamp;
	public float discountPercent;
	public boolean completed;
	public boolean archived;
	public boolean voided;
	public boolean enablePromotions;
	public String referenceNumber;
	public String referenceNumberSource;
	public float tax1Rate;
	public float tax2Rate;
	public float change;
	public String receiptPreference;
	public String ticketNumber;
	public float calcDiscount;
	public float calcTotal;
	public float calcSubtotal;
	public float calcTaxable;
	public float calcNonTaxable;
	public float calcAvgCost;
	public float calcFIFOCost;
	public float calcTax1;
	public float calcTax2;
	public float calcPayments;
	public float total;
	public float totalDue;
	public float balance;
	public int customerID;
	public int discountID;
	public int employeeID;
	public int quoteID;
	public int registerID;
	public int shipToID;
	public int shopID;
	public int taxCategoryID;
	public float taxTotal;

	@Override
	public void save(JdbcTemplate jdbcTemplate) {
    	jdbcTemplate.update(StagingQueries.INSERT_STAGING_SALE,
    			this.saleID,
    			this.timeStamp,
    			this.discountPercent,
    			this.completed,
    			this.archived,
				this.voided,
				this.enablePromotions,
				this.referenceNumber,
				this.referenceNumberSource,
				this.tax1Rate,
				this.tax2Rate,
				this.change,
				this.receiptPreference,
				this.ticketNumber,
				this.calcDiscount,
				this.calcTotal,
				this.calcSubtotal,
				this.calcTaxable,
				this.calcNonTaxable,
				this.calcAvgCost,
				this.calcFIFOCost,
				this.calcTax1,
				this.calcTax2,
				this.calcPayments,
				this.total,
				this.totalDue,
				this.balance,
				this.customerID,
				this.discountID,
				this.employeeID,
				this.quoteID,
				this.registerID,
				this.shipToID,
				this.shopID,
				this.taxCategoryID,
				this.taxTotal);
	}

}
