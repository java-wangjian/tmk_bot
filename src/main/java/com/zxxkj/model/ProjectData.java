package com.zxxkj.model;

import java.util.Date;

public class ProjectData {

	private Integer id;
	private String named;
	private Integer role;
	private Integer level;
	private String backup;
	private String keyword;
	private String fileID1;
	private String fileID2;
	private String fileID3;
    private Date datetime;
	private Integer reverse;
	private String content1;
	private String content2;
	private String content3;
	private Integer projectID;

	@Override
	public String toString() {
		return "ProjectData [id=" + id + ", named=" + named + ", role=" + role + ", level=" + level + ", backup="
				+ backup + ", keyword=" + keyword + ", fileID1=" + fileID1 + ", fileID2=" + fileID2 + ", fileID3="
				+ fileID3 + ", datetime=" + datetime + ", reverse=" + reverse + ", content1=" + content1 + ", content2="
				+ content2 + ", content3=" + content3 + ", projectID=" + projectID + "]";
	}

	public ProjectData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProjectData(Integer id, String named, Integer role, Integer level, String backup, String keyword,
			String fileID1, String fileID2, String fileID3, Date datetime, Integer reverse, String content1,
			String content2, String content3, Integer projectID) {
		super();
		this.id = id;
		this.named = named;
		this.role = role;
		this.level = level;
		this.backup = backup;
		this.keyword = keyword;
		this.fileID1 = fileID1;
		this.fileID2 = fileID2;
		this.fileID3 = fileID3;
		this.datetime = datetime;
		this.reverse = reverse;
		this.content1 = content1;
		this.content2 = content2;
		this.content3 = content3;
		this.projectID = projectID;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNamed() {
		return named;
	}

	public void setNamed(String named) {
		this.named = named;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getBackup() {
		return backup;
	}

	public void setBackup(String backup) {
		this.backup = backup;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getFileID1() {
		return fileID1;
	}

	public void setFileID1(String fileID1) {
		this.fileID1 = fileID1;
	}

	public String getFileID2() {
		return fileID2;
	}

	public void setFileID2(String fileID2) {
		this.fileID2 = fileID2;
	}

	public String getFileID3() {
		return fileID3;
	}

	public void setFileID3(String fileID3) {
		this.fileID3 = fileID3;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Integer getReverse() {
		return reverse;
	}

	public void setReverse(Integer reverse) {
		this.reverse = reverse;
	}

	public String getContent1() {
		return content1;
	}

	public void setContent1(String content1) {
		this.content1 = content1;
	}

	public String getContent2() {
		return content2;
	}

	public void setContent2(String content2) {
		this.content2 = content2;
	}

	public String getContent3() {
		return content3;
	}

	public void setContent3(String content3) {
		this.content3 = content3;
	}

	public Integer getProjectID() {
		return projectID;
	}

	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
	}

}
