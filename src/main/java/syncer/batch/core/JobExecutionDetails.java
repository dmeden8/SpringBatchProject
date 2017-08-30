package syncer.batch.core;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobExecutionDetails {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public Date getLastSuccessfulJobExecutionStartTime(String jobName) { 
	    String query = "select START_TIME from BATCH_JOB_EXECUTION a "
	    		+ "inner join BATCH_JOB_INSTANCE b on a.job_instance_id = b.job_instance_id "
	    		+ "and b.job_name = '" + jobName + "' "
	    		+ "where STATUS = 'COMPLETED' ORDER BY START_TIME DESC LIMIT 1";
	    try {
	    	return jdbcTemplate.queryForObject(query, Date.class);
	    }
	    catch (Exception ex){
	    	return null;
	    }
	}

	public Date getLastStartedJobExecutionStartTime(String jobName) {
	    String query = "select START_TIME from BATCH_JOB_EXECUTION a "
	    		+ "inner join BATCH_JOB_INSTANCE b on a.job_instance_id = b.job_instance_id "
	    		+ "and b.job_name = '" + jobName + "' "
	    		+ "where STATUS = 'STARTED' ORDER BY START_TIME DESC LIMIT 1";
	    try {
	    	return jdbcTemplate.queryForObject(query, Date.class);
	    }
	    catch (Exception ex){
	    	return null;
	    }
	}

}
