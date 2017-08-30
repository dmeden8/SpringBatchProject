package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.classes.Prices.ItemPrice;
import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "Item")
public class Item implements DatabaseObject{ 
    
    public int itemID;
    public String systemSku;
    public Float defaultCost;
    public Float avgCost;
    public Boolean discountable;
    public Boolean tax;
    public Boolean archived;
    public String itemType;
    public String description;
    public String modelYear;
    public String upc;
    public String ean;
    public String customSku;
    public String manufacturerSku;
    public Date createTime;
    public Date timeStamp;
    public int categoryID;
    public int taxClassID;
    public int departmentID;
    public int itemMatrixID;
    public int manufacturerID;
    public int seasonID;
    public int defaultVendorID;
    public int itemECommerceID;  

    public String longDescription;
    public String shortDescription;
    public float weight;
    public float width;
    public float height;
    public float length;
    public Boolean listOnStore;

    private Prices prices;
 
    @XmlElement(name="Prices")
    public void setPrices(Prices value) {
        this.prices=value;
    }
    
    public Prices getPrices() {
        return prices;
    }

    private ItemAttributes itemAttributes;

    @XmlElement(name="ItemAttributes")
    public void setItemAttributes(ItemAttributes value) {
        this.itemAttributes = value;
    }

    private ItemShops itemShops;

    @XmlElement(name="ItemShops")
    public void setItemShops(ItemShops value) {
        this.itemShops = value;
    }
        
    private ItemComponents itemComponents;

    @XmlElement(name="ItemComponents")
    public void setItemComponents(ItemComponents value) {
        this.itemComponents = value;
    }

	@Override
    public void save(JdbcTemplate jdbcTemplate) {
    	  	
    	jdbcTemplate.update(StagingQueries.INSERT_STAGING_ITEM, 
    			this.itemID, 
            	this.systemSku, 
            	this.defaultCost, 
            	this.avgCost,
            	this.discountable, 
            	this.tax, 
            	this.archived, 
            	this.itemType, 
            	this.description, 
            	this.modelYear, 
            	this.upc, 
            	this.ean, 
            	this.customSku, 
            	this.manufacturerSku, 
            	this.createTime, 
            	this.timeStamp, 
            	this.categoryID, 
            	this.taxClassID, 
            	this.departmentID, 
            	this.itemMatrixID, 
            	this.manufacturerID, 
            	this.seasonID, 
            	this.defaultVendorID, 
            	this.longDescription,
            	this.shortDescription,
            	this.weight,
            	this.width,
            	this.height,
            	this.length,
            	this.listOnStore
    			);

    	for(ItemPrice price: prices.getItemPrice()){
        	jdbcTemplate.update(StagingQueries.INSERT_STAGING_ITEM_PRICE,this.itemID,price.amount,price.useType);
    	}
    	
    	if(this.itemAttributes != null) {
        	jdbcTemplate.update(StagingQueries.INSERT_STAGING_ITEM_ATTRIBUTE,
        			this.itemAttributes.itemAttributeSetID,
        			this.itemID,
        			this.itemAttributes.attribute1,
        			this.itemAttributes.attribute2,
        			this.itemAttributes.attribute3);
    	}
    	
    	  	
    	if(this.itemShops != null) {
    		for(ItemShop itemShop: this.itemShops.getItemShop()){
    			itemShop.save(jdbcTemplate);
    		}
    	}
    	
    	if(this.itemComponents != null) {
    		for(ItemComponent itemComponent: this.itemComponents.getItemComponents()){
    			itemComponent.save(jdbcTemplate);
    		}
    	}
    	
    	
    }
}
