package com.zxxkj.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.model.CustomerAndPlan;
import com.zxxkj.model.Gateway;
import com.zxxkj.model.Plan;
import com.zxxkj.model.Port;
import com.zxxkj.quartz.QuartzTaskManager;
import com.zxxkj.service.ICallRecordService;
import com.zxxkj.service.ICustomerAndPlanService;
import com.zxxkj.service.ICustomerService;
import com.zxxkj.service.IGatewayService;
import com.zxxkj.service.IPlanService;
import com.zxxkj.service.IPortService;
import com.zxxkj.task.CallTask;
import com.zxxkj.task.WorkStartTimeTask;
import com.zxxkj.thread.ThreadUtil;
import com.zxxkj.util.CacheUtil;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.DateUtil;
import com.zxxkj.util.ParameterProperties;
import com.zxxkj.util.SortUtil;
import com.zxxkj.util.TTUtil;
import com.zxxkj.util.TransportUtil;

@Controller
@RequestMapping("/plan")
public class PlanController {

	private static final Logger LOGGER = Logger.getLogger(PlanController.class);

	@Resource
	private IPlanService planService;
	@Resource
	private ICustomerService customerService;
	@Resource
	private QuartzTaskManager quartzTaskManager;
	@Resource
	private ICallRecordService callRecordService;
	@Resource
	private ICustomerAndPlanService customerAndPlanService;
	@Resource
	private RedisCacheUtil<Integer> redisCacheUtil;
	@Resource
	private IPortService portService;
	@Resource
	private IGatewayService gatewayService;

	@RequestMapping(value = "/util", method = RequestMethod.POST)
	@ResponseBody
	public String util(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		List<Map<String, Object>> planIdAndCustomerIdListMap = planService.findAllPlanIdAndCustomerId();
		for (Map<String, Object> map : planIdAndCustomerIdListMap) {
			if (null != map.get("customerIdList")) {
				List<CustomerAndPlan> customerAndPlanList = new ArrayList<CustomerAndPlan>();
				List<Integer> customerIdList = TransportUtil
						.stringTransportListForId(map.get("customerIdList").toString());
				for (Integer integer : customerIdList) {
					CustomerAndPlan customerAndPlan = new CustomerAndPlan();
					customerAndPlan.setCustomerId(integer);
					customerAndPlan.setPlanId((int) (map.get("planId")));
					customerAndPlanList.add(customerAndPlan);
				}
				Integer count = customerAndPlanService.batchAddCustomerAndPlan(customerAndPlanList);
				LOGGER.info("计划 " + (int) (map.get("planId")) + " 添加了" + count + " 条对应关系");
			}
		}
		LOGGER.info("");
		return result.toJSONString();
	}

	/**
	 * 页面上勾选所进行的添加操作
	 * 
	 * @param request
	 * @param response
	 * @param plan
	 * @param customerIdListStr
	 * @param time
	 * @param excuteTimeStr
	 * @param isAdd 为1表示拨打未接通,为0表示新加计划
	 * @return
	 */
	@RequestMapping(value = "/addPlan", method = RequestMethod.POST)
	@ResponseBody
	public String addPlan(HttpServletRequest request, HttpServletResponse response, Plan plan, String customerIdListStr,
			String excuteTimeStr, String time, Integer isAdd, String batchNo, Integer planId) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		List<Integer> customerIdList = null;
		int userId = plan.getUserId();
		int gatewayId = plan.getGatewayId();

		if(isAdd == 1) {
			customerIdList = customerAndPlanService.findCustomerIdListByUserIdAndPlanId(userId, planId);
			List<Integer> passCustomerIdList = callRecordService.findPassCustomerIdsByPlanId(userId, planId);
			customerIdList.removeAll(passCustomerIdList);
		}else {
			if(("").equals(batchNo) && ("[]").equals(customerIdListStr)) {
				customerIdList = customerAndPlanService.findCustomerIdListByUserIdAndPlanId(userId, planId);
			}else {
				if(batchNo != null && !("").equals(batchNo)) {
					customerIdList = customerService.findCustomerIdListByBatchNo(batchNo);
				}else if(customerIdListStr != null || !("[]").equals(customerIdListStr)) {
					customerIdList = TransportUtil.stringTransportListForId(customerIdListStr);
				}
			}
		}
		Gateway gateway = gatewayService.findGatewayInfoByGatewayId(gatewayId);
		plan.setGateName(gateway.getGatewayNumbers());
		if (gateway.getType() == 1) {
			if (StringUtils.isBlank(plan.getCallPortListStr())) {
				result.put("result", 5);// 没有设置端口
				return result.toJSONString();
			}
			if (plan.getIsTransfer() == 1) {
				List<Integer> transferPortList = portService.findTracferPortByUserId(userId, 1);
				plan.setTrancferPortListStr(transferPortList.toString().substring(1,
						transferPortList.toString().replace(" ", "").length() - 1));
			}
		} else if (gateway.getType() == 2) {
		    Integer porttype=0;
			List<Port> portList = portService.findPortListByUserId(userId, gatewayId,porttype);
			plan.setSipCallCount(portList.get(0).getPort());
		}

		Integer DBPlanCount = planService.findPlanIsExist(plan);
		if (DBPlanCount == null || DBPlanCount == 0) {
			List<Integer> havePlanCustomerIdList = customerService.findhavePlandCustomerIdByCustomerIdList(userId,
					customerIdList);
			if (havePlanCustomerIdList.size() > 0) {
				int updateCount = customerService.updateIsCallAndPlanIdByCustomerIdList(havePlanCustomerIdList, 0, 0);
				LOGGER.info("有 " + updateCount + " 个客户在上个计划中已拨打完电话");
			}

			if (customerIdList.size() > 0) {
				result = quartzTime(plan, excuteTimeStr, plan.getTimeStr(), customerIdList, time, 0);
			} else {
				result.put("result", 3);// 任务已添加过
			}
		} else {
			result.put("result", 6);// 计划名重复
		}

		return result.toJSONString();
	}

	/**
	 * 查询计划里所有客户的id列表
	 * 
	 * @param request
	 * @param response
	 * @param planId
	 *            计划id
	 * @param userId
	 *            用户id
	 * @return
	 */
//	@RequestMapping(value = "/findCustomerIdByPlanId", method = RequestMethod.POST)
//	@ResponseBody
//	public String findCustomerIdByPlanId(HttpServletRequest request, HttpServletResponse response, int planId,
//			int userId) {
//		response.addHeader("Access-Control-Allow-Origin", "*");
//		JSONObject result = new JSONObject();
//		JSONArray resultJSON = new JSONArray();
//		
//		List<Integer> customerIdList = planService.findCustomerIdListByPlanId(planId);
//		for (Integer integer : customerIdList) {
//			resultJSON.add(integer);
//		}
//		result.put("customerIdList", resultJSON);
//
//		return result.toJSONString();
//	}

	/**
	 * 查询计划里没有拨打通的客户id列表
	 * 
	 * @param request
	 * @param response
	 * @param planId
	 *            计划id
	 * @param userId
	 *            用户id
	 * @return
	 */
	@RequestMapping(value = "/findNoPassCutomerIdByPlanId", method = RequestMethod.POST)
	@ResponseBody
	public String findNoPassCutomerIdByPlanId(HttpServletRequest request, HttpServletResponse response, int planId,
			int userId) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		JSONArray resultJSON = new JSONArray();

		List<Integer> allPustomerIdList = customerAndPlanService.findCustomerIdListByUserIdAndPlanId(userId, planId);
		List<Integer> passCustomerIdList = callRecordService.findPassCustomerIdsByPlanId(userId, planId);
		allPustomerIdList.removeAll(passCustomerIdList);
		for (Integer integer : allPustomerIdList) {
			resultJSON.add(integer);
		}
		result.put("customerIdList", resultJSON);

		return result.toJSONString();
	}

	@RequestMapping(value = "/quartzStart", method = RequestMethod.POST)
	@ResponseBody
	public void quartzStart(HttpServletRequest request, HttpServletResponse response, int start) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		if (start == 0) {
			CallTask.start = true;
			result.put("result", "已开启定时任务");
		} else if (start == 1) {
			CallTask.start = false;
			result.put("result", "已关闭定时任务");
		}
		TTUtil.sendDataByIOStream(response, result);
	}

	/**
	 * 取消单个计划
	 * 
	 * @param isDelete
	 * @param userId
	 * @param 计划id
	 * @return
	 */
	@RequestMapping(value = "/deletePlan", method = RequestMethod.POST)
	@ResponseBody
	public String deletePlan(HttpServletRequest request, HttpServletResponse response, Plan plan) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		int planId = plan.getId();
		int userId = plan.getUserId();
		Plan planDB = planService.findPlanInfoByPlanId(planId);
		String planTag = planDB.getPlanTag();
		Gateway gateway = gatewayService.findGatewayInfoByGatewayId(planDB.getGatewayId());
		if(gateway != null) {
			CacheUtil.removePlanForGatewayAndPlanMap(planDB.getCallPortListStr(), gateway.getGatewayNumbers(), planId);
		}
		CacheUtil.removePlan(userId, planId);
		
		if(CacheUtil.planMap.containsKey(planTag)) {
			if(CacheUtil.planMap.get(planTag) != null) {
				plan = CacheUtil.planMap.get(planTag);
			}else {
				CacheUtil.planMap.put(planTag, plan);
			}
		}else {
			CacheUtil.planMap.put(planTag, plan);
		}
		int updateCount = planService.updatePlanStatusByPlanId(3, new Date(), planId);
		plan.setPlanStatus(3);
		if (updateCount > 0) {
			plan = CacheUtil.planMap.get(planDB.getPlanTag());
			result.put("result", 0);
			boolean flag = ThreadUtil.deletePlanInCatch(planId, planService, quartzTaskManager);
			if (flag) {
				LOGGER.info("用户 [ " + plan.getUserId() + " ] 成功取消了计划  [ " + planId + " ]");
			}
			return result.toJSONString();
		}

		result.put("result", 1);// 取消计划失败
		LOGGER.info("用户 " + plan.getUserId() + " 取消了计划 " + planId + "失败");
		return result.toJSONString();
	}


	/**
	 * 页面上填入选择条数进行添加的方式
	 * 
	 * @param request
	 * @param response
	 * @param plan
	 * @param excuteTimeStr
	 * @return
	 */
	@SuppressWarnings("null")
	@RequestMapping(value = "/addManyPlan", method = RequestMethod.POST)
	@ResponseBody
	public String addManyPlan(HttpServletRequest request, HttpServletResponse response, int userId, int count,
			String excuteTimeStr, Plan plan, String time) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		Integer DBPlanCount = planService.findPlanIsExist(plan);
		if (DBPlanCount == null || DBPlanCount == 0) {
			List<Integer> customerIdList = customerService.findCustomerListByUserIdAndCountAndPlanId(userId, count);

			Integer gatewayId = plan.getGatewayId();
			Gateway gateway = gatewayService.findGatewayInfoByGatewayId(gatewayId);
			if (customerIdList.size() > 0) {
				Integer type = gateway.getType();
//				customerService.updateIsCallAndPlanIdByCustomerIdList(customerIdList, 0, 0);
				if (type == 1) {
					if (StringUtils.isBlank(plan.getCallPortListStr())) {
						result.put("result", 5);// 没有设置端口
						return result.toJSONString();
					}
				} else if (type == 2) {
					Integer porttype=0;
					List<Port> portList = portService.findPortListByUserId(userId, gatewayId,porttype);
					plan.setSipCallCount(portList.get(0).getPort());
				}
				// plan.setGatewayJSON(gatewayJSON);
				result = quartzTime(plan, excuteTimeStr, plan.getTimeStr(), customerIdList, time, 0);
				if (result == null) {
					result.put("result", 1);
					LOGGER.info("添加失败或修改用户计划失败");
					return result.toJSONString();
				}
			}
		} else {
			result.put("result", 6);
		}
		return result.toJSONString();
	}


	/**
	 * 根据计划状态查询计划列表
	 * 
	 * @param request
	 * @param response
	 * @param userId
	 * @param planStatus
	 *            1是指当前计划，2表示历史计划
	 */
	@RequestMapping(value = "/findNowPlanList", method = RequestMethod.POST)
    @ResponseBody
    public void findNowPlanList(HttpServletRequest request, HttpServletResponse response, int userId, int planStatus,
                                String searchText, int curPage, String startTimeStr, String endTimeStr) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        JSONArray JSONArr = new JSONArray();
        
        int total = findHistoryPlan(curPage, userId, startTimeStr, endTimeStr, searchText, planStatus, JSONArr);
        
        result.put("total", total);
        result.put("list", JSONArr);
        LOGGER.info("用户 " + userId + " 查询了当前的计划列表");
        TTUtil.sendDataByIOStream(response, result);
    }
	
	//查找历史计划
	private int findHistoryPlan(int curPage, int userId, String startTimeStr, String endTimeStr,
			String searchText, int planStatus, JSONArray JSONArr) {
        //添加时间筛选参数
        Date startTimes = null;
        Date endTimes = null;
        try {
            if (!StringUtils.isBlank(startTimeStr)) {
                startTimes = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(startTimeStr);
            }
            if (!StringUtils.isBlank(endTimeStr)) {
                endTimes = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(endTimeStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int pageCount = Integer.parseInt(ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "normalCount"));
        int start = (curPage - 1) * pageCount;

        int total = planService.findPlanTotalListByUserId(userId, planStatus, searchText,startTimes,endTimes);

        List<Map<String, Object>> nowPlanMapList = planService.findNowPlanListByUserId(userId, planStatus, searchText,
                start, pageCount,startTimes,endTimes);
        for (Map<String, Object> map : nowPlanMapList) {
        	int customerCount = (map.get("customerCount") == null ? 0 : (int) map.get("customerCount"));
        	int thoughCount = (map.get("thoughCount") == null ? 0
        			: Integer.valueOf(map.get("thoughCount").toString()));
        	JSONArray timeArr = getTimeRang((map.get("timeStr") == null ? "[]" : map.get("timeStr").toString()));
        	map.put("planId", (map.get("planId") == null ? "" : map.get("planId")));
        	map.put("excuteTime", map.get("excuteTime").toString().subSequence(0, 19));
        	map.put("updateTime", (map.get("updateTime") == null ? map.get("excuteTime").toString().subSequence(0, 19) : map.get("updateTime").toString().subSequence(0, 19)));
        	map.put("endTime",
        			(map.get("endTime") == null ? "" : map.get("endTime").toString().subSequence(0, 19)));
        	map.put("addTime",
        			(map.get("addTime") == null ? "" : map.get("addTime").toString().subSequence(0, 19)));
        	map.put("sourceTimeStr", map.get("timeStr") == null ? "[]" : map.get("timeStr").toString());
        	map.put("timeStr", timeArr);
        	map.put("projectId", (map.get("projectId") == null ? "" : map.get("projectId")));
        	map.put("projectName", (map.get("projectName") == null ? "" : map.get("projectName")));
        	map.put("customerCount", customerCount);
        	map.put("noThoughCount", customerCount - thoughCount);
        	map.put("isInterrupt", (map.get("isInterrupt") == null ? 0 : map.get("isInterrupt")));
        	map.put("isTransfer", (map.get("isTransfer") == null ? 0 : map.get("isTransfer")));
        	map.put("isSendSMS", (map.get("isSendSMS") == null ? 2 : map.get("isSendSMS")));
        	map.put("transferGrade", (map.get("transferGrade") == null ? "" : map.get("transferGrade")));
        	map.put("excuteCount", (map.get("transferGrade") == null ? 0 : map.get("excuteCount")));
        	JSONArr.add(JSON.toJSON(map));
        }
        
        return total;
	}

	/**
	 * 根据计划id控制计划的是否停止状态
	 * 
	 * @param request
	 * @param response
	 * @param planId
	 * @param userId
	 * @param planStatus
	 * @return
	 */
	@RequestMapping(value = "/hold", method = RequestMethod.POST)
	@ResponseBody
	public String hold(HttpServletRequest request, HttpServletResponse response, int planId, int userId,
			int planStatus) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		Plan plan = planService.findPlanInfoByPlanId(planId);
		String planTag = plan.getPlanTag();
		String callPortListStr = plan.getCallPortListStr();
		Integer gatewayId = plan.getGatewayId();
		Gateway gateway = gatewayService.findGatewayInfoByGatewayId(gatewayId);
		
		if(gateway == null) {
			result.put("result", 6);//网关已不存在，请重新编辑计划
			return result.toJSONString();
		}
		Date now = new Date();
		
		if(CacheUtil.planMap.containsKey(planTag)) {
			if(CacheUtil.planMap.get(planTag) != null) {
				plan = CacheUtil.planMap.get(planTag);
			}else {
				CacheUtil.planMap.put(planTag, plan);
			}
		}else {
			CacheUtil.planMap.put(planTag, plan);
		}
		
		if (planStatus != 4) {// 开启任务
			LOGGER.info("用户 " + userId + " 开启了计划  " + planId);
			plan.setGateName(gateway.getGatewayNumbers());
			Integer hasCallCount = customerAndPlanService.findHasCallCountByIsCall(planId, 1);
			plan.getCalledCount().set(hasCallCount);
			
			if (gateway.getType() == 1) {
				if (StringUtils.isBlank(plan.getCallPortListStr())) {
					result.put("result", 5);// 没有设置端口
					return result.toJSONString();
				}
				if (plan.getIsTransfer() == 1) {
					List<Integer> transferPortList = portService.findTracferPortByUserId(userId, 1);
					plan.setTrancferPortListStr(transferPortList.toString().substring(1,
							transferPortList.toString().replace(" ", "").length() - 1));
				}
			} else if (gateway.getType() == 2) {
				Integer porttype=0;
				List<Port> portList = portService.findPortListByUserId(userId, gatewayId,porttype);
				plan.setSipCallCount(portList.get(0).getPort());
			}
			String timeStr = plan.getTimeStr();
			List<Integer> timeList = TransportUtil.stringTransportListForId(timeStr);
			
			String updateTime = ConstantUtil.YYYY_MM_DD_SDF.format(plan.getUpdateTime());
			JSONObject timeJSON = PlanController.getTimeJSON(timeList, updateTime);
			String startTime = timeJSON.getString("startTime");
			String endTime = timeJSON.getString("endTime");
			
			CacheUtil.addPlanForGatewayAndPlanMap(callPortListStr, gateway.getGatewayNumbers(), plan);
			CacheUtil.planMap.put(planTag, plan);
			CacheUtil.addPlan(userId, plan);
			
//			if (plan.getUpdateTime().before(now)) {
//				plan.setUpdateTime(now);
//			}
			boolean flag = quartzTaskManager.startTask(startTime, plan.getUpdateTime(), plan, 1, null,
					quartzTaskManager, planTag);
			if (flag) {
				quartzTaskManager.endTask(endTime, plan.getUpdateTime(), planTag);
				result.put("result", 0);
			}
//			if (isPeriodOfTime(timeStr) && timeList.size() > 0 && isNow(updateTime)) {
//				planTag = RandomStringUtils.randomNumeric(20) + System.currentTimeMillis();;
//				Plan planClone = plan.clone();
//				planClone.setPlanTag(planTag);
//				CacheUtil.planMap.put(planTag, planClone);
//				startTime = TransportUtil.transportTimeFormat(ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(now));
//				flag = quartzTaskManager.startTask(startTime, now, planClone, 1, null, quartzTaskManager, planTag);
//				if (flag) {
//					quartzTaskManager.endTask(endTime, planClone.getUpdateTime(), planTag);
//				}
//			}
			Integer updateCount = planService.updatePlanStatusByPlanId(0, null, planId);//将计划设置未执行
			plan.setPlanStatus(0);
			if(updateCount > 0) {
				LOGGER.info("已将计划[ " + planId + " ]置为未执行");
			}
		} else {// 关闭任务
			plan.getCalledCount().set(0);
			CacheUtil.removePlanForGatewayAndPlanMap(callPortListStr, gateway.getGatewayNumbers(), planId);
			CacheUtil.removePlan(userId, planId);
			Integer updateCount = planService.updatePlanStatusByPlanId(4, null, planId);//将计划设置为关闭
			plan.setPlanStatus(4);
			if(updateCount > 0) {
				LOGGER.info("已将计划[ " + planId + " ]置为已停止");
			}
			boolean flag = ThreadUtil.deletePlanInCatch(planId, planService, quartzTaskManager);
			if (flag) {
				LOGGER.info("用户 " + userId + " 的计划 " + planId + " 成功停止");
			}
		}
		result.put("result", 0);
		LOGGER.info("用户 " + userId + " 修改了计划 " + planId + " 的状态");
		return result.toJSONString();
	}

	/**
	 * 修改计划名称，是否转接，是否打断，转接级别，执行日期和时间
	 * 
	 * @param request
	 * @param response
	 * @param 计划id
	 * @param projectId
	 * @param isInterrupt
	 * @param isTransfer
	 * @param transferGrade
	 * @param userId
	 * @param excuteTimeStr
	 * @param exuteTimeHHMM
	 * @return
	 */
	@RequestMapping(value = "/updatePlanInfo", method = RequestMethod.POST)
	@ResponseBody
	public String updatePlanInfo(HttpServletRequest request, HttpServletResponse response, Plan plan,
			String excuteTimeStr, String exuteTimeHHMM) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		int planId = plan.getId();
		int userId = plan.getUserId();
		String timeStr = plan.getTimeStr();
		List<Integer> timeList = TransportUtil.stringTransportListForId(timeStr);
		// Date now = null;
		int gatewayId = plan.getGatewayId();

		Plan DBplan = planService.findPlanInfoByPlanId(planId);
		String planTag = RandomStringUtils.randomNumeric(20) + System.currentTimeMillis();
		Date DBexcuteTime = DBplan.getExcuteTime();

		plan.setPlanTag(planTag);
		CacheUtil.planMap.put(planTag, plan);

		int isOutTime = isOutTime(plan, exuteTimeHHMM, excuteTimeStr, timeList, timeStr);
		if (isOutTime == 2) {
			result.put("result", 2);
			LOGGER.info("时间已过");
			return result.toJSONString();
		} else if (isOutTime == 4) {
			result.put("result", 4);
			return result.toJSONString();
		}

		Gateway gateway = gatewayService.findGatewayInfoByGatewayId(gatewayId);
		plan.setGateName(gateway.getGatewayNumbers());
		if (gateway.getType() == 1) {
			if (StringUtils.isBlank(plan.getCallPortListStr())) {
				result.put("result", 5);// 没有设置端口
				return result.toJSONString();
			}
			if (plan.getIsTransfer() == 1) {
				List<Integer> transferPortList = portService.findTracferPortByUserId(userId, 1);
				plan.setTrancferPortListStr(transferPortList.toString().substring(1,
						transferPortList.toString().replace(" ", "").length() - 1));
			}
		} else if (gateway.getType() == 2) {
			Integer porttype=0;

			List<Port> portList = portService.findPortListByUserId(userId, gatewayId,porttype);
			plan.setSipCallCount(portList.get(0).getPort());
		}

		String startTime = null;
		String endTime = null;
		Integer noCallCount = customerAndPlanService.findNoIsCallCustomerICountByUserIdAndPlanId(userId, planId);
		if (noCallCount != 0 || noCallCount != null) {
			plan.setCustomerCount(noCallCount);

			if ((new Date()).before(DBexcuteTime)) {
				planService.updateExcuteTime(plan);
			}

			JSONObject timeJSON = PlanController.getTimeJSON(timeList, excuteTimeStr);
			startTime = timeJSON.getString("startTime");
			endTime = timeJSON.getString("endTime");
			LOGGER.info("计划 [ " + planId + " ]删除成功，开始制定修改后的计划");
			boolean flag = quartzTaskManager.startTask(startTime, plan.getExcuteTime(), plan, 1, null,
					quartzTaskManager, planTag);
			if (flag) {
				quartzTaskManager.endTask(endTime, plan.getExcuteTime(), planTag);
				plan.setPlanStatus(0);
				result.put("result", 0);
				planService.updatePlanInfoByPlanId(plan);
			} else {
				result.put("result", 1);
			}
		} else {
			LOGGER.info("id为 [ " + planId + " ] 的计划没有未拨打的电话了");
			result.put("result", 0);
		}
		return result.toJSONString();
	}

	@RequestMapping(value = "/statisticalPlan", method = RequestMethod.POST)
	@ResponseBody
	public void statisticalPlan(HttpServletRequest request, HttpServletResponse response, int userId, int planId) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		// JSONArray resultArr = new JSONArray();

		int customerTotal = planService.findPlanedAllCustomerCount(userId, planId);
		int calledCount = callRecordService.findCalledCountByUserId(userId, planId);
		int noCalledCount = customerTotal - calledCount;
		int passCount = callRecordService.findPassCountByUserId(userId, planId);
		int noPassCount = calledCount - passCount;
		int transferCount = callRecordService.findTransferCountByUserId(userId, planId);
		// List<Map<String, Object>> listMap =
		// callRecordService.findGradeAndGradeCountByUserId(userId, planId);
		List<Map<String, Object>> listMap1 = callRecordService.selectCallRecordGradeAndCustomerPhone(userId, planId);
		JSONObject j4 = new JSONObject();
		Integer g1 = 0;
		Integer g2 = 0;
		Integer g3 = 0;
		Integer g4 = 0;
		Integer g5 = 0;
		Integer g6 = 0;
		for (Map<String, Object> tempMap : listMap1) {
			String phone = tempMap.get("phone").toString();
			Integer newGrade = (Integer) tempMap.get("grade");
			if (j4.containsKey(phone)) {
				Integer oldGrade = j4.getInteger(phone);
				if (newGrade < oldGrade) {
					j4.put(phone, newGrade);
				}
			} else {
				j4.put(phone, newGrade);
			}
		}
		for (String key : j4.keySet()) {
			Integer grade = j4.getInteger(key);
			switch (grade) {
			case 1:
				g1++;
				break;
			case 2:
				g2++;
				break;
			case 3:
				g3++;
				break;
			case 4:
				g4++;
				break;
			case 5:
				g5++;
				break;
			case 6:
				g6++;
				break;
			}
		}
		JSONArray j3 = new JSONArray();
		JSONObject j2 = null;
		j2 = new JSONObject();
		j2.put("calledCount", g1);
		j2.put("percentage", (calledCount == 0 ? 0 : String.format("%.4f", (float) g1 / calledCount)));
		j2.put("grade", 1);
		j3.add(j2);
		j2 = new JSONObject();
		j2.put("calledCount", g2);
		j2.put("percentage", (calledCount == 0 ? 0 : String.format("%.4f", (float) g2 / calledCount)));
		j2.put("grade", 2);
		j3.add(j2);
		j2 = new JSONObject();
		j2.put("calledCount", g3);
		j2.put("percentage", (calledCount == 0 ? 0 : String.format("%.4f", (float) g3 / calledCount)));
		j2.put("grade", 3);
		j3.add(j2);
		j2 = new JSONObject();
		j2.put("calledCount", g4);
		j2.put("percentage", (calledCount == 0 ? 0 : String.format("%.4f", (float) g4 / calledCount)));
		j2.put("grade", 4);
		j3.add(j2);
		j2 = new JSONObject();
		j2.put("calledCount", g5);
		j2.put("percentage", (calledCount == 0 ? 0 : String.format("%.4f", (float) g5 / calledCount)));
		j2.put("grade", 5);
		j3.add(j2);
		j2 = new JSONObject();
		j2.put("calledCount", g6);
		j2.put("percentage", (calledCount == 0 ? 0 : String.format("%.4f", (float) g6 / calledCount)));
		j2.put("grade", 6);
		j3.add(j2);
		JSONObject totalJSON = new JSONObject();
		totalJSON.put("count", customerTotal);
		totalJSON.put("percentage", 1);
		JSONObject calledCountJSON = new JSONObject();
		calledCountJSON.put("count", calledCount);
		calledCountJSON.put("percentage",
				(customerTotal == 0 ? 0 : String.format("%.4f", (float) calledCount / customerTotal)));
		JSONObject noCalledCountJSON = new JSONObject();
		noCalledCountJSON.put("count", noCalledCount);
		noCalledCountJSON.put("percentage",
				(customerTotal == 0 ? 0 : String.format("%.4f", (float) noCalledCount / customerTotal)));
		JSONObject passCountJSON = new JSONObject();
		passCountJSON.put("count", passCount);
		passCountJSON.put("percentage",
				(calledCount == 0 ? 0 : String.format("%.4f", (float) passCount / calledCount)));
		JSONObject noPassCountJSON = new JSONObject();
		noPassCountJSON.put("count", noPassCount);
		noPassCountJSON.put("percentage",
				(calledCount == 0 ? 0 : String.format("%.4f", (float) noPassCount / calledCount)));
		JSONObject transferCountJSON = new JSONObject();
		transferCountJSON.put("count", transferCount);
		transferCountJSON.put("percentage",
				(calledCount == 0 ? 0 : String.format("%.4f", (float) transferCount / calledCount)));

		result.put("total", totalJSON);
		result.put("calledCount", calledCountJSON);// 已拨打
		result.put("noCalledCount", noCalledCountJSON);// 未拨打
		result.put("passCount", passCountJSON);// 已接通
		result.put("noPassCount", noPassCountJSON);// 未接通s
		result.put("transferCount", transferCountJSON);//
		result.put("gradeAndGradeCount", SortUtil.DichotomySort(j3));
		TTUtil.sendDataByIOStream(response, result);
	}

	private static JSONArray getTimeRang(String timeStr) {
		List<String> timeArr = new ArrayList<String>();
		JSONArray jsonArr = new JSONArray();
		List<Integer> timeList = TransportUtil.stringTransportListForId(timeStr);
		int size = timeList.size();
		if (size == 0) {
			String workStartDateTimeStr = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "startTime");
			String workEndDateTimeStr = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "endTime");
			int start = Integer.valueOf(workStartDateTimeStr.substring(0, 2));
			int end = Integer.valueOf(workEndDateTimeStr.substring(0, 2));
			int tmp = start;
			while (tmp >= start && tmp < end) {
				timeList.add(tmp);
				tmp = tmp + 1;
			}
		}

		size = timeList.size();
		if (size > 0) {
			while (size != 0) {
				int mul = 0;
				List<Integer> list = new ArrayList<Integer>();

				for (int i = 0; i < timeList.size(); i++) {
					int aa = timeList.get(i) - i;
					if (i == 0) {
						int tmp = aa;
						mul = tmp;
					}

					if (aa == mul && i < timeList.size()) {
						list.add(timeList.get(i));

					} else {
						timeArr.add(list.get(0) + ":00-" + (list.get(list.size() - 1) + 1) + ":00");
						timeList.removeAll(list);
						break;
					}
				}
				if (timeList.equals(list) && timeList.size() > 0) {
					timeArr.add(list.get(0) + ":00-" + (list.get(list.size() - 1) + 1) + ":00");
					timeList.clear();
				}
				size = timeList.size();
			}
			for (String str : timeArr) {
				jsonArr.add(str);
			}
		}
		return jsonArr;
	}

	private JSONObject quartzTime(Plan plan, String excuteTimeStr, String timeStr, List<Integer> customerIdList,
			String exuteTimeHHMM, int isAdd) {
		JSONObject result = new JSONObject();
		List<Integer> timeList = TransportUtil.stringTransportListForId(timeStr);

		int isOutTime = isOutTime(plan, exuteTimeHHMM, excuteTimeStr, timeList, timeStr);
		if (isOutTime == 2) {
			result.put("result", 2);
			LOGGER.info("时间已过");
			return result;
		} else if (isOutTime == 4) {
			result.put("result", 4);
			return result;
		}

		JSONObject timeJSON = PlanController.getTimeJSON(timeList, excuteTimeStr);
		String startTime = timeJSON.getString("startTime");
		String endTime = timeJSON.getString("endTime");
		
		String planTag = RandomStringUtils.randomNumeric(20) + System.currentTimeMillis();
		plan.setPlanTag(planTag);
		plan.setCustomerCount(customerIdList.size());
		
		boolean flag = quartzTaskManager.startTask(startTime, plan.getExcuteTime(), plan, isAdd, customerIdList,
				quartzTaskManager, planTag);
		if (flag) {
			flag = quartzTaskManager.endTask(endTime, plan.getExcuteTime(), planTag);
			if (flag) {
				result.put("result", 0);
				LOGGER.info("计划 [ " + plan.getId() + " ]开始和结束的定时器启动成功");
			}
		} else {
			LOGGER.info("添加失败或修改用户计划失败");
			result.put("result", 1);
		}
		return result;
	}

	private int isOutTime(Plan plan, String exuteTimeHHMM, String excuteTimeStr, List<Integer> timeList,
			String timeStr) {
		int result = 0;
		String excuteHH = null;
		try {
			if (timeStr != null && !("[]").equals(timeStr)) {
				excuteHH = excuteTimeStr + " " + timeStr.substring(1, timeStr.length() - 1).split(",")[0] + ":00:00";
			} else {
				if (isNow(excuteTimeStr)) {
					Long currunt = DateUtil.getHHMMSSByDate(new Date()).getTime()  + 2000;
					Date date = new Date(currunt);
					excuteHH = excuteTimeStr + " " + DateUtil.getHHMMSSStrByDate(date);
				} else {
					excuteHH = excuteTimeStr + " 9:00:00";
				}
			}
			plan.setExcuteTime(ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(excuteHH));
			plan.setUpdateTime(ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(excuteHH));
			if (isNow(excuteTimeStr)) {
				if (timeList.size() > 0) {
					String exuteTimeHH = null;
					if (timeList.get(0) < 10) {
						exuteTimeHH = "0" + timeList.get(0);
					} else {
						exuteTimeHH = "" + timeList.get(0);
					}
					if ((ConstantUtil.HH_MM_SS_SDF.parse(exuteTimeHHMM + ":00")
							.after(ConstantUtil.HH_MM_SS_SDF.parse(exuteTimeHH + ":00:00")))) {
						result = 2;
						return result;
					}
				} else {
					if (WorkStartTimeTask.isInWorkTime) {
						result = 0;
					} else {
						result = 4;
					}
					return result;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将时间点的 list 解析出开始时间点和结束时间点
	 * 
	 * @param timeList
	 * @param json
	 * @return
	 */
	public static JSONObject getTimeJSON(List<Integer> timeList, String excuteTimeStr) {
		JSONObject timeJSON = new JSONObject();
		String startTime = null;
		String endTime = null;

		StringBuffer startSB = new StringBuffer();
		StringBuffer endSB = new StringBuffer();
		int size = timeList.size();
		Date now = new Date();

		if (size > 0) {
			startSB.append(timeList.get(0));
		}
		for (int i = 0; i < size - 1; i++) {
			int next = timeList.get(i + 1);
			int add = timeList.get(i) + 1;
			if (next != add) {
				startSB.append(",");
				startSB.append(next);
				endSB.append(timeList.get(i) + 1);
				endSB.append(",");
			}
		}
		if (size > 0) {
			endSB.append(timeList.get(size - 1) + 1);
		}

		if (size > 0) {// 选时间段
			startTime = "0 0 " + startSB + " * * ?";
			endTime = "0 0 " + endSB + " * * ?";
		} else {// 没有选时间段
			String workStartTime = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "startTime");
			String workEndTime = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "endTime");
			
			if (DateUtil.stringFormatToDate_YYYY_MM_DD(excuteTimeStr).after(now)) {// 当天以后的立即执行
				startTime = "0 0 "
						+ workStartTime.substring(1, 2)
						+ " * * ?";
				endTime = "0 0 "
						+ workEndTime.substring(0, 2)
						+ " * * ?";
			} else {// 当天的立即执行
				
				long currentMillSecond = System.currentTimeMillis() + 2 * 1000;// 设置比当前服务时间晚2秒执行
				Date date = new Date();
				date.setTime(currentMillSecond);
				startTime = TransportUtil.transportTimeFormat(ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(date));
				endTime = TransportUtil.transportTimeFormat(ConstantUtil.YYYY_MM_DD_SDF.format(date) + " " + ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "endTime"));
			}
		}
		timeJSON.put("startTime", startTime);
		timeJSON.put("endTime", endTime);
		return timeJSON;
	}

	/**
	 * 判断时间是不是今天
	 * 
	 * @param date
	 * @return 是返回true，不是返回false
	 */
	public static boolean isNow(String date) {
		// 当前时间
		Date now = new Date();
		// 获取今天的日期
		String nowDay = ConstantUtil.YYYY_MM_DD_SDF.format(now);

		return date.equals(nowDay);
	}

	private static boolean isPeriodOfTime(String timeStr) {
		boolean flag = false;

		JSONArray timeRangArr = getTimeRang(timeStr);
		Date now = DateUtil.getHHMMSSByDate(new Date());

		for (int i = 0; i < timeRangArr.size(); i++) {
			String timeRang = timeRangArr.getString(i);
			String startRang = timeRang.split("-")[0] + ":00";
			String endRang = timeRang.split("-")[1] + ":00";
			if (now.after(DateUtil.stringFormatToDate_HH_MM_SS(startRang))
					&& now.before(DateUtil.stringFormatToDate_HH_MM_SS(endRang))) {
				flag = true;
			}
		}
		return flag;
	}
}
