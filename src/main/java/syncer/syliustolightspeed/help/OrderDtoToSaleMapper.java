package syncer.syliustolightspeed.help;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import syncer.batch.config.AppProperties;
import syncer.syliustolightspeed.classes.Sale;
import syncer.syliustolightspeed.classes.SaleLine;
import syncer.syliustolightspeed.classes.SaleLines;
import syncer.syliustolightspeed.classes.SalePayment;
import syncer.syliustolightspeed.classes.SalePayments;
import syncer.syliustolightspeed.dto.OrderDto;

@Component
public class OrderDtoToSaleMapper {
	
	@Autowired
	private AppProperties config;
	
	public List<Sale> prepareOrdersForRestCall (List<? extends OrderDto> orders) {
		
		List<Sale> sales = new ArrayList<Sale>(); 
		
		long orderId = 0;
		long itemId = 0;
		
		Sale sale = null;
		List<SaleLine> listSaleLines = null;
		SaleLines saleLines = null;
		List<SalePayment> listSalePayments = null;
		SalePayments salePayments = null;
		
		for(int i=0; i<orders.size(); i++) {
			
			OrderDto order = orders.get(i);
						
			if (order.getOrderId() != orderId) {
												
				orderId = order.getOrderId();
				itemId = order.getItemId();
				
				sale = new Sale();
				saleLines = new SaleLines();
				listSaleLines = new ArrayList<SaleLine>();				
				salePayments = new SalePayments();
				listSalePayments = new ArrayList<SalePayment>();
				
				SaleLine saleLine = fillSaleLine(order);		
				listSaleLines.add(saleLine);				
				SalePayment salePayment = fillSalePayment(order);		
				listSalePayments.add(salePayment);
												
				saleLines.setSaleLine(listSaleLines);
				sale.setSaleLines(saleLines);				
				salePayments.setSalePayment(listSalePayments);
				sale.setSalePayments(salePayments);
				
				sale = fillSale(sale,order);	
				
				sales.add(sale);				
			}
			
			else {
				
				if(order.getItemId() != itemId) {
					itemId = order.getItemId();
					
					SaleLine saleLine = fillSaleLine(order);		
					listSaleLines.add(saleLine);
					saleLines.setSaleLine(listSaleLines);
					sale.setSaleLines(saleLines);
				}
			}
			
		}
				
		return sales;
		
	}
	
	private Sale fillSale (Sale sale, OrderDto order) {
		
		sale.setReferenceNumber(order.getOrderId());
		sale.setCustomerID(order.getCustomerId());
		sale.setEmployeeID(config.getEmployeeId());
		sale.setRegisterID(config.getRegisterId());
		sale.setShopID(config.getShopId());
		sale.setShipToID(0L);
		sale.setTaxCategoryID(order.getTaxCategoryId());
		sale.setQuoteID(0L);
		sale.setEnablePromotions(true);
		sale.setReceiptPreference("email");
		sale.setReferenceNumberSource("web");
		
		if(order.getState().equals(config.getLightspeedSendAsCompleteState())) {
			sale.setCompleted(true);
		}
		else {
			sale.setCompleted(false);
		}
				
		return sale;
		
	}
	
	private SaleLine fillSaleLine(OrderDto order) {
		
		SaleLine saleLine = new SaleLine();

		saleLine.setItemID(order.getItemId());
		saleLine.setUnitQuantity(order.getItemQuantity());
		saleLine.setUnitPrice(order.getItemAllPrice()/100.0d);
		saleLine.setNormalUnitPrice(order.getItemAllPrice()/100.0d);
		saleLine.setTax(true);
		saleLine.setTaxCategoryID(order.getTaxCategoryId());

		Double discount = 0.0;
		if (order.getAdjustType().equals("shipping")) {
			discount = (order.getItemAllPrice() - order.getAdjustPrice())/100.0d;
		} else {
			discount = -1 * order.getAdjustPrice()/100.0d;
		}
		saleLine.setDiscountAmount(discount);
		return saleLine;		
	}
	
	private SalePayment fillSalePayment(OrderDto order) {
		
		SalePayment salePayment = new SalePayment();
		
		salePayment.setSalePaymentID(0L);
		salePayment.setPaymentTypeID(3L);
		salePayment.setRegisterID(config.getRegisterId());
		salePayment.setEmployeeID(config.getEmployeeId());
		salePayment.setSaleID(0L);
		salePayment.setCreditAccountID(0L);
		salePayment.setAmount(order.getPriceTotal()/100.0d);
			
		return salePayment;		
	}
	
}
