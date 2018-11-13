package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.CallDetailMapper;
import com.zxxkj.model.CallDetail;
import com.zxxkj.service.CallDetailService;

@Service
public class CallDetailServiceImpl implements CallDetailService {
	
	@Resource
	private CallDetailMapper callDetailDao;

	@Override
	public Integer insertCallDetailData(Map<String, Object> map) {
		return callDetailDao.insertCallDetailData(map);
	}

	@Override
	public List<CallDetail> selectCallRecordDetailByCallRecordID(Map<String, Object> map) {
		return callDetailDao.selectCallRecordDetailByCallRecordID(map);
	}

	@Override
	public void updateFileUrlByCallDetailId(Map<String, Object> map) {
		callDetailDao.updateFileUrlByCallDetailId(map);
	}

}
