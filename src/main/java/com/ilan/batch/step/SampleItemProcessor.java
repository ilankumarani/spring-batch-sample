package com.ilan.batch.step;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

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
