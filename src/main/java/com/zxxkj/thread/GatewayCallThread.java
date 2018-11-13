package com.zxxkj.thread;

import static com.zxxkj.util.ConstantUtil.FREESWITCH_DIAL_SIP;
import static com.zxxkj.util.HTTPUtil.sendGet;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.model.Customer;
import com.zxxkj.model.Plan;
import com.zxxkj.quartz.QuartzTaskManager;
import com.zxxkj.service.ICallRecordService;
import com.zxxkj.service.ICustomerAndPlanService;
import com.zxxkj.service.ICustomerService;
import com.zxxkj.service.IPlanService;
import com.zxxkj.service.IProjectService;
import com.zxxkj.service.StaticticsService;
import com.zxxkj.task.WorkStartTimeTask;
import com.zxxkj.util.CacheUtil;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.SleepUtil;
import com.zxxkj.util.TransportUtil;

public class GatewayCallThread extends Thread {

	private static final Logger logger = Logger.getLogger(GatewayCallThread.class);
	public static boolean isEnd = true;

	private RedisCacheUtil redisCacheUtil;
	private ICallRecordService callRecordService;
	private ICustomerService customerService;
	private IProjectService projectService;
	private IPlanService planService;
	private ICustomerAndPlanService customerAndPlanService;
	private Integer port;
	private String gateNamePort;
	private String gatewayUrl;
	private String gatewayAuth;
	private String gatewayPassword;
	private QuartzTaskManager quartzTaskManager;
	private StaticticsService staticticsService;

	public GatewayCallThread(RedisCacheUtil redisCacheUtil, ICallRecordService callRecordService,
			ICustomerService customerService, IProjectService projectService,
			ICustomerAndPlanService customerAndPlanService, IPlanService planService, Integer port, String gateNamePort,
			String gatewayUrl, String gatewayAuth, String gatewayPassword, QuartzTaskManager quartzTaskManager,
			StaticticsService staticticsService) {
		super();
		this.redisCacheUtil = redisCacheUtil;
		this.callRecordService = callRecordService;
		this.customerService = customerService;
		this.projectService = projectService;
		this.planService = planService;
		this.customerAndPlanService = customerAndPlanService;
		this.port = port;
		this.gateNamePort = gateNamePort;
		this.gatewayUrl = gatewayUrl;
		this.gatewayAuth = gatewayAuth;
		this.gatewayPassword = gatewayPassword;
		this.quartzTaskManager = quartzTaskManager;
		this.staticticsService = staticticsService;
	}

	@Override
	public void run() {
		String threadUUID = RandomStringUtils.randomAlphanumeric(20) + "_" + System.currentTimeMillis();

		Thread.currentThread().setName(gateNamePort);
		logger.info("threadName:" + Thread.currentThread().getName() + "****** TheadId : "
				+ Thread.currentThread().getId());

		while (true) {
			List<Plan> planList = CacheUtil.gatewayAndPlanMap.get(gateNamePort);

			Plan plan = null;
			logger.info("planList 为： " + planList);
			if (planList == null || planList.isEmpty()) {
				logger.info("planList 为空");
				CacheUtil.gatewayAndPlanMap.remove(gateNamePort);
				break;
			}
			int planListSize = planList.size();
			int random = ConstantUtil.random.nextInt(planListSize) % (planListSize + 1);

			plan = planList.get(random);

			String planTag = plan.getPlanTag();
			plan = CacheUtil.planMap.get(planTag);
			if (plan == null) {
				logger.info("planMap 中无planTag为 " + planTag + " 此计划");
				plan = planList.get(random);
				CacheUtil.planMap.put(planTag, plan);
			}
			plan.setGateName(gateNamePort.split("_")[0]);

			if (!WorkStartTimeTask.isInWorkTime) {
				
				logger.info("已到工作时间的结束时间");
				break;
			}

			call(redisCacheUtil, callRecordService, customerService, projectService, customerAndPlanService, plan,
					planService, port, threadUUID, quartzTaskManager, gatewayAuth, gatewayPassword, gatewayUrl,
					staticticsService);
			SleepUtil.sleep(getRadomNum());
		}
	}

	private static void call(RedisCacheUtil<Integer> redisCacheUtil, ICallRecordService callRecordService,
			ICustomerService customerService, IProjectService projectService,
			ICustomerAndPlanService customerAndPlanService, Plan plan, IPlanService planService, Integer port,
			String threadUUID, QuartzTaskManager quartzTaskManager, String gatewayAuth, String gatewayPassword,
			String gatewayUrl, StaticticsService staticticsService) {

		int planId = plan.getId();
		int userId = plan.getUserId();
		String planTag = plan.getPlanTag();
		String outBoundUrl = FREESWITCH_DIAL_SIP;

		String gateName = plan.getGateName();
		outBoundUrl = outBoundUrl + gateName + "/";

		Customer customer = null;
		Vector<Customer> customerVector = null;
		try {
			customerVector = ThreadUtil.getCustomerVector(plan, customerAndPlanService, redisCacheUtil);
			if (customerVector == null) {
				logger.info("计划[ " + planId + " ]没有可拨打的客户");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		int customerCount = customerVector.size();
		logger.info("******************************" + customerVector);
		Vector<Customer> hasCalledCustomerVector = new Vector<Customer>();

		while (true) {
			logger.info("用户[ " + userId + " ]的计划 [ " + planId + " ]查找下一个未拨打客户,该计划的isEnd为  " + plan.getIsEnd());

			customer = ThreadUtil.getNextCustomer(customerAndPlanService, redisCacheUtil, callRecordService,
					customerVector, userId, planId, threadUUID, hasCalledCustomerVector);
			if (customer == null && (customerVector.size() == 0 || customerVector == null)) {
				ThreadUtil.afterPlanEnd(userId, planId, planTag, plan, redisCacheUtil, null, planService,
						customerAndPlanService, hasCalledCustomerVector, callRecordService, customerCount,
						quartzTaskManager);
				break;
			}
			logger.info("##########################" + customer.getCustomerPhone());

			String customerPhone = customer.getCustomerPhone();
			if (plan.getCallRecordVectoer().contains(customerPhone)) {
				logger.info(customerPhone + " 已经拨打过了，拨打下一个");
				continue;
			}
			plan.getCallRecordVectoer().add(customerPhone);

			Integer n = 0;
			int waitCount = 0;
			while (true && n == 0 && waitCount < 100) {
				List<Map<String, String>> mapList = sendGet(gatewayUrl,
						"port=" + port + "&&info_type=imei,imsi,iccid,smsc,type,number,reg,slot,callstate,signal,gprs",
						gatewayAuth, gatewayPassword);

				n = mapList.size();
				waitCount = waitCount + 1;
				logger.info("第[ " + waitCount + "|100 ]请求网关端口是否空闲");
				SleepUtil.sleep(3000);
			}

			int loopCount = 0;
			int customerId = customer.getId();
			Integer callRecordId = ThreadUtil.createCallRecord(plan.getProjectId(), customer, userId, planId,
					projectService, customerAndPlanService, callRecordService, redisCacheUtil, plan.getIsTransfer());
			JSONObject jsonObject = null;
			Integer prifix = 1000 + Integer.valueOf(port);

			jsonObject = ThreadUtil.jsonObject(String.valueOf(customerId), outBoundUrl + prifix + customerPhone,
					String.valueOf(plan.getProjectId()), String.valueOf(userId), String.valueOf(plan.getGatewayId()),
					String.valueOf(callRecordId), "gateway", gateName, plan.getIsSendSMS(), plan.getUrl(),
					plan.getTrancferPortListStr(), threadUUID, String.valueOf(plan.getId()), customerPhone);

			String aiDial = null;
			redisCacheUtil.delete(threadUUID + "aiDial");
			do {
				if (ThreadUtil.callAction(aiDial, threadUUID, redisCacheUtil, customer, jsonObject, userId, planId,
						planTag, null, staticticsService, 1, null, null, callRecordService, customerAndPlanService)) {

					break;
				}
				aiDial = (String) redisCacheUtil.getCacheObject(threadUUID + "aiDial");
				loopCount = loopCount + 1;
			} while (("wait").equals(aiDial) && (loopCount < 1));

			if (ThreadUtil.pauseOrEndOrCancel(plan, gateName + "_" + port, redisCacheUtil, customerAndPlanService,
					customerVector, quartzTaskManager, planService, callRecordService, planTag,
					hasCalledCustomerVector)) {
				logger.info("计划[ " + planId + " ]已暂停");
            	
				ThreadUtil.removePlanFromPlanList(gateName + "_" + port, planTag);
				List<Integer> time = TransportUtil.stringTransportListForId(plan.getTimeStr());
				if(plan.getIsEnd() && (time.size() == 0 || time.size() > 10)) {
					ThreadUtil.reSetQuartzTask(plan, customerAndPlanService, callRecordService, quartzTaskManager, planService);
				}
				break;
			}
		}
	}

	/**
	 * 1-3000取一个随机数
	 * 
	 * @return
	 */
	private static Long getRadomNum() {
		Long time = (long) (ConstantUtil.random.nextInt(3000) % (3001));

		return time;
	}
}
