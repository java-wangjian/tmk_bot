package com.zxxkj.model;

public class RecordHistory {

	private Integer id;
	private Integer count;
	private String content;
	private String time;
	private Integer userID;
	private String dateInterval;
	private String levels;
	private String duarts;
	private Integer minDuart;
	private String projects;
	private String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public String getDateInterval() {
		return dateInterval;
	}

	public void setDateInterval(String dateInterval) {
		this.dateInterval = dateInterval;
	}

	public String getLevels() {
		return levels;
	}

	public void setLevels(String levels) {
		this.levels = levels;
	}

	public String getDuarts() {
		return duarts;
	}

	public void setDuarts(String duarts) {
		this.duarts = duarts;
	}

	public Integer getMinDuart() {
		return minDuart;
	}

	public void setMinDuart(Integer minDuart) {
		this.minDuart = minDuart;
	}

	public String getProjects() {
		return projects;
	}

	public void setProjects(String projects) {
		this.projects = projects;
	}

	public RecordHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecordHistory(Integer id, Integer count, String content, String time, Integer userID, String dateInterval,
			String levels, String duarts, Integer minDuart, String projects, String account) {
		super();
		this.id = id;
		this.count = count;
		this.content = content;
		this.time = time;
		this.userID = userID;
		this.dateInterval = dateInterval;
		this.levels = levels;
		this.duarts = duarts;
		this.minDuart = minDuart;
		this.projects = projects;
		this.account = account;
	}
	
}
