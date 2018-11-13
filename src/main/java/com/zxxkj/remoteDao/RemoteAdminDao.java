package com.zxxkj.remoteDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import com.zxxkj.model.Admin;
import com.zxxkj.util.JdbcConn;

@Component("remoteAdminDao")
public class RemoteAdminDao {
	
	/**
	 * 根据账号和密码查询数据库中是否存在该用户
	 * @param admin
	 * @return
	 */
	public Admin findAdminByAccountAndPassword(Admin admin) {
		Admin DBAdmin = null;
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String account = admin.getAccount();
		String password = admin.getPassword();
		
		String sql = "select id,status,valid_time,linkman,phone,company,sold_port,concurrent from admin_table where account=? and password=?";
		try {
			conn = JdbcConn.printProducts();
			stm = conn.prepareStatement(sql);
			stm.setString(1, account);
			stm.setString(2, password);
			rs = stm.executeQuery();
			while (rs.next()) {
				DBAdmin = new Admin();
				DBAdmin.setId(rs.getInt("id"));;
				DBAdmin.setStatus(rs.getByte("status"));
				DBAdmin.setValidTime(rs.getDate("valid_time"));
				DBAdmin.setLinkman(rs.getString("linkman"));
				DBAdmin.setPhone(rs.getLong("phone"));
				DBAdmin.setCompany(rs.getString("company"));
				DBAdmin.setSoldPort(rs.getInt("sold_port"));
				DBAdmin.setConcurrent(rs.getInt("concurrent"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcConn.close(conn, stm, rs);
		}
		
		return DBAdmin;
	}
	
	/**
	 * 根据adminId查询其账号到期时间
	 * @param adminId
	 * @return
	 */
	public Admin findAdminByAdminId(Integer adminId) {
		Admin DBAdmin = null;
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sql = "select valid_time from admin_table where id=?";
		try {
			conn = JdbcConn.printProducts();
			stm = conn.prepareStatement(sql);
			stm.setInt(1, adminId);
			rs = stm.executeQuery();
			while (rs.next()) {
				DBAdmin = new Admin();
				DBAdmin.setValidTime(rs.getDate("valid_time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcConn.close(conn, stm, rs);
		}
		
		return DBAdmin;
	}
	
	/**
	 * 根据运营者id查找运营者账号
	 * @param adminId
	 * @return
	 */
	public String findAdminAccountById(Integer adminId) {
		String account = null;
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sql = "select account from admin_table where id=?";
		try {
			conn = JdbcConn.printProducts();
			stm = conn.prepareStatement(sql);
			stm.setInt(1, adminId);
			rs = stm.executeQuery();
			while (rs.next()) {
				account = rs.getString("account");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcConn.close(conn, stm, rs);
		}
		
		return account;
	}
}
