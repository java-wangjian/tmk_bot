package com.zxxkj.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.AdminMapper;
import com.zxxkj.model.Admin;
import com.zxxkj.service.IAdminService;

@Service
public class AdminServiceImpl implements IAdminService{

	@Resource
    private AdminMapper adminDao;
    
	@Override
	public void addAdmin(Admin admin) {
		
		adminDao.addAdmin(admin);
	}

	@Override
	public Admin findUserByAccountAndPassword(Admin admin) {
		
		return adminDao.findUserByAccountAndPassword(admin);
	}

}
