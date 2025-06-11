package com.ilan.batch.step;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ilan.constants.JobConstants.FILE_NAME_PARAM;
import static com.ilan.constants.JobConstants.ROW_COUNT;
import static com.ilan.constants.JobConstants.UU_ID;
import static com.ilan.constants.JobConstants.UpperCaseAlphabet;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class SampleItemReader implements ItemReader<String>, StepExecutionListener {


    private Boolean firstExecution = Boolean.FALSE;
    private Boolean initialized = Boolean.TRUE;

    private List<String> items;
    private Iterator<String> iterator;

    AtomicInteger count = new AtomicInteger(0);

    /**
     * called only once before the Step
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put(ROW_COUNT, count);
        JobParameters jobParameters = stepExecution.getJobParameters();
        String fileName = jobParameters.getString(FILE_NAME_PARAM);
        String uuId = jobParameters.getString(UU_ID);
        log.debug("File name from StepExecution {}", fileName);
        log.debug("UUID from StepExecution {}", uuId);
        items = initialized();
        this.iterator = items.iterator();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }


    @Override
    public String read() {
        String element = iterator.hasNext() ? iterator.next() : null;
        log.debug("Element :: {}", element);
        return element;
    }

    private List<String> initialized() {
        log.debug("Reading the file once inside Read method of ItemReader");
        return Arrays.stream(new String(UpperCaseAlphabet).split(""))
                .collect(Collectors.toList());
    }
}
