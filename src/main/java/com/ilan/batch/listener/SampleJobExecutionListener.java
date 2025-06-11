package com.ilan.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static com.ilan.constants.JobConstants.ROW_COUNT;

@Component
@Slf4j
public class SampleJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job Started: {}" , jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job Completed with Status :: {}" , jobExecution.getStatus());
        log.info("Row Count :: {}", ((AtomicInteger)jobExecution.getExecutionContext().get(ROW_COUNT)).get());
    }
}

