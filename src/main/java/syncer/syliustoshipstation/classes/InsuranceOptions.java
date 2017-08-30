package syncer.syliustoshipstation.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsuranceOptions {

    public String provider;
    public Boolean insureShipment;
    public Integer insuredValue;

}
