package com.zxxkj.model;

public class Mail {

	private Byte role;
	private String host;
	private String mail;
	private String username;
	private String password;

	public Byte getRole() {
		return role;
	}

	public void setRole(Byte role) {
		this.role = role;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Mail [role=" + role + ", host=" + host + ", mail=" + mail + ", username=" + username + ", password="
				+ password + "]";
	}

	public Mail() {
		super();
	}

	public Mail(Byte role, String host, String mail, String username, String password) {
		super();
		this.role = role;
		this.host = host;
		this.mail = mail;
		this.username = username;
		this.password = password;
	}
}
