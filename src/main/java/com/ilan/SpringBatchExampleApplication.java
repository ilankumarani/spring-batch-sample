package com.ilan;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.UUID;

import static com.ilan.constants.JobConstants.FILE_NAME;
import static com.ilan.constants.JobConstants.FILE_NAME_PARAM;
import static com.ilan.constants.JobConstants.UU_ID;

//https://spring.io/guides/gs/batch-processing
@SpringBootApplication
public class SpringBatchExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchExampleApplication.class, args);
	}

	@Bean
	public TaskExecutor jobTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);  // Number of concurrent threads
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		executor.initialize();
		return executor;
	}

	@Bean
	CommandLineRunner startJob(
			//@Qualifier("customJobLauncher")
			 JobLauncher jobLauncher, Job asyncJob) {
		return args -> {
            for (int i = 0; i < 1; i++) {
//                jobTaskExecutor().execute(() -> {
//                    try {
			UUID uuid = UUID.randomUUID();
			JobParameters jobParameters = new JobParametersBuilder()
					.addString(UU_ID, uuid.toString())
					.addString(FILE_NAME_PARAM, FILE_NAME)
					.toJobParameters();
			jobLauncher.run(asyncJob, jobParameters);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
            }
		};
	}
}
