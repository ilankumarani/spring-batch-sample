package com.ilan.custom;

import com.ilan.constants.JdbcJobExecutionDaoCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobKeyGenerator;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JdbcExecutionContextDao;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.batch.core.repository.dao.JdbcJobInstanceDao;
import org.springframework.batch.core.repository.dao.JdbcStepExecutionDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.DatabaseType;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.lob.LobHandler;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Types;

import static org.springframework.batch.support.DatabaseType.SYBASE;

@RequiredArgsConstructor
public class CustomJobRepositoryFactoryBean extends JobRepositoryFactoryBean {

    private final JdbcOperations jdbcOperations;

    private final String databaseType;

    private final String tablePrefix;

    private final DataFieldMaxValueIncrementerFactory incrementerFactory;

    private final JobKeyGenerator jobKeyGenerator;

    private final int maxVarCharLengthForExitMessage;

    private final int maxVarCharLengthForShortContext;

    private final LobHandler lobHandler;

    private final ExecutionContextSerializer serializer;

    private final Integer clobType;

    private final Charset charset = StandardCharsets.UTF_8;

    private final ConfigurableConversionService conversionService;

    @Override
    protected JobInstanceDao createJobInstanceDao() throws Exception {
        JdbcJobInstanceDao dao = new JdbcJobInstanceDao();
        dao.setJdbcTemplate(jdbcOperations);
        dao.setJobInstanceIncrementer(incrementerFactory.getIncrementer(databaseType, tablePrefix + "JOB_SEQ"));
        dao.setJobKeyGenerator(jobKeyGenerator);
        dao.setTablePrefix(tablePrefix);
        dao.afterPropertiesSet();
        return dao;
    }

    @Override
    protected JobExecutionDao createJobExecutionDao() throws Exception {
        JdbcJobExecutionDaoCustom dao = new JdbcJobExecutionDaoCustom();
        dao.setJdbcTemplate(jdbcOperations);
        dao.setJobExecutionIncrementer(
                incrementerFactory.getIncrementer(databaseType, tablePrefix + "JOB_EXECUTION_SEQ"));
        dao.setTablePrefix(tablePrefix);
        dao.setClobTypeToUse(determineClobTypeToUse(this.databaseType));
        dao.setExitMessageLength(this.maxVarCharLengthForExitMessage);
        dao.setConversionService(this.conversionService);
        dao.afterPropertiesSet();
        return dao;
    }

    @Override
    protected StepExecutionDao createStepExecutionDao() throws Exception {
        JdbcStepExecutionDao dao = new JdbcStepExecutionDao();
        dao.setJdbcTemplate(jdbcOperations);
        dao.setStepExecutionIncrementer(
                incrementerFactory.getIncrementer(databaseType, tablePrefix + "STEP_EXECUTION_SEQ"));
        dao.setTablePrefix(tablePrefix);
        dao.setClobTypeToUse(determineClobTypeToUse(this.databaseType));
        dao.setExitMessageLength(this.maxVarCharLengthForExitMessage);
        dao.afterPropertiesSet();
        return dao;
    }

    @Override
    protected ExecutionContextDao createExecutionContextDao() throws Exception {
        JdbcExecutionContextDao dao = new JdbcExecutionContextDao();
        dao.setJdbcTemplate(jdbcOperations);
        dao.setTablePrefix(tablePrefix);
        dao.setClobTypeToUse(determineClobTypeToUse(this.databaseType));
        dao.setSerializer(serializer);
        dao.setCharset(charset);
        dao.afterPropertiesSet();
        dao.setShortContextLength(this.maxVarCharLengthForShortContext);
        return dao;
    }


    /**
     * Copied from super class
     * @param databaseType
     * @return
     */
    private int determineClobTypeToUse(String databaseType) {
        if (clobType != null) {
            return clobType;
        }
        else {
            if (SYBASE == DatabaseType.valueOf(databaseType.toUpperCase())) {
                return Types.LONGVARCHAR;
            }
            else {
                return Types.CLOB;
            }
        }
    }

}
