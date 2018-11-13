package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.PortMapper;
import com.zxxkj.model.Port;
import com.zxxkj.service.IPortService;

@Service("portService")
public class PortServiceImpl implements IPortService{

	@Resource
	private PortMapper portDao;

	@Override
	public Integer batchAddPort(List<Port> portList) {
		
		return portDao.batchAddPort(portList);
	}

	@Override
	public List<Port> findPortListByUserId(int userId, int gatewayId,Integer porttype) {
		
		return portDao.findPortListByUserId(userId, gatewayId,porttype);
	}

	@Override
	public List<Integer> findTracferPortByUserId(int userId, Integer type) {
		
		return portDao.findTracferPortByUserId(userId, type);
	}

	@Override
	public Integer deletePortByUserId(Integer userId, Integer gatewayId) {
		
		return portDao.deletePortByUserId(userId, gatewayId);
	}

	@Override
	public List<Port> findPortListByGatewayId(int gatewayId) {
		
		return portDao.findPortListByGatewayId(gatewayId);
	}

	@Override
	public Integer findHaveUsedCountByGatewayId(int gatewayId) {
		
		return portDao.findHaveUsedCountByGatewayId(gatewayId);
	}

	@Override
	public List<Integer> findGatewayTypeBuUserId(int userId) {
		
		return portDao.findGatewayTypeBuUserId(userId);
	}
}
