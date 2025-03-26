package com.ilan.config;

import com.ilan.batch.listener.SampleJobExecutionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;

import java.util.UUID;
import java.util.concurrent.Future;

import static com.ilan.constants.JobConstants.FILE_NAME;
import static com.ilan.constants.JobConstants.FILE_NAME_PARAM;

@Configuration
@RequiredArgsConstructor
public class StepBatchConfig {

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
        UUID uuid = UUID.randomUUID();
        return new JobBuilder("asyncJob_" + uuid, jobRepository)
                .listener(new SampleJobExecutionListener())
                .start(asyncStep)
                .build();
    }

    @Bean
    public TaskExecutor jobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // Number of concurrent threads
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }

    @Bean
    CommandLineRunner startJob(@Qualifier("customJobLauncher") JobLauncher jobLauncher, Job asyncJob, JobRepository jobRepository, Step asyncStep) {
        return args -> {
           /* for (int i = 0; i < 5; i++) {
                jobTaskExecutor().execute(() -> {
                    try {*/
                        UUID uuid = UUID.randomUUID();
                        JobParameters jobParameters = new JobParametersBuilder()
                                .addString(FILE_NAME_PARAM, uuid + FILE_NAME)
                                .toJobParameters();
                        jobLauncher.run(asyncJob, jobParameters);
                   /* } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }*/
        };
    }


}
