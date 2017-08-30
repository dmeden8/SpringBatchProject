package syncer.lightspeedtosylius.classes;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.classes.Addresses.ContactAddress;

public class Contact { 
		
    private Addresses addresses;
 
    @XmlElement(name="Addresses")
    public void setAddresses(Addresses value) {
        this.addresses=value;
    }
    
    public Addresses getAddresses() {
        return addresses;
    }
    
    public ContactAddress getFirstAddress() {
    	if (this.addresses.getContactAddress().size() > 0)
    		return this.addresses.getContactAddress().get(0);
    	else return new ContactAddress();
    }
	
    private Phones phones;
 
    @XmlElement(name="Phones")
    public void setPhones(Phones value) {
        this.phones=value;
    }
    
    public Phones getPhones() {
        return phones;
    }
    
    public String getFirstPhone() {
    	if (this.phones.getContactPhones().size() > 0)
    		return this.phones.getContactPhones().get(0).number;
    	else return "";
    }
	
    private Emails emails;
 
    @XmlElement(name="Emails")
    public void setEmails(Emails value) {
        this.emails=value;
    }
    
    public Emails getEmails() {
        return emails;
    }
    
    public String getFirstEmail() {
    	if (this.emails.getContactEmail().size() > 0)
    		return this.emails.getContactEmail().get(0).address;
    	else return "";
    }
	
	public void save(JdbcTemplate jdbcTemplate, int customerID) {
		
    	for(Addresses.ContactAddress contactAddress: this.addresses.getContactAddress()){
    		contactAddress.save(jdbcTemplate, customerID);
    	}
						
	}
	
}
