package com.zxxkj.util;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import com.zxxkj.model.CallRecord;
import com.zxxkj.model.Customer;
import com.zxxkj.model.Plan;
import com.zxxkj.thread.ThreadPool;

public class CacheUtil {

	// 用户，网关，线程池。ConcurrentHashMap的key为用户id与字符串userId_的拼接，例如userId_1;Mao的key为网关名称
	public static ConcurrentHashMap<String, Map<String, ThreadPool>> userThreadPoolMap = new ConcurrentHashMap<String, Map<String, ThreadPool>>();

	// key为planTag，value为计划的对象
	public static ConcurrentHashMap<String, Plan> planMap = new ConcurrentHashMap<String, Plan>();

	// key:端口拨打线程name；value:线程id
//	public static ConcurrentHashMap<String, Long> threadNameIdMap = new ConcurrentHashMap<String, Long>();

	// key:网关+端口; value:planList
	public static ConcurrentHashMap<String, Vector<Plan>> gatewayAndPlanMap = new ConcurrentHashMap<String, Vector<Plan>>();

	// key:userId; value: map:key:planId,value:customerList
	public static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Vector<Customer>>> user_plan_customerList = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Vector<Customer>>>();

	// key:userId_planId_customerId; value:callRecord
	public static ConcurrentHashMap<String, CallRecord> user_plan_customer_callRecord = new ConcurrentHashMap<String, CallRecord>();

	// key:userId value:planVactor
	public static ConcurrentHashMap<Integer, Vector<Plan>> user_planVactor = new ConcurrentHashMap<Integer, Vector<Plan>>();

	/**
	 * 获取下一个要拨打的客户
	 * 
	 * @param userId
	 * @param planId
	 * @return
	 */
	public static Customer getNextCustomerByUserIdAndPlanId(int userId, int planId) {
		Customer customer = null;

		ConcurrentHashMap<Integer, Vector<Customer>> plan_customerList = user_plan_customerList.get(userId);
		if (plan_customerList == null) {

			return null;
		}

		Vector<Customer> customerVector = plan_customerList.get(planId);
		if (customerVector == null || customerVector.size() == 0) {

			return null;
		}

		customer = customerVector.get(0);
		return customer;
	}

	/**
	 * 删除客户
	 * 
	 * @param userId
	 * @param planId
	 * @param customerId
	 * @return boolean
	 */
	public static Boolean deleteCustomer(int userId, int planId, int customerId) {
		// 判断计划-计划客户Map是否为空
		ConcurrentHashMap<Integer, Vector<Customer>> plan_customerList = user_plan_customerList.get(userId);
		if (plan_customerList == null) {

			throw new NullPointerException();
		}
		// 判断客户列表是否为空
		Vector<Customer> customerVector = plan_customerList.get(planId);
		if (customerVector == null || customerVector.size() == 0) {

			throw new NullPointerException();
		}
		// 遍历客户列表
		Customer customerTmp = null;
		for (Customer customer : customerVector) {
			if (customerId == customer.getId()) {
				customerTmp = customer;
				customerVector.remove(customer);
				break;
			}
		}
		// 判断是否成功删除
		if (customerVector.contains(customerTmp)) {

			return false;
		}
		return true;
	}

	/**
	 * 修改计划状态
	 * 
	 * @param userId
	 * @param planId
	 * @param planStatus
	 * @return boolean
	 */
	public static boolean updatePlanStatus(int userId, int planId, int planStatus) {
		// 判断planVector是否为空
		Vector<Plan> planVector = user_planVactor.get(userId);
		if (planVector == null || planVector.size() == 0) {

			throw new NullPointerException();
		}
		// 遍历客户列表
		Plan planTmp = null;
		for (Plan plan : planVector) {
			if (planId == plan.getId()) {
				plan.setPlanStatus(planStatus);
				planTmp = plan;
				break;
			}
		}
		// 判断计划状态是否已修改
		if (planTmp.getPlanStatus() == planStatus) {

			return true;
		}
		return false;
	}

	/**
	 * 修改所有计划状态
	 * 
	 * @param userId
	 * @param planId
	 * @param planStatus
	 * @return boolean
	 */
	public static boolean updateAllPlanStatus(int userId, int planStatus) {
		// 判断planVector是否为空
		Vector<Plan> planVector = user_planVactor.get(userId);
		if (planVector == null || planVector.size() == 0) {

			return false;
		}
		// 遍历客户列表
		for (Plan plan : planVector) {
			plan.setPlanStatus(planStatus);
		}

		return true;
	}

	/**
	 * 修改计划开启还是暂停
	 * 
	 * @param userId
	 * @param planId
	 * @param planStatus
	 * @return boolean
	 */
	public static boolean updatePlanIsStop(int userId, int planId, int isStop) {
		// 判断planVector是否为空
		Vector<Plan> planVector = getUserPlanVactor(userId);
		if (planVector == null || planVector.size() == 0) {

			return false;
		}
		// 遍历客户列表
		Plan planTmp = null;
		for (Plan plan : planVector) {
			if (planId == plan.getId()) {
				plan.setIsStop(isStop);
				planTmp = plan;
				break;
			}
		}
		// 判断计划状态是否已修改
		if (planTmp.getIsStop() == isStop) {

			return true;
		}
		return false;
	}

	/**
	 * 添加计划
	 * 
	 * @param userId
	 * @param plan
	 * @return
	 */
	public static boolean addPlan(int userId, Plan plan) {
		// 判断planVector是否为空
		Vector<Plan> planVector = getUserPlanVactor(userId);
		if (planVector == null || planVector.size() == 0) {

			return false;
		}
		int oldSize = planVector.size();
		planVector.add(plan);
		if ((oldSize + 1) == planVector.size()) {

			return true;
		}
		return false;
	}

	/**
	 * 删除完成的计划
	 * 
	 * @param userId
	 * @param planId
	 * @return
	 */
	public static boolean removePlan(int userId, int planId) {
		// 判断planVector是否为空
		Vector<Plan> planVector = getUserPlanVactor(userId);
		if (planVector == null || planVector.size() == 0) {

			return false;
		}

		Plan planTmp = null;
		for (Plan plan : planVector) {
			if (plan.getId() == planId) {
				planTmp = plan;
			}
		}
		planVector.remove(planTmp);

		if (!planVector.contains(planTmp)) {

			return true;
		}
		return false;
	}

	/**
	 * 获取用户在某个计划中的客户列表
	 * @param userId
	 * @param planId
	 * @return
	 */
	public static Vector<Customer> getCustomerVector(int userId, int planId) {
		// 判断计划-计划客户Map是否为空
		ConcurrentHashMap<Integer, Vector<Customer>> plan_customerList = user_plan_customerList.get(userId);
		if (plan_customerList == null) {

			plan_customerList = new ConcurrentHashMap<Integer, Vector<Customer>>();
			user_plan_customerList.put(userId, plan_customerList);
		}
		// 判断客户列表是否为空
		Vector<Customer> customerVector = plan_customerList.get(planId);
		if (customerVector == null) {

			customerVector = new Vector<Customer>();
			plan_customerList.put(planId, customerVector);
		}
		
		return customerVector;
	}
	
	/**
	 * 从网关端口的计划列表中删除停止，取消的任务
	 * @param callPortListStr
	 * @param gatewayName
	 * @param planId
	 * @return
	 */
	public static boolean removePlanForGatewayAndPlanMap(String callPortListStr, String gatewayName, int planId) {
		boolean flag = false;
		String[] callPortArr = callPortListStr.split(",");
		
		for (int i = 0; i < callPortArr.length; i++) {
			String port = callPortArr[i];
			String gateNamePort = gatewayName + "_" + port;
			
			if(gatewayAndPlanMap.containsKey(gateNamePort)) {
				Vector<Plan> planVector = gatewayAndPlanMap.get(gateNamePort);
				Plan planTmp = null;
				for (Plan plan : planVector) {
					if(planId == plan.getId()) {
						planTmp = plan;
						break;
					}
				}
				flag = planVector.remove(planTmp);
			}
		}
		return flag;
	}
	
	/**
	 * 为网关端口的计划列表添加计划
	 * @param callPortListStr
	 * @param gatewayName
	 * @param plan
	 * @return
	 */
	public static boolean  addPlanForGatewayAndPlanMap(String callPortListStr, String gatewayName, Plan plan) {
		boolean flag = false;
		String[] callPortArr = callPortListStr.split(",");
		
		for (int i = 0; i < callPortArr.length; i++) {
			String port = callPortArr[i];
			String gateNamePort = gatewayName + "_" + port;
			
			Vector<Plan> planVector = null;
			if(gatewayAndPlanMap.containsKey(gateNamePort)) {
				planVector = gatewayAndPlanMap.get(gateNamePort);
				flag = planVector.add(plan);
			}else {
				planVector = new Vector<Plan>();
				flag = planVector.add(plan);
				gatewayAndPlanMap.put(gateNamePort,  planVector);
			}
		}
		return flag;
	}
	
	/**
	 * 获取用户的planVector
	 * @param userId
	 * @return
	 */
	public static Vector<Plan> getUserPlanVactor(int userId) {
		Vector<Plan> planVector = null;
		
		if(user_planVactor.containsKey(userId)) {
			planVector = user_planVactor.get(userId);
		}else {
			planVector = new Vector<Plan>();
			user_planVactor.put(userId, planVector);
		}
		
		return planVector;
	}
}