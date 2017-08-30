package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "ItemShop")
public class ItemShop implements DatabaseObject {

	public int itemShopID;
	public int qoh;
	public int backorder;
	public int componentQoh;
	public int componentBackorder;
	public int reorderPoint;
	public int reorderLevel;
	public Date timeStamp;
	public int itemID;	
	public int shopID;
  
	@Override
	public void save(JdbcTemplate jdbcTemplate) {
    	jdbcTemplate.update(StagingQueries.INSERT_STAGING_ITEM_SHOP,
    			this.itemShopID,
    			this.qoh,
    			this.backorder,
    			this.componentQoh,
    			this.componentBackorder,
    			this.reorderPoint,
    			this.reorderLevel,
    			this.timeStamp,
    			this.itemID,
    			this.shopID);
	}

}
