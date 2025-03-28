package com.ilan.constants;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;

public class JdbcJobExecutionDaoCustom extends JdbcJobExecutionDao {

    @Override
    public void updateJobExecution(JobExecution jobExecution) {

    }
}
