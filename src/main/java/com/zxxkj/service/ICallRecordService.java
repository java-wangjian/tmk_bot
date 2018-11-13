package com.zxxkj.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.quartz.Scheduler;

import com.zxxkj.model.*;


public interface ICallRecordService {

	/**
	 * 根据用户的ID,查看用户名下通话记录列表详情
	 */
	List<CallRecord> selectCallRecordListByUserID(Map<String, Object> map);

	/**
	 * 根据用户的ID,返回用户名下有多少条通话记录
	 */
	Integer selectCallRecordCountByUserID(User user);

	/**
	 * 根据UserID和通话记录集合,批量删除通话记录
	 */
	Integer batchDeleteCallRecord(Map<String, Object> map);

	/**
	 * 通过id删除通话记录
	 * @param callRecordId
	 */
	void deleteCallRecordById(Integer callRecordId);

	/**
	 * 根据多变条件,查询符合要求记录条数
	 */
	Integer selectRecordCountByMultiple(Map<String, Object> map);

	/**
	 * 根据多变条件,查询符合要求记录的集合
	 */
	List<CallRecord> selectListByMultiple(Map<String, Object> map);

	/**
	 * 根据ID的集合,返回通话记录的List,写入Excel文件
	 */
	List<CallRecord> selectCallRecordListByIdList(Map<String, Object> map);

	/**
	 * 根据通话记录ID的集合,对符合要求的记录导出次数 +1
	 */
	Integer incrExportCountByIDList(Map<String, Object> map);

	/**
	 * 插入导出通话记录的详情到服务器,方便查询通话记录导出
	 */
	Integer insertExportHistoryContent(Map<String, Object> map);
	
	/**
	 * 插入新的通话记录(暂时废弃)
	 */
	Integer insertCallRecord(CallRecord callrecord);


	/**
	 * 查询通话记录导出历史的List
	 */
	List<RecordHistory> selectExportHistoryListByUserID(Map<String, Object> map);

	/**
	 * 查询通话记录导出历史的总条数
	 */
	Integer selectExportHistoryCountByUserID(Map<String, Object> map);

	
	/**
	 * 根据通话时间范围查询客户（去重后的结果）
	 * @param callStartTime
	 * @param callEndTime
	 * @return
	 */
	List<Integer> findUserIdByCallTimeRang(Date callStartTime, Date callEndTime);
	
	/**
	 * 根据customerId查询该用户最后一条通话记录的详细信息
	 * @param customer
	 * @return
	 */
	Map<String, Object> findLastCallRecordByCustomerID(int customerId);

	/**
	 * 根据多变条件,查询所有符合条件的IDList
	 */
	List<Integer> selectAllIDsByMultiple(Map<String, Object> map);

	/**
	 * 根据userID和customerIDs联合删除通话记录
	 */
	Integer betchDeleteCallRecordListByUserIDAndCustomerIDS(Map<String, Object> map);

	/**
	 * 得到通话状态后,先插入一条临时数据,通话结束后再修改
	 */
	Integer prepareInsertCallRecord(Map<String, Object> map);

	/**
	 * 通话结束后,对通话记录的状态进行修改
	 */
	Integer insertAfterUpdateStatus(Map<String, Object> map);

	/**
	 * 修改用户通话状态
	 */
	Integer updateCallRecordStatusById(Integer callRecordId,Integer status,Integer time,Integer grade,String fileID);
	
	/**
	 * 查询已拨打的客户总量
	 * @param userId
	 * @return
	 */
	Integer findCalledCountByUserId(int userId, int planId);

	/**
	 * 查询未接通的客户总量
	 * @param userId
	 * @return
	 */
	Integer findNoCallCountByUserId(int userId, int planId);
	
	/**
	 * 查询已接通的客户总量
	 * @param userId
	 * @return
	 */
	Integer findPassCountByUserId(int userId, int planId);
	
	/**
	 * 查询接通以及转接人工的客户总量
	 * @param userId
	 * @return
	 */
	Integer findTransferCountByUserId(int userId, int planId);
	
	/**
	 * 各级别对应的客户人数
	 * @param userId
	 * @return
	 */
	List<Map<String,Object>> findGradeAndGradeCountByUserId(int userId, int planId);
	
	/**
	 * 查询该计划中接通的客户id
	 * @param userId
	 * @param planId
	 * @return
	 */
	List<Integer> findPassCustomerIdsByPlanId(int userId, int planId);
	
	/**
	 * 查询计划下的所有客户id
	 * @param userId
	 * @param planId
	 * @return
	 */
	List<Integer> findAllCustomerIdsByPlanId(int userId, int planId);
	
	/**
	 * 根据客户id和计划id查询客户的通话记录
	 * @param customerId
	 * @param planId
	 * @return
	 */
	Integer findCallRecordByCustomerIdAndPlanId(Map<String, Object> callRecordParamMap);
	
	/**
	 * 根据通话记录id查询详细信息
	 * @param callRecordId
	 * @return
	 */
	Map<String, Object> findCallRecordByCallRecordId(Map<String, Object> map);
	
	/**
	 * 根据计划id查询此计划中的客户通话记录
	 * @param userId
	 * @param planId
	 * @return
	 */
	List<Map<String,Object>> findCustomersByplanId(Map<String, Object> paramMap);
	
	/**
	 * 根据计划id查询此计划中的客户通话记录总数
	 * @param paramMap
	 * @return
	 */
	Integer findCustomersCountByplanId(Map<String, Object> paramMap);

    /**
     * 根据通话记录的ID，查询记录记录数据，用于导出的数据
     * @param map
     * @return
     */
	List<Map<String, Object>> selectCallRecordListByIdListForExcel(Map<String, Object> map);
	
	/**
	 * 根据客户id（customerId）和用户id（userId）次该意向度
	 * @param grade
	 * @param customerId
	 * @param userId
	 * @return
	 */
	Integer updateGradeByCustomerIdAndUserId(int grade, int customerId, int userId);

    /**
     * 根据用户ID和计划ID返回级别和客户电话
     * @param userId
     * @param planId
     * @return
     */
    List<Map<String,Object>> selectCallRecordGradeAndCustomerPhone(int userId, int planId);
    
    /**
	 * 根据callRecordId查询通话记录的详细信息
	 * @param callRecordId
	 * @return
	 */
	CallRecord findCallRecordInfoById(int callRecordId);
	
	/**
	 * 修改通话记录的customerGrade,durationTime,status,fileID
	 * @param callRecord
	 * @return
	 */
	Integer updateCallRecorde(List<CallRecord> callRecord);
	
	/**
	 * 查询计划中的通话记录列表
	 * @param paramMap
	 * @return
	 */
	List<CallRecord> findCallRecordByPlanId(Map<String, Object> paramMap);
	
	/**
	 * 根据计划id查询已经拨打过的客户id
	 * @param planId
	 * @return
	 */
	List<Integer> findHasCallCustomerIdList(int planId);
	
	/**
	 * 根据意向查询客户的总量
	 * @param gradeList
	 * @return
	 */
	Integer findCountByGradeList(List<Integer> gradeList, int planId);
}
