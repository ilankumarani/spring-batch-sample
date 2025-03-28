package com.ilan.custom;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JdbcExecutionContextDao;

public class JdbcExecutionContextDaoCustom extends JdbcExecutionContextDao {

    @Override
    public void updateExecutionContext(final JobExecution jobExecution) {

    }

    @Override
    public void updateExecutionContext(final StepExecution stepExecution) {

    }
}
