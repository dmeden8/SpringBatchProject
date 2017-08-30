package syncer.shipstationtosylius.classes;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Shipments {

    public List<Shipment> shipments = null;
    public Integer total;
    public Integer page;
    public Integer pages;

}
