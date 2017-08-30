package syncer.lightspeedtosylius.classes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Prices {

    @XmlElement(name="ItemPrice")
    private List<ItemPrice>itemPrice;
    
    public List<ItemPrice> getItemPrice() {
        if (itemPrice==null) {
        	itemPrice=new ArrayList<ItemPrice>();
        }
        return this.itemPrice;
    }

    public static class ItemPrice {
        public float amount;
        public String useType;
    }

}
