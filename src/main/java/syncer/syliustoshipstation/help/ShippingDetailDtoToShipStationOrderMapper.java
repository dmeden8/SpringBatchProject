package syncer.syliustoshipstation.help;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import syncer.syliustoshipstation.classes.BillTo;
import syncer.syliustoshipstation.classes.Dimensions;
import syncer.syliustoshipstation.classes.Item;
import syncer.syliustoshipstation.classes.ShipStationOrder;
import syncer.syliustoshipstation.classes.ShipTo;
import syncer.syliustoshipstation.dto.ShippingDetailDto;

@Component
public class ShippingDetailDtoToShipStationOrderMapper {
	
	public List<ShipStationOrder> prepareShippingDetailsForRestCall (List<? extends ShippingDetailDto> shippingDetails) {
		
		List<ShipStationOrder> shipStationOrders = new ArrayList<ShipStationOrder>();
		
		long orderId = 0;
		
		ShipStationOrder order = null;
		List<Item> items = null;
		
		for (ShippingDetailDto shippingDetail : shippingDetails) {
			
			if (shippingDetail.getOrderId() != orderId) {
				
				orderId = shippingDetail.getOrderId();
				
				order = new ShipStationOrder();
				items = new ArrayList<Item>();
								
				Item item = fillItem(shippingDetail);
				items.add(item);
				
				order.setItems(items);				
				order = fillOrder(order,shippingDetail);
				
				shipStationOrders.add(order);
			}
			else {
				Item item = fillItem(shippingDetail);
				items.add(item);				
			}			
		}
						
		return shipStationOrders;
		
	}
	
	private ShipStationOrder fillOrder(ShipStationOrder order, ShippingDetailDto shippingDetailDto) {
		
		String name = shippingDetailDto.getFirstName() + " " + shippingDetailDto.getLastName();
				
		order.setAmountPaid(shippingDetailDto.getPriceTotal()/100.0f);
		order.setOrderNumber(shippingDetailDto.getOrderNumber().toString());
		order.setOrderDate(transformDate(shippingDetailDto.getCreatedAt()));
		order.setOrderStatus("awaiting_shipment");
		order.setCustomerEmail(shippingDetailDto.getEmail());
		
		Dimensions dimensions = new Dimensions();
		dimensions.setHeight(shippingDetailDto.getHeight());
		dimensions.setLength(shippingDetailDto.getDepth());
		dimensions.setWidth(shippingDetailDto.getWidth());
		dimensions.setUnits("inches");
		
		BillTo billTo = new BillTo();
		billTo.setName(name);
		
		ShipTo shipTo = new ShipTo();
		shipTo.setName(name);
		shipTo.setStreet1(shippingDetailDto.getAddressStreet());
		shipTo.setCity(shippingDetailDto.getAddressCity());
		shipTo.setCountry(shippingDetailDto.getAddressCountry());
		shipTo.setState(shippingDetailDto.getAddressProvince());
		shipTo.setPostalCode(shippingDetailDto.getAddressPostcode());
		shipTo.setPhone(shippingDetailDto.getPhoneNumber());
		
		order.setDimensions(dimensions);
		order.setBillTo(billTo);
		order.setShipTo(shipTo);
				
		return order;
	}
	
	
	private Item fillItem(ShippingDetailDto shippingDetailDto) {
		
		Item item = new Item();
		
		item.setSku(shippingDetailDto.getManufacturerSku());
		item.setQuantity(shippingDetailDto.getItemQuantity());
		item.setName(shippingDetailDto.getItemName());
		
		return item;		
	}
	
	private String transformDate(String inDate) {
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(inDate);
		} catch (ParseException e) {}
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      
		return df.format(date);
	}

}
