package com.zxxkj.model;

public class RemoteGateway {
	
	private Integer id;
	
	private String gatewayNode;
	
	private String gatewaySn;
	
	private String portOn;
	
	private Integer adminId;
	
	private Integer type;
	
	
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
	public String getGatewaySn() {
		return gatewaySn;
	}
	public void setGatewaySn(String gatewaySn) {
		this.gatewaySn = gatewaySn;
	}
	public String getPortOn() {
		return portOn;
	}
	public void setPortOn(String portOn) {
		this.portOn = portOn;
	}
	public Integer getAdminId() {
		return adminId;
	}
	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "RemoteGateway [id=" + id + ", gatewayNode=" + gatewayNode + ", gatewaySn=" + gatewaySn + ", portOn="
				+ portOn + ", adminId=" + adminId + ", type=" + type + "]";
	}
	public RemoteGateway() {
		super();
	}
	
	public RemoteGateway(Integer id, String gatewayNode, String gatewaySn, String portOn, Integer adminId) {
		super();
		this.id = id;
		this.gatewayNode = gatewayNode;
		this.gatewaySn = gatewaySn;
		this.portOn = portOn;
		this.adminId = adminId;
	}
}
