package com.zxxkj.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.zxxkj.dao.PlanMapper;
import com.zxxkj.model.Plan;
import com.zxxkj.service.IPlanService;
import com.zxxkj.thread.GatewayCallThread;

@Service("planService")
public class PlanServiceImpl implements IPlanService {

	private static final Logger LOGGER = Logger.getLogger(PlanServiceImpl.class);
	@Resource
	private PlanMapper planDao;
	
	
	@Override
	public List<Plan> findUserIdByPlanStutas(int planStatus) {
		
		return planDao.findUserIdByPlanStatus(planStatus);
	}

	@Override
	public int addPlan(Plan plan) {
		
		return planDao.addPlan(plan);
	}

	@Override
	public List<Plan> findPlanByUserId(int userId) {
		
		return planDao.findPlanByUserId(userId);
	}

	@Override
	public int updateIsDeleteByUserId(int userId, int isDelete) {
		
		return planDao.updateIsDeleteByUserId(userId, isDelete);
	}

	@Override
	public int updatePlanStatusByPlanId(int planStatus, Date endTime, int planId) {
		Integer updateCount = null;
		
		try {
			updateCount = planDao.updatePlanStatusByPlanId(planStatus, endTime, planId);
		} catch (Exception e) {
			try {
				updateCount = planDao.updatePlanStatusByPlanId(planStatus, endTime, planId);
			} catch (Exception e1) {
				LOGGER.error("计划[ " + planId + " ]修改状态失败");
			}
		}
		
		return updateCount;
	}

	@Override
	public Integer findPlanIsStopByUserId(int userId) {
		
		return planDao.findPlanIsStopByUserId(userId);
	}

	@Override
	public List<Plan> findInterruptedPlanList() {
		
		return planDao.findInterruptedPlanList();
	}

	@Override
	public List<Map<String, Object>> findNowPlanListByUserId(int userId, int planStatus, String searchText,int start, int pageCount,Date startTime,Date endTime) {
		
		return planDao.findNowPlanListByUserId(userId, planStatus, searchText, start, pageCount,startTime,endTime);
	}

	@Override
	public int updateIsStopByPlanId(int planId, int isStop) {
		
		return planDao.updateIsStopByPlanId(planId, isStop);
	}

	@Override
	public int updatePlanInfoByPlanId(Plan plan) {
		
		return planDao.updatePlanInfoByPlanId(plan);
	}

	@Override
	public int updatePlanIsDeleteByPlanId(Plan plan) {
		
		return planDao.updatePlanIsDeleteByPlanId(plan);
	}

	@Override
	public int findPlanedAllCustomerCount(int userId, int planId) {
		
		return planDao.findPlanedAllCustomerCount(userId, planId);
	}

	@Override
	public int findPlanTotalListByUserId(int userId, int planStatus, String searchText,	Date startTime,Date endTime) {
		
		return planDao.findPlanTotalListByUserId(userId, planStatus, searchText,startTime,endTime);
	}

	@Override
	public List<Integer> findCustomerIdListByPlanId(int planId, int start, int pageCount) {
		
		return planDao.findCustomerIdListByPlanId(planId, start, pageCount);
	}

	@Override
	public Integer findExcuteCountByPlanId(int planId) {
		
		return planDao.findExcuteCountByPlanId(planId);
	}

	@Override
	public List<Integer> findAllPlanIdByUserId(int userId) {
		
		return planDao.findAllPlanIdByUserId(userId);
	}

	@Override
	public Integer updatePlanStatusByPlanIdList(int userId, List<Integer> planIdList, int planStatus, Date endTime) {
		
		return planDao.updatePlanStatusByPlanIdList(userId, planIdList, planStatus, endTime);
	}

	@Override
	public List<Map<String, Object>> findAllPlanIdAndCustomerId() {
		
		return planDao.findAllPlanIdAndCustomerId();
	}

	@Override
	public Plan findPlanInfoByPlanId(int planId) {
		
		return planDao.findPlanInfoByPlanId(planId);
	}

	@Override
	public String selectPlanCreateAccountByPlanID(int planId) {
		return planDao.selectPlanCreateAccountByPlanID(planId);
	}

	@Override
	public Integer updatePlanStartTime(int planId) {
		
		return planDao.updatePlanStartTime(planId);
	}

	@Override
	public Integer findPlanIsExist(Plan plan) {
		
		return planDao.findPlanIsExist(plan);
	}

	@Override
	public List<Plan> findNoEndPlanCountByUserId(int userId) {
		
		return planDao.findNoEndPlanCountByUserId(userId);
	}

	@Override
	public Integer updatePlanTag(String planTag, int planId) {
		
		return planDao.updatePlanTag(planTag, planId);
	}

	@Override
	public Integer updateExcuteTime(Plan plan) {
		
		return planDao.updateExcuteTime(plan);
	}

	@Override
	public Vector<Plan> findPlanListByGatewayIdAndPort(String port, int gatewayId, int userId) {
		
		return planDao.findPlanListByGatewayIdAndPort(port, gatewayId, userId);
	}

	@Override
	public Plan findPlanInfoByPlanTag(String planTag, int userId) {
		
		return planDao.findPlanInfoByPlanTag(planTag, userId);
	}

	@Override
	public List<Integer> findUserIdByPlanStatusIsStop(int planStatus) {
		
		return planDao.findUserIdByPlanStatusIsStop(planStatus);
	}

	@Override
	public Vector<Plan> findCurrentPlanByUserId(int userId) {
		
		return planDao.findCurrentPlanByUserId(userId, 0, 1, 0);
	}
}
