package com.zp.activitispringboot.config;//package com.zp.activitispringboot.config;


import com.alibaba.druid.pool.DruidDataSource;
//import com.zp.activitispringboot.cmd.HisActInstanceMapper;
import com.zp.activitispringboot.cmd.HisActInstanceMapper;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;


import javax.sql.DataSource;
import java.util.*;

@Configuration
public class ActivitiDatadourceConfig extends AbstractProcessEngineAutoConfiguration {


    @Bean
    public DataSource activitiDataSource() {
        DruidDataSource DruiddataSource = new DruidDataSource();
        DruiddataSource.setUrl("jdbc:mysql://localhost:3306/activiti7?serverTimezone=GMT%2B8&useUnicode=true&zeroDateTimeBehavior=convertToNull&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true");
        DruiddataSource.setDriverClassName("com.mysql.jdbc.Driver");
        DruiddataSource.setPassword("123456");
        DruiddataSource.setUsername("root");
        return DruiddataSource;
    }


    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(activitiDataSource());
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration() {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        configuration.setDataSource(activitiDataSource());
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);

        configuration.setTransactionManager(transactionManager());

        configuration.setDeploymentMode("never-fail");

        //自定义处理语句接口
        Set<Class<?>> customMybatisXMLMappers = new HashSet<Class<?>>();
        customMybatisXMLMappers.add(HisActInstanceMapper.class);
        configuration.setCustomMybatisMappers(customMybatisXMLMappers);

        return configuration;
    }
}
