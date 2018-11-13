package com.zxxkj.model;

import java.util.Date;

public class Keyword {

	private int id;
	
	private String keyword;
	
	private int recordId;
	
	private int projectId;
	
	private int userId;
	
	private Date addTime;

	public Keyword() {
		super();
	}

	public Keyword(int id, String keyword, int recordId, int projectId, int userId, Date addTime) {
		super();
		this.id = id;
		this.keyword = keyword;
		this.recordId = recordId;
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getRecordId() {
		return recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	@Override
	public String toString() {
		return "Keyword [id=" + id + ", keyword=" + keyword + ", recordId=" + recordId + ", projectId=" + projectId
				+ ", userId=" + userId + ", addTime=" + addTime + "]";
	}
	
}
