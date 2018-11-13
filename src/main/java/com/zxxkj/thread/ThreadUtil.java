package com.zxxkj.thread;

import static com.zxxkj.util.ConstantUtil.FREESWITCH_APPLICATION;
import static com.zxxkj.util.ConstantUtil.FREESWITCH_JSONRPC;
import static com.zxxkj.util.ConstantUtil.FREESWITCH_METHOD;
import static com.zxxkj.util.ConstantUtil.FREESWITCH_POST;
import static com.zxxkj.util.ConstantUtil.FREESWITCH_URL;
import static com.zxxkj.util.ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF;
import static com.zxxkj.util.Utils.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.zxxkj.service.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.controller.PlanController;
import com.zxxkj.model.CallRecord;
import com.zxxkj.model.Customer;
import com.zxxkj.model.Plan;
import com.zxxkj.quartz.QuartzTaskManager;
import com.zxxkj.task.WorkStartTimeTask;
import com.zxxkj.util.CacheUtil;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.DateUtil;
import com.zxxkj.util.HttpRequestUtil;
import com.zxxkj.util.ParameterProperties;
import com.zxxkj.util.PlanStatusUtil;
import com.zxxkj.util.SleepUtil;
import com.zxxkj.util.TransportUtil;

public class ThreadUtil {

    private static final Logger lg = Logger.getLogger(ThreadUtil.class);

    public static Vector<Customer> getCustomerVector (Plan plan, ICustomerAndPlanService customerAndPlanService,
                                                     RedisCacheUtil redisCacheUtil) throws Exception{
        Integer callCount = ParameterProperties.getIntegerValue(ConstantUtil.SETTING_FILEPATH, "callCount");
        int customerCount = plan.getCustomerCount()/plan.getSipCallCount();
        if(customerCount == 0) {
            customerCount = 1;
        }
        //如果平均拨打量大于默认拨打量，那么使用默认拨打量
        if(callCount != null && callCount > customerCount) {
            callCount = customerCount;
        }
        
        Vector<Customer> customerVector = null;
        synchronized(plan) {
        	customerVector = customerAndPlanService.getNoCallCustomersAndUpdate(plan, callCount, plan.getIsEnd());
        }
        lg.info(customerVector);
        return customerVector;
    }

    /**
     * 获取下一个要拨打的客户
     * @return
     */
    public static Customer getNextCustomer(ICustomerAndPlanService customerAndPlanService, RedisCacheUtil redisCacheUtil,
                                           ICallRecordService callRecordService, Vector<Customer> customerVector, int userId, int planId,
                                           String threadUUID, Vector<Customer> hasCalledCustomerVector) {
        Customer customer = null;

        if(customerVector == null || customerVector.size() == 0) {
            //修改客户的isCall状态
            setIsCall(redisCacheUtil, customerAndPlanService, userId, planId, threadUUID);

            //将customer置空，否则会重复拨打同一个号
            customer = null;
        }else {
            customer = customerVector.get(0);
            customerVector.remove(customer);
            hasCalledCustomerVector.add(customer);
        }

        lg.info(threadUUID + " ---计划  " + planId + "  的任务customerVector大小为  " + customerVector.size());
        return customer;
    }

    /**
     * 接到客户先记录一条最初的通话记录
     * @param projectId
     * @param customer
     * @param userId
     * @param planId
     * @param projectService
     * @param customerAndPlanService
     * @param callRecordService
     * @param redisCacheUtil
     * @return
     */
    public static Integer createCallRecord(Integer projectId, Customer customer, Integer userId,
                                           Integer planId, IProjectService projectService, ICustomerAndPlanService customerAndPlanService,
                                           ICallRecordService callRecordService, RedisCacheUtil redisCacheUtil, Integer isTransfer) {
        Map<String, Object> map = new HashMap<>();
        CallRecord callRecord = new CallRecord();
        int customerId = customer.getId();
        try {
            callRecord.setCustomerID(customerId);
            callRecord.setCustomerPhone(customer.getCustomerPhone());
            callRecord.setStatus(2);
            callRecord.setCallSignal(5);
            callRecord.setProjectID(projectId);
            callRecord.setCustomerID(customer.getId());
            callRecord.setUserPhone(8888L);
            callRecord.setProjectName(isEmpty(projectService.findProjectNameByProjectID(projectId)) ? "模板"
                    : projectService.findProjectNameByProjectID(projectId));
            callRecord.setUserID(userId);
            callRecord.setDatetime(YYYY_MM_DD_HH_MM_SS_SDF.format(new Date()));
            callRecord.setPlanId(planId);
            callRecord.setCustomerGrade(6);
            callRecord.setIsTransfer((isTransfer == 1 ? 2 : 1));
            map.put("CallRecord", callRecord);
            callRecordService.prepareInsertCallRecord(map);
            redisCacheUtil.setCacheObject(userId + "_"+ planId + "_" + customerId, callRecord);
//			customerAndPlanService.updateIsCallByPlanIdAndCustomerId(customer.getId(), planId, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // 修改通话状态
        return callRecord.getId();
    }

    public static JSONObject jsonObject(String customerId, String dialString, String projectId, String userId,
                                        String gateWayId, String callRecordId, String gateWayType, String gateName, Integer isSendSMS,
                                        String gateWayUrl, String dialPorts, String threadUUID, String planId, String customerPhone) {
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
        private_data.put("planId", planId);
        private_data.put("callRecordId", callRecordId);
        private_data.put("userId", userId);
        private_data.put("startTime", String.valueOf(System.currentTimeMillis()));
        private_data.put("gateWayType", gateWayType);
        private_data.put("gatewayId", gateWayId);
        private_data.put("gateName", gateName);
        private_data.put("isSendSMS", String.valueOf(isSendSMS));
        private_data.put("dialPorts", dialPorts);
        private_data.put("thread_uuid", threadUUID);
        private_data.put("customerPhone", customerPhone);
        jsonObject1.put("private_data", private_data);
        jsonObject.put("params", jsonObject1);
        return jsonObject;
    }

    /**
     * 修改客户列表的isCall状态
     * @param customerVector
     * @param planId
     * @param customerAndPlanService
     */
    public static void updateIsCall(Vector<Customer> customerVector, int planId, ICustomerAndPlanService customerAndPlanService,
                                    Vector<Customer> hasCalledCustomerVector) {
        if(customerVector != null && customerVector.size() > 0) {
            int updateCount = customerAndPlanService.updateIsCallByPlanIdAndCustomerVector(customerVector, planId, 0);
            if (updateCount > 0) {
                lg.info("已将[ " + customerVector.size() + " ]个客户的isCall重置为0");
            }
        }
        if(hasCalledCustomerVector != null && hasCalledCustomerVector.size() > 0) {
            int updateCount = customerAndPlanService.updateIsCallByPlanIdAndCustomerVector(hasCalledCustomerVector, planId, 1);
            if (updateCount > 0) {
                lg.info("已将[ " + hasCalledCustomerVector.size() + " ]个客户的isCall重置为1");
            }
        }
    }

    /**
     * 通过线程组获得线程
     *
     * @param threadId
     * @return
     */
    public static Thread findThread(long threadId) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group != null) {
            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            for(int i = 0; i < count; i++) {
                if(threadId == threads[i].getId()) {
                    return threads[i];
                }
            }
            group = group.getParent();
        }
        return null;
    }

    /**
     * 如果是当天立即执行，且当天没有拨打完所下任务，那么制定次日工作时间的任务去执行该任务
     * @param plan
     * @param quartzTaskManager
     * @param customerAndPlanService
     * @param planService
     * @return
     */

    private static boolean isAddTasks(Plan plan, QuartzTaskManager quartzTaskManager,
                                     ICustomerAndPlanService customerAndPlanService, IPlanService planService) throws Exception{
        boolean flag = false;

        String timeStr = plan.getTimeStr();
        String excuteTime = ConstantUtil.YYYY_MM_DD_SDF.format(plan.getUpdateTime());
        int userId = plan.getUserId();
        int planId = plan.getId();
        String planTag = plan.getPlanTag();

        Integer customerCount = customerAndPlanService.findCustomerCountByUserIdAndPlanIdAndIsCall(userId, planId, 0);
        lg.info("计划[ " + planId + " ]还有[ " + customerCount + " ]个客户没有拨打");
        if (customerCount != null && customerCount > 0) {
            lg.info("用户[ " + userId + " ] 的计划[ " + planId + " ]还未完成，即将制定次日的计划");
//            List<Integer> timeList = TransportUtil.stringTransportListForId(timeStr);
            String workStartTime = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "startTime");
			String workEndTime = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "endTime");
            String startTime = "0 0 " + workStartTime.substring(1, 2) + " * * ?";
            String endTime = "0 0 " + workEndTime.substring(0, 2) + " * * ?";
            CacheUtil.planMap.remove(planTag);
            planTag = RandomStringUtils.randomNumeric(20) + System.currentTimeMillis();
            plan.setPlanTag(planTag);
            CacheUtil.planMap.put(planTag, plan);
            planService.updatePlanTag(planTag, planId);
            flag = quartzTaskManager.startTask(startTime, DateUtil.getNextDate(excuteTime), plan, 1, null,
            		quartzTaskManager, planTag);
            if (flag) {
            	lg.info("用户[ " + userId + " ] 的计划[ " + planId + " ]的startTask已启动");
            	flag = quartzTaskManager.endTask(endTime, DateUtil.getNextDate(excuteTime), planTag);
            	if (flag) {
            		lg.info("用户[ " + userId + " ] 的计划[ " + planId + " ]的endTask已启动");
            	}
            }
        }

        return flag;
    }
    
    public static void reSetQuartzTask(Plan plan, ICustomerAndPlanService customerAndPlanService, ICallRecordService callRecordService,
    		QuartzTaskManager quartzTaskManager, IPlanService planService) {
    	
    	lg.info("检查计划id为[ "+ plan.getId() +" ]的计划是否需要重新设置次日的任务，是否在工作时间内的状态为: " + 
    				WorkStartTimeTask.isInWorkTime + " ,计划是否已重设的状态为: " + !plan.isReSet());
    	
    	if(!WorkStartTimeTask.isInWorkTime && !plan.isReSet()) {
    		plan.setReSet(true);
    		try {
				setCallCount(plan, customerAndPlanService, callRecordService);
				isAddTasks(plan, quartzTaskManager, customerAndPlanService, planService);
			} catch (Exception e) {
				plan.setReSet(false);
				e.printStackTrace();
			}
    	}
    }
    
    private static void setCallCount(Plan plan, ICustomerAndPlanService customerAndPlanService, ICallRecordService callRecordService) throws Exception{
    	int planId = plan.getId();
    	int userId = plan.getUserId();
    	List<Integer> allCustomerIdList = customerAndPlanService.findCustomerIdListByUserIdAndPlanId(userId, planId);
		List<Integer> hasCallCustomerIdList = callRecordService.findHasCallCustomerIdList(planId);
		allCustomerIdList.removeAll(hasCallCustomerIdList);
		lg.info("计划[ " + planId + " ], 已经打了[ " + hasCallCustomerIdList.size() 
				+" ]个客户,还有[ " + allCustomerIdList.size() + " ]个客户没有拨打");
		if(allCustomerIdList.size() > 0) {
			Integer updateCount = customerAndPlanService.updateIsCallByPlanIdAndCustomerId(allCustomerIdList, planId, 0);
			lg.info("已经未拨打却标记已拨打的 [ " + updateCount + " ]客户重置为未拨打");
		}
		plan.getCalledCount().addAndGet(hasCallCustomerIdList.size());
		lg.info("计划[ " + planId + " ]重置完成");
    }

    /**
     * 从计划Vector中删除做完的任务
     * @param gateNamePort
     * @param planTag
     * @return
     */
    public static boolean removePlanFromPlanList(String gateNamePort, String planTag) {
        boolean flag = false;
        if(CacheUtil.gatewayAndPlanMap != null) {
            List<Plan> planList = CacheUtil.gatewayAndPlanMap.get(gateNamePort);
            if(planList == null) {
                flag = true;
                return flag;
            }
            for (int i = 0; i < planList.size(); i++) {
                if(planList.get(i).getPlanTag().equals(planTag)) {
                    Plan plan = planList.remove(i);
                    if(plan != null) {
                        flag = true;
                    }
                }
            }
            if(planList.isEmpty()) {
                CacheUtil.gatewayAndPlanMap.remove(gateNamePort);
            }
        }
        return flag;
    }

    /**
     * 计划执行结束后对计划的操作
     * @param plan
     * @param redisCacheUtil
     * @param gateNamePort
     * @param planService
     * @param customerAndPlanService
     */
    public static void afterPlanEnd(int userId, int planId, String planTag, Plan plan, RedisCacheUtil redisCacheUtil, String gateNamePort, IPlanService planService,
                                    ICustomerAndPlanService customerAndPlanService, Vector<Customer> hasCalledCustomerVector, ICallRecordService callRecordService
                                    , int customerCount, QuartzTaskManager quartzTaskManager) {

        int hasCalledCustomerVectorSize = hasCalledCustomerVector.size();
        if(hasCalledCustomerVectorSize > 0) {
            int updateIsCallCount = customerAndPlanService.updateIsCallByPlanIdAndCustomerVector(hasCalledCustomerVector, planId, 1);
            if(updateIsCallCount == hasCalledCustomerVectorSize) {
                lg.info("已将 [ " + planId + " ]hasCalledCustomerVector 中的 " + hasCalledCustomerVectorSize + " 条数据置为已拨打");
                hasCalledCustomerVector.clear();
            }
        }
        
        int hasCallCount = plan.getCalledCount().addAndGet(customerCount);
        lg.info("缓存中的值为： " + hasCallCount + ", 线程vector中的客户为：" + customerCount + " ,计划总量为：" + plan.getCustomerCount());
        if(plan.getCustomerCount() <= hasCallCount) {
        	Integer isCallingCount = customerAndPlanService.findCountByIsCall(planId, 1);
        	if(isCallingCount != null && isCallingCount == 0) {
        		synchronized (plan) {
        			if(!plan.getIsEnd()) {
        				plan.setIsEnd(true);
        				plan.getCallRecordVectoer().clear();
        				int updatePlanStatusCount = planService.updatePlanStatusByPlanId(2, new Date(), planId);
        				plan.setPlanStatus(2);
        				lg.info("计划完成，修改计划id为 " + planId + "的计划状态为已完成");
        				if (updatePlanStatusCount > 0) {
        					lg.info("计划 " + planId + "修改成功");
        					if(gateNamePort != null) {
        						ThreadUtil.removePlanFromPlanList(gateNamePort, planTag);
        					}
        					boolean isRemove = CacheUtil.removePlan(userId, planId);
        					if(isRemove) {
        						lg.info("在缓存中成功删除已完成的计划[ " + planId + " ]");
        					}
        					deletePlanInCatch(planId, planService, quartzTaskManager);
        				}
        			}
        		}
        	}else {
        		lg.info("计划[ " + planId + " ]存在漏打，已重置");
        		synchronized (plan) {
        			try {
						setCallCount(plan, customerAndPlanService, callRecordService);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
        	}
        }else {
        	Integer isCallingCount = customerAndPlanService.findCountByIsCall(planId, 1);
        	if(isCallingCount != null && isCallingCount == 0) {
        		plan.getCalledCount().set(plan.getCustomerCount());
        		lg.info("计划[ " + planId + " ]已执行完，但技术不对，已修改为计划的任务量");
        	}
        }
    }

    /**
     * 拨打电话的动作
     * @param aiDial
     * @param threadUUID
     * @param redisCacheUtil
     * @param customer
     * @param jsonObject
     * @param userId
     * @param planId
     * @return
     */
    public static boolean callAction(String aiDial, String threadUUID, RedisCacheUtil redisCacheUtil, Customer customer,
                                     JSONObject jsonObject, int userId, int planId, String planTag, FinanceService financeService, StaticticsService staticticsService, Integer type,
                                     Integer gatewayId, String gatewayName, ICallRecordService callRecordService, ICustomerAndPlanService customerAndPlanService) {
        boolean flag = false;
        
        boolean isDelete = redisCacheUtil.delete(threadUUID + "aiDial");
        if(!isDelete) {
        	redisCacheUtil.setCacheObject(threadUUID + "aiDial", null);
        }
        
        int statusCode = HttpRequestUtil.doPost(FREESWITCH_POST, jsonObject);
        String customerPhone = customer.getCustomerPhone();
        int customerId = customer.getId();

        lg.info("[" + threadUUID + "] 呼出，拨打电话  " + customerPhone);
        int iCount = 0;
        int maxCount = ((statusCode == 200) ? 100 : 24);
        
        do {
            SleepUtil.sleep(5000);
            try {
                aiDial = (String) redisCacheUtil.getCacheObject(threadUUID + "aiDial");
            } catch (Exception e) {
                aiDial = null;
                break;
            }
            lg.info(threadUUID + "~" + aiDial + " 第 [ " + iCount + "|" + maxCount + " ]次等" + customerPhone
                    + "打完电话");
            iCount = iCount + 1;
        } while ((("null").equals(aiDial) || aiDial == null) && (iCount < maxCount));

        if ((("over").equals(aiDial)) || (iCount == maxCount)) {
            //将已拨打完电话的用户加到 IsCalledCustomerList 中
            try {
				redisCacheUtil.setRedisIsCalledCustomerList(customer, threadUUID);
			} catch (Exception e) {
				List<Integer> customerIdList = new ArrayList<Integer>();
				customerIdList.add(customer.getId());
				Integer updateCount = customerAndPlanService.updateIsCallByPlanIdAndCustomerId(customerIdList, planId, 1);
				if(updateCount > 0) {
					lg.info("用户 " + customer + " 加入redis缓存失败，单独修改其是否拨打状态");
				}
			}
            //获取通话结束的通话记录，添加到RedisCallRecordList 中
            CallRecord callRecord = (CallRecord) redisCacheUtil.getCacheObject(userId + "_" + planId + "_" + customerId);

            if(callRecord.getCustomerGrade() < 6 && type == 2) {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("linkPhone", customerPhone);
                param.put("sipId", gatewayId);
                param.put("sipName", gatewayName);
                param.put("callTime", callRecord.getDatetime());
                param.put("longTime", callRecord.getDurationTime());
                param.put("userId", userId);
                financeService.insertUserConsumRecord(param);
                lg.info(customerPhone + "  的通话账单添加成功");
            }
            //数据统计数据修改操作
            //[duratTotal,通话时长 toStaffCount, 转接数 callCount,总呼出量 status 状态（1 为已接听，2, 未接听）  customer AI意向级默认F, userId，用户ID]

            Integer duratTotal=callRecord.getDurationTime();
//            Integer toStaffCount=callRecord.getIsTransfer();//
            Integer callCount=1;
            Integer status=callRecord.getStatus()==1?1:2;
            Integer customers=callRecord.getCustomerGrade();
            staticticsService.insertStatictics(duratTotal, callRecord.getIsTransfer() ,callCount ,status , customers,userId);
            Integer updateCount = callRecordService.updateCallRecordStatusById(callRecord.getId(), callRecord.getStatus(), callRecord.getDurationTime(), callRecord.getCustomerGrade(), callRecord.getFileID());
            if(updateCount != null && updateCount != 0) {
                lg.info("计划 [ " + planId + " ]的通话记录[ " + callRecord.getId() + " ]修改成功");
            }
            flag = true;
        }
        return flag;
    }

    /**
     * 暂停或取消计划
     * @param plan
     * @param gateNamePort
     * @param redisCacheUtil
     * @param customerAndPlanService
     * @param customerVector
     * @param quartzTaskManager
     * @param planService
     * @param callRecordService
     * @return
     */
    public static boolean pauseOrEndOrCancel(Plan plan, String gateNamePort, RedisCacheUtil<?> redisCacheUtil,
                                             ICustomerAndPlanService customerAndPlanService, Vector<Customer> customerVector,
                                             QuartzTaskManager quartzTaskManager, IPlanService planService, ICallRecordService callRecordService,
                                             String threadUUID, Vector<Customer> hasCalledCustomerVector) {
        boolean flag = false;
        int planId = plan.getId();

        if (plan.getIsEnd() || !WorkStartTimeTask.isInWorkTime || PlanStatusUtil.planStatus(plan.getPlanStatus())) {// IsEnd值在EndTask中的定时任务中设置,只用来衡量子计划是否结束
            updateIsCall(customerVector, planId, customerAndPlanService, hasCalledCustomerVector);
            customerVector.clear();
            plan.getCalledCount().addAndGet(hasCalledCustomerVector.size());
            lg.info("id为[ " + planId + " ] 的计划的子计划已执行完");
            flag = true;
        }
        return flag;
    }

    /**
     * 向数据库中添加通话记录
     * @param redisCacheUtil
     * @param callRecordService
     * @param userId
     * @param planId
     * @param customerVector
     * @return
     */
    public static boolean callRecordIntoDB(RedisCacheUtil<?> redisCacheUtil, ICallRecordService callRecordService, int userId,
                                           int planId, Vector<Customer> customerVector, String threadUUID, List<CallRecord> callRecordList) {
// 		List<CallRecord> callRecordeList = redisCacheUtil.getRedisCallRecordList(threadUUID);
        if(customerVector != null && callRecordList.size() > 0) {
            Integer count = callRecordService.updateCallRecorde(callRecordList);
            if(count != null && count > 0) {
//				redisCacheUtil.setCacheObject(threadUUID + ConstantUtil.REDIS_CALLRECORDELIST, null);
                lg.info("添加了[ " + count + " ]条通话记录");
                return true;
            }
        }
        return false;
    }

    /**
     * 暂停或取消时，将已拨打的用户置为已拨打状态
     * @param redisCacheUtil
     * @param customerAndPlanService
     * @param userId
     * @param planId
     * @param customerVector
     * @param threadUUID
     * @return
     */
    public static boolean setIsCall(RedisCacheUtil<?> redisCacheUtil, ICustomerAndPlanService customerAndPlanService, int userId,
                                    int planId, String threadUUID) {
        Vector<Customer> customerList = (Vector<Customer>) redisCacheUtil.getCacheObject(threadUUID + "_isCalledCustomer");
        if(customerList != null && customerList.size() > 0) {
            Integer updateCount = customerAndPlanService.updateIsCallByPlanIdAndCustomerVector(customerList, planId, 1);
            if(updateCount != null || updateCount != null && updateCount > 0) {
                redisCacheUtil.setCacheObject(threadUUID + "_isCalledCustomer", null);
                lg.info("成功将[ " + updateCount + " ]条客户修改为已拨打,且将[ " + threadUUID + "_isCalledCustomer ]置空");
            }
        }
        return false;
    }
    
    public static boolean deletePlanInCatch(int planId, IPlanService planService, QuartzTaskManager quartzTaskManager) {
		Plan plan = planService.findPlanInfoByPlanId(planId);
		String planTag = plan.getPlanTag();
		int userId = plan.getUserId();
		boolean flag = quartzTaskManager.deleteJob(planTag, "_startJob", "_startTrigger");
		if (flag) {// 如果开始定时任务删除成功，删除结束任务
			flag = quartzTaskManager.deleteJob(planTag, "_endJob", "_endTrigger");
			if (flag) {
				lg.info("用户 " + userId + " 成功取消了计划 " + planId + " 结束的定时任务");
			} else {
				lg.info("用户 " + userId + " 取消计划 " + planId + " 结束的定时任务失败");
			}
		} else {
			lg.info("用户 " + userId + " 取消计划 " + planId + " 开始的定时任务失败");
		}
		return flag;
	}
}
