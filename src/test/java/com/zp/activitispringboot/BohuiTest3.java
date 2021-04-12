package com.zp.activitispringboot;

import com.zp.activitispringboot.utils.ActivitiUtil;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

/**
 * candidate user的场合
 * 获取当前节点和上个节点的定义信息，将当前节点的流向指向上个节点后完成任务，然后将流向复原
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BohuiTest3 {

    @Test
    public void testProcessInstance(){
        String key = "bohui3";
        String username = "zhangsan";

        HashMap<String, Object> map = new HashMap<>();
        // 启动流程实例时给变量赋值
        map.put("assignee1", username);
        ActivitiUtil.startProcessInstanceWithVariables(username, key,"请假", map);
    }

    @Test
    public void testQueryTask(){
        String assignee = "zhangsan";
        ActivitiUtil.printTaskList(assignee, 0, 10);
    }

    @Test
    public void testCompleteTask(){
        String assignee = "zhangsan";
        ActivitiUtil.completeTask(assignee);
    }

    @Test
    public void testCompleteTask2(){
        String assignee = "c1";
        ActivitiUtil.completeTaskWithGroup(assignee);
    }


    @Test
    public void testCompleteTask3() throws Exception {
        String assignee = "c3";
        HashMap<String, Object> map = new HashMap<>();
        // 完成任务时同时指定审核为驳回
        map.put("audit", false);

        // 驳回
        ActivitiUtil.auditByCandidate(assignee, false, map);
    }
 //查询 C1 任务
    @Test
    public void testQueryTask2(){
        String assignee = "c1";
        ActivitiUtil.printTaskList(assignee, 0, 10);
    }

    //查询无数据 用户已经从 历史表中取出 C1 设置到 act_ru_task assignee
    @Test
    public void testQueryTask3(){
        //查询无数据 用户已经从 历史表中取出 C1 设置到 act_ru_task assignee
        String assignee = "c2";
        ActivitiUtil.printTaskList(assignee, 0, 10);
    }

    @Test
    public void testCompleteTask4(){
        String assignee = "c1";
        ActivitiUtil.completeTask(assignee);

    }

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRuntime taskRuntime;
    //设置下一步 办理人, 从历史办结数据获取，和上一步同时设置 testCompleteTask4
    //ru_task assingee 用户设置为 C3 这步有 版本claim
    //通过实例ID 6a7597e6-967a-11eb-93e5-dc7196b7d4a6
    @Test
    public  void  setassignee (){
        org.activiti.engine.task.Task nextTask = taskService
                .createTaskQuery().processInstanceId("6a7597e6-967a-11eb-93e5-dc7196b7d4a6").singleResult();
        // 设置执行人
        if (nextTask != null) {
            List<HistoricTaskInstance> htiList = ActivitiUtil.getHistoryTaskList("6a7597e6-967a-11eb-93e5-dc7196b7d4a6");
            HistoricTaskInstance h5= null;
            for (HistoricTaskInstance h:
                    htiList) {
                if(h.getTaskDefinitionKey().equals( nextTask.getTaskDefinitionKey())){
                    h5 =h;
                    break;
                }

            }
            //这步有 版本claim
            taskService.setAssignee(nextTask.getId(), h5.getAssignee());

            //这步有 版本claim
            //release
            taskRuntime.release(
                    TaskPayloadBuilder.release().withTaskId(nextTask.getId()).build());
        }
    }


    /**
     * c3 这样办结有点问 前面步骤 有claim
     * @throws Exception
     */
    @Test
    public void testCompleteTaskC3() throws Exception {
        String assignee = "c3";
        HashMap<String, Object> map = new HashMap<>();
        // 完成任务时同时指定审核为通过
        map.put("audit", true);

        // 审核通过
        ActivitiUtil.auditByCandidate(assignee, true, map);
    }



    //查询C4 能否获取到
    @Test
    public void testQueryTaskc4(){
        //查询无数据 用户已经从 历史表中取出 C1 设置到 act_ru_task assignee
        String assignee = "c4";
        ActivitiUtil.printTaskList(assignee, 0, 10);
    }


    /**
     * c3  c4 都可以认领 代码可以执行莫民奇妙
     * @throws Exception
     */
    @Test
    public void testCompleteTask5() throws Exception {
        String assignee = "c4";
        HashMap<String, Object> map = new HashMap<>();
        // 完成任务时同时指定审核为通过
        map.put("audit", true);

        // 审核通过
        ActivitiUtil.auditByCandidate(assignee, true, map);
    }





}
