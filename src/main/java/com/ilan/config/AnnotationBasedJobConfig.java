package com.ilan.config;

import com.ilan.batch.listener.SampleJobExecutionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;

import java.util.concurrent.Future;


@Profile("annotationBased")
@EnableBatchProcessing(databaseType = "H2", // This is optional
        dataSourceRef = "dataSource",
        transactionManagerRef = "transactionManager",
        tablePrefix = "SPRING_BATCH.BATCH_",
        maxVarCharLength = 1000, // https://docs.spring.io/spring-batch/reference/job/configuring-repository.html
        isolationLevelForCreate = "ISOLATION_READ_COMMITTED",
        taskExecutorRef = "customJobTaskExecutor")
//BatchRegistrar //@EnableBatchProcessing Annotation Register
//DefaultBatchConfiguration
@Configuration
@RequiredArgsConstructor
public class AnnotationBasedJobConfig {

    @Value("${demo.parallelism:#{T(java.lang.Runtime).getRuntime().availableProcessors()}}")
    private Integer corePoolSize;



    @Bean(name = "customJobTaskExecutor")
    public TaskExecutor jobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(corePoolSize * 3);
        executor.initialize();
        return executor;
    }



}
