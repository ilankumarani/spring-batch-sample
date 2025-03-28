package com.ilan.custom;

import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JdbcStepExecutionDao;

public class JdbcStepExecutionDaoCustom extends JdbcStepExecutionDao {

    @Override
    public void updateStepExecution(StepExecution stepExecution) {

    }

    @Override
    public StepExecution getLastStepExecution(JobInstance jobInstance, String stepName) {
        return null;
    }

    @Override
    public long countStepExecutions(JobInstance jobInstance, String stepName) {
        return 0;
    }
}
