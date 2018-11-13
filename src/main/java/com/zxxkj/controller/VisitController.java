package com.zxxkj.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Visit;
import com.zxxkj.service.ICallRecordService;
import com.zxxkj.service.ICustomerService;
import com.zxxkj.service.IVisitService;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.ParameterProperties;
import com.zxxkj.util.TTUtil;

@Controller
@RequestMapping("/visit")
public class VisitController {

	private static final Logger LOGGER = Logger.getLogger(VisitController.class);
	
	@Resource
	private IVisitService visitService;
	@Resource
	private ICustomerService customerService;
	@Resource
	private ICallRecordService callRecordService;
	
	
	@RequestMapping(value = "/addVisit", method = RequestMethod.POST)
	@ResponseBody
	public String addVisit(HttpServletRequest request, HttpServletResponse response, Visit visit) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		
		int customerId = visit.getCustomerId();
		int grade = visit.getGrade();
		int updateCount = customerService.updateGradeByCustomerId(customerId, grade);
		//callRecordService.updateGradeByCustomerIdAndUserId(grade, customerId, visit.getUserId());
		if(updateCount > 0) {
			visit.setVisitWay("电话");
			int addCount = visitService.addVisitRecord(visit);
			if(addCount > 0) {
				result.put("add", visit.getId());
				LOGGER.info("添加拜访记录成功");
			}else {
				result.put("add", 1);
				LOGGER.info("添加拜访记录失败");
			}
			result.put("result", 0);
			return result.toJSONString();
		}
		result.put("result", 1);
		return result.toJSONString();
	}

    @RequestMapping(value = "/findVisitListByPlanId", method = RequestMethod.POST)
    @ResponseBody
    public void findVisitListByPlanId(HttpServletRequest request, HttpServletResponse response, Integer userId, Integer curPage,Integer per, Integer planId) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if ( TTUtil.isAnyNull(userId,curPage,per,planId)) {
            LOGGER.info("查看拜访接口传递参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "查看拜访接口传递参数错误");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> map = TTUtil.getParamMap();
        map.put("userId",userId);
        map.put("planId",planId);
        Integer total = visitService.selectVisitCountByPlanId(map);
        if (null != total && total > 0) {
            curPage = (curPage - 1) * per;
            map.put("start",curPage);
            map.put("per",per);
            List<Map<String, Object>> visitMapList = visitService.selectVisitListByPlanId(map);
            for (Map<String, Object> tempMap : visitMapList) {
                tempMap.put("customerName", null == tempMap.get("customerName") ? "" : tempMap.get("customerName"));
                tempMap.put("grade",null == tempMap.get("grade") ? "" : tempMap.get("grade"));
                tempMap.put("visitWay",null == tempMap.get("visitWay") ? "" : tempMap.get("visitWay"));
                tempMap.put("visitDetails",null == tempMap.get("visitDetails") ? "" : tempMap.get("visitDetails"));
                tempMap.put("planName",null == tempMap.get("planName") ? "" : tempMap.get("planName"));
            }
            JSONObject temp = new JSONObject();
            temp.put("count", total);
            temp.put("list", visitMapList);
            resultJSON.put("data", temp);
            TTUtil.formatReturn(resultJSON,0,"成功");
        } else {
            TTUtil.formatReturn(resultJSON,1,"失败");
        }
        TTUtil.sendDataByIOStream(response,resultJSON);
    }

	@RequestMapping(value = "/findVisitList", method = RequestMethod.POST)
	@ResponseBody
	public void findVisitList(HttpServletRequest request, HttpServletResponse response, int userId, int curPage) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		JSONArray resultArr = new JSONArray();
		int count = Integer.parseInt(ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "normalCount"));
		int start = (curPage - 1) * count;
		
		int total = visitService.findCountByUserId(userId);
		List<Map<String, Object>> visitMapList = visitService.findVisitListByUserId(userId, start, count);
		for (Map<String, Object> map : visitMapList) {
			map.put("company", (map.get("company") == null ? "" : map.get("company")));
			map.put("customerName", (map.get("customerName") == null ? "" : map.get("customerName")));
			map.put("customerPhone", (map.get("customerPhone") == null ? "" : map.get("customerPhone")));
			map.put("grade", (map.get("grade") == null ? "" : map.get("grade")));
			map.put("visitTime", (map.get("visitTime") == null ? "" : map.get("visitTime")));
			map.put("visitWay", (map.get("visitWay") == null ? "" : map.get("visitWay")));
			map.put("visitDetails", (map.get("visitDetails") == null ? "" : map.get("visitDetails")));
			resultArr.add(JSON.toJSON(map));
		}
		LOGGER.info(userId + " 查询了拜访列表");
		
		result.put("list", resultArr);
		result.put("total", total);
		TTUtil.sendDataByIOStream(response, result);
	}
}
