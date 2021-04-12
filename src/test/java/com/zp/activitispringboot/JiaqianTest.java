package com.zp.activitispringboot;

import com.zp.activitispringboot.utils.ActivitiUtil;
import org.activiti.api.task.model.Task;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * 加签
 * 动态加入节点
 * @author admin
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JiaqianTest {
    @Autowired
    private RepositoryService repositoryService;
    @Test
    public void testDefiniton(){
        String username = "zhangsan";
        ActivitiUtil.printProcessDefinitionList(username);
    }

    /**
     * 手动方式部署
     */
    @Test
    public void deploy(){
        // 进行部署
        Deployment deployment = repositoryService.createDeployment()
                // 文件夹的名称不能是process
                .addClasspathResource("processes/jiaqian.bpmn")
                //act_re_procdef name 只针对bpmn定义  act_re_deployment name “processtest03”
                .name("jiaqian")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    @Test
    public void testProcessInstance(){
        String key = "jiaqian";
        String username = "zhangsan";
        String assignee3 = "zhaoliu";

        HashMap<String, Object> map = new HashMap<>();
        // 启动流程实例时给变量赋值
        map.put("assignee1", username);
        map.put("assignee3", assignee3);

        ActivitiUtil.startProcessInstanceWithVariables(username, key,"会签", map);
    }

    @Test
    public void testQueryTask(){
        String assignee = "zhangsan";
        ActivitiUtil.printTaskList(assignee, 0, 10);
    }

    @Test
    public void testCompleteTask(){
        String assignee = "zhangsan";
        HashMap map = new HashMap<String, Object>();
        map.put("assignee2","lisi");
        ActivitiUtil.completeTaskWithVariables(assignee, map);
    }


    @Test
    public void testQueryTask2(){
        String assignee = "lisi";
        ActivitiUtil.printTaskList(assignee, 0, 10);
    }

    @Test
    public void jiaqian() throws Exception {
        String assignee = "lisi";
        // 动态加签,并设置加签节点的执行人
        Task task = ActivitiUtil.getTaskList(assignee, 0, 10).getContent().get(0);
        String assigneeJiaqian = "wangwu";
        ActivitiUtil.addTask((org.activiti.engine.task.Task) task, assigneeJiaqian);


//        ActivitiUtil.createTask(assignee, task.getId(), assigneeJiaqian);
    }

    @Test
    public void testCompleteTask2(){
        String assignee = "lisi";
//        HashMap map = new HashMap<String, Object>();
//        String assignee3 = "zhaoliu";
//        map.put("assignee3", assignee3);
//        ActivitiUtil.completeTaskWithVariables(assignee, map);
        ActivitiUtil.completeTask(assignee);
    }

    @Test
    public void testCompleteTask3(){
        String assignee = "wangwu";
        HashMap map = new HashMap<String, Object>();
        String assignee3 = "zhaoliu";
        map.put("assignee3", assignee3);
        ActivitiUtil.completeTaskWithVariables(assignee, map);
    }

    @Test
    public void testCompleteTask4(){
        String assignee = "zhaoliu";
        ActivitiUtil.completeTask(assignee);
    }
}
