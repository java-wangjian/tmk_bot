package com.zxxkj.service;

import com.zxxkj.model.Mail;

import java.util.List;
import java.util.Map;

public interface MailService {

	/**
	 * 从数据库里取出发邮件的配置
	 */
	Mail selectRole0Info(Map<String, Object> map);

	/**
	 * 查看所有市场部人邮箱
	 */
	List<Mail> selectSalesInfoList(Map<String, Object> map);

}
