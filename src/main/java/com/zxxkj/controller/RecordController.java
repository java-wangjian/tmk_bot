package com.zxxkj.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Record;
import com.zxxkj.service.IRecordService;

@Controller
@RequestMapping("/record")
public class RecordController {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(AdminController.class);
	
	@Resource
	private IRecordService recordService;
	
	
	@RequestMapping(value = "/findRecordByProjectId", method = RequestMethod.POST)
	@ResponseBody
	public String findRecordByProjectId(HttpServletRequest request, HttpServletResponse response, Record record) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		int projectId = record.getProjectId();
		
		@SuppressWarnings("unused")
		List<Record> recordList = recordService.findRecordByProjectId(projectId);
		
		
		return result.toJSONString();
	}
}
