package com.zxxkj.listener;

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.zxxkj.quartz.QuartzTaskManager;
import com.zxxkj.task.WorkEndTimeTask;
import com.zxxkj.task.WorkStartTimeTask;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.DateUtil;
import com.zxxkj.util.ParameterProperties;

@Component("workTimeListner")
public class WorkTimeListner implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(WorkTimeListner.class);
	private QuartzTaskManager quartzTaskManager = null;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.info("contextDestroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOGGER.info("进入 WorkTimeListner 监听");
		WebApplicationContext webApplicationContext= WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		quartzTaskManager=(QuartzTaskManager) webApplicationContext.getBean("quartzTaskManager");
		
		String startWorkTime = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "startTime");
		String endWorkTime = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "endTime");
		
		String startTime = "0 0 "
				+ ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "startTime").substring(1, 2)
				+ " * * ?";
		String endTime = "0 0 "
				+ ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "endTime").substring(0, 2)
				+ " * * ?";
		
		Date now = DateUtil.getHHMMSSByDate(new Date());
		Date workStartDateTime = DateUtil.stringFormatToDate_HH_MM_SS(startWorkTime);
		Date workEndDateTime = DateUtil.stringFormatToDate_HH_MM_SS(endWorkTime);
		
		boolean startFlag = quartzTaskManager.addWorkTimeTask(startTime, new WorkStartTimeTask());
		boolean endFlag = quartzTaskManager.addWorkTimeTask(endTime, new WorkEndTimeTask());
		
		if(startFlag) {
			LOGGER.info("工作时间的开始任务开启成功");
		}
		if(endFlag) {
			LOGGER.info("工作时间的结束任务开启成功");
		}
		
		if(now.after(workStartDateTime) && now.before(workEndDateTime)) {
			WorkStartTimeTask.isInWorkTime = true;
			LOGGER.info("已重置 isInWorkTime 的值");
		}
	}

}
