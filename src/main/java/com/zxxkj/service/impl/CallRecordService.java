package com.zxxkj.service.impl;

import java.util.*;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.controller.PlanController;
import com.zxxkj.dao.CustomerMapper;
import com.zxxkj.dao.GatewayMapper;
import com.zxxkj.dao.PlanMapper;
import com.zxxkj.dao.ProjectMapper;
import com.zxxkj.model.*;
import com.zxxkj.quartz.QuartzTaskManager;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.DateUtil;
import com.zxxkj.util.HttpRequestUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zxxkj.dao.CallRecordMapper;
import com.zxxkj.dao.CustomerAndPlanMapper;
import com.zxxkj.service.ICallRecordService;
import com.zxxkj.service.ICustomerAndPlanService;
import com.zxxkj.task.CallTask;
import com.zxxkj.util.TTUtil;
import com.zxxkj.util.TransportUtil;

import static com.zxxkj.util.ConstantUtil.*;
import static com.zxxkj.util.HTTPUtil.sendGet;
import static com.zxxkj.util.Utils.isEmpty;

@Service
public class CallRecordService implements ICallRecordService {


    private static final Logger lg = Logger.getLogger(CallRecordService.class);

    @Resource
    private CallRecordMapper callRecordDao;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private GatewayMapper gatewayMapper;
    
    @Resource
	private PlanMapper planDao;
    
    @Resource
	private CustomerAndPlanMapper customerAndPlanDao;
    
    @Resource
	private QuartzTaskManager quartzTaskManager;
    
    @Autowired
	private ICustomerAndPlanService customerAndPlanService;

    /**
     * 根据用户的ID,查看用户名下通话记录列表详情
     */
    @Override
    public List<CallRecord> selectCallRecordListByUserID(Map<String, Object> map) {
        return callRecordDao.selectCallRecordListByUserID(map);
    }

    /**
     * 根据用户的ID,返回用户名下有多少条通话记录
     */
    @Override
    public Integer selectCallRecordCountByUserID(User user) {
        return callRecordDao.selectCallRecordCountByUserID(user);
    }

    /**
     * 根据UserID和通话记录集合,批量删除通话记录
     */
    @Override
    public Integer batchDeleteCallRecord(Map<String, Object> map) {
        return callRecordDao.batchDeleteCallRecord(map);
    }

    @Override
    public void deleteCallRecordById(Integer callRecordId) {
         callRecordDao.deleteCallRecordById(callRecordId);
    }

    /**
     * 根据多变条件,查询符合要求记录条数
     */
    @Override
    public Integer selectRecordCountByMultiple(Map<String, Object> map) {
        return callRecordDao.selectRecordCountByMultiple(map);
    }

    /**
     * 根据多变条件,查询符合要求记录的集合
     */
    @Override
    public List<CallRecord> selectListByMultiple(Map<String, Object> map) {
        return callRecordDao.selectListByMultiple(map);
    }

    /**
     * 根据ID的集合,返回通话记录的List,写入Excel文件
     */
    @Override
    public List<CallRecord> selectCallRecordListByIdList(Map<String, Object> map) {
        return callRecordDao.selectCallRecordListByIdList(map);
    }

    /**
     * 根据通话记录ID的集合,对符合要求的记录导出次数 +1
     */
    @Override
    public Integer incrExportCountByIDList(Map<String, Object> map) {
        return callRecordDao.incrExportCountByIDList(map);
    }

    /**
     * 插入导出通话记录的详情到服务器,方便查询通话记录导出
     */
    @Override
    public Integer insertExportHistoryContent(Map<String, Object> map) {
        return callRecordDao.insertExportHistoryContent(map);
    }

    /**
     * 查询通话记录导出历史的List
     */
    @Override
    public List<RecordHistory> selectExportHistoryListByUserID(Map<String, Object> map) {
        return callRecordDao.selectExportHistoryListByUserID(map);
    }

    /**
     * 查询通话记录导出历史的总条数
     */
    @Override
    public Integer selectExportHistoryCountByUserID(Map<String, Object> map) {
        return callRecordDao.selectExportHistoryCountByUserID(map);
    }


    @Override
    public List<Integer> findUserIdByCallTimeRang(Date callStartTime, Date callEndTime) {

        return callRecordDao.findUserIdByCallTimeRang(callStartTime, callEndTime);
    }

    @Override
    public Map<String, Object> findLastCallRecordByCustomerID(int customerId) {

        return callRecordDao.findLastCallRecordByCustomerID(customerId);
    }

    @Override
    public Integer insertCallRecord(CallRecord callrecord) {
        if (TTUtil.isAnyNull(callrecord.getStatus(), callrecord.getCallSignal(), callrecord.getDurationTime(),
                callrecord.getProjectID(), callrecord.getCustomerID(), callrecord.getUserID(),
                callrecord.getDatetime())) {
            lg.info("插入新的通话记录到数据异常,有字段为空!!!");
            lg.info(callrecord);
            return 0;
        }
        return callRecordDao.insertCallRecord(callrecord);
    }

    public Integer createCallRecord(Integer projectId, Customer customer, Integer userId, Integer planId) {
        Map<String, Object> map = new HashMap<>();
        CallRecord callRecord = new CallRecord();
        callRecord.setCustomerID(customer.getId());
        callRecord.setCustomerPhone(customer.getCustomerPhone());
        callRecord.setStatus(2);
        callRecord.setCallSignal(5);
        callRecord.setProjectID(projectId);
        callRecord.setCustomerID(customer.getId());
        callRecord.setUserPhone(8888L);
        callRecord.setProjectName(isEmpty(projectMapper.findProjectNameByProjectID(projectId)) ? "模板" : projectMapper.findProjectNameByProjectID(projectId));
        callRecord.setUserID(userId);
        callRecord.setDatetime(YYYY_MM_DD_HH_MM_SS_SDF.format(new Date()));
        callRecord.setPlanId(planId);
        callRecord.setCustomerGrade(6);
        map.put("CallRecord", callRecord);
        prepareInsertCallRecord(map);
        //修改通话状态
//        customerAndPlanDao.updateIsCallByPlanIdAndCustomerId(customer.getId(), planId, 1);
        return callRecord.getId();
    }

    @Override
    public List<Integer> selectAllIDsByMultiple(Map<String, Object> map) {
        return callRecordDao.selectAllIDsByMultiple(map);
    }

    @Override
    public Integer betchDeleteCallRecordListByUserIDAndCustomerIDS(Map<String, Object> map) {
        String userID = null;
        if (null == map.get("userID") || null == map.get("customerIDS")) {
            lg.info("根据userID和customerIDs联合删除通话记录失败,传递参数有异常!!!");
            return 0;
        }
        userID = (String) map.get("userID").toString();
        List<String> customerIDSList = (List<String>) map.get("customerIDS");
        if (customerIDSList.size() < 1) {
            lg.info("根据userID和customerIDs联合删除通话记录失败,传递参数有异常!!!");
            return 0;
        }
        map.put("userID", userID);
        map.put("customerIDS", customerIDSList);
        lg.info(map);
        return callRecordDao.betchDeleteCallRecordListByUserIDAndCustomerIDS(map);
    }

    @Override
    public Integer prepareInsertCallRecord(Map<String, Object> map) {
        CallRecord callRecord = (CallRecord) map.get("CallRecord");
        if (null == callRecord) {
            lg.info("传递的参数不合法,通话记录对象为空!!!");
            return null;
        }
        if (TTUtil.isAnyNull(callRecord.getStatus(), callRecord.getCallSignal(), callRecord.getProjectID(),
                callRecord.getProjectName(), callRecord.getUserPhone(), callRecord.getCustomerPhone(),
                callRecord.getCustomerID(), callRecord.getDatetime(), callRecord.getUserID())) {
            lg.info("传递的参数不合法,通话记录对象有参数为空!!!");
            return null;
        }
        return callRecordDao.prepareInsertCallRecord(map);
    }

    @Override
    public Integer insertAfterUpdateStatus(Map<String, Object> map) {
        return callRecordDao.insertAfterUpdateStatus(map);
    }

    @Override
    public Integer updateCallRecordStatusById(Integer callRecordId, Integer status,Integer time,Integer grade, String fileID) {
        
    	return callRecordDao.updateCallRecordStatusById(callRecordId, status,time,grade, fileID);
    }

    @Override
    public Integer findCalledCountByUserId(int userId, int planId) {

        return callRecordDao.findCalledCountByUserId(userId, planId);
    }

    @Override
    public Integer findNoCallCountByUserId(int userId, int planId) {

        return callRecordDao.findNoCallCountByUserId(userId, planId);
    }

    @Override
    public Integer findPassCountByUserId(int userId, int planId) {

        return callRecordDao.findPassCountByUserId(userId, planId);
    }

    @Override
    public Integer findTransferCountByUserId(int userId, int planId) {

        return callRecordDao.findTransferCountByUserId(userId, planId);
    }

    @Override
    public List<Map<String, Object>> findGradeAndGradeCountByUserId(int userId, int planId) {

        return callRecordDao.findGradeAndGradeCountByUserId(userId, planId);
    }

    @Override
    public List<Integer> findPassCustomerIdsByPlanId(int userId, int planId) {

        return callRecordDao.findPassCustomerIdsByPlanId(userId, planId);
    }

    @Override
    public List<Integer> findAllCustomerIdsByPlanId(int userId, int planId) {

        return callRecordDao.findAllCustomerIdsByPlanId(userId, planId);
    }

    @Override
    public Integer findCallRecordByCustomerIdAndPlanId(Map<String, Object> callRecordParamMap) {

        return callRecordDao.findCallRecordByCustomerIdAndPlanId(callRecordParamMap);
    }

    @Override
    public Map<String, Object> findCallRecordByCallRecordId(Map<String, Object> map) {

        return callRecordDao.findCallRecordByCallRecordId(map);
    }

    @Override
    public List<Map<String, Object>> findCustomersByplanId(Map<String, Object> paramMap) {

        return callRecordDao.findCustomersByplanId(paramMap);
    }

    @Override
    public Integer findCustomersCountByplanId(Map<String, Object> paramMap) {

        return callRecordDao.findCustomersCountByplanId(paramMap);
    }

    @Override
    public List<Map<String, Object>> selectCallRecordListByIdListForExcel(Map<String, Object> map) {
        return callRecordDao.selectCallRecordListByIdListForExcel(map);
    }

    private static JSONObject jsonObject(String customerId, String dialString, String projectId, String userId, String gateWayId, String callRecordId, String gateWayType,String gateName,Integer isSendSMS,String gateWayUrl,String dialPorts,Integer planId) {
        String str_startTime= String.valueOf(System.currentTimeMillis());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jsonrpc", FREESWITCH_JSONRPC);
        jsonObject.put("method", FREESWITCH_METHOD);
        jsonObject.put("customerId", customerId);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("application", FREESWITCH_APPLICATION);
        jsonObject1.put("dial_string", dialString);
        jsonObject1.put("from", "20170090");
        jsonObject1.put("url", FREESWITCH_URL);
        JSONObject private_data = new JSONObject();
        private_data.put("customerId", customerId);
        private_data.put("projectId", projectId);
        private_data.put("planId",String.valueOf(planId));
        private_data.put("callRecordId", callRecordId);
        private_data.put("userId", userId);
        private_data.put("startTime", str_startTime);
        private_data.put("gateWayType", gateWayType);
        private_data.put("gatewayId", gateWayId);
        private_data.put("gateName", gateName);
        private_data.put("isSendSMS",String.valueOf(isSendSMS));
        private_data.put("dialPorts", dialPorts);
        private_data.put("thread_uuid",RandomStringUtils.randomAlphanumeric(20)+"_"+str_startTime);

        jsonObject1.put("private_data", private_data);
        jsonObject.put("params", jsonObject1);
        return jsonObject;
    }

    @Override
    public Integer updateGradeByCustomerIdAndUserId(int grade, int customerId, int userId) {

        return callRecordDao.updateGradeByCustomerIdAndUserId(grade, customerId, userId);
    }
    @Override
    public List<Map<String, Object>> selectCallRecordGradeAndCustomerPhone(int userId, int planId) {
        return callRecordDao.selectCallRecordGradeAndCustomerPhone(userId, planId);
    }
    
   

	@Override
	public Integer updateCallRecorde(List<CallRecord> callRecord) {
		
		return callRecordDao.updateCallRecorde(callRecord);
	}

	@Override
	public CallRecord findCallRecordInfoById(int callRecordId) {
		
		return callRecordDao.findCallRecordInfoById(callRecordId);
	}

	@Override
	public List<CallRecord> findCallRecordByPlanId(Map<String, Object> paramMap) {
		
		return callRecordDao.findCallRecordByPlanId(paramMap);
	}

	@Override
	public List<Integer> findHasCallCustomerIdList(int planId) {
		
		return callRecordDao.findHasCallCustomerIdList(planId);
	}

	@Override
	public Integer findCountByGradeList(List<Integer> gradeList, int planId) {
		
		return callRecordDao.findCountByGradeList(gradeList, planId);
	}
}
