package com.zxxkj.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.KeywordMapper;
import com.zxxkj.model.Keyword;
import com.zxxkj.service.IKeywordService;

@Service
public class KeywordServiceImpl implements IKeywordService {

	@Resource
	private KeywordMapper keywordDao;
	
	@Override
	public int addKeywords(List<Keyword> keywordList) {
		
		return keywordDao.addKeywords(keywordList);
	}

	@Override
	public int deleteKeywordByRecordId(int recordId) {
		
		return keywordDao.deleteKeywordByRecordId(recordId);
	}

	@Override
	public int deleteKeywordByProjectId(int projectId) {
		
		return keywordDao.deleteKeywordByProjectId(projectId);
	}

	@Override
	public List<Keyword> findRecordByProjectId(int recordId) {
		
		return keywordDao.findRecordByProjectId(recordId);
	}

}
