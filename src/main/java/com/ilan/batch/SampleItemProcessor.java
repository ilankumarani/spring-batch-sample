package com.ilan.batch;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.ilan.constants.JobConstants.FILE_NAME_PARAM;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class SampleItemProcessor implements ItemProcessor<String, String> {


    @Override
    public String process(String item) throws Exception {
        return item;
    }
}
