package com.ilan.batch.step;

import com.ilan.exception.IlanBatchException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ilan.constants.JobConstants.JOB_EXECUTION_CONTEXT_PARAM;
import static com.ilan.constants.JobConstants.JOB_EXECUTION_CONTEXT_VALUE;
import static com.ilan.constants.JobConstants.ROW_COUNT;
import static com.ilan.constants.JobConstants.STEP_EXECUTION_CONTEXT_PARAM;
import static com.ilan.constants.JobConstants.STEP_EXECUTION_CONTEXT_VALUE;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
@Data
public class SampleItemWriter implements ItemWriter<String> {


    @Value("#{jobParameters['fileName']}")
    private String fileName;

    @Value("#{stepExecution}")
    private StepExecution stepExecution;

    AtomicInteger count = new AtomicInteger(0);

    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        log.debug("JOB ExecutionContext() :: {}", stepExecution.getJobExecution().getExecutionContext().get(JOB_EXECUTION_CONTEXT_PARAM));
        log.debug("STEP ExecutionContext() :: {}", stepExecution.getExecutionContext().get(STEP_EXECUTION_CONTEXT_PARAM));
        log.debug("File name parameter received {}", fileName);
        List<String> chunkItems = (List<String>) chunk.getItems();

        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        if (stepExecution.getJobExecution().getExecutionContext().containsKey(ROW_COUNT)) {
            count = (AtomicInteger) executionContext.get(ROW_COUNT);
            count.addAndGet(chunkItems.size());
            executionContext.put(ROW_COUNT, count);
        } else {
            executionContext.put(ROW_COUNT, count);
        }


        log.info("Chunk items :: {} and size :: {}", chunkItems, chunkItems.size());
/*        if (chunkItems.contains("Z")) {
            log.info("If chunk fails for some reason, then we re-try according to retry limit");
            throw new IlanBatchException("Manual Throw");
        }*/
    }
}
