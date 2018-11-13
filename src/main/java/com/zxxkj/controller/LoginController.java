package com.zxxkj.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Admin;
import com.zxxkj.model.User;
import com.zxxkj.remoteDao.RemoteAdminDao;
import com.zxxkj.service.IAdminService;
import com.zxxkj.service.IUserService;
import com.zxxkj.util.ConstantUtil;

@Controller
@RequestMapping("/login")
public class LoginController {

	private static final Logger LOGGER = Logger.getLogger(LoginController.class);
	
	@Resource
    private IUserService userService;
	@Resource
	private IAdminService adminService;
	@Resource
	private RemoteAdminDao remoteAdminDao;
	
	/**
	 * B端用户登录
	 * @param request
	 * @param respons
	 * @param user
	 * @return 添加成功后的id
	 */
	@RequestMapping(value="/userLogin", method= RequestMethod.POST)
	@ResponseBody
	public String userLogin(HttpServletRequest request, HttpServletResponse response, 
			User user){
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		
		userService.addUser(user);
		
		result.put("result", user.getId());
		return result.toJSONString();
	}
	
	/**
	 * 后台管理员登录-
	 * @param request
	 * @param admin
	 * @return 添加成功后的id
	 */
	@RequestMapping(value="/adminLogin", method= RequestMethod.POST)
	@ResponseBody
	public String adminLogin(HttpServletRequest request, HttpServletResponse response, 
			Admin admin){
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		
		//Admin DBadmin = adminService.findUserByAccountAndPassword(admin);
		Admin DBadmin = remoteAdminDao.findAdminByAccountAndPassword(admin);
		if(DBadmin == null) {
			result.put("result", -1);
			LOGGER.info(admin.getAccount() + " 登录错误 ");
			return result.toJSONString();
		} if (-1 == DBadmin.getStatus()) {
            result.put("result", -2);
            LOGGER.info(admin.getAccount() + " 用户到期 ");
            return result.toJSONString();
		}else {
			//int adminId = DBadmin.getId();
			int adminId = DBadmin.getId();
			List<User> userList = userService.findAllUserByAdminId(adminId);
			List<Integer> userIdList = new ArrayList<Integer>();
			for (User user : userList) {
				try {
					if(new Date().getTime() > ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(user.getValidTime()).getTime() && user.getIsActive() != 3) {
						userIdList.add(user.getId());
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if(userIdList.size() > 0) {
				LOGGER.info("有 " + userIdList.size() + " 个用户已到期" );
				int updateCount = userService.updateActiveByUserIdList(userIdList, 3);
				if(updateCount == userIdList.size()) {
					LOGGER.info("有 " + updateCount + " 已成功修改为已到期状态");
				}
			}
			result.put("result", adminId);
			LOGGER.info(admin.getAccount() + " 登录后台 ");
		}
		
		return result.toJSONString();
	}
	
	@RequestMapping(value="/test", method= RequestMethod.POST)
	@ResponseBody
	public String test(HttpServletRequest request, HttpServletResponse response, 
			Admin admin){
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		
		//Integer adminId = remoteAdminDao.findUserByAccountAndPassword(admin);
		//result.put("adminId", adminId);
		return result.toJSONString();
	}
}
