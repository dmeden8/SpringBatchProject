package syncer.lightspeedtosylius.classes;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "ItemComponent")
public class ItemComponent implements DatabaseObject{
 
	public int itemComponentID;
	public int quantity;
	public int componentGroup;
	public int assemblyItemID;
	public int componentItemID;
	
	@Override
	public void save(JdbcTemplate jdbcTemplate) {
   		
		jdbcTemplate.update(StagingQueries.INSERT_ITEM_COMPONENT, 
   				this.itemComponentID, this.quantity, this.componentGroup, this.assemblyItemID, this.componentItemID
		);
   		  
	}

}
