package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "Manufacturer")
public class Manufacturer implements DatabaseObject {
		
	public int manufacturerID;
	public String name;
	public Date timeStamp;

	@Override
	public void save(JdbcTemplate jdbcTemplate) {
    	jdbcTemplate.update(StagingQueries.INSERT_MANUFACTURER,
    			this.manufacturerID,
    			this.name,
    			this.timeStamp);
	}

}
