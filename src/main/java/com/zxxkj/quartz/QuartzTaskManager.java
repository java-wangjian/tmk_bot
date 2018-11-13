package com.zxxkj.quartz;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import com.zxxkj.service.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.model.CustomerAndPlan;
import com.zxxkj.model.Plan;
import com.zxxkj.task.CallTask;
import com.zxxkj.task.EndTask;
import com.zxxkj.util.CacheUtil;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.SleepUtil;
import com.zxxkj.util.TransportUtil;

/**
 * 
 * @Program Name : Quartz.com.hamob.task.QuartzTaskManager.java
 * @Written by : tianyp
 * @Creation Date : 2012-8-21 下午2:46:16
 * @version : v4.00
 * @Description : quartz Copyright (c) 2012 by Hamob, Inc. All rights reserved.
 * 
 * 
 * @ModificationHistory Who When What -------- ----------
 *                      ------------------------------------------------ tianyp
 *                      2012-8-21下午2:46:16 TODO
 * 
 */

@Component("quartzTaskManager")
public class QuartzTaskManager {

	private static final Logger LOGGER = Logger.getLogger(QuartzTaskManager.class);
	private static SchedulerFactory schedulerFactory = QuartzSchedulerFactory.schedulerFactory;
	
	@Autowired
	private ICustomerService customerService;
	@Autowired
	private IPlanService planService;
	@Autowired
	private ICallRecordService callRecordService;
	@Autowired
	private RedisCacheUtil redisCacheUtil;
	@Autowired
	private IGatewayService gatewayService;
	@Autowired
	private IProjectService projectService;
	@Autowired
	private ICustomerAndPlanService customerAndPlanService;
	@Autowired
    private FinanceService financeService;
	@Autowired
	private StaticticsService staticticsService;
	
	/**
	 * 
	 * @param time
	 * @param startTime
	 * @param endTime
	 * @param plan
	 * @param isAdd
	 *            计划是否为新添加；0表示新添加，1表示不是新添加
	 * @param customerIdList
	 * @return
	 */
	public boolean startTask(String time, Date startTime, Plan plan, int isAdd,
			List<Integer> customerIdList, QuartzTaskManager quartzTaskManager, String planTag) {
		LOGGER.info("Come into startTask of Class QuartzTaskManager !");
		boolean flag = false;

		try {
			LOGGER.info("开启定时任务的时间为 ：" + time);
			flag = addJob(ConstantUtil.JOBNAME_BASE_START + plan.getPlanTag() + "_startJob", time, startTime,
					plan, isAdd, customerIdList, quartzTaskManager, planTag, staticticsService);
		} catch (ParseException e) {
			LOGGER.info("trigger定时失败");
			LOGGER.error(e);
			e.printStackTrace();
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			LOGGER.info(" Scheduler对象或JobDetail对象以及CronTrigger可能为空");
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 结束工作任务的定时任务
	 * 
	 * @param time
	 * @param i
	 */
	public boolean endTask(String time, Date startTime, String planTag) {
		LOGGER.info("Come into endTask of Class QuartzTaskManager !");
		boolean flag = false;

		try {
			flag = addEndJob(ConstantUtil.JOBNAME_BASE_END + planTag + "_endJob", time, startTime, planTag);
		} catch (ParseException e) {
			LOGGER.info("trigger定时失败");
			LOGGER.error(e);
		} catch (SchedulerException e) {
			LOGGER.info("获取 Scheduler 对象失败");
			LOGGER.error(e);
		} catch (NullPointerException e) {
			LOGGER.info("Scheduler对象或JobDetail对象以及CronTrigger可能为空");
			LOGGER.error(e);
		}
		
		return flag;
	}

	/**
	 * 
	 * @Enclosing_Method : addJob
	 * @Written by : tianyp
	 * @Creation Date : 2012-8-23 下午2:47:40
	 * @version : v4.00
	 * @Description :
	 * @param jobName
	 * @param job
	 * @param time
	 * @throws SchedulerException
	 * @throws ParseException
	 *
	 */
	public boolean addJob(String jobName, String time, Date startTime, Plan plan, int isAdd,
			List<Integer> customerIdList, QuartzTaskManager quartzTaskManager, String planTag,
			StaticticsService staticticsService) throws SchedulerException, NullPointerException, ParseException {
		boolean flag = false;
		Scheduler scheduler = schedulerFactory.getScheduler();
		JobDetail jobDetail = JobBuilder.newJob(CallTask.class)
				.withIdentity(jobName, ConstantUtil.JOB_GROUP_NAME_BASE + planTag).build();
		
		LOGGER.info("JobDetail[ " + jobDetail.getKey() + " ]" + "实例化成功");
		JobDataMap paramMap = jobDetail.getJobDataMap();
		paramMap.put("planTag", planTag);
		paramMap.put("userId", plan.getUserId());
		paramMap.put("customerService", customerService);
		paramMap.put("planService", planService);
		paramMap.put("callRecordService", callRecordService);
		paramMap.put("redisCacheUtil", redisCacheUtil);
		paramMap.put("gatewayService", gatewayService);
		paramMap.put("projectService", projectService);
		paramMap.put("customerAndPlanService", customerAndPlanService);
		paramMap.put("quartzTaskManager", quartzTaskManager);
        paramMap.put("financeService", financeService);
        paramMap.put("staticticsService", staticticsService);
        

		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		triggerBuilder.withIdentity(ConstantUtil.TRIGGER_GROUP_NAME_BASE + planTag + "_startTrigger",
				ConstantUtil.TRIGGER_GROUP_NAME_BASE + planTag);
		
		triggerBuilder.startAt(startTime);
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(time));
		CronTrigger trigger = (CronTrigger) triggerBuilder.build();
		LOGGER.info("CronTrigger[ " + trigger.getKey() + " ]" + "实例化成功");
		scheduler.scheduleJob(jobDetail, trigger);

		CacheUtil.planMap.put(planTag, plan);
		if (!scheduler.isShutdown()) {
			scheduler.start();
			LOGGER.info(jobName + " 计划已加入了日程中, CronExpression的参数为 " + time);
		}
		if (scheduler.isStarted()) {
			if (isAdd == 0) {// 表示新加计划
				planService.addPlan(plan);
				int planId = plan.getId();
				int userId = plan.getUserId();
				if (planId != 0) {
					boolean isAddSuccess = CacheUtil.addPlan(userId, plan);
					if(isAddSuccess) {
						LOGGER.info("缓存中已同步添加计划[ " + planId + " ]");
					}
					int size = customerIdList.size();
					int count = size/5000;
                	if((size % 5000) != 0) {
                		count = count + 1;
                	}
                	int updateCount = 0;
					for (int i = 0; i < count; i++) {
						List<Integer> intoDBcustomerIdList = new ArrayList<Integer>();
						List<CustomerAndPlan> customerAndPlanList = new ArrayList<CustomerAndPlan>();
						for (int j = 0; j < 5000; j++) {
							if(customerIdList.size() == 0) {
								break;
							}
							Integer customerId = customerIdList.get(0);
							customerIdList.remove(0);
							CustomerAndPlan customerAndPlan = new CustomerAndPlan();
							customerAndPlan.setCustomerId(customerId);
							customerAndPlan.setPlanId(planId);
							customerAndPlan.setUserId(userId);
							customerAndPlanList.add(customerAndPlan);
							intoDBcustomerIdList.add(customerId);
						}
						customerAndPlanService.batchAddCustomerAndPlan(customerAndPlanList);
						updateCount = updateCount + customerService.updateIsPlanedIdByCustomerIdList(intoDBcustomerIdList, 1);
					}
					 
					if (size == updateCount) {
						LOGGER.info(updateCount + " 个客户添加计划成功");
					}
				}
				LOGGER.info("添加新计划成功");
			}
			flag = true;
		} else {
			LOGGER.info("scheduler 没有启动");
		}
		return flag;
	}

	/**
	 * 
	 * @param jobName
	 * @param job
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public boolean addEndJob(String jobName, String time, Date startTime, String planTag) throws SchedulerException, ParseException {
		boolean flag = false;
		Scheduler scheduler = schedulerFactory.getScheduler();
		JobDetail jobDetail = JobBuilder.newJob(EndTask.class)
				.withIdentity(jobName, ConstantUtil.JOB_GROUP_NAME_BASE + planTag).build();
		
		LOGGER.info("JobDetail[ " + jobDetail.getKey() + " ]" + "实例化成功");
		JobDataMap paramMap = jobDetail.getJobDataMap();
		paramMap.put("planTag", planTag);
		paramMap.put("planService", planService);
		
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		// 触发器名,触发器组
		triggerBuilder.withIdentity(ConstantUtil.TRIGGER_GROUP_NAME_BASE + planTag + "_endTrigger",
				ConstantUtil.TRIGGER_GROUP_NAME_BASE + planTag);
		triggerBuilder.startAt(startTime);
		// 触发器时间设定
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(time));
		// 创建Trigger对象
		CronTrigger trigger = (CronTrigger) triggerBuilder.build();
		LOGGER.info("CronTrigger[ " + trigger.getKey() + " ]" + "实例化成功");
		Date date = scheduler.scheduleJob(jobDetail, trigger);

		if (!scheduler.isShutdown()) {
			scheduler.start();
			LOGGER.info(jobName + " 计划已加入了日程中, CronExpression的参数为 " + time);
			if(date != null) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 删除现有任务
	 * 
	 * @param planId
	 * @param i
	 * @return
	 * @throws SchedulerException
	 */
	public boolean deleteJob(String planTag, String jobSuffix, String triggerSuffix) {
		boolean flag = false;
		String jobName = ConstantUtil.JOBNAME_BASE_START + planTag + jobSuffix;
		String triggerName = ConstantUtil.TRIGGER_GROUP_NAME_BASE + planTag + triggerSuffix;
		String triggerGroupName = ConstantUtil.TRIGGER_GROUP_NAME_BASE + planTag;
		Scheduler sched = null;
		
		try {
			sched = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

			sched.pauseTrigger(triggerKey);// 停止触发器
			flag = sched.unscheduleJob(triggerKey);// 移除触发器
			if(flag) {
				LOGGER.info("unscheduleJob [ " + triggerName + " ]删除成功");
			}else {
				flag = sched.deleteJob(JobKey.jobKey(jobName));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return flag;
	}
	
	/**
	 * 
	 * @Enclosing_Method : shutdown
	 * @Written by : tianyp
	 * @Creation Date : 2012-8-24 上午9:57:41
	 * @version : v4.00
	 * @Description :
	 *
	 */
	public void shutdown() {
		try {
			schedulerFactory.getScheduler().shutdown();
		} catch (SchedulerException e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}
	
	public boolean addWorkTimeTask(String startTime, Job job) {
		boolean flag = false;
		
		String tag = RandomStringUtils.randomNumeric(20) + System.currentTimeMillis();
		String jobName = ConstantUtil.JOBNAME_BASE_START + tag + "_WorkStartTimeJob";
		try {
			flag = addWorkTimeJob(jobName, startTime, tag, job);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private boolean addWorkTimeJob(String jobName, String time,String tag, Job job) throws SchedulerException {
		boolean flag = false;
		
		Scheduler scheduler = schedulerFactory.getScheduler();
		JobDetail jobDetail = JobBuilder.newJob(job.getClass())
				.withIdentity(jobName, ConstantUtil.JOB_GROUP_NAME_BASE + tag).build();
		
		LOGGER.info("JobDetail[ " + jobDetail.getKey() + " ]" + "实例化成功");
		
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		// 触发器名,触发器组
		triggerBuilder.withIdentity(ConstantUtil.TRIGGER_GROUP_NAME_BASE + tag + "_endTrigger",
				ConstantUtil.TRIGGER_GROUP_NAME_BASE + tag);
		triggerBuilder.startNow();
		// 触发器时间设定
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(time));
		// 创建Trigger对象
		CronTrigger trigger = (CronTrigger) triggerBuilder.build();
		LOGGER.info("CronTrigger[ " + trigger.getKey() + " ]" + "实例化成功");
		Date date = scheduler.scheduleJob(jobDetail, trigger);

		if (!scheduler.isShutdown()) {
			scheduler.start();
			LOGGER.info(jobName + " 计划已加入了日程中, CronExpression的参数为 " + time);
			if(date != null) {
				flag = true;
			}
		}
		return flag;
	}
}
