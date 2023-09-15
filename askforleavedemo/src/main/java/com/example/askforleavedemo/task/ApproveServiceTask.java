package com.example.askforleavedemo.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/*请求通过的服务类,自定义的Bean注入方式*/
@Service
public class ApproveServiceTask implements JavaDelegate {//ServiceTask类型。监听类

    public static final Logger logger= LoggerFactory.getLogger(ApproveServiceTask.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Map<String, Object> variables = delegateExecution.getVariables();
        logger.info("{}请假{}天的申请通过",variables.get("name"),variables.get("days"));
    }
}
