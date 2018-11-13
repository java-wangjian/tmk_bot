package com.zxxkj.model;

public class SimCard {
	private Integer id;
	private String gatewayNode;
	private String gatewayUrl;
	private Integer gatewayId;
	private String auth;
	private String pwd;
	private Integer signal;
	private Integer port;
	private Byte label;
	private Byte status;
	private Byte now;
	private Integer userID;
	private Long phone;
	private Float balance;
	public SimCard(Integer id, String gatewayNode, String gatewayUrl, Integer gatewayId, String auth, String pwd,
			Integer signal, Integer port, Byte label, Byte status, Byte now, Integer userID, Long phone,
			Float balance) {
		super();
		this.id = id;
		this.gatewayNode = gatewayNode;
		this.gatewayUrl = gatewayUrl;
		this.gatewayId = gatewayId;
		this.auth = auth;
		this.pwd = pwd;
		this.signal = signal;
		this.port = port;
		this.label = label;
		this.status = status;
		this.now = now;
		this.userID = userID;
		this.phone = phone;
		this.balance = balance;
	}
	@Override
	public String toString() {
		return "SimCard [id=" + id + ", gatewayNode=" + gatewayNode + ", gatewayUrl=" + gatewayUrl + ", gatewayId="
				+ gatewayId + ", auth=" + auth + ", pwd=" + pwd + ", signal=" + signal + ", port=" + port + ", label="
				+ label + ", status=" + status + ", now=" + now + ", userID=" + userID + ", phone=" + phone
				+ ", balance=" + balance + "]";
	}
	public SimCard() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGatewayNode() {
		return gatewayNode;
	}
	public void setGatewayNode(String gatewayNode) {
		this.gatewayNode = gatewayNode;
	}
	public String getGatewayUrl() {
		return gatewayUrl;
	}
	public void setGatewayUrl(String gatewayUrl) {
		this.gatewayUrl = gatewayUrl;
	}
	public Integer getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(Integer gatewayId) {
		this.gatewayId = gatewayId;
	}
	public Integer getSignal() {
		return signal;
	}
	public void setSignal(Integer signal) {
		this.signal = signal;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Byte getLabel() {
		return label;
	}
	public void setLabel(Byte label) {
		this.label = label;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public Byte getNow() {
		return now;
	}
	public void setNow(Byte now) {
		this.now = now;
	}
	public Integer getUserID() {
		return userID;
	}
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	public Long getPhone() {
		return phone;
	}
	public void setPhone(Long phone) {
		this.phone = phone;
	}
	public Float getBalance() {
		return balance;
	}
	public void setBalance(Float balance) {
		this.balance = balance;
	}
}
