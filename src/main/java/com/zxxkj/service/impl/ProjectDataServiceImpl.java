package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.ProjectDataMapper;
import com.zxxkj.model.ProjectData;
import com.zxxkj.service.ProjectDataService;

@Service
public class ProjectDataServiceImpl implements ProjectDataService {
	
	@Resource
	private ProjectDataMapper projectdataDao;

	@Override
	public Integer insertProjectDataByPOJO(Map<String, Object> map) {
		return projectdataDao.insertProjectDataByPOJO(map);
	}

	@Override
	public List<ProjectData> selectProjectDataListByProjectID(Map<String, Object> map) {
		return projectdataDao.selectProjectDataListByProjectID(map);
	}

	@Override
	public Integer updateProjectDataByProjectDataID(Map<String, Object> map) {
		return projectdataDao.updateProjectDataByProjectDataID(map);
	}

	@Override
	public Integer deleteProjectDataByProjectDataID(Map<String, Object> map) {
		return projectdataDao.deleteProjectDataByProjectDataID(map);
	}

	@Override
	public Integer deleteAllProjectDataByProjectDataID(Map<String, Object> map) {
		return projectdataDao.deleteAllProjectDataByProjectDataID(map);
	}

	@Override
	public List<ProjectData> selectPositionProjectDataListByProjectID(Map<String, Object> map) {
		return projectdataDao.selectPositionProjectDataListByProjectID(map);
	}
	
	@Override
	public Integer selectNodeCountByPOJO(Map<String, Object> map) {
		return projectdataDao.selectNodeCountByPOJO(map);
	}

	@Override
	public ProjectData shotProjectDataByProjectID(Map<String, Object> map) {
		return projectdataDao.shotProjectDataByProjectID(map);
	}

	@Override
	public List<ProjectData> shotProjectDatasByProjectID(Map<String, Object> map) {
		return projectdataDao.shotProjectDatasByProjectID(map);
	}

	@Override
	public ProjectData shotProjectDataMainByProjectID(Map<String, Object> map) {
		return projectdataDao.shotProjectDataMainByProjectID(map);
	}

	@Override
	public Integer searchProjectDatadMainsNum(Map<String, Object> map) {
		return projectdataDao.searchProjectDatadMainsNum(map);
	}

	@Override
	public ProjectData selectProjectDateFirstByProjectId(Integer projectId, Integer role) {
		return projectdataDao.selectProjectDateFirstByProjectId(projectId,role);
	}

	@Override
	public int selectFixedDataExist(Map<String, Object> map) {
		return projectdataDao.selectFixedDataExist(map);
	}

	@Override
	public int updateProjectDataByProjectIDAndNamedAndRole(Map<String, Object> map) {
		return projectdataDao.updateProjectDataByProjectIDAndNamedAndRole(map);
	}

	@Override
	public Integer batchInsertProjectData(List<ProjectData> projectDataList) {
		
		return projectdataDao.batchInsertProjectData(projectDataList);
	}

}
