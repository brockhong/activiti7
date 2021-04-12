package com.zp.activitispringboot.utils;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.GetProcessDefinitionsPayload;
import org.activiti.api.process.runtime.conf.ProcessRuntimeConfiguration;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.core.common.spring.security.policies.ProcessSecurityPoliciesManager;
import org.activiti.core.common.spring.security.policies.SecurityPolicyAccess;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.runtime.api.impl.ProcessRuntimeImpl;
import org.activiti.runtime.api.impl.ProcessVariablesPayloadValidator;
import org.activiti.runtime.api.model.impl.APIDeploymentConverter;
import org.activiti.runtime.api.model.impl.APIProcessDefinitionConverter;
import org.activiti.runtime.api.model.impl.APIProcessInstanceConverter;
import org.activiti.runtime.api.model.impl.APIVariableInstanceConverter;
import org.activiti.runtime.api.query.impl.PageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ACTIVITI_USER')")
@Component
public class MyProcessRuntimeImpl extends ProcessRuntimeImpl {
    @Autowired
    private  RepositoryService repositoryService;
    @Autowired
    private  APIProcessDefinitionConverter processDefinitionConverter;

    @Autowired
    private  ProcessSecurityPoliciesManager securityPoliciesManager;


    public MyProcessRuntimeImpl(RepositoryService repositoryService, APIProcessDefinitionConverter processDefinitionConverter, RuntimeService runtimeService, ProcessSecurityPoliciesManager securityPoliciesManager, APIProcessInstanceConverter processInstanceConverter, APIVariableInstanceConverter variableInstanceConverter, APIDeploymentConverter deploymentConverter, ProcessRuntimeConfiguration configuration, ApplicationEventPublisher eventPublisher, ProcessVariablesPayloadValidator processVariablesValidator) {
        super(repositoryService, processDefinitionConverter, runtimeService, securityPoliciesManager, processInstanceConverter, variableInstanceConverter, deploymentConverter, configuration, eventPublisher, processVariablesValidator);
    }
    @Override
    public Page<ProcessDefinition> processDefinitions(Pageable pageable) {
        return processDefinitions(pageable,
                ProcessPayloadBuilder.processDefinitions().build());
    }

    @Override
    public Page<ProcessDefinition> processDefinitions(Pageable pageable,
                                                      GetProcessDefinitionsPayload getProcessDefinitionsPayload) {
        if (getProcessDefinitionsPayload == null) {
            throw new IllegalStateException("payload cannot be null");
        }
        GetProcessDefinitionsPayload securityKeysInPayload = securityPoliciesManager.restrictProcessDefQuery(SecurityPolicyAccess.READ);
        // If the security policies keys are not empty it means that I will need to use them to filter results,
        //   else ignore and use the user provided ones.
        if (!securityKeysInPayload.getProcessDefinitionKeys().isEmpty()) {
            getProcessDefinitionsPayload.setProcessDefinitionKeys(securityKeysInPayload.getProcessDefinitionKeys());
        }
        ProcessDefinitionQuery processDefinitionQuery = repositoryService
                .createProcessDefinitionQuery();
        processDefinitionQuery.listPage(pageable.getStartIndex(),pageable.getMaxItems());
        if (getProcessDefinitionsPayload.hasDefinitionKeys()) {
            processDefinitionQuery.processDefinitionKeys(getProcessDefinitionsPayload.getProcessDefinitionKeys());
        }

        List<org.activiti.engine.repository.ProcessDefinition> currentVersionDefinitions = filterCurrentVersionDefinitions(processDefinitionQuery.list());

        return new PageImpl<>(processDefinitionConverter.from(currentVersionDefinitions),
                Math.toIntExact(processDefinitionQuery.count()));
    }
    private List<org.activiti.engine.repository.ProcessDefinition> filterCurrentVersionDefinitions (List<org.activiti.engine.repository.ProcessDefinition> allDefinitions){
        int currentVersion = selectLatestDeployment().getVersion();
        return allDefinitions
                .stream()
                .filter(processDefinition -> processDefinition.getAppVersion() == null ||
                        processDefinition.getAppVersion().equals(currentVersion))
                //we fetch possible unversioned definitions from different types of deployments
                .collect(Collectors.toList());
    }

}
