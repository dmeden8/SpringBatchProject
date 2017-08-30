package syncer.syliustoshipstation.classes;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdvancedOptions {

    public Integer warehouseId;
    public Boolean nonMachinable;
    public Boolean saturdayDelivery;
    public Boolean containsAlcohol;
    public Boolean mergedOrSplit;
    public List<Object> mergedIds = null;
    public Object parentId;
    public Integer storeId;
    public String customField1;
    public String customField2;
    public String customField3;
    public String source;
    public Object billToParty;
    public Object billToAccount;
    public Object billToPostalCode;
    public Object billToCountryCode;

}
