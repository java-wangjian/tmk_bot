package com.zxxkj.service;

import java.util.Map;

import com.zxxkj.model.Statictics;

public interface StaticticsService {

	/**
	 * 根据用户ID和日期,返回当日统计的详细信息
	 */
	Statictics selectDataByUserIDAndDate(Map<String, Object> map);
	
	/**
	 * 对数据统计进行处理
	 */
	Integer insertStatictics(Integer duratTotal, Integer toStaffCount, Integer callCount, Integer status,
                             Integer customer, Integer userId);

}
