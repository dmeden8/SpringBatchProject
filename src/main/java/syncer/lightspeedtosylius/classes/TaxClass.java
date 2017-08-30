package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "TaxClass")
public class TaxClass implements DatabaseObject {
	
	public int taxClassID;
	public String name;
	public Date timeStamp;

	@Override
	public void save(JdbcTemplate jdbcTemplate) {
    	jdbcTemplate.update(StagingQueries.INSERT_TAX_CLASSES,
    			this.taxClassID,
    			this.name,
    			this.timeStamp);
	}

}
