package syncer.batch.core;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import syncer.lightspeedtosylius.classes.DatabaseObject;

import java.util.List;

public class DatabaseItemWriter implements ItemWriter<DatabaseObject> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public void write(List<? extends DatabaseObject> items) throws Exception {	
    	System.out.println("WRITING");
		for(DatabaseObject object : items) {
			object.save(jdbcTemplate);
		}
	}
}
