package com.zxxkj.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

public class Customer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 213254365476L;

	private int id;

	private String company;

	private String customerName;

	private String customerPhone; //被叫号码

	private String note;

	private int userId;

	private int grade;

	private int isCall;

	private String batchNo;

	private int callCount;

	private Date addTime;

	private Integer isPlaned;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("company", company)
                .append("customerName", customerName)
                .append("customerPhone", customerPhone)
                .append("note", note)
                .append("userId", userId)
                .append("grade", grade)
                .append("isCall", isCall)
                .append("batchNo", batchNo)
                .append("callCount", callCount)
                .append("addTime", addTime)
                .append("isPlaned", isPlaned)
                .toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getIsCall() {
        return isCall;
    }

    public void setIsCall(int isCall) {
        this.isCall = isCall;
    }

    public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getIsPlaned() {
        return isPlaned;
    }

    public void setIsPlaned(Integer isPlaned) {
        this.isPlaned = isPlaned;
    }

    public Customer() {
    }

    public Customer(int id, String company, String customerName, String customerPhone, String note, int userId, int grade, int isCall, String batchNo, int callCount, Date addTime, Integer isPlaned) {
        this.id = id;
        this.company = company;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.note = note;
        this.userId = userId;
        this.grade = grade;
        this.isCall = isCall;
        this.batchNo = batchNo;
        this.callCount = callCount;
        this.addTime = addTime;
        this.isPlaned = isPlaned;
    }
}
