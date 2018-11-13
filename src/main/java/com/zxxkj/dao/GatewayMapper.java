package com.zxxkj.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Gateway;

public interface GatewayMapper {

	/**
	 * 添加网关数据
	 * @param gateway
	 * @return
	 */
	Integer addGateway(Gateway gatewayList); 
	
	/**
	 * 根据网关编号(gatewayNumbers)查询该网关编号是否已经存在
	 * @param gatewayNumbers
	 * @return
	 */
	Gateway findGatewayByGatewayNumbers(@Param("gatewayNumbers")String gatewayNumbers, @Param("adminId") int adminId);
	
	/**
	 * 根据用户id查询用户的网关编号
	 * @param userId
	 * @return
	 */
	List<Gateway> findGatewayListByUserId(@Param("userId")int userId);
	
	/**
	 * 查询该代理商所有网关
	 * @return
	 */
	List<String> findAllGatewayBy();
	
	/**
	 * 根据网关编号修改url
	 * @param gatewayNumbers
	 * @return
	 */
	Integer updateGatewayByGatewayNumbers(@Param("gateway") String gatewayNumbers, @Param("url") String url,
			@Param("auth") String auth, @Param("pwd") String pwd);
	
	/**
	 * 根据网关id查询网关的信息
	 * @param gatewayId
	 * @return
	 */
	Gateway findGatewayInfoByGatewayId(@Param("gatewayId") int gatewayId);
	
	/**
	 * 根据网关url查询网关的信息
	 * @param url
	 * @return
	 */
	Gateway findGatewayInfoByURL(@Param("url") String url);
	
	/**
	 * 根据adminId删除网关
	 * @param adminId
	 * @return
	 */
	Integer deleteGatewayByAdminId(@Param("adminId") int adminId);
	
	/**
	 * 批量添加网关
	 * @param gatewayList
	 * @return
	 */
	Integer bathAddGateways(List<Gateway> gatewayList);
	
	/**
	 * 根据网关id修改网关url，账户，密码
	 * @param gatewayId
	 * @param url
	 * @param auth
	 * @param pwd
	 * @return
	 */
	Integer updateGatewayByGatewayId(@Param("gatewayId") int gatewayId, @Param("url") String url,
			@Param("auth") String auth, @Param("pwd") String pwd);
	
	/**
	 * 查询该代理商的所有网关
	 * @param adminId
	 * @return
	 */
	List<Gateway> findGatewayByAdminId(@Param("adminId") int adminId);
	
	/**
	 * 批量删除网关
	 * @param gatewayIdList
	 * @return
	 */
	Integer bathDeleteGateway(@Param("gatewayIdList") List<Integer> gatewayIdList);
	
	/**
	 * 根据网关id修改网关编号，端口号，类型
	 * @param gatewayId
	 * @param gatewayNumbers
	 * @param port_no
	 * @param type
	 * @return
	 */
	Integer updateGatewayNumAndPortAndTypeByGatewayId(@Param("gatewayId") int gatewayId, @Param("gatewayNumbers") String gatewayNumbers,
			@Param("port_no") String port_no, @Param("type") Integer type, @Param("gatewaySn") String gatewaySn);

    List<Map<String,Object>> selectUserSipData(Map<String,Object> map);
}
