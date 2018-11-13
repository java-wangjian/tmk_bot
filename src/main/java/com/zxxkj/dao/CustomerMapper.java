package com.zxxkj.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Customer;

public interface CustomerMapper {

	/**
	 * 添加多个用户信息
	 * 
	 * @param customerList
	 */
	int addCustomers(List<Customer> customerList) throws Exception;

	/**
	 * 添加单个用户
	 * 
	 * @param customer
	 * @return
	 */
	int addCustomer(Customer customer);

	/**
	 * 根据userId和手机号查询是否已有此人
	 * 
	 * @param customer
	 * @return
	 */
	Integer findCustomerByUserIdAndPhone(Customer customer);

	/**
	 * 批量删除
	 * 
	 * @param idList
	 * @return
	 */
	int batchDeleteUser(@Param("customerIdList")List<Integer> customerIdList, @Param("isDelete")int isDelete);

	/**
	 * 根据客户手机,时间范围,意向查询客户
	 * @param customerPhone
	 * @param startTime
	 * @param endTime
	 * @param grade
	 * @return
	 */
	List<Customer> findCustomerByPhoneOrDateTimeRangOrGrade(@Param("customerPhone") long customerPhone,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("grade") int grade,
			@Param("callStatus") int callStatus, List<Integer> customerList, @Param("start")int start,
			@Param("count")int count, @Param("userId")int userId);
	
	/**
	 * 根据userId查询该用户下的用户数
	 * @param userId
	 * @return
	 */
	Integer findCountByUserId(Map<String, Object> paramMap);
	
	/**
	 * 动态查询客户
	 * @param paramMap
	 * @return
	 */
	List<Map<String,Object>> findCustomerByPhoneOrDateTimeRangOrGradeMap(Map<String, Object> paramMap);
	
	/**
	 * 根据userId查询该用户下的所有客户
	 * @param userId
	 * @return
	 */
	List<Customer> findCustomerListByUserId(@Param("userId")int userId);
	
	/**
	 * 根据customerId修改客户的意向度
	 * @return
	 */
	int updateGradeByCustomerId(@Param("customerId")int customerId, @Param("grade")int grade);
	
	/**
	 * 根据userId和固定数量查询客户
	 * @param userId
	 * @param count
	 * @return
	 */
	List<Customer> findCustomerListByUserIdAndCount(@Param("userId")int userId, @Param("count")int count);
	
	/**
	 * 根据 customerIdList 修改客户的执行计划
	 * @param customerIdList
	 * @return
	 */
	int updateIsPlanedIdByCustomerIdList(@Param("customerIdList")List<Integer> customerIdList, @Param("isPlaned")int isPlaned);
	
	/**
	 * 根据手机号修改通话次数
	 * @param customerPhone
	 * @return
	 */
	int updateCallCountByPhone(@Param("customerPhone")long customerPhone);
	
	/**
	 * 根据手机号查询当前拨打的数量
	 * @param customerPhone
	 * @return
	 */
	int findCallCountByPhone(long customerPhone);
	
	/**
	 *  删除用户下的所有计划 
	 * @param userId
	 * @return
	 */
	int batchUpdatePlanIdByUserId(int userId);
	
	/**
	 * 根据userId查询该用户下的正处于计划中的客户
	 * @return
	 */
	int findCustomerHavePlanByuserId(int userId);

	/**
	 * 根据userId获取该用户下的客户总数
	 * @param userId
	 * @return
	 */
	int findCustomerNumByUserId(@Param("userId") int userId, @Param("planId")int planId);

	/**
	 * 查询该userId下的所有客户（分页）
	 * @param userId
	 * @param pageNo
	 * @param num
	 * @return
	 */
	List<Customer> findCustomers(@Param("userId") int userId,@Param("pageNo") int pageNo,@Param("num") int num, @Param("planId")int planId);
	
	/**
	 * 根据userId修改该用户下的所有客户的isCall状态
	 * @param userId
	 * @param isCall
	 * @param planId
	 * @return
	 */
	int updateAllCustomerIsCall(@Param("userId")int userId, @Param("isCall")int isCall, @Param("planId")int planId);
	
	/**
	 * 根据userId和planId修改用户下在此计划下的所有客户planId
	 * @param userId
	 * @param planId
	 * @return
	 */
	int batchUpdatePlanIdByUserIdAndPlanId(@Param("customerIdList")List<Integer> customerIdList);
	
	/**
	 * 根据customerIdList查询已经有计划的customerId
	 * @param userId
	 * @param customerIdList
	 * @return
	 */
	List<Integer> findhavePlandCustomerIdByCustomerIdList(@Param("userId")int userId, @Param("customerIdList")List<Integer> customerIdList);
	
	/**
	 * 根据userId查询未添加计划的客户
	 * @param userId
	 * @param count
	 * @return
	 */
	List<Integer> findCustomerListByUserIdAndCountAndPlanId(@Param("userId")int userId, @Param("count")int count);

	/**
	 * 根据customer修改isCall状态
	 * @param customerId
	 * @param isCall
	 * @return
	 */
	int updateIsCallById(@Param("customerId") int customerId, @Param("isCall")int isCall);
	
	/**
	 * 根据planId查询该计划下的客户id列表
	 * @param planId
	 * @return
	 */
	List<Integer> findInterruptedPlanCustomerIdList(int planId);
	
	/**
	 * 根据customerIdList修改isCall状态和planId
	 * @param customerIdList
	 * @return
	 */
	int updateIsCallAndPlanIdByCustomerIdList(@Param("customerIdList")List<Integer> customerIdList, @Param("isCall")int isCall, @Param("planId")int planId);
	
	/**
	 * 根据计划id查询计划中的客户列表 
	 * @param paramMap
	 * @return
	 */
	List<Map<String,Object>> findCusomerByPlanIdAndUserId(Map<String, Object> paramMap);
	
	/**
	 * 根据计划id查询计划中的客户列表总数 
	 * @param paramMap
	 * @return
	 */
	int findCustomerCountByPlanIdAndUserId(Map<String, Object> paramMap);


	List<Map<String, Object>> findCusomerByPlanIdAndUserIdForExcel(Map<String, Object> paramMap);
	
	/**
	 * 根据计划planId查询该计划下的客户列表
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> findCustomersByPlanId(Map<String, Object> paramMap);
	
	/**
	 * 根据客户id列表查询客户信息
	 * @param customerIdList
	 * @return
	 */
	List<Map<String, Object>> findCustomersBycustomerIdList(@Param("customerIdList")List<Integer> customerIdList);

	/**
	 * 查询计划中未拨打电话的客户id列表
	 * @param planId
	 * @return
	 */
	List<Integer> findNoCallCustomerIdByPlanId(@Param("planId")int planId);
	
	/**
	 * 查询最新添加的客户id列表
	 * @param userId
	 * @param count
	 * @return
	 */
	List<Integer> findNewAddIdList(@Param("userId")int userId, @Param("count")int count);
	
	/**
<<<<<<< HEAD
=======
	 * 根据计划id查询计划中的客户列表
	 * @param planId
	 * @return
	 */
	Vector<Customer> findCustomerListByPlanId(@Param("planId") int planId, @Param("isCall") int isCall);
	
	/**
>>>>>>> test
	 * 根据导入的批次号查询该批次导入的所有客户id
	 * @param batchNo
	 * @return
	 */
	List<Integer> findCustomerIdListByBatchNo(@Param("batchNo")String batchNo);
}
