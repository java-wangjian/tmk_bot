package com.zxxkj.task;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class WorkStartTimeTask implements Job{

	private static final Logger LOGGER = Logger.getLogger(WorkStartTimeTask.class);
	public static boolean isInWorkTime = false;//是否在工作时间内，false表示不在
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("进入 WorkStartTimeTask 类");
		isInWorkTime = true;
		LOGGER.info("isInWorkTime 的值改为: " + isInWorkTime);
	}

}
