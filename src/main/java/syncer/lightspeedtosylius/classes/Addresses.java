package syncer.lightspeedtosylius.classes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

public class Addresses {

    @XmlElement(name="ContactAddress")
    private List<ContactAddress> contactAddress;
    
    public List<ContactAddress> getContactAddress() {
        if (contactAddress==null) {
        	contactAddress=new ArrayList<ContactAddress>();
        }
        return this.contactAddress;
    }
    
	public static class ContactAddress {
	
		public String address1;
		public String city;
		public String state;
		public String zip;
		public String country;

		public void save(JdbcTemplate jdbcTemplate, int customerId) {
			if(this.address1 != null) {
		    	jdbcTemplate.update(StagingQueries.INSERT_STAGING_CUSTOMER_ADDRESS,
		    			customerId,
		    			this.address1,
		    			this.city,
		    			this.state,
		    			this.zip,
		    			this.country);
			}
			
		}
	
	}

}
