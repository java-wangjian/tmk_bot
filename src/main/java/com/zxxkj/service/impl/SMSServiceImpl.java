package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.SMSMapper;
import com.zxxkj.model.SMS;
import com.zxxkj.service.SMSService;

@Service
public class SMSServiceImpl implements SMSService {
	
	@Resource
	private SMSMapper smsMapper;

	@Override
	public List<SMS> selectSmsListByUserID(Map<String, Object> map) {
		return smsMapper.selectSmsListByUserID(map);
	}

	@Override
	public Integer insertSmsTemplet(Map<String, Object> map) {
		return smsMapper.insertSmsTemplet(map);
	}

	@Override
	public Integer deleteSmsTemplet(Map<String, Object> map) {
		return smsMapper.deleteSmsTemplet(map);
	}

	@Override
	public Integer updateSmsTemplet(Map<String, Object> map) {
		return smsMapper.updateSmsTemplet(map);
	}

	@Override
	public Integer switchSmsTemplet(Map<String, Object> map) {
		return smsMapper.switchSmsTemplet(map);
	}

	@Override
	public Integer selectSmsCountByUserIdAndCondition(Map<String, Object> map) {
		return smsMapper.selectSmsCountByUserIdAndCondition(map);
	}

	@Override
	public SMS selectSmsModelByUserIdAndProjectIdAndGrade(Map<String, Object> map) {
		return smsMapper.selectSmsModelByUserIdAndProjectIdAndGrade(map);
	}

	@Override
	public Integer insertSmsLogModel(Map<String, Object> map) {
		return smsMapper.insertSmsLogModel(map);
	}

	@Override
	public SMS getSmsContentByProjectIdAndGrade(Map<String, Object> map) {
		return smsMapper.getSmsContentByProjectIdAndGrade(map);
	}

}
