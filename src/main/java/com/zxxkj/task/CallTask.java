package com.zxxkj.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.zxxkj.service.*;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.model.Gateway;
import com.zxxkj.model.Plan;
import com.zxxkj.quartz.QuartzTaskManager;
import com.zxxkj.thread.CallThread;
import com.zxxkj.thread.GatewayCallThread;
import com.zxxkj.thread.ThreadPool;
import com.zxxkj.thread.ThreadUtil;
import com.zxxkj.util.CacheUtil;
import com.zxxkj.util.PlanStatusUtil;
import com.zxxkj.util.SleepUtil;
import com.zxxkj.util.TransportUtil;

public class CallTask implements Job {

	private static final Logger LOGGER = Logger.getLogger(CallTask.class);
	public static boolean start = true;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("进入 CallTask 类的 execute()方法中");
		Scheduler scheduler = context.getScheduler();
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		Trigger trigger = context.getTrigger();
		String planTag = (String) jobDataMap.get("planTag");
		Integer userId = (Integer) jobDataMap.get("userId");
		ICustomerService customerService = (ICustomerService) jobDataMap.get("customerService");
		IPlanService planService = (IPlanService) jobDataMap.get("planService");
		ICallRecordService callRecordService = (ICallRecordService) jobDataMap.get("callRecordService");
		RedisCacheUtil redisCacheUtil = (RedisCacheUtil) jobDataMap.get("redisCacheUtil");
		IGatewayService gatewayService = (IGatewayService) jobDataMap.get("gatewayService");
		IProjectService projectService = (IProjectService) jobDataMap.get("projectService");
		ICustomerAndPlanService customerAndPlanService = (ICustomerAndPlanService) jobDataMap
				.get("customerAndPlanService");
		QuartzTaskManager quartzTaskManager = (QuartzTaskManager) jobDataMap.get("quartzTaskManager");
        FinanceService financeService = (FinanceService) jobDataMap.get("financeService");
        StaticticsService staticticsService=(StaticticsService)jobDataMap.get("staticticsService");
        
		handler(trigger, scheduler, planTag, customerService, planService, callRecordService, redisCacheUtil,
				gatewayService, projectService, customerAndPlanService, quartzTaskManager, userId, financeService,staticticsService);
	}

	private void handler(Trigger trigger, Scheduler scheduler, String planTag, ICustomerService customerService,
			IPlanService planService, ICallRecordService callRecordService, RedisCacheUtil redisCacheUtil,
			IGatewayService gatewayService, IProjectService projectService,
			ICustomerAndPlanService customerAndPlanService, QuartzTaskManager quartzTaskManager, Integer userId1,
                         FinanceService financeService,StaticticsService staticticsService) {
		LOGGER.info("进入 定时器的handler");
		Plan plan = CacheUtil.planMap.get(planTag);
		if(plan == null) {
			LOGGER.info("CacheUtil.planMap中无此计划，去库里查询");
			plan = planService.findPlanInfoByPlanTag(planTag, userId1);
			CacheUtil.planMap.put(planTag, plan);
		}
		
		plan.setIsEnd(false);
		int planId = plan.getId();
		int userId = plan.getUserId();
		int gatewayId = plan.getGatewayId();
		String gateName = plan.getGateName();
		LOGGER.info("计划 [ " + plan.getId() + " ]的isEnd值为  " + plan.getIsEnd() + " ,计划状态为 " + plan.getPlanStatus());
		Gateway gateway = gatewayService.findGatewayInfoByGatewayId(gatewayId);
		LOGGER.info("网关 [ " + gatewayId + " ]的详细信息已查到, " + gateway.toString());
		Integer updateCount = planService.updatePlanStatusByPlanId(1, null, planId);//将计划设置为执行中
		plan.setPlanStatus(1);
		if(updateCount > 0) {
			LOGGER.info("已将计划[ " + planId + " ]置为执行中");
		}
		Integer gatewayType = gateway.getType();
		int callCount = 0;
		String[] callPortArr = null;
		ThreadPool threadPool = null;

		if (start) {
			redisCacheUtil.setCacheObjectTimeOut("planId_" + planId + "_isTransfer", plan.getIsTransfer(), 60 * 12);
			if (1 == gatewayType) {// 1表示网关设备呼出
				Vector<Plan> planList = null;
				callPortArr = plan.getCallPortListStr().split(",");
				callCount = callPortArr.length;
				plan.setSipCallCount(callCount);
				plan.setUrl(gateway.getUrl());

				for (int i = 0; i < callPortArr.length; i++) {
					String port = callPortArr[i];
					String gateNamePort = gateName + "_" + port;

					try {
						planList = planService.findPlanListByGatewayIdAndPort(port, gatewayId, userId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!CacheUtil.gatewayAndPlanMap.containsKey(gateNamePort)) {
						planList = new Vector<Plan>();
						planList.add(plan);
						CacheUtil.gatewayAndPlanMap.put(gateNamePort, planList);
						new GatewayCallThread(redisCacheUtil, callRecordService, customerService, projectService,
								customerAndPlanService, planService, Integer.valueOf(port), gateNamePort,
								gateway.getUrl(), gateway.getAuth(), gateway.getPwd(), quartzTaskManager,
								staticticsService).start();
					}else {
						LOGGER.info("gatewayAndPlanMap 已包含" + gateNamePort);
						planList = CacheUtil.gatewayAndPlanMap.get(gateNamePort);
						if(planList == null) {
							planList = new Vector<Plan>();
						}
						List<Integer> planIdList = new ArrayList<Integer>();
						for (Plan plan2 : planList) {
							planIdList.add(plan2.getId());
						}
						if(!planIdList.contains(planId)) {
							planList.add(plan);
						}
						CacheUtil.gatewayAndPlanMap.put(gateNamePort, planList);
						ThreadGroup group = Thread.currentThread().getThreadGroup();
						Thread[] threads = new Thread[(int)(group.activeCount() + 2)];
						int count = group.enumerate(threads, true);
						boolean flag = false;
						for(int j = 0; j < count; j++) {
							if((gateNamePort).equals(threads[j].getName())) {
								flag = true;
								break;
							}
						}
						if(!flag) {
							LOGGER.info("之前的 " + gateNamePort + " 已执行完，重新创建任务线程");
							new GatewayCallThread(redisCacheUtil, callRecordService, customerService, projectService,
									customerAndPlanService, planService, Integer.valueOf(port), gateNamePort,
									gateway.getUrl(), gateway.getAuth(), gateway.getPwd(), quartzTaskManager,
									staticticsService).start();
						}
					}
				}

				LOGGER.info("[ " + gateway.getGatewayNumbers() + " ]网关设备呼出");
			} else if (2 == gatewayType) {// 2表示sip线路呼出
				callCount = Integer.valueOf(plan.getCallPortListStr());
				plan.setSipCallCount(callCount);
				plan.setPrifix(gateway.getGateway_sn());

				LOGGER.info(plan.getId() + " sip线路呼出");
				Map<String, ThreadPool> threadPoolMap = CacheUtil.userThreadPoolMap.get("userId_" + userId);
				if (threadPoolMap == null) {
					LOGGER.info("用户 " + userId + " 没有线程池map，新创建一个" );
					threadPoolMap = new HashMap<String, ThreadPool>();
					threadPool = new ThreadPool(callCount, 1);
					threadPoolMap.put(gateName, threadPool);
					LOGGER.info(userId + " 没有化线程池  [ " + gateName + " ],初始化该线程池");
					CacheUtil.userThreadPoolMap.put("userId_" + userId, threadPoolMap);
					threadListner(userId, gateName, plan, quartzTaskManager);
				} else {
					LOGGER.info("用户 " + userId + " 有线程池map" );
					if (threadPoolMap.containsKey(gateName)) {
						threadPool = threadPoolMap.get(gateName);
						if(threadPool != null) {
							if(threadPool.isShutdown()) {//如果没有任务，上次实例化的线程池被回收了
								threadPool = new ThreadPool(callCount, 1);
								threadPoolMap.put(gateName, threadPool);
							}
							LOGGER.info("用户 " + userId + " 有 " + gateName + " 线程池");
						}else {
							threadPool = new ThreadPool(callCount, 1);
							threadPoolMap.put(gateName, threadPool);
							LOGGER.info("用户 " + userId + " 没有 " + gateName + " 线程池，已初始化");
						}
						threadPool.setCorePoolSize(callCount);
						threadPool.setMaximumPoolSize(callCount);
						LOGGER.info(userId + " 已经初始化线程池  [ " + gateName + " ]");
					} else {
						LOGGER.info(userId + " 没有化线程池  [ " + gateName + " ],初始化该线程池");
						threadPool = new ThreadPool(callCount, 1);
						threadPoolMap.put(gateName, threadPool);
					}
				}
				sipCall(threadPool, plan, customerService, planService, callRecordService, callCount, redisCacheUtil,
						gatewayService, projectService, customerAndPlanService, quartzTaskManager, gatewayType,
						callPortArr, financeService,staticticsService);
				LOGGER.info("[ " + gateName + " ]sip线路呼出,前缀为[ " + plan.getPrifix() + " ]呼出上限为[ " + callCount + " ]");
			}
			LOGGER.info("定时已开启");
		} else {
			LOGGER.info("定时已关闭");
		}
	}

	/**
	 * sip线路拨打方法
	 *
	 * @param threadPool
	 * @param plan
	 * @param customerService
	 * @param planService
	 * @param callRecordService
	 * @param i
	 * @param redisCacheUtil
	 * @param gatewayService
	 * @param projectService
	 * @param customerAndPlanService
	 */
	private void sipCall(ThreadPool threadPool, Plan plan, ICustomerService customerService, IPlanService planService,
			ICallRecordService callRecordService, int i, RedisCacheUtil redisCacheUtil, IGatewayService gatewayService,
			IProjectService projectService, ICustomerAndPlanService customerAndPlanService,
			QuartzTaskManager quartzTaskManager, int gatewayType, String[] callPortArr, FinanceService financeService,StaticticsService staticticsService) {
		Integer activeCount = null;
		Integer threadPoolSize = threadPool.getMaximumPoolSize();
		int planId = plan.getId();
		int userId = plan.getUserId();

		while (true) {
			LOGGER.info("计划 [ " + planId + " ] 的isEnd值为 " + plan.getIsEnd());
			if (plan.getIsEnd() || PlanStatusUtil.planStatus(plan.getPlanStatus())) {
				LOGGER.info(planId + " 计划停止");
				List<Integer> time = TransportUtil.stringTransportListForId(plan.getTimeStr());
				if(plan.getIsEnd() && (time.size() == 0 || time.size() > 10)) {
					ThreadUtil.reSetQuartzTask(plan, customerAndPlanService, callRecordService, quartzTaskManager, planService);
				}
				break;
			}
			if (!WorkStartTimeTask.isInWorkTime) {
				LOGGER.info("非工作时间,跳出监听循环");
				ThreadUtil.reSetQuartzTask(plan, customerAndPlanService, callRecordService, quartzTaskManager, planService);
				break;
			}
			LOGGER.info("用户 " + userId + " 的线程池大小为： " + threadPool.getPoolSize() + "-------" + threadPool.getTaskCount());
			activeCount = threadPool.getActiveCount();
			synchronized (threadPool) {
				LOGGER.info("活跃线程数为  " + activeCount);
				if(activeCount < threadPoolSize) {
					LOGGER.info("活跃线程数小于总线程数，唤醒线程");
					threadPool.execute(new CallThread(redisCacheUtil, callRecordService, customerService,
							projectService, customerAndPlanService, plan, planService, quartzTaskManager, financeService,staticticsService));
				}
			}
			SleepUtil.sleep(5000);
		}
	}

	/**
	 * 监听线程池的变化，若没有活跃线程就将该线程池回收
	 * @param userId
	 * @param gateName
	 * @param planTag
	 * @param quartzTaskManager
	 */
	private void threadListner(int userId, String gateName, Plan plan, QuartzTaskManager quartzTaskManager) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ThreadPool threadPool = null;
				Integer activeCount = null;

				while (true) {
					SleepUtil.sleep(5000);
					threadPool = threadIsEmpty(userId, gateName);
					if (threadPool != null) {
						activeCount = threadPool.getActiveCount();
						LOGGER.info("线程池的活跃线程数： [ " + activeCount + " ]");
						LOGGER.info(CacheUtil.getUserPlanVactor(userId).size() + "-------" +
								CacheUtil.getUserPlanVactor(userId));
						if (activeCount == 0 && CacheUtil.getUserPlanVactor(userId).size() == 0 &&
								threadPool.getTaskCount() == 0 && threadPool.getQueue().size() == 0) {

							break;
						}
					}
					if (threadPool == null) {
						LOGGER.info("线程池为空，跳出监听");
						break;
					}
				}
				if (threadPool != null) {
					threadPool.shutdownNow();
					if (threadPool.isShutdown()) {
						LOGGER.info("线程池已回收");
					}
				}
			}
		}).start();
	}

	/**
	 * 判断用户的对应线路的线程池是否为空
	 * @param userId
	 * @param gateName
	 * @return true：为空
	 * 		   false:不为空
	 */
	private ThreadPool threadIsEmpty(int userId, String gateName) {
		ThreadPool threadPool = null;
		Map<String, ThreadPool> threadPoolMap = CacheUtil.userThreadPoolMap
				.get("userId_" + userId);
		if (threadPoolMap != null && threadPoolMap.containsKey(gateName)) {
			threadPool = threadPoolMap.get(gateName);
		}
		return threadPool;
	}
}
