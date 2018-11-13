package com.zxxkj.service;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Customer;
import com.zxxkj.model.CustomerAndPlan;
import com.zxxkj.model.Plan;

public interface ICustomerAndPlanService {

	/**
	 * 根据计划Id查询客户列表以及对应通话记录
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>>findCustomerListByPlanId(Map<String, Object> paramMap);
	
	/**
	 * 根据计划Id查询客户列表的数量
	 * @param paramMap
	 * @return
	 */
	Integer findCustomerListSizeByPlanId(Map<String, Object> paramMap);
	
	/**
	 * 批量添加CustomerAndPlan
	 * @param customerAndPlanList
	 * @return
	 */
	Integer batchAddCustomerAndPlan(List<CustomerAndPlan> customerAndPlanList);

    /**
     */
	List<Map<String, Object>> findCustomerListByPlanIdByExcel(Map<String, Object> paramMap);

    /**
     * 查看手机号的拜访级别
     */
	List<Map<String, Object>> findCustomerLastVisitGrade(Map<String, Object> paramMap);
	
	/**
	 * 根据用户id和计划id查询所有客户的id列表
	 * @param userId
	 * @param planId
	 * @return
	 */
	List<Integer> findCustomerIdListByUserIdAndPlanId(int userId, int planId);
	
	/**
	 * 根据是否拨打查询计划中的相应客户量
	 * @param userId
	 * @param planId
	 * @param isCall
	 * @return
	 */
	Integer findCustomerCountByUserIdAndPlanIdAndIsCall(int userId, int planId, int isCall);
	
	/**
	 * 根据是否拨打查询计划中的相应客户量
	 * @param userId
	 * @param planId
	 * @param isCall
	 * @return
	 */
	Integer updateIsCallByPlanIdAndCustomerVector(Vector<Customer> customerList, int planId, int isCall);
	
	/**
	 * 根据是否拨打查询计划中的相应客户量
	 * @param userId
	 * @param planId
	 * @param isCall
	 * @return
	 */
	Integer updateIsCallByPlanIdAndCustomerId(List<Integer> customerIdList, int planId, int isCall);
	
	/**
	 * 查询计划中未拨打的客户
	 * @param userId
	 * @param planId
	 * @param pageNo
	 * @param num
	 * @return
	 */
	Vector<Customer> getNoCallCustomersAndUpdate(Plan plan, int num, boolean isEnd);
	
	/**
	 * 根据userId和planId查询该计划中未拨打的客户数量
	 * @param userId
	 * @param planId
	 * @return
	 */
	Integer findNoIsCallCustomerICountByUserIdAndPlanId(int userId, int planId);
	
	/**
	 * 根据客户id和计划id查询该客户是否已被拨打
	 * @param customerId
	 * @param planId
	 * @return
	 */
	Integer findIsCallByCustomerIdAndPlanId(int customerId, int planId);

    /**
     * 取消的计划在导出的时候添加没有拨打的记录
     */
    List<Map<String,Object>> selectNotCalledCustomerInfo(Map<String,Object> paramMap);
    
    /**
     * 根据isCall查询客户的数量(此方法是查询非isCall的数量)
     * @param planId
     * @param isCall
     * @return
     */
    Integer findCountByIsCall(int planId, int isCall);
    
    /**
     * 根据已经拨打的数量
     * @param planId
     * @param isCall
     * @return
     */
    Integer findHasCallCountByIsCall(int planId, int isCall);
    
    /**
     * 根据计划id查询正在拨打的客户
     * @param paramMap
     * @return
     */
    Customer findIsCallingCustomer(int planId);
    
    /**
     * 根据计划id将计划中的客户都修改为已拨打
     */
    Integer updateIsCallByPlanId(int isCall, int planId);
    
    /**
     * 根据计划id查询计划下的客户列表
     * @param planId
     * @return
     */
    List<Customer> findCustomerByPlanId(int planId, Long cutomerPhone);
    
    /**
     * 根据isCall修改isCall的值
     * @param planId
     * @param isCall 要被修改为目标值
     * @param searchIsCall 条件中的isCall
     * @return
     */
    Integer updateIsCallByIsCallAndPlanId(int planId, int isCall, int searchIsCall);
    
    /**
     * 根据isCall修改isCall
     * @param newIsCall
     * @param oldIsCall
     * @return
     */
    Integer updateIsCallByIsCall(int newIsCall, int oldIsCall);
}
