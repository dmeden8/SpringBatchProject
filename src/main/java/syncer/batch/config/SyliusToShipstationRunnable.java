package syncer.batch.config;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;

public class SyliusToShipstationRunnable implements Runnable {

	ApplicationContext context;

	JobLauncher jobLauncher;

	public SyliusToShipstationRunnable(ApplicationContext context, JobLauncher jobLauncher) {
		this.context = context;
		this.jobLauncher = jobLauncher;
	}

	public void run() {
		System.out.println("##############NEW JOB INSTANCE syliusToShipStation: " + new Date().toString());

		Job job = (Job) context.getBean("syliusToShipStationSyncer");

		String dateParam = new Date().toString();
		JobParameters param = new JobParametersBuilder().addString("date", dateParam).toJobParameters();

		((SimpleJob) job).setRestartable(false);

		JobExecution execution;
		try {
			execution = jobLauncher.run(job, param);
			System.out.println("Exit Status : " + execution.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
