package com.zxxkj.model;

public class CustomerAndPlan {

	private Integer id;
	
	private Integer customerId;
	
	private Integer planId;
	
	private Integer userId;
	
	private Integer isCall;

	public CustomerAndPlan() {
		
	}

	public CustomerAndPlan(Integer id, Integer customerId, Integer planId, Integer userId, Integer isCall) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.planId = planId;
		this.userId = userId;
		this.isCall = isCall;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getIsCall() {
		return isCall;
	}

	public void setIsCall(Integer isCall) {
		this.isCall = isCall;
	}
	
}
