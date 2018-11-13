package com.zxxkj.service;

import java.util.List;
import java.util.Map;

import com.zxxkj.model.ProjectData;
import org.apache.ibatis.annotations.Param;

public interface ProjectDataService {

	/**
	 * 根据一个项目数据的实体类添加数据到数据表中
	 */
	Integer insertProjectDataByPOJO(Map<String, Object> map);

	/**
	 * (暂时废弃) 一次性查找三个库的所有数据
	 */
	List<ProjectData> selectProjectDataListByProjectID(Map<String, Object> map);

	/**
	 * 根据一个项目数据的ID,来修改项目数据的详情内容
	 */
	Integer updateProjectDataByProjectDataID(Map<String, Object> map);

	/**
	 * 根据一个项目的数据的ID,联合直正删除项目中的数据
	 */
	Integer deleteProjectDataByProjectDataID(Map<String, Object> map);

	/**
	 * 根据一个项目ID,假删项目数据
	 */
	Integer deleteAllProjectDataByProjectDataID(Map<String, Object> map);

	/**
	 * 根据库的Role来返回结点总数
	 */
	Integer selectNodeCountByPOJO(Map<String, Object> map);

	/**
	 * 根据3个表的role,分别查询数据的List
	 */
	List<ProjectData> selectPositionProjectDataListByProjectID(Map<String, Object> map);

	/**
	 * 根据ID查询结点详细信息
	 */
	ProjectData shotProjectDataByProjectID(Map<String, Object> map);

    /**
     * 根据项目的ID，查看话术信息
     */
	List<ProjectData> shotProjectDatasByProjectID(Map<String, Object> map);

    /**
     * 查看项目ID，查看数据
     */
	ProjectData shotProjectDataMainByProjectID(Map<String, Object> map);

    /**
     * 获取流程结点数
     */
	Integer searchProjectDatadMainsNum(Map<String,Object> map);

    /**
     * 拿到第一条项目数据
     */
	ProjectData selectProjectDateFirstByProjectId(Integer projectId,Integer role);

	/**
	 * 根据项目ID和固定名字和Role，查看数据是否存在
	 */
	int selectFixedDataExist(Map<String, Object> map);

	/**
	 * 根据项目ID和固定名字和Role，修改项目数据 
	 */
	int updateProjectDataByProjectIDAndNamedAndRole(Map<String, Object> map);
	
	/**
	 * 批量添加ProjectData
	 * @param projectDataList
	 * @return
	 */
	Integer batchInsertProjectData(List<ProjectData> projectDataList);
}
