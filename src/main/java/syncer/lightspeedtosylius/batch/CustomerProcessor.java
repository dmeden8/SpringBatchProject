package syncer.lightspeedtosylius.batch;

import org.springframework.batch.item.ItemProcessor;

import syncer.lightspeedtosylius.classes.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(final Customer item) throws Exception {
    	return item.process();
	}
}
