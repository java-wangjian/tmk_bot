package com.zxxkj.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.service.IKeywordService;

@Controller
@RequestMapping("/keyword")
public class KeywordController {

	private static final Logger LOGGER = Logger.getLogger(KeywordController.class);
	
	@Resource
	private IKeywordService keywordService;
	
	/**
	 * 根据keywordId删除单条关键字
	 * @param request
	 * @param response
	 * @param keywordId
	 * @return
	 */
	@RequestMapping(value = "/deleteKeyword", method = RequestMethod.POST)
	@ResponseBody
	public String addAdmin(HttpServletRequest request, HttpServletResponse response, int adminId, int recordId) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		int deleteCount = keywordService.deleteKeywordByRecordId(recordId);
		
		if(deleteCount != 0) {
			result.put("result", 0);
			LOGGER.info(adminId + " 删除了recordId为 " + recordId + " 的关键字");
			return result.toJSONString();
		}
		result.put("result", 1);
		return result.toJSONString();
	}
}
