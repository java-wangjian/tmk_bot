package com.zxxkj.model;

public class SysMsg {
	
	private Integer id;
	private Integer pointID;
	private Integer status;
	private String title;
	private Integer msgType;
	private Integer isHiden;
	private Integer msgObj;
	private Integer userID;
	private String content;
	private String createTime;

	public Integer getPointID() {
		return pointID;
	}
	public void setPointID(Integer pointID) {
		this.pointID = pointID;
	}
	public Integer getUserID() {
		return userID;
	}
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMsgType() {
		return msgType;
	}
	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}
	public Integer getIsHiden() {
		return isHiden;
	}
	public void setIsHiden(Integer isHiden) {
		this.isHiden = isHiden;
	}
	public Integer getMsgObj() {
		return msgObj;
	}
	public void setMsgObj(Integer msgObj) {
		this.msgObj = msgObj;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "SysMsg [id=" + id + ", pointID=" + pointID + ", status=" + status + ", title=" + title + ", msgType="
				+ msgType + ", isHiden=" + isHiden + ", msgObj=" + msgObj + ", userID=" + userID + ", content="
				+ content + ", createTime=" + createTime + "]";
	}
	public SysMsg(Integer id, Integer pointID, Integer status, String title, Integer msgType, Integer isHiden,
			Integer msgObj, Integer userID, String content, String createTime) {
		super();
		this.id = id;
		this.pointID = pointID;
		this.status = status;
		this.title = title;
		this.msgType = msgType;
		this.isHiden = isHiden;
		this.msgObj = msgObj;
		this.userID = userID;
		this.content = content;
		this.createTime = createTime;
	}
	public SysMsg() {
		super();
		// TODO Auto-generated constructor stub
	}
}
