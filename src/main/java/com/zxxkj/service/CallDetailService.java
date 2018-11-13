package com.zxxkj.service;

import java.util.List;
import java.util.Map;

import com.zxxkj.model.CallDetail;

public interface CallDetailService {

    /**
     * 插入通话详情数据
     */
	Integer insertCallDetailData(Map<String, Object> map);

    /**
     * 根据通话记录的ID，查看通话记录的详情
     */
	List<CallDetail> selectCallRecordDetailByCallRecordID(Map<String, Object> map);

    /**
     * 根据通话ID，更新通话录音地址
     */
	void updateFileUrlByCallDetailId(Map<String,Object> map);

}
