package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.GatewayMapper;
import com.zxxkj.model.Gateway;
import com.zxxkj.service.IGatewayService;

@Service("gatewayService")
public class GatewayServiceImpl implements IGatewayService {

	@Resource
	private GatewayMapper garewayDao;
	
	@Override
	public Integer addGateway(Gateway gateway) {
		
		return garewayDao.addGateway(gateway);
	}

	@Override
	public Gateway findGatewayByGatewayNumbers(String gatewayNumbers, int adminId) {
		
		return garewayDao.findGatewayByGatewayNumbers(gatewayNumbers, adminId);
	}

	@Override
	public List<Gateway> findGatewayListByUserId(int userId) {
		
		return garewayDao.findGatewayListByUserId(userId);
	}

	@Override
	public List<String> findAllGatewayBy() {
		
		return garewayDao.findAllGatewayBy();
	}

	@Override
	public Integer updateGatewayByGatewayNumbers(String gatewayNumbers, String url, String auth, String pwd) {
		
		return garewayDao.updateGatewayByGatewayNumbers(gatewayNumbers, url, auth, pwd);
	}

	@Override
	public Gateway findGatewayInfoByGatewayId(int gatewayId) {
		
		return garewayDao.findGatewayInfoByGatewayId(gatewayId);
	}

	@Override
	public Gateway findGatewayInfoByURL(String url) {
		
		return garewayDao.findGatewayInfoByURL(url);
	}

	@Override
	public Integer deleteGatewayByAdminId(int adminId) {
		
		return garewayDao.deleteGatewayByAdminId(adminId);
	}

	@Override
	public Integer bathAddGateways(List<Gateway> gatewayList) {
		
		return garewayDao.bathAddGateways(gatewayList);
	}

	@Override
	public Integer updateGatewayByGatewayId(int gatewayId, String url, String auth, String pwd) {
		
		return garewayDao.updateGatewayByGatewayId(gatewayId, url, auth, pwd);
	}

	@Override
	public List<Gateway> findGatewayByAdminId(int adminId) {
		
		return garewayDao.findGatewayByAdminId(adminId);
	}

	@Override
	public Integer bathDeleteGateway(List<Integer> gatewayIdList) {
		
		return garewayDao.bathDeleteGateway(gatewayIdList);
	}

	@Override
	public Integer updateGatewayNumAndPortAndTypeByGatewayId(int gatewayId, String gatewayNumbers, String port_no,
			Integer type, String gatewaySn) {
		
		return garewayDao.updateGatewayNumAndPortAndTypeByGatewayId(gatewayId, gatewayNumbers, port_no, type, gatewaySn);
	}

    @Override
    public List<Map<String, Object>> selectUserSipData(Map<String, Object> map) {
        return garewayDao.selectUserSipData(map);
    }
}
