package com.ilan.batch.step;

import com.ilan.exception.IlanBatchException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        log.debug("JOB ExecutionContext() :: {}", stepExecution.getJobExecution().getExecutionContext().get(JOB_EXECUTION_CONTEXT_PARAM));
        log.debug("STEP ExecutionContext() :: {}", stepExecution.getExecutionContext().get(STEP_EXECUTION_CONTEXT_PARAM));
        log.debug("File name parameter received {}", fileName);
        List<String> chunkItems = (List<String>) chunk.getItems();

        if (stepExecution.getJobExecution().getExecutionContext().containsKey(ROW_COUNT)) {
            Integer rowCount = stepExecution.getJobExecution().getExecutionContext().getInt(ROW_COUNT);
            stepExecution.getJobExecution().getExecutionContext().putInt(ROW_COUNT, rowCount + chunkItems.size());
        } else {
            stepExecution.getJobExecution().getExecutionContext().putInt(ROW_COUNT, 0);
        }


        log.info("Chunk items :: {}", chunkItems);
/*        if (chunkItems.contains("Z")) {
            log.info("If chunk fails for some reason, then we re-try according to retry limit");
            throw new IlanBatchException("Manual Throw");
        }*/
    }
}
