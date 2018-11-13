package com.zxxkj.model;

import java.util.Date;

public class Record {

	private int id;
	
	private String url;
	
	private int projectId;
	
	private int userId;
	
	
	private Date addTime;

	public Record() {
		super();
	}

	public Record(int id, String url, int projectId, int userId, Date addTime) {
		super();
		this.id = id;
		this.url = url;
		this.projectId = projectId;
		this.userId = userId;
		this.addTime = addTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "Record [id=" + id + ", url=" + url + ", projectId=" + projectId + ", addTime=" + addTime + "]";
	}
	
}
