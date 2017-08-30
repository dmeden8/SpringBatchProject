package syncer.lightspeedtosylius.classes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ItemComponents {

    @XmlElement(name="ItemComponent")
    private List<ItemComponent>itemComponent;
    
    public List<ItemComponent> getItemComponents() {
        if (itemComponent==null) {
        	itemComponent=new ArrayList<ItemComponent>();
        }
        return this.itemComponent;
    }

}
