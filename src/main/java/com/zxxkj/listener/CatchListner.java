package com.zxxkj.listener;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.model.Customer;
import com.zxxkj.model.Plan;
import com.zxxkj.service.ICustomerAndPlanService;
import com.zxxkj.service.ICustomerService;
import com.zxxkj.service.IPlanService;
import com.zxxkj.util.CacheUtil;

@Component("catchListner")
public class CatchListner implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(CatchListner.class);
	
	private IPlanService planService;
	private ICustomerService customerService;
	private RedisCacheUtil<String> redisCacheUtil;
	private ICustomerAndPlanService customerAndPlanService;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
		LOGGER.info("进入 contextDestroyed 方法");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOGGER.info("进入 contextInitialized 方法");
		WebApplicationContext webApplicationContext= WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		planService = (IPlanService) webApplicationContext.getBean("planService");
		customerService = (ICustomerService) webApplicationContext.getBean("customerService");
		redisCacheUtil = (RedisCacheUtil) webApplicationContext.getBean("redisCache");
		customerAndPlanService = (ICustomerAndPlanService) webApplicationContext.getBean("customerAndPlanService");
		
		//获取有当前计划的user
		List<Integer> userIdList = planService.findUserIdByPlanStatusIsStop(1);
		LOGGER.info("有[ " + userIdList.size() + " ]个用户有当前计划");
		for (Integer userId : userIdList) {
			//获取该user的当前计划列表
			Vector<Plan> planVector = planService.findCurrentPlanByUserId(userId);
			CacheUtil.user_planVactor.put(userId, planVector);
		}
		Integer count = customerAndPlanService.updateIsCallByIsCall(0, 2);
		LOGGER.info("一共有[ " + count + " ]条isCall为2的客户已重置为0的状态");
	}

}
