package com.zp.activitispringboot.cmd;

import org.apache.ibatis.annotations.Delete;


public  interface HisActInstanceMapper {
    @Delete("delete from act_hi_actinst where TASK_ID_ = #{taskId}")
    Integer deletetaskId(String taskId);
}
