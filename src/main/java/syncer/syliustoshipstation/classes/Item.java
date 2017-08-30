package syncer.syliustoshipstation.classes;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    public Object lineItemKey;
    public String sku;
    public String name;
    public Object imageUrl;
    public Weight weight;
    public Long quantity;
    public Float unitPrice;
    public Object taxAmount;
    public Object shippingAmount;
    public Object warehouseLocation;
    public List<Option> options = null;
    public Integer productId;
    public String fulfillmentSku;
    public Boolean adjustment;
    public Object upc;

}
