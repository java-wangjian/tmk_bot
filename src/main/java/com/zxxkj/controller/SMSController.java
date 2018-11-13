package com.zxxkj.controller;

import java.util.ArrayList;
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

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.SMS;
import com.zxxkj.service.SMSService;
import com.zxxkj.service.SimCardService;
import com.zxxkj.util.TTUtil;

@Controller
@RequestMapping("/sms")
public class SMSController {

	private static final Logger lg = Logger.getLogger(SMSController.class);
	@Resource
	private SMSService smsService;
	
	@RequestMapping(value = "/listSMS", method = RequestMethod.POST)
	@ResponseBody // 测试方法:短信模板 列表
	public void listSMS(HttpServletRequest request, HttpServletResponse response,Integer page ,Integer per,Integer userId,String condition) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (TTUtil.isAnyNull(page,per,userId)) {
			lg.info("必选参数为空!!!");
			TTUtil.formatReturn(resultJSON, 404, "必选参数为空!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = TTUtil.getParamMap();
		page = (page - 1) * per;
		map.put("userId", userId);
		map.put("page", page);
		map.put("per", per);
		map.put("condition", condition);
		Integer count = smsService.selectSmsCountByUserIdAndCondition(map);
		if (null == count) {
			lg.info("短信模板列表 查询失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "失败!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		JSONObject temp = new JSONObject();
		if (0 == count) {
			temp.put("count", 0);
			temp.put("list", new ArrayList<SMS>());
			resultJSON.put("data", temp);
			lg.info("短信模板列表 查询成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "成功!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		List<SMS> smsList = smsService.selectSmsListByUserID(map);
		if (null == smsList) {
			lg.info("短信模板列表 查询失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "失败!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		temp.put("count", smsList.size());
		temp.put("list", smsList);
		resultJSON.put("data", temp);
		lg.info("短信模板列表 查询成功!!!");
		TTUtil.formatReturn(resultJSON, 0, "成功!!!");
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/insertSMS", method = RequestMethod.POST)
	@ResponseBody // 测试方法: 新建 短信模板
	public void insertSMS(HttpServletRequest request, HttpServletResponse response,SMS sms) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (TTUtil.isAnyNull(sms.getContent(),sms.getGrade(),sms.getProjectId(),sms.getUserId(),sms.getProjectName(),sms.getName())) {
			lg.info("必选参数为空!!!");
			TTUtil.formatReturn(resultJSON, 404, "必选参数为空!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		if (sms.getContent().length() > 70) {
			lg.info("短信长度过长!!!");
			TTUtil.formatReturn(resultJSON, 403, "短信长度过长!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> paramMap = TTUtil.getParamMap();
		paramMap.put("sms", sms);
		Integer effect = smsService.insertSmsTemplet(paramMap);
		if (null == effect) {
			lg.info("插入 短信模板 失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "失败!!!");
		} else {
			lg.info("插入 短信模板 成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "成功!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/deleteSMS", method = RequestMethod.POST)
	@ResponseBody // 测试方法: 删除 短信模板
	public void deleteSMS(HttpServletRequest request, HttpServletResponse response,Integer smsId) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (null == smsId) {
			lg.info("必选参数为空!!!");
			TTUtil.formatReturn(resultJSON, 404, "必选参数为空!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = TTUtil.getParamMap();
		map.put("smsId", smsId);
		Integer effect = smsService.deleteSmsTemplet(map);
		if (null == effect) {
			lg.info("删除 短信模板 失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "失败!!!");
		} else {
			lg.info("删除 短信模板 成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "成功!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/updateSMS", method = RequestMethod.POST)
	@ResponseBody // 测试方法:修改 短信模板
	public void updateSMS(HttpServletRequest request, HttpServletResponse response,SMS sms) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (null == sms.getId()) {
			lg.info("必选参数为空!!!");
			TTUtil.formatReturn(resultJSON, 404, "必选参数为空!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = TTUtil.getParamMap();
		map.put("sms", sms);
		Integer effect = smsService.updateSmsTemplet(map);
		if (null == effect) {
			lg.info("更新 短信模板 失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "失败!!!");
		} else {
			lg.info("更新 短信模板 成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "成功!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/switchSMS", method = RequestMethod.POST)
	@ResponseBody // 测试方法:短信模板 开关
	public void switchSMS(HttpServletRequest request, HttpServletResponse response,Integer smsId,Integer operate) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if ( TTUtil.isAnyNull(smsId,operate) ) {
			lg.info("必选参数为空!!!");
			TTUtil.formatReturn(resultJSON, 404, "必选参数为空!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = TTUtil.getParamMap();
		map.put("smsId",smsId);
		map.put("operate", operate);
		Integer effect = smsService.switchSmsTemplet(map);
		if (null == effect) {
			lg.info("状态 短信模板 失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "失败!!!");
		} else {
			lg.info("状态 短信模板 成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "成功!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}
	
	@RequestMapping(value = "/01", method = RequestMethod.POST)
	@ResponseBody // 测试方法
	public void insertAfter(HttpServletRequest request, HttpServletResponse response) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);

	}

}
