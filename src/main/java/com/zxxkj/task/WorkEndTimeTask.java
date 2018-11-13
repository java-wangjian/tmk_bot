package com.zxxkj.task;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WorkEndTimeTask implements Job{

	private static final Logger LOGGER = Logger.getLogger(WorkEndTimeTask.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("进入 WorkEndTimeTask 类");
		WorkStartTimeTask.isInWorkTime = false;
		LOGGER.info("isInWorkTime 的值改为: " + WorkStartTimeTask.isInWorkTime);
	}

}
