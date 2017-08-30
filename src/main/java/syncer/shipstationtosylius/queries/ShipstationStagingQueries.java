package syncer.shipstationtosylius.queries;

public class ShipstationStagingQueries {
	
	public static final String INSERT_SHIPPING_INFO = "INSERT INTO ss_staging_shipping_info "
			+ "(order_id, order_status) "
			+ "VALUES (?,?)";
}
