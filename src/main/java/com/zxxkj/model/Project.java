package com.zxxkj.model;

/*
 *@Copyright 北京知行信科技有限公司
 *Author: FuJacKing
 *@Date 2018/8/27 11:11 修改，新增话术开关
 *Description 话术模版Model类
 *
 */
import java.util.Date;

public class Project {

	private int id;
	
	private String projectName;
	
	private int switchStatus;
	
	private int userId;
	
	private Date addTime;

	public Project() {
		super();
	}

	public Project(int id, String projectName, int switchStatus, int userId, Date addTime) {
		super();
		this.id = id;
		this.projectName = projectName;
		this.switchStatus = switchStatus;
		this.userId = userId;
		this.addTime = addTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

    public int getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(int switchStatus) {
        this.switchStatus = switchStatus;
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
		return "Project [id=" + id + ", projectName=" + projectName + ", switchStatus=" + switchStatus + ", userId=" + userId
				+ ", addTime=" + addTime + "]";
	}

}
