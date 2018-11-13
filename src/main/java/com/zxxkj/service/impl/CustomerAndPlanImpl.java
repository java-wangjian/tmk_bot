package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.zxxkj.dao.CustomerAndPlanMapper;
import com.zxxkj.model.Customer;
import com.zxxkj.model.CustomerAndPlan;
import com.zxxkj.model.Plan;
import com.zxxkj.service.ICustomerAndPlanService;

@Service("customerAndPlanService")
public class CustomerAndPlanImpl implements ICustomerAndPlanService{

	private static final Logger LOGGER = Logger.getLogger(CustomerAndPlanImpl.class);
	@Resource
    private CustomerAndPlanMapper customerAndPlanDao;
	
	
	@Override
	public List<Map<String, Object>> findCustomerListByPlanId(Map<String, Object> paramMap) {
		
		return customerAndPlanDao.findCustomerListByPlanId(paramMap);
	}


	@Override
	public Integer findCustomerListSizeByPlanId(Map<String, Object> paramMap) {
		
		return customerAndPlanDao.findCustomerListSizeByPlanId(paramMap);
	}


	@Override
	public Integer batchAddCustomerAndPlan(List<CustomerAndPlan> customerAndPlanList) {
		
		return customerAndPlanDao.batchAddCustomerAndPlan(customerAndPlanList);
	}


	@Override
	public List<Map<String, Object>> findCustomerListByPlanIdByExcel(Map<String, Object> paramMap) {
		return customerAndPlanDao.findCustomerListByPlanIdByExcel(paramMap);
	}


	@Override
	public List<Map<String, Object>> findCustomerLastVisitGrade(Map<String, Object> paramMap) {
		return customerAndPlanDao.findCustomerLastVisitGrade(paramMap);
	}


	@Override
	public List<Integer> findCustomerIdListByUserIdAndPlanId(int userId, int planId) {
		
		return customerAndPlanDao.findCustomerIdListByUserIdAndPlanId(userId, planId);
	}


	@Override
	public Integer findCustomerCountByUserIdAndPlanIdAndIsCall(int userId, int planId, int isCall) {
		
		return customerAndPlanDao.findCustomerCountByUserIdAndPlanIdAndIsCall(userId, planId, isCall);
	}


	@Override
	public Integer updateIsCallByPlanIdAndCustomerVector(Vector<Customer> customerList, int planId, int isCall) {
		
		return customerAndPlanDao.updateIsCallByPlanIdAndCustomerVector(customerList, planId, isCall);
	}


	@Override
	public Vector<Customer> getNoCallCustomersAndUpdate(Plan plan, int num, boolean isEnd) {
		Vector<Customer> customerVector = null;
		
		synchronized (plan) {
			Integer planId = plan.getId();
			if(!isEnd) {
				Integer userId = plan.getUserId();
				customerVector = customerAndPlanDao.findNoCallCustomers(userId, planId, num);
				if(customerVector != null && customerVector.size() > 0) {
					Integer updateCount = updateIsCallByPlanIdAndCustomerVector(customerVector, planId, 2);
					if(updateCount == customerVector.size()) {
						LOGGER.info("修改了 " + updateCount + " 条客户的拨打状态");
					}
				}
			}
			LOGGER.info("id为[ " + planId + " ]的计划的isEnd值为 " + isEnd);
		}
		
		return customerVector;
	}


	@Override
	public Integer findNoIsCallCustomerICountByUserIdAndPlanId(int userId, int planId) {
		
		return customerAndPlanDao.findNoIsCallCustomerICountByUserIdAndPlanId(userId, planId);
	}


	@Override
	public Integer findIsCallByCustomerIdAndPlanId(int customerId, int planId) {
		
		return customerAndPlanDao.findIsCallByCustomerIdAndPlanId(customerId, planId);
	}

    @Override
    public List<Map<String, Object>> selectNotCalledCustomerInfo(Map<String, Object> paramMap) {
	    return customerAndPlanDao.selectNotCalledCustomerInfo(paramMap);
    }


	@Override
	public Integer findCountByIsCall(int planId, int isCall) {
		
		return customerAndPlanDao.findCountByIsCall(planId, isCall);
	}


	@Override
	public Customer findIsCallingCustomer(int planId) {
		
		return customerAndPlanDao.findIsCallingCustomer(planId);
	}


	@Override
	public Integer updateIsCallByPlanId(int isCall, int planId) {
		
		return customerAndPlanDao.updateIsCallByPlanId(isCall, planId);
	}


	@Override
	public List<Customer> findCustomerByPlanId(int planId, Long cutomerPhone) {
		
		return customerAndPlanDao.findCustomerByPlanId(planId, cutomerPhone);
	}


	@Override
	public Integer updateIsCallByPlanIdAndCustomerId(List<Integer> customerIdList, int planId, int isCall) {
		
		return customerAndPlanDao.updateIsCallByPlanIdAndCustomerId(customerIdList, planId, isCall);
	}


	@Override
	public Integer updateIsCallByIsCallAndPlanId(int planId, int isCall, int searchIsCall) {
		
		return customerAndPlanDao.updateIsCallByIsCallAndPlanId(planId, isCall, searchIsCall);
	}


	@Override
	public Integer findHasCallCountByIsCall(int planId, int isCall) {
		
		return customerAndPlanDao.findHasCallCountByIsCall(planId, isCall);
	}


	@Override
	public Integer updateIsCallByIsCall(int newIsCall, int oldIsCall) {
		
		return customerAndPlanDao.updateIsCallByIsCall(newIsCall, oldIsCall);
	}
}
