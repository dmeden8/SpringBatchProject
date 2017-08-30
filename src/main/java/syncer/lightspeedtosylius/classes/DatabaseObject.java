package syncer.lightspeedtosylius.classes;

import org.springframework.jdbc.core.JdbcTemplate;

public interface DatabaseObject {

    public void save(JdbcTemplate jdbcTemplate);
}
