package com.ilan.config;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@EnableBatchProcessing(dataSourceRef = "dataSource",
        transactionManagerRef = "transactionManager",
        isolationLevelForCreate = "ISOLATION_READ_COMMITTED",
        taskExecutorRef = "jobTaskExecutor")
@Configuration
public class DefaultBatchConfig {

    @Value("${demo.parallelism:#{T(java.lang.Runtime).getRuntime().availableProcessors()}}")
    private Integer corePoolSize;

    @Bean(name = "jobTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
//        executor.setQueueCapacity(10);
//        executor.initialize();
        return executor;
    }

    @Bean
    public TaskExecutor customTaskExecutor() {
        return new SimpleAsyncTaskExecutor("Spring-Batch");
    }

    @Bean(name = "customJobLauncher")
    public JobLauncher jobLauncher(JobRepository jobRepository, @Qualifier("jobTaskExecutor") TaskExecutor jobTaskExecutor) throws BatchConfigurationException {
        TaskExecutorJobLauncher taskExecutorJobLauncher = new TaskExecutorJobLauncher();
        taskExecutorJobLauncher.setJobRepository(jobRepository);
        taskExecutorJobLauncher.setTaskExecutor(jobTaskExecutor);

        try {
            taskExecutorJobLauncher.afterPropertiesSet();
            return taskExecutorJobLauncher;
        } catch (Exception var4) {
            Exception e = var4;
            throw new BatchConfigurationException("Unable to configure the default job launcher", e);
        }
    }
}
