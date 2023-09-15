package com.example.askforleavedemo.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RejectServiceTask implements JavaDelegate {
    public static final Logger logger= LoggerFactory.getLogger(RejectServiceTask.class);
    @Override
    public void execute(DelegateExecution delegateExecution) {
        Map<String, Object> variables = delegateExecution.getVariables();
        logger.error("{}请假{}天的申请没有通过",variables.get("name"),variables.get("days"));
    }
}
