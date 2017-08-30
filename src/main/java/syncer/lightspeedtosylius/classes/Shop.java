package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "Shop")
public class Shop implements DatabaseObject {
	
	public int shopID;
	public String name;
	public float serviceRate;
	public String timeZone;
	public boolean taxLabor;
	public String labelTitle;
	public boolean labelMsrp;
	public boolean archived;
	public Date timeStamp;
	public int contactID;
	public int taxCategoryID;
	public int receiptSetupID;
	public int ccGatewayID;
	public int priceLevelID;
	
	@Override
	public void save(JdbcTemplate jdbcTemplate) {
    	jdbcTemplate.update(StagingQueries.INSERT_STAGING_SHOP,
    			this.shopID,
    			this.name,
    			this.serviceRate,
    			this.timeZone,
    			this.taxLabor,
    			this.labelTitle,
    			this.labelMsrp,
    			this.archived,
    			this.timeStamp,
    			this.contactID,
    			this.taxCategoryID,
    			this.receiptSetupID,
    			this.ccGatewayID,
    			this.priceLevelID);    	
	}
}
