package com.zxxkj.dao;

import java.util.List;
import java.util.Map;

import com.zxxkj.model.SimCard;
import com.zxxkj.model.User;

public interface SimCardMapper {

	/**
	 * 根据用户的ID,查询用户名下手机卡的数量,方便分页
	 */
	Integer selectCountFromSimCardTable(User user);

	/**
	 * 根据用户ID,分页显示用户名下手机卡的列表
	 */
	List<SimCard> selectListFromSimCardTable(Map<String, Object> map);

	/**
	 * 根据手机卡ID的集合,批量删除手机卡
	 */
	Integer batchDeleteSimCard(Map<String, Object> map);

	/**
	 * 根据手机卡的ID,修改手机卡的信息
	 */
	Integer updateSimCardInfo(SimCard simCard);

	/**
	 * 根据手机卡的ID,调节手机卡的激活开关
	 */
	Integer switchSimCard(SimCard simCard);

	/**
	 * 根据参数,计算模糊搜索到的符合参数的手机号数量
	 */
	Integer selectCountPhoneNum(Map<String, Object> map);

	/**
	 * 根据参数,模糊搜索手机卡,分页返回手机卡列表
	 */
	List<SimCard> selectListPhoneNum(Map<String, Object> map);

	/**
	 * 插入一个新的Sim卡信息
	 */
	Integer insertSimCardInfo(SimCard simCard);

    List<Map<String,Object>> selectPortsInfo(Map<String,Object> map);
}
