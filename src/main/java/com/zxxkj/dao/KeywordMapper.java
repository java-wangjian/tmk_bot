package com.zxxkj.dao;

import java.util.List;

import com.zxxkj.model.Keyword;

public interface KeywordMapper {

	/**
	 * 添加关键字
	 * @param keywordList
	 */
	int addKeywords(List<Keyword> keywordList);
	
	/**
	 * 根据recordId删除相应的关键字
	 * @param recordId
	 * @return
	 */
	int deleteKeywordByRecordId(int recordId);
	
	/**
	 * 根据projectId删除相应的关键字
	 * @param projectId
	 * @return
	 */
	int deleteKeywordByProjectId(int projectId);
	
	/**
	 * 根据recordId 查询该录音对应的所有关键字
	 * @param recordId
	 * @return
	 */
	List<Keyword> findRecordByProjectId(int recordId);
}
