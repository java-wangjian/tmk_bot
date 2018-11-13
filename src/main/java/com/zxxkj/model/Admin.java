package com.zxxkj.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class Admin {
	private Integer id;
	private Long phone; // 联系电话
	private Byte status; // 账号状态
	private Date addTime; // 添加时间
	private String linkman; // 联系人
	private String company; // 公司名称
	private String account; // 账号
	private String password; // 密码
	private Integer addUser; // 创建者ID
	private Integer surplus; // 剩余天数
	private Integer soldPort; // 卖出的端口数量
	private Integer portCount; // 端口总量
	private Integer concurrent; // 并发量
	private Integer gatewayCount; // 网关数量
	private Integer soldCustomer; // 企业账号数量 
	private List<Gateway> gateways; // 专属网关
	private HashSet<String> adminArea; // 代理区域
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date validTime; // 到期时间
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getPhone() {
		return phone;
	}
	public void setPhone(Long phone) {
		this.phone = phone;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getAddUser() {
		return addUser;
	}
	public void setAddUser(Integer addUser) {
		this.addUser = addUser;
	}
	public Integer getSurplus() {
		return surplus;
	}
	public void setSurplus(Integer surplus) {
		this.surplus = surplus;
	}
	public Integer getSoldPort() {
		return soldPort;
	}
	public void setSoldPort(Integer soldPort) {
		this.soldPort = soldPort;
	}
	public Integer getPortCount() {
		return portCount;
	}
	public void setPortCount(Integer portCount) {
		this.portCount = portCount;
	}
	public Integer getConcurrent() {
		return concurrent;
	}
	public void setConcurrent(Integer concurrent) {
		this.concurrent = concurrent;
	}
	public Integer getGatewayCount() {
		return gatewayCount;
	}
	public void setGatewayCount(Integer gatewayCount) {
		this.gatewayCount = gatewayCount;
	}
	public Integer getSoldCustomer() {
		return soldCustomer;
	}
	public void setSoldCustomer(Integer soldCustomer) {
		this.soldCustomer = soldCustomer;
	}
	public List<Gateway> getGateways() {
		return gateways;
	}
	public void setGateways(List<Gateway> gateways) {
		this.gateways = gateways;
	}
	public HashSet<String> getAdminArea() {
		return adminArea;
	}
	public void setAdminArea(HashSet<String> adminArea) {
		this.adminArea = adminArea;
	}
	public Date getValidTime() {
		return validTime;
	}
	public void setValidTime(Date validTime) {
		this.validTime = validTime;
	}
	@Override
	public String toString() {
		return "Admin [id=" + id + ", phone=" + phone + ", status=" + status + ", addTime=" + addTime + ", linkman="
				+ linkman + ", company=" + company + ", account=" + account + ", password=" + password + ", addUser="
				+ addUser + ", surplus=" + surplus + ", soldPort=" + soldPort + ", portCount=" + portCount
				+ ", concurrent=" + concurrent + ", gatewayCount=" + gatewayCount + ", soldCustomer=" + soldCustomer
				+ ", gateways=" + gateways + ", adminArea=" + adminArea + ", validTime=" + validTime + "]";
	}
	public Admin(Integer id, Long phone, Byte status, Date addTime, String linkman, String company, String account,
			String password, Integer addUser, Integer surplus, Integer soldPort, Integer portCount, Integer concurrent,
			Integer gatewayCount, Integer soldCustomer, List<Gateway> gateways, HashSet<String> adminArea,
			Date validTime) {
		super();
		this.id = id;
		this.phone = phone;
		this.status = status;
		this.addTime = addTime;
		this.linkman = linkman;
		this.company = company;
		this.account = account;
		this.password = password;
		this.addUser = addUser;
		this.surplus = surplus;
		this.soldPort = soldPort;
		this.portCount = portCount;
		this.concurrent = concurrent;
		this.gatewayCount = gatewayCount;
		this.soldCustomer = soldCustomer;
		this.gateways = gateways;
		this.adminArea = adminArea;
		this.validTime = validTime;
	}
	public Admin() {
		super();
		// TODO Auto-generated constructor stub
	}
}