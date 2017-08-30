package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "ItemMatrix")
public class ItemMatrix implements DatabaseObject {

    public int itemMatrixID;
    public String description;
    public boolean tax;
    public Float defaultCost;
    public String itemType;
    public int modelYear;
    public Boolean archived;
    public Date timeStamp;
    public int itemAttributeSetID;
    public int manufacturerID;
    public int categoryID;
    public int defaultVendorID;
    public int taxClassID;
    public int seasonID;
    public int departmentID;

    public String longDescription;
    public String shortDescription;
    public float weight;
    public float width;
    public float height;
    public float length;
    public Boolean listOnStore;

	@Override
	public void save(JdbcTemplate jdbcTemplate) {
   	
    	jdbcTemplate.update(StagingQueries.INSERT_STAGING_ITEM_MATRIX, 
    			this.itemMatrixID, 
            	this.description, 
            	this.tax, 
            	this.defaultCost, 
            	this.itemType, 
            	this.modelYear, 
            	this.archived, 
            	this.timeStamp, 
            	this.itemAttributeSetID,
            	this.manufacturerID,
            	this.categoryID, 
            	this.defaultVendorID, 
            	this.taxClassID,  
            	this.seasonID, 
            	this.departmentID, 
            	this.longDescription,
            	this.shortDescription,
            	this.weight,
            	this.width,
            	this.height,
            	this.length,
            	this.listOnStore
    			);
    	
	}

}
