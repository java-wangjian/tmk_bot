package com.zxxkj.dao;

import java.util.List;
import java.util.Map;

import com.zxxkj.model.CallDetail;

public interface CallDetailMapper {

	Integer insertCallDetailData(Map<String, Object> map);

	List<CallDetail> selectCallRecordDetailByCallRecordID(Map<String, Object> map);

	void updateFileUrlByCallDetailId(Map<String, Object> map);

}
