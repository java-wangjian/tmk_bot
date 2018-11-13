package com.zxxkj.enums;

public enum ExceptionEnum {

	EXCEPTION_NULLEXCEPTION(1, "空指针"),
	EXCEPTION_TIMOUT(2, "连接超时")
	;
	
	private Integer code;
	private String type;

	ExceptionEnum(int code, String type) {
		this.code = code;
		this.type = type;
	}

	public int getCode() {
		return code;
	}

	public String getType() {
		return type;
	}

}
