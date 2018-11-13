package com.zxxkj.dao;

import java.util.List;
import java.util.Map;

import com.zxxkj.model.SysMsg;

public interface SysMsgMapper {

	/**
	 * 从消息表中初始所有系统消息,筛选符合条件的系统消息,返回消息ID的集合
	 */
	List<SysMsg> findMsgListByTimeAndUserID(Map<String, Object> map);

	/**
	 * 根据消息ID判断消息在中间表中是否存在,初始化中间表
	 */
	Integer findMsgStatusByMsgIDAndUserID(Map<String, Object> map);

	/**
	 * 根据UserID和MsgID,在中间表没有找到阅读状态,初始化为1(未读)
	 */
	Integer insertMsgIDConnUserID(Map<String , Object> map);

	/**
	 * 从中间表中,计算这个UserID名下未读消息的数量
	 */
	Integer selectUnreadMsgCountByUserID(Integer userID);

	/**
	 * 通过系统消息ID的集合,批量设置消息状态 为已读
	 */
	Integer batchReadSysMsgByMsgIDList(Map<String, Object> map);

	/**
	 * 通过系统消息ID的集合,批量删除系统消息
	 */
	Integer batchDeleteSysMsgByMsgIDList(Map<String, Object> map);

	/**
	 * 向数据库插入用户提交的意见反馈
	 */
	Integer insertFeedbackMsg(SysMsg msg);

	/**
	 * 根据用户ID,分页查询用户提交的意见反馈列表
	 */
	List<SysMsg> selectFeedbackMsgList(Map<String, Object> map);

	/**
	 * 查询用户一共提交了多少条用户反馈
	 */
	Integer selectFeedbackCountByUserID(Map<String, Object> map);

    /**
     * 返回符合条件的所有系统消息ID集合
     */
    List<SysMsg> selectAllMsgID(Map<String, Object> map);

    /**
     * 初始化系统消息库,计算所有符合条件的系统消息数量
     */
    Integer selectMsgCountByConnTable(Map<String, Object> map);

    /**
     * 根据中间表的集合,来找系统消息列表
     */
    List<SysMsg> findMsgListByConnTableAndMsgIDList(Map<String, Object> map);


    Integer selectConnMsgCountByUserID(Map<String, Object> map);

    List<Integer> findAllSysMsgIdListByContidation(Map<String,Object> map);

    List<Map<String,Object>> selectMsgMapListByCondetion(Map<String,Object> map);

    Integer selectMsgMapListSizeByCondetion(Map<String,Object> map);

    Integer insertPointMsgByUserId(Map<String,Object> map);

    Integer selectRechargeSysMsgCountByUserIdAndContent(Map<String,Object> map);

    Integer insertPointMsgConnn(Map<String,Object> map);
}
