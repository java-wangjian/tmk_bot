package com.zxxkj.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.zxxkj.dao.CustomerMapper;
import com.zxxkj.model.Customer;
import com.zxxkj.service.ICustomerService;

@Service("customerService")
public class CustomerServiceImpl implements ICustomerService {

	@Resource
	private CustomerMapper customerDao;
	
	@Override
	public Integer addCustomers(List<Customer> customerList) {
		Integer addCount = 0;
		
		try {
			addCount = customerDao.addCustomers(customerList);
		} catch (Exception e) {
			e.printStackTrace();
			addCount = null;
		}
		return addCount;
	}

	@Override
	public int addCustomer(Customer customer) {
		
		return customerDao.addCustomer(customer);
	}

	@Override
	public Integer findCustomerByUserIdAndPhone(Customer customer) {
		
		return customerDao.findCustomerByUserIdAndPhone(customer);
	}

	@Override
	public int batchDeleteUser(List<Integer> idList, int isDelete) {
		
		return customerDao.batchDeleteUser(idList, isDelete);
	}

	@Override
	public List<Customer> findCustomerByPhoneOrDateTimeRangOrGrade(long customerPhone, Date startTime, Date endTime,
			int grade, int callStatus, List<Integer> userIdList, int start, int count, int userId) {
		
		return customerDao.findCustomerByPhoneOrDateTimeRangOrGrade(customerPhone, startTime, endTime, grade,
				callStatus, userIdList, start, count, userId);
	}

	@Override
	public Integer findCountByUserId(Map<String, Object> paramMap) {
		
		return customerDao.findCountByUserId(paramMap);
	}

	@Override
	public List<Map<String,Object>> findCustomerByPhoneOrDateTimeRangOrGradeMap(Map<String, Object> paramMap) {
		
		return customerDao.findCustomerByPhoneOrDateTimeRangOrGradeMap(paramMap);
	}

	@Override
	public List<Customer> findCustomerListByUserId(int userId) {
		
		return customerDao.findCustomerListByUserId(userId);
	}

	@Override
	public int updateGradeByCustomerId(int customerId, int grade) {
		
		return customerDao.updateGradeByCustomerId(customerId, grade);
	}

	@Override
	public List<Customer> findCustomerListByUserIdAndCount(int userId, int count) {
		
		return customerDao.findCustomerListByUserIdAndCount(userId, count);
	}

	@Override
	public int updateIsPlanedIdByCustomerIdList(List<Integer> customerIdList, int isPlaned) {
		
		return customerDao.updateIsPlanedIdByCustomerIdList(customerIdList, isPlaned);
	}

	@Override
	public int updateCallCountByPhone(long customerPhone) {
		
		return customerDao.updateCallCountByPhone(customerPhone);
	}

	@Override
	public int findCallCountByPhone(long customerPhone) {
		
		return customerDao.findCallCountByPhone(customerPhone);
	}

	@Override
	public int batchUpdatePlanIdByUserId(int userId) {
		
		return customerDao.batchUpdatePlanIdByUserId(userId);
	}

	@Override
	public int findCustomerHavePlanByuserId(int userId) {
		
		return customerDao.findCustomerHavePlanByuserId(userId);
	}

	@Override
	public int updateAllCustomerIsCall(int userId, int isCall, int planId) {
		
		return customerDao.updateAllCustomerIsCall(userId, isCall, planId);
	}

	@Override
	public int batchUpdatePlanIdByUserIdAndPlanId(List<Integer> customerIdList) {
		
		return customerDao.batchUpdatePlanIdByUserIdAndPlanId(customerIdList);
	}

	@Override
	public List<Integer> findhavePlandCustomerIdByCustomerIdList(int userId, List<Integer> customerIdList) {
		
		return customerDao.findhavePlandCustomerIdByCustomerIdList(userId, customerIdList);
	}

	@Override
	public List<Integer> findCustomerListByUserIdAndCountAndPlanId(int userId, int count) {
		
		return customerDao.findCustomerListByUserIdAndCountAndPlanId(userId, count);
	}

	@Override
	public List<Integer> findInterruptedPlanCustomerIdList(int planId) {
		
		return customerDao.findInterruptedPlanCustomerIdList(planId);
	}

	@Override
	public int updateIsCallById(int customerId, int isCall) {
		
		return customerDao.updateIsCallById(customerId, isCall);
	}

	@Override
	public int updateIsCallAndPlanIdByCustomerIdList(List<Integer> customerIdList, int isCall, int planId) {
		
		return customerDao.updateIsCallAndPlanIdByCustomerIdList(customerIdList, isCall, planId);
	}

	@Override
	public List<Map<String, Object>> findCusomerByPlanIdAndUserId(Map<String, Object> paramMap) {
		
		return customerDao.findCusomerByPlanIdAndUserId(paramMap);
	}

	@Override
	public int findCustomerCountByPlanIdAndUserId(Map<String, Object> paramMap) {
		
		return customerDao.findCustomerCountByPlanIdAndUserId(paramMap);
	}

	@Override
	public List<Map<String, Object>> findCusomerByPlanIdAndUserIdForExcel(Map<String, Object> paramMap) {
		return customerDao.findCusomerByPlanIdAndUserIdForExcel(paramMap);
	}

	@Override
	public List<Map<String, Object>> findCustomersByPlanId(Map<String, Object> paramMap) {
		
		return customerDao.findCustomersByPlanId(paramMap);
	}

	@Override
	public List<Map<String, Object>> findCustomersBycustomerIdList(List<Integer> customerIdList) {
		
		return customerDao.findCustomersBycustomerIdList(customerIdList);
	}

	@Override
	public List<Integer> findNoCallCustomerIdByPlanId(int planId) {
		
		return customerDao.findNoCallCustomerIdByPlanId(planId);
	}

	@Override
	public List<Integer> findNewAddIdList(int userId, int count) {
		
		return customerDao.findNewAddIdList(userId, count);
	}

	@Override
	public Vector<Customer> findCustomerListByPlanId(int planId, int isCall) {
		
		return customerDao.findCustomerListByPlanId(planId, isCall);
	}

	@Override
	public List<Integer> findCustomerIdListByBatchNo(String batchNo) {
		
		return customerDao.findCustomerIdListByBatchNo(batchNo);
	}
}
