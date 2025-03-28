package com.ilan.custom;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;

public class JdbcJobExecutionDaoCustom extends JdbcJobExecutionDao {

    @Override
    public void updateJobExecution(JobExecution jobExecution) {

    }

    @Override
    public void synchronizeStatus(JobExecution jobExecution) {

    }
}
