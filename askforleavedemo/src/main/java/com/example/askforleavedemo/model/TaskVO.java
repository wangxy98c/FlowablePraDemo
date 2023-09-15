package com.example.askforleavedemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TaskVO {
    private Integer days;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date endTime;
    private String approveUser;
    private String taskId;
    private Boolean approval;
    private String applicant;

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public TaskVO() {
    }

    public TaskVO(Integer days, String reason, Date startTime, Date endTime, String approveUser, String taskId, Boolean approve, String applicant) {
        this.days = days;
        this.reason = reason;
        this.startTime = startTime;
        this.endTime = endTime;
        this.approveUser = approveUser;
        this.taskId = taskId;
        this.approval = approve;
        this.applicant=applicant;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(String approveUser) {
        this.approveUser = approveUser;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean getApproval() {
        return approval;
    }

    public void setApproval(Boolean approve) {
        this.approval = approve;
    }
}
