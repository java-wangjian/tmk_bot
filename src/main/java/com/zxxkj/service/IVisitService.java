package com.zxxkj.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Visit;

public interface IVisitService {

	/**
	 * 根据customer修改客户的意向程度
	 * @param customerId
	 * @return
	 */
	int updateGradeByCustomerId(int customerId, String visitDetails);
	
	/**
	 * 添加拜访记录
	 * @param visit
	 * @return
	 */
	int addVisitRecord(Visit visit);
	
	/**
	 * 根据userId查询此用户的所有拜访记录
	 * @param userId
	 * @param start
	 * @param count
	 * @return
	 */
	List<Map<String, Object>> findVisitListByUserId(@Param("userId")int userId, @Param("start")int start, @Param("count")int count);
	
	/**
	 * 根据userId查询该用户拜访用户的总数
	 * @param userId
	 * @return
	 */
	int findCountByUserId(int userId);

	List<Map<String, Object>> selectVisitGradeByUserId(Map<String, Object> map);

    Integer selectVisitCountByPlanId(Map<String,Object> map);

    List<Map<String,Object>> selectVisitListByPlanId(Map<String,Object> map);
}
