package syncer.lightspeedtosylius.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.queries.StagingQueries;

@XmlRootElement(name = "Customer")
public class Customer implements DatabaseObject {

	public int customerID;
	public String firstName;
	public String lastName;
	public Date dob;
	public boolean archived;

    @XmlElement(name="Contact")
	public Contact contact;
    
    public Customer process() {
    	if (this.contact.getEmails().getContactEmail().size() > 0) {
        	return this;
    	}
    	else return null;
    }
    
	@Override
	public void save(JdbcTemplate jdbcTemplate) {
		
    	jdbcTemplate.update(StagingQueries.INSERT_STAGING_CUSTOMER, 
    			this.customerID,
    			this.contact.getFirstEmail(),
    			this.contact.getFirstEmail().toLowerCase(),
    			this.firstName,
    			this.lastName,
    			this.dob,
    			this.contact.getFirstPhone(),
    			this.archived
    			);
    	
    	for(Addresses.ContactAddress contactAddress: this.contact.getAddresses().getContactAddress()){
    		contactAddress.save(jdbcTemplate, this.customerID);
    	}
    	
	}

}
