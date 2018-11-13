package com.zxxkj.listener;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.controller.PlanController;
import com.zxxkj.model.Gateway;
import com.zxxkj.model.Plan;
import com.zxxkj.model.Port;
import com.zxxkj.quartz.QuartzTaskManager;
import com.zxxkj.service.ICustomerService;
import com.zxxkj.service.IGatewayService;
import com.zxxkj.service.IPlanService;
import com.zxxkj.service.IPortService;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.TransportUtil;

@Component("quartzListener")
public class QuartzListener implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(QuartzListener.class);
	
	private IPlanService planService = null;
	private ICustomerService customerService = null;
	private QuartzTaskManager quartzTaskManager = null;
	private IGatewayService gatewayService = null;
	private IPortService portService = null;
	
	private JSONObject getGatewayJSON(int userId) {
		JSONObject gatewayJSON = new JSONObject();
		
		List<Gateway> gatewayList = gatewayService.findGatewayListByUserId(userId);
		if(gatewayList.size() > 0) {
			Gateway gateway = gatewayList.get(0);
			Integer porttype=0;
			List<Port> portList = portService.findPortListByUserId(userId, gateway.getId(),porttype);
			gatewayJSON.put("url", gateway.getUrl());
			gatewayJSON.put("port", portList);
		}
		
		return gatewayJSON;
	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebApplicationContext webApplicationContext= WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		planService=(IPlanService) webApplicationContext.getBean("planService");
		quartzTaskManager=(QuartzTaskManager) webApplicationContext.getBean("quartzTaskManager");
		customerService=(ICustomerService) webApplicationContext.getBean("customerService");
		gatewayService=(IGatewayService) webApplicationContext.getBean("gatewayService");
		portService=(IPortService) webApplicationContext.getBean("portService");
		List<Plan> interruptedPlanList = planService.findInterruptedPlanList();
		for (Plan plan : interruptedPlanList) {
			List<Integer> customerIdList = customerService.findInterruptedPlanCustomerIdList(plan.getId());
//			JSONObject gatewayJSON = getGatewayJSON(plan.getUserId());
//			plan.setGatewayJSON(gatewayJSON);
			quartzTime(plan, customerIdList);
			LOGGER.info("执行完一个客户的计划");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.info("have Destroyed!!!");
	}

	@SuppressWarnings("unchecked")
	private JSONObject quartzTime(Plan plan, List<Integer> customerIdList) {
		JSONObject result = new JSONObject();
		List<Integer> timeList = TransportUtil.stringTransportListForId(plan.getTimeStr());
		int size = timeList.size();
		String excuteTimeStr = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(plan.getExcuteTime()).substring(0, 10);
		LOGGER.info(plan.getId() + " timeList为 ：" + timeList);
		JSONObject json = new JSONObject();
		while (size != 0) {
//			size = PlanController.getList(timeList, json);
		}
		
		Set<String> jsonKeySet = json.keySet();
		if(size > 0) {
			int i = 0;
			for (String string : jsonKeySet) {
				
				List<Integer> list = (List<Integer>) json.get(string);
				int startInt = list.get(0);
				int endInt = list.get(list.size() - 1) + 1;
				String startTime = string;
				String endTime = json.getString(startTime);
				
				if (startInt < 10) {
					startTime = excuteTimeStr + " 0" + startInt + ":00:00";
				} else {
					startTime = excuteTimeStr + " " + startInt + ":00:00";
				}
				if (endInt < 10) {
					endTime = excuteTimeStr + " 0" + endInt + ":00:00";
				} else {
					endTime = excuteTimeStr + " " + endInt + ":00:00";
				}
				Date start = null;
				Date end = null;
				try {
					start = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(startTime);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				String time = TransportUtil.transportTimeFormatToEveryDay(startTime);
				if(start.getTime() > new Date().getTime()) {
					JSONObject quartzJson = new JSONObject();
//					quartzTaskManager.startTask(time, start, endTime, plan, i);
					quartzJson.put("time", time);
					quartzJson.put("start", start);
					quartzJson.put("endTime", endTime);
					LOGGER.info("计划 " + plan.getId() +"[ " + startTime + " ] 计划已生效" );
				}else {
					long startLong = start.getTime() + 86400000;//如果此计划执行时间已过，第二天执行
					long endLong = 0L;
					try {
						end = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(startTime);
						endLong = end.getTime() + 86400000;
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Date newStart = new Date(startLong);
					Date newEnd = new Date(endLong);
//					quartzTaskManager.startTask(time, newStart, ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(newEnd), plan, i);
					LOGGER.info("计划 " + plan.getId() + "[ " + startTime + " ] 时间已过，改为 [ " + ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(newStart) + " ] 执行"
							+ " 结束时间为 [ " + ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(newEnd) + " ] ");
				}
				i++;
			}
		}else {
			String startTime = excuteTimeStr + " " + ConstantUtil.HH_MM_SS_SDF.format(new Date()).substring(0, 5) + ":00";
			String workStartDateTimeStr = excuteTimeStr + " 09:00:00";
			String workEndDateTimeStr = excuteTimeStr + " 20:00:00";
			Date start = null;
			Date workStartDateTime = null;
			Date workEndDateTime = null;
			try {
				start = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(startTime);
				workStartDateTime = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(workStartDateTimeStr);
				workEndDateTime = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(workEndDateTimeStr);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			if(start != null && start.before(workStartDateTime)) {//如果计划时间在九点之前，那么九点执行
				start = workStartDateTime;
			}else if(start.after(workEndDateTime)) {//如果计划开始的时间在工作时间以后，则第二天九点开始执行
				start = new Date(workStartDateTime.getTime() + 86400000);
				workEndDateTime = new Date(workEndDateTime.getTime() + 86400000);
				startTime = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(start);
				workEndDateTimeStr = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(workEndDateTime);
			}
			String time = TransportUtil.transportTimeFormat(startTime);
			
//			quartzTaskManager.startTask(time, start, workEndDateTimeStr, plan, 0);
			LOGGER.info(ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(start) + " 的定时任务已经开启");
		}
		return result;
	}
}
