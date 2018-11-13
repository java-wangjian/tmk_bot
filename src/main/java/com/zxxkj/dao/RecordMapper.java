package com.zxxkj.dao;

import java.util.List;

import com.zxxkj.model.Record;

public interface RecordMapper {

	
	/**
	 * 添加记录
	 * @param record
	 */
	void addRecord(Record record);
	
	/**
	 * 根据id删除录音
	 * @param recordId
	 * @return
	 */
	int deleteRecordById(int recordId);
	
	/**
	 * 根据projectId删除录音
	 * @param projectId
	 * @return
	 */
	int deleteRecordByProjectId(int projectId);
	
	/**
	 * 根据projectId查询该项目下的所有录音
	 * @param projectId
	 * @return
	 */
	List<Record> findRecordByProjectId(int projectId);
}
