package com.zxxkj.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zxxkj.model.Project;
import com.zxxkj.model.SMS;

public interface ProjectMapper {

	/**
	 * 为指定账户添加项目
	 * @param project
	 * @return
	 */
	void addProject(Project project);
	
	/**
	 * 根据项目名和userId查询项目
	 * @param projectName
	 * @param userId
	 * @return
	 */
	Project findProjectByUserIdAndprojectName(@Param("projectName")String projectName, @Param("userId")int userId);
	
	/**
	 * 根据projectId删除某个项目
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
	 * 根据项目ID查询项目名称
	 */
	String findProjectNameByProjectID(Integer id);

	/**
	 * 根据用户ID获取全部项目名
	 */
	List<Project> selectAllProjectNameByUserID(Map<String, Object> paramMap);

	/**
	 * 获取项目中哪些用户级别已经使用
	 */
	List<SMS> selectProjectSelectedGrade(Map<String, Object> paramMap);
    /*** 
    * @Param: [project] 修改的数据
    * @return: java.lang.Integer 
    * @Author: FuJacKing
    * @Description:  话术一键关闭和开启
    */
    Integer upDateSwitchStatus(Project project);
}
