package com.zxxkj.service;

import com.zxxkj.model.Admin;

public interface IAdminService {

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
}
