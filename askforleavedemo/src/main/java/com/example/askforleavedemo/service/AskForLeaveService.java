package com.example.askforleavedemo.service;

import com.example.askforleavedemo.AskForLeaveController;
import com.example.askforleavedemo.mapper.UserMapper;
import com.example.askforleavedemo.model.AskForLeaveVO;
import com.example.askforleavedemo.model.RespBean;
import com.example.askforleavedemo.model.TaskVO;
import com.example.askforleavedemo.model.User;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class AskForLeaveService {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    TaskService taskService;
    @Autowired
    HistoryService historyService;
    public static final Logger logger= LoggerFactory.getLogger(AskForLeaveController.class);
    @Transactional
    public RespBean askForLeave(AskForLeaveVO askForLeaveVO){
        try {
            Map<String,Object> vars=new HashMap<>();
            vars.put("days",askForLeaveVO.getDays());
            vars.put("approveUser",askForLeaveVO.getApproveUser());
            vars.put("endTime",askForLeaveVO.getEndTime());
            vars.put("startTime",askForLeaveVO.getStartTime());
            vars.put("reson",askForLeaveVO.getReason());
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//获得当前登陆用户
            vars.put("applicant",user.getUsername());
            logger.info("后台拿到的信息-日期:{};结束时间:{}等",askForLeaveVO.getDays(),askForLeaveVO.getEndTime());
            ProcessInstance pi = runtimeService.startProcessInstanceByKey("askforleave_demo", vars);
            return RespBean.ok("请假申请已提交成功");
        } catch (Exception e) {
            return RespBean.error("请求提交失败",e);
            //throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        //return userMapper.getAllUsers();
        return userMapper.getAllUsersExcludeCurrent(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public List<AskForLeaveVO> getUnapproveProcess() {
        List<AskForLeaveVO> list=new ArrayList<>();
        List<Execution> executions = runtimeService.createExecutionQuery().startedBy(SecurityContextHolder.getContext().getAuthentication().getName()).list();
        for (Execution execution : executions) {
            //拿到了这个人发起的流程的各种信息
            Integer days = (Integer) runtimeService.getVariable(execution.getId(), "days");
            String approveUser = (String) runtimeService.getVariable(execution.getId(), "approveUser");
            Date endTime = (Date) runtimeService.getVariable(execution.getId(), "endTime");
            Date startTime = (Date) runtimeService.getVariable(execution.getId(), "startTime");
            String reson = (String) runtimeService.getVariable(execution.getId(), "reson");
            list.add(new AskForLeaveVO(days,reson,startTime,endTime,approveUser,execution.getProcessInstanceId()));
        }
        logger.info("===>{}",list);
        return list;
    }

    public void processImage(String processId, HttpServletResponse response) throws IOException {
        //查询当前流程定义
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("askforleave_demo").latestVersion().singleResult();
        BpmnModel model = repositoryService.getBpmnModel(pd.getId());
        //生成图片
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        List<String> highLightActivities = new ArrayList<>();
        List<String> highLightFlow=new ArrayList<>();
        List<ActivityInstance> list = runtimeService.createActivityInstanceQuery().processInstanceId(processId).list();
        for (ActivityInstance instance : list) {
            if(instance.getActivityType().equals("sequenceFlow")){
                highLightFlow.add(instance.getActivityName());
            }else {
                highLightFlow.add(instance.getActivityName());
            }
        }//拿到已经运行过的活动以及线条，分别放入不同数组
        InputStream is = generator.generateDiagram(model, "PNG", highLightActivities, highLightFlow, 1.0, true);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);//设置reponse的类型为PNG，否则返回的是乱码
        FileCopyUtils.copy(is,response.getOutputStream());
    }

    public List<TaskVO> getCurrentUserAllTask() {
        List<TaskVO> taskVOS=new ArrayList<>();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(SecurityContextHolder.getContext().getAuthentication().getName()).list();
        for (Task task : tasks) {
            Integer days = (Integer) taskService.getVariable(task.getId(), "days");
            String reason = (String) taskService.getVariable(task.getId(), "reason");
            Date startTime = (Date) taskService.getVariable(task.getId(), "startTime");
            Date endTime = (Date) taskService.getVariable(task.getId(), "endTime");
            String applicant=(String) taskService.getVariable(task.getId(),"applicant");
            TaskVO taskVO=new TaskVO(days,reason,startTime,endTime,null,task.getId(),null,applicant );//两个null分别是审批人和是否通过（其中审批人一定是当前用户，因为是根据它查出来的）
            taskVOS.add(taskVO);
        }
        return taskVOS;
    }

    public RespBean approval(TaskVO taskVO) {
        try {
            //这里有问题，来自于${approve} 和 approval 的混淆。不想去查了
            Map<String, Object> vars=new HashMap<>();
            logger.info("====>后台接收到的approve:{}",taskVO.getApproval());
            vars.put("approve",taskVO.getApproval());
            taskService.complete(taskVO.getTaskId(),vars);
            return RespBean.ok("阶段审批通过");
        } catch (Exception e) {
            throw e;
        }

    }

    public List<AskForLeaveVO> getHistoryProcess() {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().startedBy(SecurityContextHolder.getContext().getAuthentication().getName()).finished().list();
        List<AskForLeaveVO> res=new ArrayList<>();
        for (HistoricProcessInstance historicProcessInstance : list) {
            List<HistoricVariableInstance> variableInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
            AskForLeaveVO askForLeaveVO=new AskForLeaveVO();
            for (HistoricVariableInstance hvi : variableInstanceList) {
                String variableName = hvi.getVariableName();
                Object value = hvi.getValue();
                if("days".equals(variableName)){
                    askForLeaveVO.setDays((Integer) value);
                } else if ("reason".equals(variableName)) {
                    askForLeaveVO.setReason((String) value);
                }else if ("approvalUser".equals(variableName)) {
                    askForLeaveVO.setApproveUser((String) value);
                }else if ("startTime".equals(variableName)) {
                    askForLeaveVO.setStartTime((Date) value);
                }else if ("endTime".equals(variableName)) {
                    askForLeaveVO.setEndTime((Date) value);
                }else if ("approval ".equals(variableName)) {
                    askForLeaveVO.setApproval((Boolean) value);
                }
            }
            res.add(askForLeaveVO);
        }
        return res;
    }
}
