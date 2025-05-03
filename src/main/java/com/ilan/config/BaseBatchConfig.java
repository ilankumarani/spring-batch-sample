package com.ilan.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Profile("annotationBased")
@EnableBatchProcessing(databaseType = "H2", // This is optional
        dataSourceRef = "dataSource",
        transactionManagerRef = "transactionManager",
        tablePrefix = "SPRING_BATCH.BATCH_",
        maxVarCharLength = 1000, // https://docs.spring.io/spring-batch/reference/job/configuring-repository.html
        isolationLevelForCreate = "ISOLATION_READ_COMMITTED",
        taskExecutorRef = "customJobTaskExecutor")
@Configuration
//BatchRegistrar //@EnableBatchProcessing Annotation Register
//DefaultBatchConfiguration
public class BaseBatchConfig {

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
