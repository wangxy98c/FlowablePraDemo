package com.example.askforleavedemo;

import com.example.askforleavedemo.model.AskForLeaveVO;
import com.example.askforleavedemo.model.RespBean;
import com.example.askforleavedemo.model.TaskVO;
import com.example.askforleavedemo.model.User;
import com.example.askforleavedemo.service.AskForLeaveService;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AskForLeaveController {
    @Autowired
    AskForLeaveService askForLeaveService;
    @PostMapping("/ask_for_leave")//流程启动
    public RespBean askForLeave(@RequestBody AskForLeaveVO askForLeaveVO){
        RespBean respBean = askForLeaveService.askForLeave(askForLeaveVO);
        return respBean;
    }
    @GetMapping("/users")
    public List<User> getAllUsers(){
        return askForLeaveService.getAllUsers();
    }

    @GetMapping("/unapprove")
    public List<AskForLeaveVO> getUnapproveProcess(){//查找所有由当前用户发起且未审批的流程
        System.out.println("==========>unapprove");
        return askForLeaveService.getUnapproveProcess();
    }
    @GetMapping("/image/{processId}")//返回流程图片
    public void processImage(@PathVariable String processId, HttpServletResponse response) throws IOException {
        askForLeaveService.processImage(processId,response);
    }
    @GetMapping("/tasks")
    public List<TaskVO> getCurrentUserAllTask(){
        List<TaskVO> currentUserAllTask = askForLeaveService.getCurrentUserAllTask();
        return currentUserAllTask;
    }
    @PostMapping("/approval")
    public RespBean approval(@RequestBody TaskVO taskVO){
        return askForLeaveService.approval(taskVO);
    }
    @GetMapping("/history")
    public List<AskForLeaveVO> historyProcess(){
        return askForLeaveService.getHistoryProcess();
    }
}
