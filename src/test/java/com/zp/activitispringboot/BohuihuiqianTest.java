package com.zp.activitispringboot;

import com.zp.activitispringboot.utils.ActivitiUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 驳回会签节点  顺序会签
 * 获取当前节点和上个节点的定义信息，将当前节点的流向指向上个节点后完成任务，然后将流向复原
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BohuihuiqianTest {
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 手动方式部署
     */
    @Test
    public void deploy(){
        // 进行部署
        Deployment deployment = repositoryService.createDeployment()
                // 文件夹的名称不能是process
                .addClasspathResource("processes/Bohui4huiqianz.bpmn")
                //act_re_procdef name 只针对bpmn定义  act_re_deployment name “processtest03”
                .name("Bohui4huiqianz")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }


    //张三发起流程
    @Test
    public void testProcessInstance(){
        String key = "Bohui4huiqianz";
        String username = "zhangsan";

        HashMap<String, Object> map = new HashMap<>();
        // 启动流程实例时给变量赋值
        map.put("assignee1", username);
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
        //设置会签人员
        HashMap map = new HashMap<String, Object>();
        List<String> signList = new ArrayList<>();
        signList.add("lisi");
        signList.add("wangwu");
        map.put("assignee2List", signList);
        ActivitiUtil.completeTaskWithVariables(assignee, map);
    }

    //lisi 通过
    @Test
    public void testQueryTask2(){
        String assignee = "lisi";
        ActivitiUtil.printTaskList(assignee, 0, 10);
    }
    @Test
    public void testCompleteTask3(){
        String assignee = "lisi";
        ActivitiUtil.completeTask(assignee);
    }

    //wangwu 驳回
    //修改当前人员就行

    //lisi 通过
    @Test
    public void testQueryTask4(){
        String assignee = "wangwu";
        ActivitiUtil.printTaskList(assignee, 0, 10);
    }
    @Test
    public void testCompleteTask5(){
        String assignee = "wangwu";
        HashMap map = new HashMap<String, Object>();
        map.put("assignee3","zhaoliu");
        ActivitiUtil.completeTaskWithVariables(assignee, map);
    }


    @Test
    public void testCompleteTask6(){
        String assignee = "zhaoliu";
        ActivitiUtil.completeTask(assignee);
    }
}
