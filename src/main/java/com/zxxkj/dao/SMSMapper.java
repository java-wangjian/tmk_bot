package com.zxxkj.dao;

import java.util.List;
import java.util.Map;

import com.zxxkj.model.SMS;

public interface SMSMapper {

	/**
	 * 查询 短信模板列表
	 */
	List<SMS> selectSmsListByUserID(Map<String, Object> map);

	/**
	 * 插入 短信模板
	 */
	Integer insertSmsTemplet(Map<String, Object> map);

	/**
	 * 删除 短信模板
	 */
	Integer deleteSmsTemplet(Map<String, Object> map);

	/**
	 * 更新 短信模板
	 */
	Integer updateSmsTemplet(Map<String, Object> map);

	/**
	 * 开关 短信模板
	 */
	Integer switchSmsTemplet(Map<String, Object> map);

	/**
	 * 根据用户ID和条件查找符合要求条数
	 */
	Integer selectSmsCountByUserIdAndCondition(Map<String, Object> map);

	/**
	 * 根据用户ID和项目ID和客户级别找到唯一短信
	 */
	SMS selectSmsModelByUserIdAndProjectIdAndGrade(Map<String, Object> map);

	/**
	 * 插入短信的发送记录
	 */
	Integer insertSmsLogModel(Map<String, Object> map);

	/**
	 * 获取短信的内容
	 */
	SMS getSmsContentByProjectIdAndGrade(Map<String, Object> map);

}
