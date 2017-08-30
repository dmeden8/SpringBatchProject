package syncer.syliustolightspeed.help;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import syncer.syliustolightspeed.classes.Addresses;
import syncer.syliustolightspeed.classes.Contact;
import syncer.syliustolightspeed.classes.ContactAddress;
import syncer.syliustolightspeed.classes.ContactEmail;
import syncer.syliustolightspeed.classes.ContactPhone;
import syncer.syliustolightspeed.classes.Customer;
import syncer.syliustolightspeed.classes.Emails;
import syncer.syliustolightspeed.classes.Phones;
import syncer.syliustolightspeed.dto.CustomerDto;

@Component
public class CustomerDtoToCustomerMapper {
	
	public List<Customer> prepareCustomersForRestCall (List<? extends CustomerDto> customersSylius) {
		
		List<Customer> customers = new ArrayList<Customer>(); 
		
		for (CustomerDto customerSylius : customersSylius) {
			
			Customer customer = new Customer();
			
			customer.setFirstName(customerSylius.getFirstName());
			customer.setLastName(customerSylius.getLastName());
			customer.setCustomerTypeID(0L);
			customer.setDiscountID(0L);
			customer.setTaxCategoryID(0L);
			
			Contact contact = new Contact();
			
			Emails emails = new Emails();
			ContactEmail contactEmail = new ContactEmail();
			contactEmail.setAddress(customerSylius.getEmail());
			contactEmail.setUseType("Primary");
			emails.setContactEmail(contactEmail);
						
			Phones phones = new Phones();
			ContactPhone contactPhone = new ContactPhone();
			contactPhone.setNumber(customerSylius.getPhoneNumber());
			contactPhone.setUseType("Home");
			phones.setContactPhone(contactPhone);
			
			Addresses addresses = new Addresses();
			ContactAddress contactAddress = new ContactAddress();
			contactAddress.setAddress1(customerSylius.getAddressStreet());
			contactAddress.setCity(customerSylius.getAddressCity());
			contactAddress.setCountry(customerSylius.getAddressCountry());
			contactAddress.setState(customerSylius.getAddressProvince());
			contactAddress.setZip(customerSylius.getAddressPostcode());
			addresses.setContactAddress(contactAddress);
			
			contact.setAddresses(addresses);
			contact.setEmails(emails);
			contact.setPhones(phones);
			
			customer.setContact(contact);
			customer.setReferenceID(customerSylius.getCustomerSyliusId());
			
			customers.add(customer);
						
		}
				
		
		return customers;
		
	}

}
