package com.ilan.batch.step;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import static com.ilan.constants.JobConstants.FILE_NAME_PARAM;
import static com.ilan.constants.JobConstants.UU_ID;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class SampleItemProcessor implements ItemProcessor<String, String>, StepExecutionListener {

    private StepExecution stepExecution;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }


    @Override
    public String process(String item) throws Exception {
        return item;
    }
}
