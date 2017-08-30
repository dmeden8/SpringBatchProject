package syncer.lightspeedtosylius.classes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Phones {
	
    @XmlElement(name="ContactPhone")
    private List<ContactPhone> contactPhone;
    
    public List<ContactPhone> getContactPhones() {
        if (contactPhone==null) {
        	contactPhone=new ArrayList<ContactPhone>();
        }
        return this.contactPhone;
    }

	public static class ContactPhone {
		
		public String number;
		public String useType;
	
	}

}
