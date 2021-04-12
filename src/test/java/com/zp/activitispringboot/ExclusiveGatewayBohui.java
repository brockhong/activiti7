package com.zp.activitispringboot;

import com.zp.activitispringboot.utils.ActivitiUtil;
import org.activiti.core.common.project.model.ProjectManifest;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * 驳回网关
 * 获取当前节点和上个节点的定义信息，将当前节点的流向指向上个节点后完成任务，然后将流向复原
 * 展示流程代码有问题
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExclusiveGatewayBohui {
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 手动方式部署
     */
    @Test
    public void deploy(){
        //估计就是测试类加载不到 这个东西把代码 放到web 工程试试
        ProjectManifest projectManifest = new ProjectManifest();
        projectManifest.setCreatedBy("superadminuser");
        projectManifest.setVersion("1");
        projectManifest.setCreationDate("2019-08-16T15:58:46.056+0000");
        projectManifest.setId("c519a458-539f-4385-a937-2edfb4045eb9");
        projectManifest.setLastModifiedBy("qa-modeler-1");
        projectManifest.setLastModifiedDate("2019-08-16T16:03:41.941+0000");
        projectManifest.setName("projectA");
        // 进行部署
        Deployment deployment = repositoryService.createDeployment().setProjectManifest(projectManifest)
                // 文件夹的名称不能是process
                .addClasspathResource("processes/ExclusiveGatewaybohui.bpmn")
                //act_re_procdef name 只针对bpmn定义  act_re_deployment name “processtest03”
                .name("ExclusiveGatewaybohui")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    @Test
    public void testProcessInstance() {
        String key = "ExclusiveGatewaybohui";
        String username = "zhangsan";
        String businessKey = "test.4";

        HashMap<String, Object> map = new HashMap<>();
        // 启动流程实例时给变量赋值
        map.put("assignee1", username);
        map.put("assignee2", "lisi");
        map.put("assignee3", "wangwu");
        map.put("num", 5);
        ActivitiUtil.startProcessInstance(username, key, "请假", businessKey, map);
    }


    @Test
    public void testCompleteTaskWithBusinessKey() {
        String assignee = "zhangsan";
        String businessKey = "test.4";
        ActivitiUtil.completeTask(assignee, businessKey);
    }





    //lisi 李四驳回 网关
    @Test
    public void testRejection() throws Exception {

        String assignee = "lisi";
        // 驳回
        ActivitiUtil.rejection(assignee);
    }
    /**
     * 任务相关操作
     */
    @Autowired
    private RuntimeService runtimeService;
    // 先修改 在完成   map.put("num", 1);  //这个方法有时不灵
    @Test
    public void setnum(){
        runtimeService.setVariable("42501","num", 2 );
    }

    //被驳回 就2天吧
    @Test
    public void testCompleteTaskWithBusinessKey1() {
        String assignee = "zhangsan";
        String businessKey = "test.4";
        HashMap<String, Object> map = new HashMap<>();
        // 启动流程实例时给变量赋值
        map.put("num", 1);  //这个方法有时不灵
        //试试这个//此方法的更新会同时更新 ru_ 和 hi_的数据
        //    @Override
        //    public Map<String, Object> updateProcessFormValue(String processInstanceId,String value) {
        //        // TODO Auto-generated method stub
        //        //String value = JacksonUtils.mapToJson(bizMap);
        //
        //           runtimeService.setVariable(processInstanceId, "formValue", value);
        //        return null;
        //    }
        //先修改 在完成
        //runtimeService.setVariable("processInstanceId","num", 2 );

        ActivitiUtil.completeTaskWithVariables(assignee, map);
    }

   //完结  wangwu 办结
    @Test
    public void testCompleteTask6() throws Exception {
        String assignee = "wangwu";
        //
        ActivitiUtil.completeTask(assignee);
    }
}
