package com.zxxkj.model;

import java.util.Date;

public class Port {

	private Integer id;
	
	private Integer port;
	
	private Integer gatewayId;
	
	private Integer type;//1表示拨打端口；2表示转接端口
	
	private Integer userId;

	private Date addTime;
	
	public Port() {
	}

	public Port(Integer id, Integer port, Integer gatewayId, Integer type, Integer userId, Date addTime) {
		super();
		this.id = id;
		this.port = port;
		this.gatewayId = gatewayId;
		this.type = type;
		this.userId = userId;
		this.addTime = addTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Integer gatewayId) {
		this.gatewayId = gatewayId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	@Override
	public String toString() {
		return "Port [id=" + id + ", port=" + port + ", gatewayId=" + gatewayId + ", type=" + type + ", userId="
				+ userId + ", addTime=" + addTime + "]";
	}
}
