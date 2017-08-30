
package syncer.shipstationtosylius.classes;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Orders {

    public List<Order> orders = null;
    public Integer total;
    public Integer page;
    public Integer pages;

}
