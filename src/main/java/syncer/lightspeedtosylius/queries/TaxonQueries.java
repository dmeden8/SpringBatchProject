package syncer.lightspeedtosylius.queries;

public class TaxonQueries {
	
	public static final String SET_BRAND_VARIABLES = "SET @brandId=(SELECT id FROM sylius_taxon WHERE code='brand'),@treeLeft=0,@treeRight=1,@position=-1";
    
	public static final String INSERT_BRANDS = "INSERT INTO sylius_taxon "
    		+ "(tree_root,parent_id,code,tree_left, tree_right, tree_level,position, created_at) "
    		+ "(SELECT @brandId,@brandId,CONCAT('ls-',manufacturer_id),(@treeLeft := @treeLeft + 2),(@treeRight := @treeRight + 2),1,(@position := @position + 1), NOW() FROM ls_staging_manufacturers)"
			+ "ON DUPLICATE KEY UPDATE "
			+ "tree_left = VALUES(tree_left),"
			+ "tree_right = VALUES(tree_right),"
			+ "position = VALUES(position)";

	public static final String DELETE_BRANDS = "DELETE a FROM sylius_taxon a LEFT JOIN ls_staging_manufacturers b ON a.code = CONCAT('ls-',b.manufacturer_id) WHERE b.ID IS NULL AND a.parent_id=@brandId";

	public static final String UPDATE_BRAND_ROOT = "UPDATE sylius_taxon SET tree_right=(@treeRight+1) WHERE id=@brandId";

	public static final String INSERT_BRAND_TRANSLATION = "INSERT INTO sylius_taxon_translation "
    		+ "(translatable_id,name,slug,locale) "
    		+ "(SELECT b.id,a.name AS name,a.name AS slug,'en_US' FROM ls_staging_manufacturers a INNER JOIN sylius_taxon b ON CONCAT('ls-',a.manufacturer_id) = b.code) "
    		+ "ON DUPLICATE KEY UPDATE "
    		+ "name = VALUES(name),"
    		+ "slug = VALUES(slug)";
	
	public static final String INSERT_BRAND_ON_PRODUCT_SINGLE = "INSERT INTO sylius_product_taxon "
			+ "(product_id, taxon_id, position) "
			+ "(SELECT c.id, a.id, 0 FROM sylius_taxon a "
			+ "INNER JOIN ls_staging_items b ON a.code = CONCAT('ls-',b.manufacturer_id) "
			+ "INNER JOIN sylius_product c ON c.code = CONCAT('ls-',b.item_id) "
			+ "INNER JOIN sylius_taxon d ON a.parent_id = d.id "
			+ "WHERE d.code = 'brand' "
            + "AND b.item_matrix_id = 0) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "taxon_id = VALUES(taxon_id)";
	
	public static final String INSERT_BRAND_ON_PRODUCT_MATRIX = "INSERT INTO sylius_product_taxon "
			+ "(product_id, taxon_id, position) "
			+ "(SELECT c.id, a.id, 0 FROM sylius_taxon a "
			+ "INNER JOIN ls_staging_item_matrices b ON a.code = CONCAT('ls-',b.manufacturer_id) "
			+ "INNER JOIN sylius_product c ON c.code = CONCAT('ls-m-',b.item_matrix_id) "
			+ "INNER JOIN sylius_taxon d ON a.parent_id = d.id "
			+ "WHERE d.code = 'brand' "
            + "AND b.item_matrix_id != 0) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "taxon_id = VALUES(taxon_id)";
	
}
