package syncer.lightspeedtosylius.queries;

public class TaxQueries {
	
	public static final String INSERT_TAX_CATEGORIES= "INSERT INTO sylius_tax_category (code, name, description, created_at) "
			+ "(SELECT CONCAT('ls-',a.tax_class_id), a.name, a.name, a.`timestamp` FROM ls_staging_tax_classes a ) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "name = VALUES(name)";
	
	public static final String SET_DEFAULT_TAX_CATEGORY_VARIABLE = "SET @taxCategoryId=(SELECT id FROM sylius_tax_category WHERE code='ls-1')";
}
