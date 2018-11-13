package com.zxxkj.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

public class Visit {

	private int id;
	
	private Date visitTime;
	

    private String visitWay;
	private int customerId;

	private int planId;

    private int userId;

	private String visitDetails;

	private int grade;

	private Date addTime;

	public Visit() {
		super();
	}

    public Visit(int id, Date visitTime, String visitWay, int customerId, int planId, int userId, String visitDetails, int grade, Date addTime) {
        this.id = id;
        this.visitTime = visitTime;
        this.visitWay = visitWay;
        this.customerId = customerId;
        this.planId = planId;
        this.userId = userId;
        this.visitDetails = visitDetails;
        this.grade = grade;
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("visitTime", visitTime)
                .append("visitWay", visitWay)
                .append("customerId", customerId)
                .append("planId", planId)
                .append("userId", userId)
                .append("visitDetails", visitDetails)
                .append("grade", grade)
                .append("addTime", addTime)
                .toString();
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}

	public String getVisitWay() {
		return visitWay;
	}

	public void setVisitWay(String visitWay) {
		this.visitWay = visitWay;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getVisitDetails() {
		return visitDetails;
	}

	public void setVisitDetails(String visitDetails) {
		this.visitDetails = visitDetails;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}


}
