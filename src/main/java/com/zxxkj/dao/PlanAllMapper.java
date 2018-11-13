package com.zxxkj.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PlanAllMapper {




	/**
	 * 根据userId查询当前执行中的计划
	 *
	 *
	 * @return
	 */
	List<Map<String, Object>> findNowPlansList(@Param("planStatus") int planStatus,
                                                      @Param("searchText") String searchText, @Param("start") int start, @Param("pageCount") int pageCount, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("adminId") int adminId);



	/**
	 * 根据userId和计划状态查询计划总数
	 * @param
	 * @param planStatus
	 * @param searchText
	 * @return
	 */
	int findPlansTotalList(@Param("planStatus") int planStatus,
                                  @Param("searchText") String searchText, @Param("startTime") Date startTime, @Param("endTime") Date endTime,@Param("adminId") int adminId);
















}
