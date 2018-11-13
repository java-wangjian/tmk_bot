package com.zxxkj.model;

import java.util.Date;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Plan implements Cloneable {

	private int id;
	
	private Date excuteTime;
	
	private String belong;
	
	private String timeStr;
	
	private int planStatus;
	
	private int userId;
	
	private int isInterrupt;
	
	private int isTransfer;//1表示转接；2表示不转接
	
	private int transferGrade;
	
	private Date addTime;
	
	private int projectId;
	
	private int isDelete;
	
	private int isStop;
	
	private int excuteCount;
	
	private String planName;
	
	private int customerCount;
	
	private String url;
	
	private String callPortListStr;
	
	private String trancferPortListStr;
	
	private Integer gatewayId;
	
	private Integer isSendSMS;//1表示发送短信；2表示不发送短信
	
	private Integer sipCallCount;

	private String prifix;

	private String gateName;
	
	private Date endTime;
	
	private String planTag;//随机生成的十位计划唯一标识
	
	private Boolean isEnd = false;//当前时间段的子计划是否结束
	
	private Date updateTime;//修改后的执行时间
	
	private AtomicInteger calledCount = new AtomicInteger(0);
	
	private Vector<String> callRecordVectoer = new Vector<String>();
	
	private boolean isReSet = false;//是否已经重置了第二天的计划
	
 	public Plan() {
		super();
	}

	public Plan(int id, Date excuteTime, String belong, String timeStr, int planStatus, int userId, int isInterrupt,
			int isTransfer, int transferGrade, Date addTime, int projectId, int isDelete, int isStop, int excuteCount,
			String planName, int customerCount, String url, String callPortListStr, String trancferPortListStr,
			Integer gatewayId, Integer isSendSMS, Integer sipCallCount, String prifix, String gateName, Date endTime
			, AtomicInteger calledCount) {
		super();
		this.id = id;
		this.excuteTime = excuteTime;
		this.belong = belong;
		this.timeStr = timeStr;
		this.planStatus = planStatus;
		this.userId = userId;
		this.isInterrupt = isInterrupt;
		this.isTransfer = isTransfer;
		this.transferGrade = transferGrade;
		this.addTime = addTime;
		this.projectId = projectId;
		this.isDelete = isDelete;
		this.isStop = isStop;
		this.excuteCount = excuteCount;
		this.planName = planName;
		this.customerCount = customerCount;
		this.url = url;
		this.callPortListStr = callPortListStr;
		this.trancferPortListStr = trancferPortListStr;
		this.gatewayId = gatewayId;
		this.isSendSMS = isSendSMS;
		this.sipCallCount = sipCallCount;
		this.prifix = prifix;
		this.gateName = gateName;
		this.endTime = endTime;
		this.calledCount = calledCount;
	}

	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getExcuteTime() {
		return excuteTime;
	}

	public void setExcuteTime(Date excuteTime) {
		this.excuteTime = excuteTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getIsInterrupt() {
		return isInterrupt;
	}

	public void setIsInterrupt(int isInterrupt) {
		this.isInterrupt = isInterrupt;
	}

	public int getIsTransfer() {
		return isTransfer;
	}

	public void setIsTransfer(int isTransfer) {
		this.isTransfer = isTransfer;
	}

	public int getTransferGrade() {
		return transferGrade;
	}

	public void setTransferGrade(int transferGrade) {
		this.transferGrade = transferGrade;
	}

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public int getExcuteCount() {
		return excuteCount;
	}

	public void setExcuteCount(int excuteCount) {
		this.excuteCount = excuteCount;
	}

	public int getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(int planStatus) {
		this.planStatus = planStatus;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public int getIsStop() {
		return isStop;
	}

	public void setIsStop(int isStop) {
		this.isStop = isStop;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public int getCustomerCount() {
		return customerCount;
	}

	public void setCustomerCount(int customerCount) {
		this.customerCount = customerCount;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCallPortListStr() {
		return callPortListStr;
	}

	public void setCallPortListStr(String callPortListStr) {
		this.callPortListStr = callPortListStr;
	}

	public String getTrancferPortListStr() {
		return trancferPortListStr;
	}

	public void setTrancferPortListStr(String trancferPortListStr) {
		this.trancferPortListStr = trancferPortListStr;
	}
	
	public Integer getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Integer gatewayId) {
		this.gatewayId = gatewayId;
	}

	public Integer getIsSendSMS() {
		return isSendSMS;
	}

	public void setIsSendSMS(Integer isSendSMS) {
		this.isSendSMS = isSendSMS;
	}

	public Integer getSipCallCount() {
		return sipCallCount;
	}

	public void setSipCallCount(Integer sipCallCount) {
		this.sipCallCount = sipCallCount;
	}

	public String getPrifix() {
		return prifix;
	}

	public void setPrifix(String prifix) {
		this.prifix = prifix;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}

	public String getPlanTag() {
		return planTag;
	}

	public void setPlanTag(String planTag) {
		this.planTag = planTag;
	}

	public Boolean getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(Boolean isEnd) {
		this.isEnd = isEnd;
	}
	
	@Override
	public Plan clone(){
		Plan plan = null;
		
		try {
			plan = (Plan) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return plan;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public AtomicInteger getCalledCount() {
		return calledCount;
	}

	public void setCalledCount(AtomicInteger calledCount) {
		this.calledCount = calledCount;
	}

	public Vector<String> getCallRecordVectoer() {
		return callRecordVectoer;
	}

	public void setCallRecordVectoer(Vector<String> callRecordVectoer) {
		this.callRecordVectoer = callRecordVectoer;
	}

	public boolean isReSet() {
		return isReSet;
	}

	public void setReSet(boolean isReSet) {
		this.isReSet = isReSet;
	}

	@Override
	public String toString() {
		return "Plan [id=" + id + ", excuteTime=" + excuteTime + ", belong=" + belong + ", timeStr=" + timeStr
				+ ", planStatus=" + planStatus + ", userId=" + userId + ", isInterrupt=" + isInterrupt + ", isTransfer="
				+ isTransfer + ", transferGrade=" + transferGrade + ", addTime=" + addTime + ", projectId=" + projectId
				+ ", isDelete=" + isDelete + ", isStop=" + isStop + ", excuteCount=" + excuteCount + ", planName="
				+ planName + ", customerCount=" + customerCount + ", url=" + url + ", callPortListStr="
				+ callPortListStr + ", trancferPortListStr=" + trancferPortListStr + ", gatewayId=" + gatewayId
				+ ", isSendSMS=" + isSendSMS + ", sipCallCount=" + sipCallCount + ", prifix=" + prifix + ", gateName="
				+ gateName + ", endTime=" + endTime + ", planTag=" + planTag + ", isEnd=" + isEnd + "]";
	}

}
