package com.zxxkj.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.UserMapper;
import com.zxxkj.model.User;
import com.zxxkj.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{

	@Resource
    private UserMapper userDao;
	
	/**
	 * 根据用户的账号和密码,查找用户是否存在或信息是否正确
	 */
	@Override
	public User findUserInfoByAccAndPwd(User user) {
		return userDao.findUserInfoByAccAndPwd(user);
	}

	/**
	 * 根据客服的ID,查看客户的详细信息
	 */
	@Override
	public User findUserInfoByID(User user) {
		return userDao.findUserInfoByID(user);
	}

	/**
	 * 用户ID做为客服的父ID,分页查看用户名下客服
	 */
	@Override
	public List<User> selectStaffListByParentID(Map<String , Object> map) {
		return userDao.selectStaffListByParentID(map);
	}

	@Override
	public List<User> selectStaffAllByParentID(Map<String, Object> map) {
		return userDao.selectStaffAllByParentID(map);
	}

	/**
	 * 把用户ID做为客服的父ID,计算用户名下客服数量
	 */
	@Override
	public Integer selectStaffCountByParentID(int userID) {
		return userDao.selectStaffCountByParentID(userID);
	}

	/**
	 * 为平台用户添加一个客服
	 */
	@Override
	public Integer insertStaffByUser(User user) {
		return userDao.insertStaffByUser(user);
	}

	/**
	 * 根据客服ID的集体,批量删除客服
	 */
	@Override
	public Integer deleteStaffsByIDs(Map<String, Object> map) {
		return userDao.deleteStaffsByIDs(map);
	}

	/**
	 * 根据客服的ID,修改客服的开关
	 */
	@Override
	public Integer editStaffSwitch(Map<String, Object> map) {
		return userDao.editStaffSwitch(map);
	}

	/**
	 * 根据人工客服的ID,修改客服的信息
	 */
	@Override
	public Integer editUserInfoByID(User user) {
		return userDao.editUserInfoByID(user);
	}

	/**
	 * 根据用户名和旧密码,设置新密码
	 */
	@Override
	public Integer updateUserPassword(Map<String, Object> map) {
		return userDao.updateUserPassword(map);
	}

	/**
	 * 通过用户ID修改用户的状态
	 */
	@Override
	public Integer updateUserStatusByID(Map<String, Object> map) {
		return userDao.updateUserStatusByID(map);
	}

	/**
	 * 用户账户名查库,防止重复
	 */
	@Override
	public List<User> selectAccountNameNotRepeat(Map<String, Object> map) {
		return userDao.selectAccountNameNotRepeat(map);
	}
	
	
	@Override
	public void addUser(User user) {
		
		userDao.addUser(user);
	}

	@Override
	public User findUserByAccount(String account) {
		
		return userDao.findUserByAccount(account);
	}

	@Override
	public int updatePassword(int userId, String password) {
		
		return userDao.updatePassword(userId, password);
	}

	@Override
	public int updateActiveById(int userId, int isActive) {
		
		return userDao.updateActiveById(userId, isActive);
	}

	@Override
	public List<User> findUserListByAdminId(int adminId, int start, int count, String account) {
		
		return userDao.findUserListByAdminId(adminId, start, count, account);
	}

	@Override
	public int findCountByAdminId(int adminId) {
		
		return userDao.findCountByAdminId(adminId);
	}

	@Override
	public List<User> findAllUserByAdminId(int adminId) {
		
		return userDao.findAllUserByAdminId(adminId);
	}

	@Override
	public int updateActiveByUserIdList(List<Integer> userIdList, int isActive) {
		
		return userDao.updateActiveByUserIdList(userIdList, isActive);
	}

	@Override
	public User findUserInfoByUserId(int userId) {
		
		return userDao.findUserInfoByUserId(userId);
	}

	@Override
	public int updateValidTime(int userId, String validTime) {
		
		return userDao.updateValidTime(userId, validTime);
	}

	@Override
	public List<User> findOnStaffPhoneList(Map<String, Object> map) {
		return userDao.findOnStaffPhoneList(map);
	}

	@Override
	public Integer updateUserInfoByUserId(User user) {
		
		return userDao.updateUserInfoByUserId(user);
	}

	@Override
    public int selectUserFlagStatusByUserId(Map<String, Object> map) {
        return userDao.selectUserFlagStatusByUserId(map);
    }

}
