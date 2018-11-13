package com.zxxkj.task;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zxxkj.model.Plan;
import com.zxxkj.service.IPlanService;
import com.zxxkj.util.CacheUtil;

public class EndTask implements Job {

	private static final Logger LOGGER = Logger.getLogger(EndTask.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("进入 EndTask 类的 execute()方法中");
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		String planTag = (String) context.getJobDetail().getJobDataMap().get("planTag");
		IPlanService planService = (IPlanService) jobDataMap.get("planService");
		
		handler(planTag, planService);
	}

	public void handler(String planTag, IPlanService planService) {
		LOGGER.info("计划planTag为 " + planTag + " 的计划进入EndTask");
		Plan plan = CacheUtil.planMap.get(planTag);
		plan.setIsEnd(true);
		Integer planId = plan.getId();
		Plan DBPlan = planService.findPlanInfoByPlanId(planId);
		Integer planStatus = DBPlan.getPlanStatus();
		if(planStatus ==2 || planStatus == 3) {
			LOGGER.info("该计划已停止，不需将其设为未执行");
		}else {
			Integer updateCount = planService.updatePlanStatusByPlanId(0, null, planId);//将计划设置为未执行
			if(updateCount > 0) {
				plan.setPlanStatus(0);
				LOGGER.info("已将计划[ " + planId + " ]置为未执行");
			}
			LOGGER.info("已将id为[ " + plan.getId() + " ]的计划的子任务设为执行完");
		}
	}
}
