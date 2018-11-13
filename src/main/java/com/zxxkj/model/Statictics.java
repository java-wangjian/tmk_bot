package com.zxxkj.model;

public class Statictics {
	
	private Integer id;
	private Integer duratTotal;//通话总时长
	private Integer toStaffCount;//转接数
	private Integer refuseCount;//拒绝or未接
	private Integer callCount;//总呼出量
	private Integer connCount;//接通量
	private Integer missCount;
	private Integer customerA;
	private Integer customerB;
	private Integer customerC;
	private Integer customerD;
	private Integer customerE;
	private Integer customerF;
	private Integer userId;
	private String date;
	private Integer lt10gt5;
	private Integer gt30;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDuratTotal() {
		return duratTotal;
	}
	public void setDuratTotal(Integer duratTotal) {
		this.duratTotal = duratTotal;
	}
	public Integer getToStaffCount() {
		return toStaffCount;
	}
	public void setToStaffCount(Integer toStaffCount) {
		this.toStaffCount = toStaffCount;
	}
	public Integer getRefuseCount() {
		return refuseCount;
	}
	public void setRefuseCount(Integer refuseCount) {
		this.refuseCount = refuseCount;
	}
	public Integer getCallCount() {
		return callCount;
	}
	public void setCallCount(Integer callCount) {
		this.callCount = callCount;
	}
	public Integer getConnCount() {
		return connCount;
	}
	public void setConnCount(Integer connCount) {
		this.connCount = connCount;
	}
	public Integer getMissCount() {
		return missCount;
	}
	public void setMissCount(Integer missCount) {
		this.missCount = missCount;
	}
	public Integer getCustomerA() {
		return customerA;
	}
	public void setCustomerA(Integer customerA) {
		this.customerA = customerA;
	}
	public Integer getCustomerB() {
		return customerB;
	}
	public void setCustomerB(Integer customerB) {
		this.customerB = customerB;
	}
	public Integer getCustomerC() {
		return customerC;
	}
	public void setCustomerC(Integer customerC) {
		this.customerC = customerC;
	}
	public Integer getCustomerD() {
		return customerD;
	}
	public void setCustomerD(Integer customerD) {
		this.customerD = customerD;
	}
	public Integer getCustomerE() {
		return customerE;
	}
	public void setCustomerE(Integer customerE) {
		this.customerE = customerE;
	}
	public Integer getCustomerF() {
		return customerF;
	}
	public void setCustomerF(Integer customerF) {
		this.customerF = customerF;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getLt10gt5() {
		return lt10gt5;
	}
	public void setLt10gt5(Integer lt10gt5) {
		this.lt10gt5 = lt10gt5;
	}
	public Integer getGt30() {
		return gt30;
	}
	public void setGt30(Integer gt30) {
		this.gt30 = gt30;
	}
	public Statictics() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Statictics(Integer id, Integer duratTotal, Integer toStaffCount, Integer refuseCount, Integer callCount,
			Integer connCount, Integer missCount, Integer customerA, Integer customerB, Integer customerC,
			Integer customerD, Integer customerE, Integer customerF, Integer userId, String date, Integer lt10gt5,
			Integer gt30) {
		super();
		this.id = id;
		this.duratTotal = duratTotal;
		this.toStaffCount = toStaffCount;
		this.refuseCount = refuseCount;
		this.callCount = callCount;
		this.connCount = connCount;
		this.missCount = missCount;
		this.customerA = customerA;
		this.customerB = customerB;
		this.customerC = customerC;
		this.customerD = customerD;
		this.customerE = customerE;
		this.customerF = customerF;
		this.userId = userId;
		this.date = date;
		this.lt10gt5 = lt10gt5;
		this.gt30 = gt30;
	}
	@Override
	public String toString() {
		return "Statictics [id=" + id + ", duratTotal=" + duratTotal + ", toStaffCount=" + toStaffCount
				+ ", refuseCount=" + refuseCount + ", callCount=" + callCount + ", connCount=" + connCount
				+ ", missCount=" + missCount + ", customerA=" + customerA + ", customerB=" + customerB + ", customerC="
				+ customerC + ", customerD=" + customerD + ", customerE=" + customerE + ", customerF=" + customerF
				+ ", userId=" + userId + ", date=" + date + ", lt10gt5=" + lt10gt5 + ", gt30=" + gt30 + "]";
	}
	
}