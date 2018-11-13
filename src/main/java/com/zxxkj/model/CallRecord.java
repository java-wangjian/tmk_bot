package com.zxxkj.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class CallRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7643549379365L;
	private Integer id;
	private String customerNote;
	private Integer status;// 通话状态1 接通、2 未接 、3拒接
	private Integer durationTime;// 通话持续时间
	private Integer projectID;// 模板ID
	private Integer customerID;// 客户ID 被叫ID
	private Integer userID;// 用户ID 主叫ID
	private String projectName; // 模板名称
	private Integer callSignal;// SIM信号
	private String reverse;
	private Integer exportCount;
	private String customerCompany;
	private String userCompany;
	private String customerName;
	private String datetime; // 呼叫开始时间
	private String customerPhone;// 被叫号码
	private Long userPhone;// 主叫号码
	private Integer customerGrade;// 客户登记
	private String fileID;// 全程录音地址
	private Integer planId;
	private String planName;
	private Integer isTransfer;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("customerNote", customerNote)
                .append("status", status)
                .append("durationTime", durationTime)
                .append("projectID", projectID)
                .append("customerID", customerID)
                .append("userID", userID)
                .append("projectName", projectName)
                .append("callSignal", callSignal)
                .append("reverse", reverse)
                .append("exportCount", exportCount)
                .append("customerCompany", customerCompany)
                .append("userCompany", userCompany)
                .append("customerName", customerName)
                .append("datetime", datetime)
                .append("customerPhone", customerPhone)
                .append("userPhone", userPhone)
                .append("customerGrade", customerGrade)
                .append("fileID", fileID)
                .append("planId", planId)
                .append("planName", planName)
                .append("isTransfer", isTransfer)
                .toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getCallSignal() {
        return callSignal;
    }

    public void setCallSignal(Integer callSignal) {
        this.callSignal = callSignal;
    }

    public String getReverse() {
        return reverse;
    }

    public void setReverse(String reverse) {
        this.reverse = reverse;
    }

    public Integer getExportCount() {
        return exportCount;
    }

    public void setExportCount(Integer exportCount) {
        this.exportCount = exportCount;
    }

    public String getCustomerCompany() {
        return customerCompany;
    }

    public void setCustomerCompany(String customerCompany) {
        this.customerCompany = customerCompany;
    }

    public String getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }

    public Integer getCustomerGrade() {
        return customerGrade;
    }

    public void setCustomerGrade(Integer customerGrade) {
        this.customerGrade = customerGrade;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Integer getIsTransfer() {
        return isTransfer;
    }

    public void setIsTransfer(Integer isTransfer) {
        this.isTransfer = isTransfer;
    }

    public CallRecord(Integer id, String customerNote, Integer status, Integer durationTime, Integer projectID, Integer customerID, Integer userID, String projectName, Integer callSignal, String reverse, Integer exportCount, String customerCompany, String userCompany, String customerName, String datetime, String customerPhone, Long userPhone, Integer customerGrade, String fileID, Integer planId, String planName, Integer isTransfer) {
        this.id = id;
        this.customerNote = customerNote;
        this.status = status;
        this.durationTime = durationTime;
        this.projectID = projectID;
        this.customerID = customerID;
        this.userID = userID;
        this.projectName = projectName;
        this.callSignal = callSignal;
        this.reverse = reverse;
        this.exportCount = exportCount;
        this.customerCompany = customerCompany;
        this.userCompany = userCompany;
        this.customerName = customerName;
        this.datetime = datetime;
        this.customerPhone = customerPhone;
        this.userPhone = userPhone;
        this.customerGrade = customerGrade;
        this.fileID = fileID;
        this.planId = planId;
        this.planName = planName;
        this.isTransfer = isTransfer;
    }

    public CallRecord() {

    }
}
