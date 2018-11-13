package com.zxxkj.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Port;

public interface PortMapper {

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
	List<Port> findPortListByUserId(@Param("userId")int userId, @Param("gatewayId")int gatewayId,@Param("porttype")int porttype);
	
	/**
	 * 根据userId查询该客户的转接端口
	 * @param userId
	 * @return
	 */
	List<Integer> findTracferPortByUserId(@Param("userId") int userId, @Param("type") Integer type);
	
	/**
	 * 根据用户id删除该用户对应的端口
	 * @param map
	 */
	Integer deletePortByUserId(@Param("userId") Integer userId, @Param("gatewayId") Integer gatewayId);
	
	/**
	 * 根据网关编号查询端口
	 * @param gatewayNumbers
	 * @return
	 */
	List<Port> findPortListByGatewayId(@Param("gatewayId") int gatewayId);
	
	/**
	 * 查找网关已用的并发数量
	 * @param gatewayId
	 * @return
	 */
	Integer findHaveUsedCountByGatewayId(@Param("gatewayId") int gatewayId);
	
	/**
	 * 查询用户已有的网关类型
	 * @param userId
	 * @return
	 */
	List<Integer> findGatewayTypeBuUserId(@Param("userId") int userId);
}
