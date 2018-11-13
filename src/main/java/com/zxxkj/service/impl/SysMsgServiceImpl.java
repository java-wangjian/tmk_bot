package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.SysMsgMapper;
import com.zxxkj.model.SysMsg;
import com.zxxkj.service.SysMsgService;

@Service
public class SysMsgServiceImpl implements SysMsgService {

    @Resource
    private SysMsgMapper sysMsgDao;

    /**
     * 根据消息ID判断消息在中间表中是否存在,初始化中间表
     */
    @Override
    public Integer findMsgStatusByMsgIDAndUserID(Map<String, Object> map) {
        return sysMsgDao.findMsgStatusByMsgIDAndUserID(map);
    }

    /**
     * 根据UserID和MsgID,在中间表没有找到阅读状态,初始化为1(未读)
     */
    @Override
    public Integer insertMsgIDConnUserID(Map<String, Object> map) {
        return sysMsgDao.insertMsgIDConnUserID(map);
    }

    /**
     * 从中间表中,计算这个UserID名下未读消息的数量
     */
    @Override
    public Integer selectUnreadMsgCountByUserID(Integer userID) {
        return sysMsgDao.selectUnreadMsgCountByUserID(userID);
    }

    /**
     * 通过系统消息ID的集合,批量设置消息状态 为已读
     */
    @Override
    public Integer batchReadSysMsgByMsgIDList(Map<String, Object> map) {
        return sysMsgDao.batchReadSysMsgByMsgIDList(map);
    }

    /**
     * 通过系统消息ID的集合,批量删除系统消息
     */
    @Override
    public Integer batchDeleteSysMsgByMsgIDList(Map<String, Object> map) {
        return sysMsgDao.batchDeleteSysMsgByMsgIDList(map);
    }

    /**
     * 向数据库插入用户提交的意见反馈
     */
    @Override
    public Integer insertFeedbackMsg(SysMsg msg) {
        return sysMsgDao.insertFeedbackMsg(msg);
    }

    /**
     * 根据用户ID,分页查询用户提交的意见反馈列表
     */
    @Override
    public List<SysMsg> selectFeedbackMsgList(Map<String, Object> map) {
        return sysMsgDao.selectFeedbackMsgList(map);
    }

    /**
     * 查询用户一共提交了多少条用户反馈
     */
    @Override
    public Integer selectFeedbackCountByUserID(Map<String, Object> map) {
        return sysMsgDao.selectFeedbackCountByUserID(map);
    }

    @Override
    public List<Integer> findAllSysMsgIdListByContidation(Map<String, Object> map) {
        return sysMsgDao.findAllSysMsgIdListByContidation(map);
    }

    @Override
    public List<Map<String, Object>> selectMsgMapListByCondetion(Map<String, Object> map) {
        return sysMsgDao.selectMsgMapListByCondetion(map);
    }

    @Override
    public Integer selectMsgMapListSizeByCondetion(Map<String, Object> map) {
        return sysMsgDao.selectMsgMapListSizeByCondetion(map);
    }

    @Override
    public Integer insertPointMsgByUserId(Map<String, Object> map) {
        Integer rechargeMsgCount = sysMsgDao.selectRechargeSysMsgCountByUserIdAndContent(map);
        if (null == rechargeMsgCount || rechargeMsgCount == 0) {
            Integer effect = sysMsgDao.insertPointMsgByUserId(map);
            if ( null != effect && effect > 0) {
                String msgId = map.get("id").toString();
                map.put("msg_id", msgId);
                effect = sysMsgDao.insertPointMsgConnn(map);
                if (null != effect && effect > 0) {
                    return 1;
                }
            }
        }
        return 0;
    }

}
