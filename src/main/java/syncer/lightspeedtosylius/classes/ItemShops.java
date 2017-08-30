package syncer.lightspeedtosylius.classes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ItemShops {

    @XmlElement(name="ItemShop")
    private List<ItemShop>itemShop;
    
    public List<ItemShop> getItemShop() {
        if (itemShop==null) {
        	itemShop=new ArrayList<ItemShop>();
        }
        return this.itemShop;
    }

}
