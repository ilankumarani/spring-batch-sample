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


@EnableBatchProcessing(databaseType = "H2", // This is optional
        dataSourceRef = "dataSource",
        transactionManagerRef = "transactionManager",
        tablePrefix = "SPRING_BATCH.BATCH_",
        maxVarCharLength = 1000, // https://docs.spring.io/spring-batch/reference/job/configuring-repository.html
        isolationLevelForCreate = "ISOLATION_READ_COMMITTED",
        taskExecutorRef = "customJobTaskExecutor")
@Configuration
//BatchRegistrar //Annotation Register
//DefaultBatchConfiguration
public class DefaultBatchConfig {

    @Value("${demo.parallelism:#{T(java.lang.Runtime).getRuntime().availableProcessors()}}")
    private Integer corePoolSize;

    @Bean(name = "customJobTaskExecutor")
    public TaskExecutor jobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
//        executor.initialize();
        return executor;
    }

    @Bean(name = "stepTaskExecutor")
    public TaskExecutor stepTaskExecutor() {
        return new SimpleAsyncTaskExecutor("step-taskExecutor");
    }


    /*@Bean(name = "customJobLauncher")
    @SneakyThrows
    public JobLauncher jobLauncher(JobRepository jobRepository, @Qualifier("customJobTaskExecutor") TaskExecutor jobTaskExecutor) throws BatchConfigurationException {
        TaskExecutorJobLauncher taskExecutorJobLauncher = new TaskExecutorJobLauncher();
        taskExecutorJobLauncher.setJobRepository(jobRepository);
        taskExecutorJobLauncher.setTaskExecutor(jobTaskExecutor);
        taskExecutorJobLauncher.afterPropertiesSet();
        return taskExecutorJobLauncher;
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
    }*/
}
