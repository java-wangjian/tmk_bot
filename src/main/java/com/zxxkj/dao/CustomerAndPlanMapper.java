package com.zxxkj.dao;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Customer;
import com.zxxkj.model.CustomerAndPlan;

public interface  CustomerAndPlanMapper {

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

	List<Map<String, Object>> findCustomerListByPlanIdByExcel(Map<String, Object> paramMap);

	List<Map<String, Object>> findCustomerLastVisitGrade(Map<String, Object> paramMap);
	
	/**
	 * 根据用户id和计划id查询所有客户的id列表
	 * @param userId
	 * @param planId
	 * @return
	 */
	List<Integer> findCustomerIdListByUserIdAndPlanId(@Param("userId") int userId, @Param("planId") int planId);
	
	/**
	 * 查询计划中未拨打的客户
	 * @param userId
	 * @param planId
	 * @param pageNo
	 * @param num
	 * @return
	 */
	Vector<Customer> findNoCallCustomers(@Param("userId")int userId, @Param("planId")int planId,
			@Param("num")int num);
	
	/**
	 * 修改客户是否已被拨打
	 * @param userId
	 * @param planId
	 * @param isCall
	 * @return
	 */
	Integer updateIsCallByPlanIdAndCustomerId(@Param("customerIdList")List<Integer> customerIdList, @Param("planId")int planId, @Param("isCall")int isCall);
	
	/**
	 * 修改客户是否已被拨打
	 * @param userId
	 * @param planId
	 * @param isCall
	 * @return
	 */
	Integer updateIsCallByPlanIdAndCustomerVector(@Param("customerVector")Vector<Customer> customerVector, @Param("planId")int planId, @Param("isCall")int isCall);
	
	/**
	 * 根据是否拨打查询计划中的相应客户量
	 * @param userId
	 * @param planId
	 * @param isCall
	 * @return
	 */
	Integer findCustomerCountByUserIdAndPlanIdAndIsCall(@Param("userId")int userId, @Param("planId")int planId, @Param("isCall")int isCall);
	
	/**
	 * 根据userId和planId查询该计划中未拨打的客户数量
	 * @param userId
	 * @param planId
	 * @return
	 */
	Integer findNoIsCallCustomerICountByUserIdAndPlanId(@Param("userId")int userId, @Param("planId")int planId);
	
	/**
	 * 根据客户id和计划id查询该客户是否已被拨打
	 * @param customerId
	 * @param planId
	 * @return
	 */
	Integer findIsCallByCustomerIdAndPlanId(@Param("customerId")int customerId, @Param("planId")int planId);

    List<Map<String,Object>> selectNotCalledCustomerInfo(Map<String,Object> paramMap);
    
    /**
     * 根据isCall查询客户的数量
     * @param planId
     * @param isCall
     * @return
     */
    Integer findCountByIsCall(@Param("planId") int planId, @Param("isCall") int isCall);
    
    /**
     * 根据计划id查询正在拨打的客户
     * @param paramMap
     * @return
     */
    Customer findIsCallingCustomer(@Param("planId")int planId);
    
    /**
     * 根据计划id将计划中的客户都修改为已拨打
     */
    Integer updateIsCallByPlanId(@Param("isCall")int isCall,@Param("planId")int planId);
    
    /**
     * 根据计划id查询计划下的客户列表
     * @param planId
     * @return
     */
    List<Customer> findCustomerByPlanId(@Param("planId") int planId, @Param("cutomerPhone")Long cutomerPhone);

    /**
     * 根据isCall和计划id修改isCall的值
     * @param planId
     * @param isCall 被修改的isCall
     * @param searchIsCall 条件中的isCall
     * @return
     */
    Integer updateIsCallByIsCallAndPlanId(@Param("planId") int planId, @Param("isCall") int isCall, @Param("searchIsCall") int searchIsCall);

    /**
     * 根据已经拨打的数量
     * @param planId
     * @param isCall
     * @return
     */
    Integer findHasCallCountByIsCall(@Param("planId")int planId, @Param("isCall") int isCall);
    
    /**
     * 根据isCall修改isCall
     * @param newIsCall
     * @param oldIsCall
     * @return
     */
    Integer updateIsCallByIsCall(@Param("newIsCall") int newIsCall, @Param("oldIsCall") int oldIsCall);
}
