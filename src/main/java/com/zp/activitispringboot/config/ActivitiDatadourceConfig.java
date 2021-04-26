package com.zp.activitispringboot.config;//package com.zp.activitispringboot.config;


import com.alibaba.druid.pool.DruidDataSource;
//import com.zp.activitispringboot.cmd.HisActInstanceMapper;
import com.zp.activitispringboot.cmd.HisActInstanceMapper;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;

import org.activiti.spring.boot.ActivitiProperties;
import org.activiti.spring.boot.DefaultActivityBehaviorFactoryMappingConfigurer;
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
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(SpringAsyncExecutor springAsyncExecutor,
                                                                             ActivitiProperties activitiProperties,
                                                                             DefaultActivityBehaviorFactoryMappingConfigurer processEngineConfigurationConfigurer) {
        SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration();
        conf.setDataSource(activitiDataSource());
        //properties 复制过来
        conf.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        conf.setTransactionManager(transactionManager());
        conf.setHistoryLevel(HistoryLevel.AUDIT);
        //设置不自动部署
        conf.setDeploymentMode("never-fail");

        //properties 复制过来 end
        //下面不知道哪个起了作用
        if (springAsyncExecutor != null) {
            conf.setAsyncExecutor(springAsyncExecutor);
        }
        //conf.setDeploymentName(activitiProperties.getDeploymentName());
        conf.setDatabaseSchema(activitiProperties.getDatabaseSchema());
        conf.setDatabaseSchemaUpdate(activitiProperties.getDatabaseSchemaUpdate());
        conf.setDbHistoryUsed(activitiProperties.isDbHistoryUsed());
        //默认查询数据 o.a.e.i.p.e.J.selectJobsToExecute   activitiProperties.isAsyncExecutorActivate()
        conf.setAsyncExecutorActivate(false);

        //自定义处理语句接口
        Set<Class<?>> customMybatisXMLMappers = new HashSet<Class<?>>();
        customMybatisXMLMappers.add(HisActInstanceMapper.class);
        conf.setCustomMybatisMappers(customMybatisXMLMappers);


        conf.setActivityBehaviorFactory(new DefaultActivityBehaviorFactory());

        if (processEngineConfigurationConfigurer != null) {
            processEngineConfigurationConfigurer.configure(conf);
        }

        return conf;
    }
}
