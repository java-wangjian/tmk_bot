package com.zxxkj.model;

import java.util.Date;

public class Gateway {

	private Integer id;
	
	private String gatewayNumbers;
	
	private String url;
	
	private String port_no;
	
	private Integer adminId;
	
	private String auth;
	
	private String pwd;
	
	private Integer type;//1表示插卡网关;2表示sip线路
	
	private String gateway_sn;
	
	private Date addTime;
    //SIP线路剩余时常
	private Integer leftover;
	//单价
	private Double unitPrice;
	//余额
	private Double balanceMoney;

    public Integer getLeftover() {
        return leftover;
    }

    public void setLeftover(Integer leftover) {
        this.leftover = leftover;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(Double balanceMoney) {
        this.balanceMoney = balanceMoney;
    }

    public Gateway() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGatewayNumbers() {
		return gatewayNumbers;
	}

	public void setGatewayNumbers(String gatewayNumbers) {
		this.gatewayNumbers = gatewayNumbers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPort_no() {
		return port_no;
	}

	public void setPort_no(String port_no) {
		this.port_no = port_no;
	}

	public String getGateway_sn() {
		return gateway_sn;
	}

	public void setGateway_sn(String gateway_sn) {
		this.gateway_sn = gateway_sn;
	}

	@Override
	public String toString() {
		return "Gateway [id=" + id + ", gatewayNumbers=" + gatewayNumbers + ", url=" + url + ", port_no=" + port_no
				+ ", adminId=" + adminId + ", auth=" + auth + ", pwd=" + pwd + ", type=" + type + ", gateway_sn="
				+ gateway_sn + ", addTime=" + addTime + "]";
	}

}
