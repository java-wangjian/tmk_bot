package com.zxxkj.model.option;

public class CdrOption {
    private Integer id;
    private String sessionId; //每次通话唯一标识
    private String callerDirection;//呼出方向
    private String callerCallerIdNumber;//被叫号码
    private String callerOrigCallerIdNumber;//主叫号码
    private Long callerCreatedTime;//通话开始时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCallerDirection() {
        return callerDirection;
    }

    public void setCallerDirection(String callerDirection) {
        this.callerDirection = callerDirection;
    }

    public String getCallerCallerIdNumber() {
        return callerCallerIdNumber;
    }

    public void setCallerCallerIdNumber(String callerCallerIdNumber) {
        this.callerCallerIdNumber = callerCallerIdNumber;
    }

    public String getCallerOrigCallerIdNumber() {
        return callerOrigCallerIdNumber;
    }

    public void setCallerOrigCallerIdNumber(String callerOrigCallerIdNumber) {
        this.callerOrigCallerIdNumber = callerOrigCallerIdNumber;
    }

    public Long getCallerCreatedTime() {
        return callerCreatedTime;
    }

    public void setCallerCreatedTime(Long callerCreatedTime) {
        this.callerCreatedTime = callerCreatedTime;
    }
}
