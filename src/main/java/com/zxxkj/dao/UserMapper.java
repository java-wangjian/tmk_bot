package com.zxxkj.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.User;

public interface UserMapper {
	
	/**
	 * 根据用户的账号和密码,查找用户是否存在或信息是否正确
	 */
	User findUserInfoByAccAndPwd(User user);

	/**
	 * 根据客服的ID,查看客户的详细信息
	 */
	User findUserInfoByID(User user);

	/**
	 * 用户ID做为客服的父ID,分页查看用户名下客服
	 */
	List<User> selectStaffListByParentID(Map<String , Object> map);

	List<User> selectStaffAllByParentID(Map<String , Object> map);

	/**
	 * 把用户ID做为客服的父ID,计算用户名下客服数量
	 */
	Integer selectStaffCountByParentID(int userID);

	/**
	 * 为平台用户添加一个客服
	 */
	Integer insertStaffByUser(User user);

	/**
	 * 根据客服ID的集体,批量删除客服
	 */
	Integer deleteStaffsByIDs(Map<String, Object> map);

	/**
	 * 根据客服的ID,修改客服的开关
	 */
	Integer editStaffSwitch(Map<String, Object> map);

	/**
	 * 根据人工客服的ID,修改客服的信息
	 */
	Integer editUserInfoByID(User user);

	/**
	 * 根据用户名和旧密码,设置新密码
	 */
	Integer updateUserPassword(Map<String, Object> map);

	/**
	 * 通过用户ID修改用户的状态
	 */
	Integer updateUserStatusByID(Map<String, Object> map);

	/**
	 * 用户账户名查库,防止重复
	 */
	List<User> selectAccountNameNotRepeat(Map<String, Object> map);

	/**
	 * 添加客户端用户
	 * @param user
	 * @return
	 */
	void addUser(User user);
	
	/**
	 * 根据账号查询此账号是否已存在
	 * @param account
	 * @return
	 */
	User findUserByAccount(String account);
	
	/**
	 * 修改密码
	 * @param userId
	 * @param password
	 * @return
	 */
	int updatePassword(@Param("userId")int userId, @Param("password")String password);
	
	/**
	 * 修改账号激活状态
	 * @param userId
	 * @param isActive
	 * @return
	 */
	int updateActiveById(@Param("userId")int userId, @Param("isActive")int isActive);
	
	/**
	 * 根据账号查询用户
	 * @return
	 */
	List<User> findUserListByAdminId(@Param("adminId")int adminId, @Param("start")int start, @Param("count")int count, @Param("account")String account);
	
	/**
	 * 根据adminId查询该adminId下的用户数量
	 * @param adminId
	 * @return
	 */
	int findCountByAdminId(int adminId);
	
	/**
	 * 根据adminId查询该adminId下的所有用户
	 * @param adminId
	 * @return
	 */
	List<User> findAllUserByAdminId(int adminId);
	
	/**
	 * 根据userIdList修改这些用户账户的状态
	 * @param userIdList
	 * @return
	 */
	int updateActiveByUserIdList(@Param("list")List<Integer> userIdList, @Param("isActive")int isActive);
	
	/**
	 * 根据userId查询用户的信息
	 * @param userId
	 * @return
	 */
	User findUserInfoByUserId(int userId);
	
	/**
	 * 根据userId修改到期时间
	 * @param userId
	 * @param validTime
	 * @return
	 */
	int updateValidTime(@Param("userId")int userId, @Param("validTime")String validTime);

	/**
	 * 根据用户的ID，返回名下客服的电话List
	 */
	List<User> findOnStaffPhoneList(Map<String, Object> map);
	
	/**
	 * 根据用户id编辑用户的信息
	 * @param userId
	 * @return
	 */
	Integer updateUserInfoByUserId(User user);
	
	int selectUserFlagStatusByUserId(Map<String, Object> map);
}
