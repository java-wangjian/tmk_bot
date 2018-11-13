package com.zxxkj.model;


public class User {

	private int id;
	
	private String account;
	
	private String company;
	
	private String password;
	
	private int isActive; //账号是否激活0:未激活；1:已激活
	
	private String validTime; //账号到期时间
	
	private long phone;
	
	private String supportStaffName;//客服人员名字
	
	private String robotPort;//机器人端口号
	
	private String identity;//身份
	
	private int parentId;
	
	private int adminId;
	
	private String createTime;
	
	private String userAccount;
	
	private String adminAccount;

	private String activeTime;//激活时间
	
	private String contactPerson;
	
	private long contactPhone;
	
	private String city;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public String getSupportStaffName() {
		return supportStaffName;
	}

	public void setSupportStaffName(String supportStaffName) {
		this.supportStaffName = supportStaffName;
	}

	public String getRobotPort() {
		return robotPort;
	}

	public void setRobotPort(String robotPort) {
		this.robotPort = robotPort;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(String activeTime) {
		this.activeTime = activeTime;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public long getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(long contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getAdminAccount() {
		return adminAccount;
	}

	public void setAdminAccount(String adminAccount) {
		this.adminAccount = adminAccount;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", account=" + account + ", company=" + company + ", password=" + password
				+ ", isActive=" + isActive + ", validTime=" + validTime + ", phone=" + phone + ", supportStaffName="
				+ supportStaffName + ", robotPort=" + robotPort + ", identity=" + identity + ", parentId=" + parentId
				+ ", adminId=" + adminId 
				+ ", createTime=" + createTime + ", activeTime=" + activeTime + "]";
	}

	public User(int id, String account, String company, String password, int isActive, String validTime, long phone,
			String supportStaffName, String robotPort, String identity, int parentId, int adminId, 
			String createTime, String activeTime, String contactPerson, long contactPhone,
			String city) {
		super();
		this.id = id;
		this.account = account;
		this.company = company;
		this.password = password;
		this.isActive = isActive;
		this.validTime = validTime;
		this.phone = phone;
		this.supportStaffName = supportStaffName;
		this.robotPort = robotPort;
		this.identity = identity;
		this.parentId = parentId;
		this.adminId = adminId;
		this.createTime = createTime;
		this.activeTime = activeTime;
		this.contactPerson = contactPerson;
		this.contactPhone = contactPhone;
		this.city = city;
	}

	public User() {
		super();
	}

}
