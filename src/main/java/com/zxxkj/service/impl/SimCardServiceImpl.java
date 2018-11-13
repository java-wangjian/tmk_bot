package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.SimCardMapper;
import com.zxxkj.model.SimCard;
import com.zxxkj.model.User;
import com.zxxkj.service.SimCardService;

@Service
public class SimCardServiceImpl implements SimCardService {
	
	@Resource
	private SimCardMapper simCardDao;

	/**
	 * 根据用户ID,分页显示用户名下手机卡的列表
	 */
	@Override
	public Integer selectCountFromSimCardTable(User user) {
		return simCardDao.selectCountFromSimCardTable(user);
	}

	/**
	 * 根据用户的ID,查询用户名下手机卡的数量,方便分页
	 */
	@Override
	public List<SimCard> selectListFromSimCardTable(Map<String, Object> map) {
		return simCardDao.selectListFromSimCardTable(map);
	}

	/**
	 * 根据手机卡ID的集合,批量删除手机卡
	 */
	@Override
	public Integer batchDeleteSimCard(Map<String, Object> map) {
		return simCardDao.batchDeleteSimCard(map);
	}

	/**
	 * 根据手机卡的ID,修改手机卡的信息
	 */
	@Override
	public Integer updateSimCardInfo(SimCard simCard) {
		return simCardDao.updateSimCardInfo(simCard);
	}

	/**
	 * 根据手机卡的ID,调节手机卡的激活开关
	 */
	@Override
	public Integer switchSimCardInfo(SimCard simCard) {
		return simCardDao.switchSimCard(simCard);
	}

	/**
	 * 根据参数,计算模糊搜索到的符合参数的手机号数量
	 */
	@Override
	public Integer selectCountPhoneNum(Map<String, Object> map) {
		return simCardDao.selectCountPhoneNum(map);
	}

	/**
	 * 根据参数,模糊搜索手机卡,分页返回手机卡列表
	 */
	@Override
	public List<SimCard> selectListPhoneNum(Map<String, Object> map) {
		return simCardDao.selectListPhoneNum(map);
	}

	@Override
	public List<Map<String, Object>> selectPortsInfo(Map<String, Object> map) {
		return simCardDao.selectPortsInfo(map);
	}

	@Override
	public Integer insertSimCardInfo(SimCard simCard) {
		return simCardDao.insertSimCardInfo(simCard);
	}
}
