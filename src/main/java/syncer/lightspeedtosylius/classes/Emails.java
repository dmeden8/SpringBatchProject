package syncer.lightspeedtosylius.classes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Emails {

    @XmlElement(name="ContactEmail")
    private List<ContactEmail> contactEmail;
    
    public List<ContactEmail> getContactEmail() {
        if (contactEmail==null) {
        	contactEmail=new ArrayList<ContactEmail>();
        }
        return this.contactEmail;
    }
    
	public static class ContactEmail {
		
		public String address;
		public String useType;
	
	}

}
