package com.ilan.batch;

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

import static com.ilan.constants.JobConstants.FILE_NAME_PARAM;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class SampleItemReader implements ItemReader<String>, StepExecutionListener {


    private String fileName;
    private Boolean firstExecution = Boolean.FALSE;
    private Boolean initialized = Boolean.TRUE;

    /**
     * called only once before the Step
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        fileName = jobParameters.getString(FILE_NAME_PARAM);
        log.info("File name from StepExecution {}", fileName);
        initialized();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }


    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(!firstExecution){
            firstExecution = Boolean.TRUE;
            oneTimeLoadRead();
            return "Test String";
        }
        return null;
    }

    private void oneTimeLoadRead(){
        log.info("Reading the file once inside Read method of ItemReader");
    }

    private void initialized(){
        log.info("Reading the file once inside BeforeStep method");
    }
}
