package com.ilan.config;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;

import javax.sql.DataSource;


@Profile("javaBased")
@Configuration
//BatchRegistrar //Annotation Register
//DefaultBatchConfiguration
public class CustomDefaultBatchConfig extends DefaultBatchConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Value("${demo.parallelism:#{T(java.lang.Runtime).getRuntime().availableProcessors()}}")
    private Integer corePoolSize;

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public PlatformTransactionManager getTransactionManager(){
        return transactionManager;
    }

    @Override
    public String getTablePrefix() {
        return "SPRING_BATCH.BATCH_";
    }

    @Override
    public Isolation getIsolationLevelForCreate() {
        return Isolation.READ_COMMITTED;
    }

    public TaskExecutor getTaskExecutor() {
        return jobTaskExecutor();
    }

    public TaskExecutor jobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(corePoolSize * 3);
        executor.initialize();
        return executor;
    }

    /*@Bean
    @Override
    public JobRepository jobRepository() throws BatchConfigurationException {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();

        try {
            jobRepositoryFactoryBean.setDataSource(this.getDataSource());
            jobRepositoryFactoryBean.setTransactionManager(this.getTransactionManager());
            jobRepositoryFactoryBean.setDatabaseType(this.getDatabaseType());
            jobRepositoryFactoryBean.setIncrementerFactory(this.getIncrementerFactory());
            jobRepositoryFactoryBean.setJobKeyGenerator(this.getJobKeyGenerator());
            jobRepositoryFactoryBean.setClobType(this.getClobType());
            jobRepositoryFactoryBean.setTablePrefix(this.getTablePrefix());
            jobRepositoryFactoryBean.setSerializer(this.getExecutionContextSerializer());
            jobRepositoryFactoryBean.setConversionService(this.getConversionService());
            jobRepositoryFactoryBean.setJdbcOperations(this.getJdbcOperations());
            //jobRepositoryFactoryBean.setLobHandler(this.getLobHandler());
            jobRepositoryFactoryBean.setCharset(this.getCharset());
            jobRepositoryFactoryBean.setMaxVarCharLength(this.getMaxVarCharLength());
            jobRepositoryFactoryBean.setIsolationLevelForCreateEnum(this.getIsolationLevelForCreate());
            jobRepositoryFactoryBean.setValidateTransactionState(this.getValidateTransactionState());
            jobRepositoryFactoryBean.afterPropertiesSet();
            return jobRepositoryFactoryBean.getObject();
        } catch (Exception var3) {
            Exception e = var3;
            throw new BatchConfigurationException("Unable to configure the default job repository", e);
        }
    }*/
}
