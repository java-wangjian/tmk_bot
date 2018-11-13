package com.zxxkj.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zxxkj.dao.ProjectMapper;
import com.zxxkj.model.Project;
import com.zxxkj.model.SMS;
import com.zxxkj.service.IProjectService;

@Service
public class ProjectServiceImpl implements IProjectService {

	@Resource
    private ProjectMapper projectDao;
    
	@Override
	public void addProject(Project project) {
		
		projectDao.addProject(project);
	}

	@Override
	public Project findProjectByUserIdAndprojectName(String projectName, int userId) {
		
		return projectDao.findProjectByUserIdAndprojectName(projectName, userId);
	}

	@Override
	public int deleteProject(int projectId) {
		
		return projectDao.deleteProject(projectId);
	}

	@Override
	public List<Project> findProjectByUserId(int userId) {
		
		return projectDao.findProjectByUserId(userId);
	}

	@Override
	public List<Project> findProjectByUserIdList(List<Integer> userIdList) {
		
		return projectDao.findProjectByUserIdList(userIdList);
	}

	@Override
	public String findProjectNameByProjectID(Integer id) {
		return projectDao.findProjectNameByProjectID(id);
	}

	@Override
	public List<Project> selectAllProjectNameByUserID(Map<String, Object> paramMap) {
		return projectDao.selectAllProjectNameByUserID(paramMap);
	}

	@Override
	public List<SMS> selectProjectSelectedGrade(Map<String, Object> paramMap) {
		return projectDao.selectProjectSelectedGrade(paramMap);
	}

	/*** 
	* @Param: [project]修改时所用的参数
	* @return: java.lang.Integer 
	* @Author: FuJacKing
	* @Description:  话术一键开启和关闭的功能
	*/ 
    @Override
    public Integer upDateSwitchStatus(Project project) {
        return projectDao.upDateSwitchStatus(project);
    }
}
