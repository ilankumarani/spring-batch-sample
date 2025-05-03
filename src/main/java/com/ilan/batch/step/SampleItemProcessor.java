package com.ilan.batch.step;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.ilan.constants.JobConstants.JOB_EXECUTION_CONTEXT_PARAM;
import static com.ilan.constants.JobConstants.JOB_EXECUTION_CONTEXT_VALUE;
import static com.ilan.constants.JobConstants.STEP_EXECUTION_CONTEXT_PARAM;
import static com.ilan.constants.JobConstants.STEP_EXECUTION_CONTEXT_VALUE;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
@StepScope
public class SampleItemProcessor implements ItemProcessor<String, String> {

    @Value("#{stepExecution}")
    private StepExecution stepExecution;


    @Override
    public String process(String item) throws Exception {
        log.info("ItemProcessor in stepExecution :: {}", stepExecution.getJobExecution().getJobParameters().toString());
        stepExecution.getExecutionContext().put(STEP_EXECUTION_CONTEXT_PARAM, STEP_EXECUTION_CONTEXT_VALUE);
        stepExecution.getJobExecution().getExecutionContext().put(JOB_EXECUTION_CONTEXT_PARAM, JOB_EXECUTION_CONTEXT_VALUE);
        return item;
    }
}
