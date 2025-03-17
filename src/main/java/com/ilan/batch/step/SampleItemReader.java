package com.ilan.batch.step;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ilan.constants.JobConstants.FILE_NAME_PARAM;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class SampleItemReader implements ItemReader<String>, StepExecutionListener {


    private String fileName;
    private Boolean firstExecution = Boolean.FALSE;
    private Boolean initialized = Boolean.TRUE;

    private List<String> items;
    private Iterator<String> iterator;

    /**
     * called only once before the Step
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        fileName = jobParameters.getString(FILE_NAME_PARAM);
        log.info("File name from StepExecution {}", fileName);
        items = initialized();
        this.iterator = items.iterator();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }


    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return iterator.hasNext() ? iterator.next() : null;
    }

    private void oneTimeLoadRead(){
        log.info("Reading the file once inside Read method of ItemReader");
    }

    private List<String> initialized(){
        log.info("Reading the file once inside BeforeStep method");
        char[] UpperCaseAlphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        return Arrays.stream(new String(UpperCaseAlphabet).split(""))
                .collect(Collectors.toList());
    }
}
