package com.zxxkj.model;

public class SMS {
	private Integer id;
	private String name;
	private Integer projectId;
	private String projectName;
	private String grade;
	private Byte status;
	private String content;
	private Byte check;
	private Integer userId;
	public SMS(Integer id, String name, Integer projectId, String projectName, String grade, Byte status,
			String content, Byte check, Integer userId) {
		super();
		this.id = id;
		this.name = name;
		this.projectId = projectId;
		this.projectName = projectName;
		this.grade = grade;
		this.status = status;
		this.content = content;
		this.check = check;
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "SMS [id=" + id + ", name=" + name + ", projectId=" + projectId + ", projectName=" + projectName
				+ ", grade=" + grade + ", status=" + status + ", content=" + content + ", check=" + check + ", userId="
				+ userId + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Byte getCheck() {
		return check;
	}
	public void setCheck(Byte check) {
		this.check = check;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public SMS() {
		super();
		// TODO Auto-generated constructor stub
	}
}
