package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.VisitMapper;
import com.zxxkj.model.Visit;
import com.zxxkj.service.IVisitService;

@Service
public class VisitServiceImpl implements IVisitService {

	@Resource
	private VisitMapper visitDao;
	
	
	@Override
	public int updateGradeByCustomerId(int customerId, String visitDetails) {
		
		return visitDao.updateGradeByCustomerId(customerId, visitDetails);
	}


	@Override
	public int addVisitRecord(Visit visit) {
		
		return visitDao.addVisitRecord(visit);
	}


	@Override
	public List<Map<String, Object>> findVisitListByUserId(int userId, int start, int count) {
		
		return visitDao.findVisitListByUserId(userId, start, count);
	}


	@Override
	public int findCountByUserId(int userId) {
		
		return visitDao.findCountByUserId(userId);
	}


	@Override
	public List<Map<String, Object>> selectVisitGradeByUserId(Map<String, Object> map) {
		return visitDao.selectVisitGradeByUserId(map);
	}

    @Override
    public Integer selectVisitCountByPlanId(Map<String, Object> map) {
        return visitDao.selectVisitCountByPlanId(map);
    }

    @Override
    public List<Map<String, Object>> selectVisitListByPlanId(Map<String, Object> map) {
        return visitDao.selectVisitListByPlanId(map);
    }

}
