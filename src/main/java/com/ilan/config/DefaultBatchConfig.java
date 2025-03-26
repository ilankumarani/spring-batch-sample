package com.ilan.config;

import lombok.SneakyThrows;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;


@EnableBatchProcessing(dataSourceRef = "dataSource",
        transactionManagerRef = "transactionManager",
        isolationLevelForCreate = "ISOLATION_READ_COMMITTED",
        taskExecutorRef = "customJobTaskExecutor")
@Configuration
//DefaultBatchConfiguration
public class DefaultBatchConfig {

    @Value("${demo.parallelism:#{T(java.lang.Runtime).getRuntime().availableProcessors()}}")
    private Integer corePoolSize;

    @Bean(name = "customJobTaskExecutor")
    public TaskExecutor jobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
//        executor.setQueueCapacity(10);
//        executor.initialize();
        return executor;
    }

    @Bean(name = "stepTaskExecutor")
    public TaskExecutor stepTaskExecutor() {
        return new SimpleAsyncTaskExecutor("step-taskExecutor");
    }


    @Bean(name = "customJobLauncher")
    public JobLauncher jobLauncher(JobRepository jobRepository, @Qualifier("customJobTaskExecutor") TaskExecutor jobTaskExecutor) throws BatchConfigurationException {
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

    @Primary
    @Bean
    @SneakyThrows
    public JobOperator jobOperator(JobRepository jobRepository, JobExplorer jobExplorer, JobRegistry jobRegistry, @Qualifier("customJobLauncher") JobLauncher jobLauncher, PlatformTransactionManager transactionManager) throws BatchConfigurationException {
        SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
        simpleJobOperator.setJobRepository(jobRepository);
        simpleJobOperator.setJobExplorer(jobExplorer);
        simpleJobOperator.setJobRegistry(jobRegistry);
        simpleJobOperator.setJobLauncher(jobLauncher);
        simpleJobOperator.afterPropertiesSet();
        return simpleJobOperator;
    }
}
