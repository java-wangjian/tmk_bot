package com.zxxkj.remoteDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.zxxkj.model.RemoteGateway;
import com.zxxkj.util.JdbcConn;

@Component("remoteGatewayDao")
public class RemoteGatewayDao {

	public List<RemoteGateway> findRemoteGatewayInfoByAdminId(Integer adminId) {
		List<RemoteGateway> remoteGatewayList = new ArrayList<RemoteGateway>();
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sql = "select id,gateway_node,gateway_sn,port_on,type from gateway_table where admin_id=?";
		try {
			conn = JdbcConn.printProducts();
			stm = conn.prepareStatement(sql);
			stm.setInt(1, adminId);
			rs = stm.executeQuery();
			while (rs.next()) {
				RemoteGateway remoteGateway = new RemoteGateway();
				remoteGateway.setAdminId(adminId);
				remoteGateway.setGatewayNode(rs.getString("gateway_node"));
				remoteGateway.setGatewaySn(rs.getString("gateway_sn"));
				remoteGateway.setPortOn(rs.getString("port_on"));
				remoteGateway.setType(rs.getInt("type"));
				remoteGatewayList.add(remoteGateway);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcConn.close(conn, stm, rs);
		}
		return remoteGatewayList;
	}
	
	public RemoteGateway findRemoteGatewayInfoByAdminIdAndGatewayNode(Integer adminId, String gatewayNode) {
		RemoteGateway remoteGateway = null;
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sql = "select id,gateway_sn,port_on,type from gateway_table where admin_id=? and gateway_node=?";
		try {
			conn = JdbcConn.printProducts();
			stm = conn.prepareStatement(sql);
			stm.setInt(1, adminId);
			stm.setString(2, gatewayNode);
			rs = stm.executeQuery();
			while (rs.next()) {
				remoteGateway = new RemoteGateway();
				remoteGateway.setAdminId(adminId);
				remoteGateway.setGatewaySn(rs.getString("gateway_sn"));
				remoteGateway.setPortOn(rs.getString("port_on"));
				remoteGateway.setType(rs.getInt("type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcConn.close(conn, stm, rs);
		}
		return remoteGateway;
	}
	
	public Boolean updateSortPortByAdminId(Integer adminId, String gatewayNode, Integer haveUsedCount) {
		Connection conn = null;
		PreparedStatement stm = null;
		Integer updateCount = null;
		
		String sql = "update gateway_table set soldPort=? where admin_id=? and gateway_node=?";
		try {
			conn = JdbcConn.printProducts();
			stm = conn.prepareStatement(sql);
			stm.setInt(1, haveUsedCount);
			stm.setInt(2, adminId);
			stm.setString(3, gatewayNode);
			updateCount = stm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcConn.close(conn, stm, null);
		}
		if(updateCount == null || updateCount == 0) {
			return false;
		}
		return true;
	}
}
