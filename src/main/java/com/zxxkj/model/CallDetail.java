package com.zxxkj.model;

public class CallDetail {
	private Integer id;
	private Integer callrecordID;
	private String fileURL;
	private String fileWord;
	private String recordURL;
	private String recordWord;
	private Integer role;
	private String datetime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCallrecordID() {
		return callrecordID;
	}
	public void setCallrecordID(Integer callrecordID) {
		this.callrecordID = callrecordID;
	}
	public String getFileURL() {
		return fileURL;
	}
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}
	public String getFileWord() {
		return fileWord;
	}
	public void setFileWord(String fileWord) {
		this.fileWord = fileWord;
	}
	public String getRecordURL() {
		return recordURL;
	}
	public void setRecordURL(String recordURL) {
		this.recordURL = recordURL;
	}
	public String getRecordWord() {
		return recordWord;
	}
	public void setRecordWord(String recordWord) {
		this.recordWord = recordWord;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public CallDetail(Integer id, Integer callrecordID, String fileURL, String fileWord, String recordURL,
			String recordWord, Integer role, String datetime) {
		super();
		this.id = id;
		this.callrecordID = callrecordID;
		this.fileURL = fileURL;
		this.fileWord = fileWord;
		this.recordURL = recordURL;
		this.recordWord = recordWord;
		this.role = role;
		this.datetime = datetime;
	}
	public CallDetail() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "CallDetail [id=" + id + ", callrecordID=" + callrecordID + ", fileURL=" + fileURL + ", fileWord="
				+ fileWord + ", recordURL=" + recordURL + ", recordWord=" + recordWord + ", role=" + role
				+ ", datetime=" + datetime + "]";
	}
}
