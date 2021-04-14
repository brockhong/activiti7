# activitispringboot
* 此工程为activiti7与springboot整合
* activiti7默认采用了spring security来做权限控制
* bpmn文件放在resources/processes目录下可以自动部署
* activiti7自动生成的表里没有历史表，需要开启配置
* 7.1.0.M6开始加入了全新的版本控制逻辑，需要在default-project.json文件里指定版本，不然启动实例会报错
# 流程图说明
* springboot01.bpmn candidateGroup为官方示例中的activitiTeam
* springboot02.bpmn candidateGroup为自定义的firstGroup和secondGroup
* qingjia.bpmn 分享演示用
* Bohui.bpmn 单个执行人的驳回，用连线方式可以解决
* Bohui2.bpmn candidate user的驳回，用连线方式有问题
* Bohui3.bpmn candidate user的驳回，用底层操作取得节点信息，重新指定节点流向解决
* Huiqian.bpmn 节点多实例实现会签 并行 条件：所有人通过才进入下一个节点
* Huiqian2.bpmn 节点多实例实现会签 并行 条件：一半以上通过则进入下一个节点
* Huiqian3.bpmn 节点多实例实现会签 串行 条件：所有人通过才进入下一个节点
* Huiqian4.bpmn 节点多实例实现会签 并行 在会签节点动态增加节点实例
* Jiaqian.bpmn 动态加签（即动态增加一个节点）：暂时无法实现，只能通过修改流程图或者代码动态增加节点后保存到流程图来实现加签，但这样一来会影响所有实例。建议尽量用节点多实例会签来替代。
* HuiqianDaiqian.bpmn 节点多实例实现代签 用户组 模拟单实例
* HuiqianDaiqian2.bpmn 节点多实例实现代签 用户组 模拟多实例
* HuiqianDaiqian3.bpmn 节点多实例实现代签 同一个流程定义，模拟单实例和多实例两种情况

=======添加==========
* Bohui4huiqianz.bpmn 驳回会签并行  BohuihuiqianTest
* Bohui4huiqianh.bpmn  驳回会签节点  顺序会签
* ExclusiveGatewaybohui.bpmn  驳回网关  mainifest这个问题 流程定义 流程部署时 设置版本字段 如果没有就回报错

# 问题点
* activiti7的自动识别processes下的bpmn文件，但是只会识别第一次，如果再次新建一个bpmn文件，再执行代码数据库里不会增加记录
* =》待确认 =》activiti7读的是target/classes下的processes里的bpmn文件，需要重新打包才会读取最新的bpmn文件
* 上面结论不正确，看了源码暂时也没搞清楚怎么回事
* 解决方法：用7之前的deploy方法来部署 => 此方法有问题，启动实例时会报错
* =》最新结论：activiti7中的版本控制逻辑为：工程里需要有一个mainifest文件，在application.properties属性文件中用project.mainifest.file.path属性设定。
当mainifest文件里的工程版本改变时，所有的流程定义的版本都会被改变。
比如：现在工程版本是1，现在新加了一个流程图，我把工程版本改为2，这时所有流程图的版本都会被改成2
而如果不改工程版本的话，新加的流程图是不会刷到数据库中的。
其它问题可以参照我的博客：http://zpycloud.com/archives/category/%e5%b7%a5%e4%bd%9c%e6%b5%81%e5%bc%95%e6%93%8e/

*  <font color=red>MyProcessRuntimeImpl.java 修复分页</font>

* <font color=red>JumpTaskTest.java  修复跳转</font>

* 切换数据源

# 版本问题
## 7.1.0.M6
* 新增bpmn流程图时新增的这个不会自动部署
* 把数据库删除后可以部署
## 7.1.0.M5
* 自动部署时会报错
Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: Unknown column 'VERSION_' in 'field list'
## 7.1.0.M1 7.0.0.SR1
* 新增bpmn流程图时可以自动部署，但是已经存在的会重复部署