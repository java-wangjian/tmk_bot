package com.zxxkj.thread;

import static com.zxxkj.util.ConstantUtil.FREESWITCH_DIAL_SIP;

import java.util.List;
import java.util.Vector;

import com.zxxkj.service.*;
import com.zxxkj.task.WorkStartTimeTask;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.model.Customer;
import com.zxxkj.model.Plan;
import com.zxxkj.quartz.QuartzTaskManager;

public class CallThread implements Runnable {

    private static final Logger lg = Logger.getLogger(CallThread.class);
    private RedisCacheUtil redisCacheUtil;
    private ICallRecordService callRecordService;
    private IProjectService projectService;
    private IPlanService planService;
    private ICustomerAndPlanService customerAndPlanService;
    private Plan plan;
    private QuartzTaskManager quartzTaskManager;
    private FinanceService financeService;
    //数据统计
    private StaticticsService staticticsService;

    public CallThread(RedisCacheUtil redisCacheUtil, ICallRecordService callRecordService,
                      ICustomerService customerService, IProjectService projectService,
                      ICustomerAndPlanService customerAndPlanService, Plan plan, IPlanService planService,
                      QuartzTaskManager quartzTaskManager, FinanceService financeService, StaticticsService staticticsService) {
        super();
        this.redisCacheUtil = redisCacheUtil;
        this.callRecordService = callRecordService;
        this.projectService = projectService;
        this.planService = planService;
        this.customerAndPlanService = customerAndPlanService;
        this.plan = plan;
        this.quartzTaskManager = quartzTaskManager;
        this.financeService = financeService;
        this.staticticsService = staticticsService;
    }

    @Override
    public void run() {
        String threadUUID = RandomStringUtils.randomAlphanumeric(20) + "_" + System.currentTimeMillis();
        int planId = plan.getId();
        int userId = plan.getUserId();
        String planTag = plan.getPlanTag();

        String outBoundUrl = FREESWITCH_DIAL_SIP;
        String gateName = plan.getGateName();
        outBoundUrl = outBoundUrl + gateName + "/";

        Customer customer = null;
        String customerPhone = null;
        Vector<Customer> customerVector = null;
		try {
			customerVector = ThreadUtil.getCustomerVector(plan, customerAndPlanService, redisCacheUtil);
			if(customerVector == null) {
				lg.info("计划[ " + planId + " ]没有可拨打的客户");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        int customerCount = customerVector.size();
        Vector<Customer> hasCalledCustomerVector = new Vector<Customer>();
        
        while (true) {
            lg.info("用户[ " + userId + " ]的计划 [ " + planId + " ]查找下一个未拨打客户,该计划的isEnd为  " + plan.getIsEnd() + ",计划的状态为 " + plan.getPlanStatus());

            customer = ThreadUtil.getNextCustomer(customerAndPlanService, redisCacheUtil, callRecordService,
                    customerVector, userId, planId, threadUUID, hasCalledCustomerVector);
          
            if (customer == null) {
                ThreadUtil.afterPlanEnd(userId, planId, planTag, plan, redisCacheUtil, null, planService,
                        customerAndPlanService, hasCalledCustomerVector, callRecordService, customerCount,
                        quartzTaskManager);
                break;
            }

            int loopCount = 0;
            int customerId = customer.getId();
            customerPhone = customer.getCustomerPhone();
            
            if(plan.getCallRecordVectoer().contains(customerPhone)) {
            	
            	lg.info(customerPhone + " 已经拨打过了，拨打下一个");
            	continue;
            }
            plan.getCallRecordVectoer().add(customerPhone);
            Integer callRecordId = ThreadUtil.createCallRecord(plan.getProjectId(), customer, userId, planId,
                    projectService, customerAndPlanService, callRecordService, redisCacheUtil, 2);
            JSONObject jsonObject = ThreadUtil.jsonObject(String.valueOf(customerId),
                    outBoundUrl + (plan.getPrifix() == null ? "" : plan.getPrifix()) + customerPhone,
                    String.valueOf(plan.getProjectId()), String.valueOf(userId), String.valueOf(plan.getGatewayId()),
                    String.valueOf(callRecordId), "sipLine", gateName, 2, null, null, threadUUID,
                    String.valueOf(planId), customerPhone);
            String aiDial = null;
            redisCacheUtil.delete(threadUUID + "aiDial");
            //增加数据统计接口
            do {
                if (ThreadUtil.callAction(aiDial, threadUUID, redisCacheUtil, customer, jsonObject, userId,
                        planId, threadUUID, financeService, staticticsService,2, plan.getGatewayId(), gateName, 
                        callRecordService, customerAndPlanService)) {

                    break;
                }
                aiDial = (String) redisCacheUtil.getCacheObject(threadUUID + "aiDial");
                loopCount = loopCount + 1;
            } while (("wait").equals(aiDial) && (loopCount < 3));

            if (ThreadUtil.pauseOrEndOrCancel(plan, null, redisCacheUtil, customerAndPlanService, customerVector,
                    quartzTaskManager, planService, callRecordService, threadUUID, hasCalledCustomerVector)) {
            	
            	break;
            }
        }
    }
}
