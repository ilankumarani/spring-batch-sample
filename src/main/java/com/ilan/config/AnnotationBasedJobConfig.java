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

    public static final int CHUNK_SIZE = 10;
    private final SampleJobExecutionListener sampleJobExecutionListener;

    @Bean(name = "customJobTaskExecutor")
    public TaskExecutor jobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(corePoolSize * 3);
        executor.initialize();
        return executor;
    }

    @Bean(name = "stepTaskExecutor")
    public TaskExecutor stepTaskExecutor() {
        return new SimpleAsyncTaskExecutor("step-taskExecutor");
    }

    /*@StepScope
    @Bean
    public ItemProcessor<String, String> itemProcessor( @Value("#{jobParameters['fileName']}") String fileName){
        return new SampleItemProcessor();
    }*/

    @Bean
    public AsyncItemProcessor<String, String> asyncProcessor(ItemProcessor<String, String> sampleItemProcessor, @Qualifier("stepTaskExecutor") TaskExecutor stepTaskExecutor) {
        AsyncItemProcessor<String, String> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(sampleItemProcessor);
        asyncItemProcessor.setTaskExecutor(stepTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter<String> asyncWriter(ItemWriter<String> sampleItemWriter) {
        AsyncItemWriter<String> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(sampleItemWriter);
        return asyncItemWriter;
    }

    @Bean
    public Step asyncStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          ItemReader<String> sampleItemReader,
                          AsyncItemProcessor<String, String> asyncProcessor,
                          AsyncItemWriter<String> asyncWriter,
                          @Qualifier("stepTaskExecutor") TaskExecutor stepTaskExecutor) {
        return new StepBuilder("asyncStep", jobRepository)
                .<String, Future<String>>chunk(CHUNK_SIZE, transactionManager)
                .reader(sampleItemReader)
                .processor(asyncProcessor)
                .writer(asyncWriter)
                .taskExecutor(stepTaskExecutor)  // Enable multi-threading
                .build();
    }

    @Bean
    public Job asyncJob(JobRepository jobRepository, Step asyncStep) {
        return new JobBuilder("asyncJob", jobRepository)
                .listener(new SampleJobExecutionListener())
                .start(asyncStep)
                .build();
    }

}
