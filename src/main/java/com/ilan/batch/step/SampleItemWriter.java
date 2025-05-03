package com.ilan.batch.step;

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
        log.debug("File name parameter received {}", fileName);
        List<String> chunkItems = (List<String>) chunk.getItems();
        log.info("Chunk items :: {}", chunkItems);
    }
}
