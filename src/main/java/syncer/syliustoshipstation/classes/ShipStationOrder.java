package syncer.syliustoshipstation.classes;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipStationOrder {

    public String orderNumber;
    public String orderKey;
    public String orderDate;
    public String paymentDate;
    public String shipByDate;
    public String orderStatus;
    public Integer customerId;
    public String customerUsername;
    public String customerEmail;
    public BillTo billTo;
    public ShipTo shipTo;
    public List<Item> items = null;
    public Float amountPaid;
    public Integer taxAmount;
    public Integer shippingAmount;
    public String customerNotes;
    public String internalNotes;
    public Boolean gift;
    public String giftMessage;
    public String paymentMethod;
    public String requestedShippingService;
    public String carrierCode;
    public String serviceCode;
    public String packageCode;
    public String confirmation;
    public String shipDate;
    public Weight weight;
    public Dimensions dimensions;
    public InsuranceOptions insuranceOptions;
    public InternationalOptions internationalOptions;
    public AdvancedOptions advancedOptions;

}
