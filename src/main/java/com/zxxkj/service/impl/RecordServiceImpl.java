package com.zxxkj.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.RecordMapper;
import com.zxxkj.model.Record;
import com.zxxkj.service.IRecordService;

@Service
public class RecordServiceImpl implements IRecordService {

	@Resource
	private  RecordMapper recordDao;
	
	@Override
	public void addRecord(Record record) {
		
		recordDao.addRecord(record);
	}

	@Override
	public int deleteRecordById(int recordId) {
		
		return recordDao.deleteRecordById(recordId);
	}

	@Override
	public int deleteRecordByProjectId(int projectId) {
		
		return recordDao.deleteRecordByProjectId(projectId);
	}

	@Override
	public List<Record> findRecordByProjectId(int projectId) {
		
		return recordDao.findRecordByProjectId(projectId);
	}

}
