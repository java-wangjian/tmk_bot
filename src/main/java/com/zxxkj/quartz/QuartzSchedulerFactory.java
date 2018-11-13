package com.zxxkj.quartz;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.ParameterProperties;

public class QuartzSchedulerFactory {

	private static final Logger LOGGER = Logger.getLogger(QuartzSchedulerFactory.class);
	public static StdSchedulerFactory schedulerFactory;
	
	static {
		synchronized (QuartzSchedulerFactory.class) {
			Properties props = new Properties();
			props.put(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
					"org.quartz.simpl.SimpleThreadPool");
			props.put("org.quartz.threadPool.threadCount", 
					ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "quartzThreadCount"));
			props.put("org.quartz.threadPool.threadPriority", 
					ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "threadPriority"));
			props.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", 
					ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "threadsInheritContextClassLoaderOfInitializingThread"));
			
			if(schedulerFactory == null) {
				try {
					schedulerFactory = new StdSchedulerFactory(props);
					LOGGER.info("获取StdSchedulerFactory成功");
				} catch (SchedulerException e) {
					LOGGER.info("获取StdSchedulerFactory失败");
				}
			}else {
				LOGGER.info("schedulerFactory 已存在");
			}
		}
	}
}
