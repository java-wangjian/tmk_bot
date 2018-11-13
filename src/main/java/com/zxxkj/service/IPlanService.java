package com.zxxkj.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Plan;

public interface IPlanService {

	/**
	 * 根据计划状态查询userId(去重后的)
	 * @param planStutas
	 * @return
	 */
	List<Plan> findUserIdByPlanStutas(int planStatus);
	
	/**
	 * 添加一个计划
	 * @param plan
	 * @return
	 */
	int addPlan(Plan plan);
	
	/**
	 * 根据userId查询该用户的计划数量
	 * @param userId
	 * @return
	 */
	List<Plan> findPlanByUserId(int userId);
	
	/**
	 * 修改用户的计划是否删除状态
	 * @param userId
	 * @param isDelete
	 * @return
	 */
	int updateIsDeleteByUserId(int userId, int isDelete);
	
	/**
	 * 根据planId修改计划的执行状态
	 * @param planId
	 * @param planStatus
	 * @return
	 */
	int updatePlanStatusByPlanId(int planStatus, Date endTime, int planId);
	
	/**
	 * 根据userId查询该用户下所有计划的是否暂停状态
	 * @param userId
	 * @return
	 */
	Integer findPlanIsStopByUserId(int userId);
	
	/**
	 * 根据planStutas查询没有完成而中断的计划
	 * @return
	 */
	List<Plan> findInterruptedPlanList();
	
	/**
	 * 根据userId查询当前执行中的计划
	 * @param userId
	 * @return
	 */
	List<Map<String,Object>> findNowPlanListByUserId(int userId, int planStatus, String searchText, int start, int pageCount,Date startTime,Date endTime);
	
	/**
	 * 根据planId修改计划的是否停止状态
	 * @param planId
	 * @param isStop
	 * @return
	 */
	int updateIsStopByPlanId(int planId, int isStop);
	
	/**
	 * 根据planId修改计划的详细信息
	 * @param plan
	 * @return
	 */
	int updatePlanInfoByPlanId(Plan plan);
	
	/**
	 * 根据planId修改计划的isDelete状态
	 * @param plan
	 * @return
	 */
	int updatePlanIsDeleteByPlanId(Plan plan);
	
	/**
	 * 查询该用户计划的客户总量
	 * @param userId
	 * @return
	 */
	int findPlanedAllCustomerCount(int userId, int planId);
	
	/**
	 * 根据userId和计划状态查询计划总数
	 * @param userId
	 * @param planStatus
	 * @param searchText
	 * @return
	 */
	int findPlanTotalListByUserId(int userId, int planStatus, String searchText ,Date startTime,Date endTime);
	
	/**
	 * 根据planId查询该计划中的客户id列表
	 * @param userId
	 * @param planId
	 * @return
	 */
	List<Integer> findCustomerIdListByPlanId(int planId, int start, int pageCount);
	
	/**
	 * 根据planId查询计划的 excuteCount字段(该计划一天之内执行的次数)
	 * @param planId
	 * @return
	 */
	Integer findExcuteCountByPlanId(int planId);
	
	/**
	 * 根据 userId 查询所有未完成的计划
	 * @param userId
	 * @return
	 */
	List<Integer> findAllPlanIdByUserId(int userId);
	
	/**
	 * 修改planIdList的计划状态
	 * @param userId
	 * @param planIdList
	 * @return
	 */
	Integer updatePlanStatusByPlanIdList(int userId, List<Integer> planIdList, int planStatus, Date endTime);
	
	/**
	 * 查询所有计划以及对应的客户id
	 * @return
	 */
	List<Map<String, Object>> findAllPlanIdAndCustomerId();
	
	/**
	 * 根据计划id查询计划的详细信息
	 * @param planId
	 * @return
	 */
	Plan findPlanInfoByPlanId(int planId);

	String selectPlanCreateAccountByPlanID(int planId);
	
	/**
	 * 根据计划id修改计划的执行时间
	 * @param planId
	 * @return
	 */
	Integer updatePlanStartTime(int planId);
	
	/**
	 * 查询该计划是否已存在（根据计划名，项目id，呼叫端口以及拨打时间）
	 * @param plan
	 * @return
	 */
	Integer findPlanIsExist(Plan plan);
	
	/**
	 * 根据用户id查询该用户未完成的计划数量
	 * @param userId
	 * @return
	 */
	List<Plan> findNoEndPlanCountByUserId(int userId);
	
	/**
	 * 根据计划id修改planTag
	 * @param planTag
	 * @param planId
	 * @return
	 */
	Integer updatePlanTag(String planTag, int planId);
	
	/**
	 * 根据计划id修改执行时间
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
	Vector<Plan> findPlanListByGatewayIdAndPort(String port, int gatewayId, int userId);
	
	Plan findPlanInfoByPlanTag(String planTag, int userId);
	
	/**
	 * 根据计划状态和计划是否开启查询userIdList
	 * @param planStatus
	 * @param isStop
	 * @return
	 */
	List<Integer> findUserIdByPlanStatusIsStop(int planStatus);
	
	/**
	 * 根据userId查询当前计划列表
	 * @param userId
	 * @param isDelete
	 * @param planStatus
	 * @return
	 */
	Vector<Plan> findCurrentPlanByUserId(int userId);
}
