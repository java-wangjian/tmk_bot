package com.zxxkj.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Plan;

public interface PlanMapper {

	/**
	 * 根据计划状态查询userId(去重后的)
	 * 
	 * @param planStutas
	 * @return
	 */
	List<Plan> findUserIdByPlanStatus(@Param("planStatus") int planStatus);

	/**
	 * 添加一个计划
	 * 
	 * @param plan
	 * @return
	 */
	int addPlan(Plan plan);

	/**
	 * 根据userId查询该用户的计划数量
	 * 
	 * @param userId
	 * @return
	 */
	List<Plan> findPlanByUserId(int userId);

	/**
	 * 修改用户的计划是否删除状态
	 * 
	 * @param userId
	 * @param isDelete
	 * @return
	 */
	int updateIsDeleteByUserId(@Param("userId") int userId, @Param("isDelete") int isDelete);

	/**
	 * 根据planId修改计划的执行状态
	 * 
	 * @param planId
	 * @param planStatus
	 * @return
	 */
	int updatePlanStatusByPlanId(@Param("planStatus")int planStatus, @Param("endTime") Date endTime, @Param("planId") int planId) throws Exception;

	/**
	 * 根据userId查询该用户下所有计划的是否暂停状态
	 * 
	 * @param userId
	 * @return
	 */
	Integer findPlanIsStopByUserId(int userId);

	/**
	 * 根据planStutas查询没有完成而中断的计划
	 * 
	 * @return
	 */
	List<Plan> findInterruptedPlanList();

	/**
	 * 根据userId查询当前执行中的计划
	 * 
	 * @param userId
	 * @return
	 */
	List<Map<String, Object>> findNowPlanListByUserId(@Param("userId") int userId, @Param("planStatus") int planStatus,
			@Param("searchText") String searchText, @Param("start") int start, @Param("pageCount") int pageCount,  @Param("startTime") Date startTime,@Param("endTime")  Date endTime);

	/**
	 * 根据planId修改计划的是否停止状态
	 * 
	 * @param planId
	 * @param isStop
	 * @return
	 */
	int updateIsStopByPlanId(@Param("planId") int planId, @Param("isStop") int isStop);

	/**
	 * 根据planId修改计划的详细信息
	 * 
	 * @param plan
	 * @return
	 */
	int updatePlanInfoByPlanId(Plan plan);

	/**
	 * 根据planId修改计划的isDelete状态
	 * 
	 * @param plan
	 * @return
	 */
	int updatePlanIsDeleteByPlanId(Plan plan);

	/**
	 * 查询该用户计划的客户总量
	 * 
	 * @param userId
	 * @return
	 */
	int findPlanedAllCustomerCount(@Param("userId") int userId, @Param("planId") int planId);

	/**
	 * 根据userId和计划状态查询计划总数
	 * 
	 * @param userId
	 * @param planStatus
	 * @param searchText
	 * @return
	 */
	int findPlanTotalListByUserId(@Param("userId") int userId, @Param("planStatus") int planStatus,
			@Param("searchText") String searchText,	@Param("startTime") Date startTime,@Param("endTime") Date endTime);

	/**
	 * 根据planId查询该计划中的客户id列表
	 * 
	 * @param userId
	 * @param planId
	 * @return
	 */
	List<Integer> findCustomerIdListByPlanId(@Param("planId") int planId, @Param("start") int start, @Param("pageCount") int pageCount);

	/**
	 * 根据planId查询计划的 excuteCount字段(该计划一天之内执行的次数)
	 * 
	 * @param planId
	 * @return
	 */
	Integer findExcuteCountByPlanId(int planId);

	/**
	 * 根据 userId 查询所有未完成的计划
	 * 
	 * @param userId
	 * @return
	 */
	List<Integer> findAllPlanIdByUserId(int userId);

	/**
	 * 修改planIdList的计划状态
	 * 
	 * @param userId
	 * @param planIdList
	 * @return
	 */
	Integer updatePlanStatusByPlanIdList(@Param("userId") int userId, @Param("planIdList") List<Integer> planIdList,
			@Param("planStatus") int planStatus, @Param("endTime") Date endTime);

	/**
	 * 查询所有计划以及对应的客户id
	 * 
	 * @return
	 */
	List<Map<String, Object>> findAllPlanIdAndCustomerId();

	/**
	 * 根据计划id查询计划的详细信息
	 * 
	 * @param planId
	 * @return
	 */
	Plan findPlanInfoByPlanId(@Param("planId") int planId);

	String selectPlanCreateAccountByPlanID(int planId);

	/**
	 * 根据计划id修改计划的执行时间
	 * 
	 * @param planId
	 * @return
	 */
	Integer updatePlanStartTime(@Param("planId") int planId);

	/**
	 * 查询该计划是否已存在（根据计划名，项目id，呼叫端口以及拨打时间）
	 * 
	 * @param plan
	 * @return
	 */
	Integer findPlanIsExist(Plan plan);

	/**
	 * 根据用户id查询该用户未完成的计划数量
	 * 
	 * @param userId
	 * @return
	 */
	List<Plan> findNoEndPlanCountByUserId(@Param("userId") int userId);

	/**
	 * 根据计划id修改planTag
	 * 
	 * @param planTag
	 * @param planId
	 * @return
	 */
	Integer updatePlanTag(@Param("planTag") String planTag, @Param("planId") int planId);

	/**
	 * 根据计划id修改执行时间
	 * 
	 * @param plan
	 * @return
	 */
	Integer updateExcuteTime(Plan plan);
	
	/**
	 * 根据网关和端口查询用该端口号拨打的计划
	 * @param port
	 * @param gatewayId
	 * @return
	 */
	Vector<Plan> findPlanListByGatewayIdAndPort(@Param("port") String port, @Param("gatewayId") int gatewayId, 
			@Param("userId") int userId);
	
	Plan findPlanInfoByPlanTag(@Param("planTag") String planTag, @Param("userId") int userId);
	
	/**
	 * 根据计划状态和计划是否开启查询userIdList
	 * @param planStatus
	 * @param isStop
	 * @return
	 */
	List<Integer> findUserIdByPlanStatusIsStop(@Param("planStatus") int planStatus);

	/**
	 * 根据userId,isDelete,planStatus查询计划列表
	 * @param userId
	 * @param isDelete
	 * @param planStatus
	 * @return
	 */
	Vector<Plan> findCurrentPlanByUserId(@Param("userId") int userId, @Param("isDelete")int isDelete, 
			@Param("planStatus")int planStatus, @Param("isStop") int isStop);
}
