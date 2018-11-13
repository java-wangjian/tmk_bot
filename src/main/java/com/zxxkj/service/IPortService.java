package com.zxxkj.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Port;

public interface IPortService {

	/**
	 * 批量加入端口
	 * @param portList
	 * @return
	 */
	Integer batchAddPort(List<Port> portList);
	
	/**
	 * 根据用户id查询 端口号
	 * @param userId
	 * @return
	 */
	List<Port> findPortListByUserId(int userId, int gatewayId,Integer porttype);
	
	/**
	 * 根据用户id查询用户的转接端口
	 * @param userId
	 * @return
	 */
	List<Integer> findTracferPortByUserId(int userId, Integer type);
	
	/**
	 * 根据用户id删除该用户对应的端口
	 * @param userId
	 */
	Integer deletePortByUserId(Integer userId, Integer gatewayId);
	
	/**
	 * 根据网关编号查询端口
	 * @param gatewayNumbers
	 * @return
	 */
	List<Port> findPortListByGatewayId(int gatewayId);
	
	/**
	 * 查找网关已用的并发数量
	 * @param gatewayId
	 * @return
	 */
	Integer findHaveUsedCountByGatewayId(int gatewayId);
	
	/**
	 * 查询用户已有的网关类型
	 * @param userId
	 * @return
	 */
	List<Integer> findGatewayTypeBuUserId(int userId);
}
