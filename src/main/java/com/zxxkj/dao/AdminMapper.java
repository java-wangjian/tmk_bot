package com.zxxkj.dao;

import com.zxxkj.model.Admin;

public interface AdminMapper {

	/**
	 * 添加后台管理员
	 * @param admin
	 */
	void addAdmin(Admin admin);
	
	/**
	 * 根据账号和密码查询是否存在此用户
	 * @param admin
	 * @return
	 */
	Admin findUserByAccountAndPassword(Admin admin);
	
	/**
	 * 修改密码
	 * @param adminId
	 * @param password
	 * @return
	 */
	int updatePassword(int adminId, String password);
	
}
