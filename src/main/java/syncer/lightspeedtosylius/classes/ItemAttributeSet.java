package syncer.lightspeedtosylius.classes;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "ItemAttributeSet")
public class ItemAttributeSet implements DatabaseObject {
 
	public int itemAttributeSetID;
	public String name;
	public String attributeName1;
	public String attributeName2;
	public String attributeName3;

	@Override
	public void save(JdbcTemplate jdbcTemplate) {
   		jdbcTemplate.update(StagingQueries.INSERT_ITEM_ATTRIBUTE_SET, this.itemAttributeSetID, this.name, this.attributeName1, this.attributeName2, this.attributeName3); 
	}

}
