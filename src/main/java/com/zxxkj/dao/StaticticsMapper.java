package com.zxxkj.dao;

import java.util.Map;

import com.zxxkj.model.Statictics;

public interface StaticticsMapper {

	/**
	 * 根据用户ID和日期,返回当日统计的详细信息
	 */
	Statictics selectDataByUserIDAndDate(Map<String, Object> map);

	/**
	 * 数据库里没有保存今天的数据,初始化数据
	 */
	Integer initDateData(Map<String, Object> map);

	/**
	 * 根据userID和date,来判断数据库中有没有今天的初始化数据
	 */
	Integer selectInitDateDate(Map<String, Object> map);

	/**
	 * 根据Map中的数据,选择性更新数据
	 */
	Integer updateStaticticsInfo(Map<String, Object> map);

}
