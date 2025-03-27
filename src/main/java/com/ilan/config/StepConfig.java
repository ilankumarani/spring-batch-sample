package com.ilan.config;

import com.ilan.batch.listener.SampleJobExecutionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;

import java.util.concurrent.Future;

@Configuration
@RequiredArgsConstructor
public class StepConfig {

    @Bean(name = "stepTaskExecutor")
    public TaskExecutor stepTaskExecutor() {
        return new SimpleAsyncTaskExecutor("step-taskExecutor");
    }

    public static final int CHUNK_SIZE = 10;
    private final SampleJobExecutionListener sampleJobExecutionListener;

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
