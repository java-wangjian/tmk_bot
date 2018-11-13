package com.zxxkj.service;

import java.util.List;
import java.util.Map;

import com.zxxkj.model.Project;
import com.zxxkj.model.SMS;

public interface IProjectService {

	/**
	 * 为指定用户添加项目
	 * @param project
	 * @return
	 */
	void addProject(Project project);
	
	/**
	 * 根据项目名和userId查询项目,若此用户下没有添加此项目，返回true，否则返回false
	 * @param projectName
	 * @param userId
	 * @return
	 */
	Project findProjectByUserIdAndprojectName(String projectName, int userId);
	
	/**
	 * 根据项目ID查询项目名称
	 */
	String findProjectNameByProjectID(Integer id);
	
	/**
	 * 根据projectId删除项目
	 * @param projectId
	 * @return
	 */
	int deleteProject(int projectId);
	
	/**
	 * 根据userId查询该用户下的所有项目
	 * @param userId
	 * @return
	 */
	List<Project> findProjectByUserId(int userId);
	
	/**
	 * 根据userIdList查询项目
	 * @param userIdList
	 * @return
	 */
	List<Project> findProjectByUserIdList(List<Integer> userIdList);

	/**
	 * 根据用户ID获取全部项目名
	 */
	List<Project> selectAllProjectNameByUserID(Map<String, Object> paramMap);

	/**
	 * 获取项目中哪些用户级别已经使用
	 */
	List<SMS> selectProjectSelectedGrade(Map<String, Object> paramMap);
/*** 
* @Param: [project]
* @return: java.lang.Integer 
* @Author: FuJacKing
* @Description:  话术的一键开启和关闭
*/ 
    Integer upDateSwitchStatus(Project project);
}
