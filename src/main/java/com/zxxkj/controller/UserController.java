package com.zxxkj.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zxxkj.model.Port;
import com.zxxkj.model.Project;
import com.zxxkj.service.IPortService;
import com.zxxkj.service.IProjectService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.User;
import com.zxxkj.service.IUserService;
import com.zxxkj.util.TTUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger lg = Logger.getLogger(UserController.class);
	@Resource
	private IUserService userService;
    @Resource
    private IProjectService iProjectService;
    @Resource
    private IPortService portService;

	@RequestMapping(value = "/edituserinfo", method = RequestMethod.POST)
	@ResponseBody
	public void edituserinfo(HttpServletRequest request, HttpServletResponse response, User user) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || 0 == user.getPhone() || StringUtils.isAnyBlank(user.getSupportStaffName(),
				user.getPassword(), user.getRobotPort(), user.getIdentity())) {
			lg.info("修改人工客服信息接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "修改人工客服信息接口参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		// 根据人工客服的ID,修改客服的信息
		Integer count = userService.editUserInfoByID(user);
		if (null != count && count > 0) {
			lg.info("用户信息 修改成功");
			TTUtil.formatReturn(resultJSON, 0, "用户信息 修改成功");
		} else {
			lg.info("用户信息 修改失败");
			TTUtil.formatReturn(resultJSON, 1, "用户信息 修改失败");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/shotstaffinfo", method = RequestMethod.POST)
	@ResponseBody
	public void shotstaffinfo(HttpServletRequest request, HttpServletResponse response, User user) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1) {
			lg.info("修改人工客服开关接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "修改人工客服开关接口参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		// 根据客服的ID,查看客户的详细信息
		User staff = userService.findUserInfoByID(user);
		if (null == staff) {
			lg.info("查看客服信息 失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "没有找到这个用户");
		} else {
			lg.info("查看客服信息 成功!!!");
			resultJSON.put("data", staff);
			TTUtil.formatReturn(resultJSON, 0, "找到了这个用户");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/editstaffswitch", method = RequestMethod.POST)
	@ResponseBody
	public void editstaffswitch(HttpServletRequest request, HttpServletResponse response, User user, Integer operate) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (null == operate || user.getId() < 1) {
			lg.info("修改人工客服开关接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "修改人工客服开关接口参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userID", user.getId());
		map.put("operate", operate);
		// 根据客服的ID,修改客服的开关
		Integer count = userService.editStaffSwitch(map);
		if (null != count && count > 0) {
			lg.info("客服账号状态 修改成功");
			TTUtil.formatReturn(resultJSON, 0, "客服账号状态 修改成功");
		} else {
			lg.info("客服账号状态 修改失败");
			TTUtil.formatReturn(resultJSON, 1, "客服账号状态 修改失败");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/deletestaffs", method = RequestMethod.POST)
	@ResponseBody
	public void deletestaffs(HttpServletRequest request, HttpServletResponse response, User user, String staffIDs) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (StringUtils.isAnyBlank(staffIDs) || user.getId() < 1) {
			lg.info("批量删除人工客服接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "批量删除人工客服接口参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		int userID = user.getId();
		List<String> staffs = TTUtil.string2list(staffIDs);
		if (null == staffs || staffs.size() < 1) {
			lg.info("批量删除人工客服接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		List<Integer> newStaffIDs = new ArrayList<Integer>();
		for (String tempStaffID : staffs) {
			tempStaffID = tempStaffID.replace("\"", "");
			Integer staffID = Integer.valueOf(tempStaffID);
			newStaffIDs.add(staffID);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentID", userID);
		map.put("staffIDs", newStaffIDs);
		// 根据客服ID的集体,批量删除客服
		Integer count = userService.deleteStaffsByIDs(map);
		if (null != count && count > 0) {
			lg.info("批量删除成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "批量删除成功");
		} else {
			lg.info("批量删除失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "批量删除失败");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/addstaff", method = RequestMethod.POST)
	@ResponseBody
	public void addstaff(HttpServletRequest request, HttpServletResponse response, User user) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (StringUtils.isAnyBlank(user.getPassword(), user.getIdentity(), user.getSupportStaffName(),
				user.getRobotPort(), user.getAccount()) || 0 == user.getPhone() || 0 == user.getParentId()) {
			lg.info("添加人工客服接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "添加人工客服接口参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("account", user.getAccount());
		List<User> users = userService.selectAccountNameNotRepeat(map);
		if (users.size() > 0) {
			lg.info("用户名重复,请更换用户名!!!");
			TTUtil.formatReturn(resultJSON, -1, "用户名重复,请更换用户名!!!");
		} else {
			// 为平台用户添加一个客服
			Integer count = userService.insertStaffByUser(user);
			if (null != count && count > 0) {
				lg.info("添加人工客服成功!!!");
				TTUtil.formatReturn(resultJSON, 0, "添加人工客服成功!!!");
			} else {
				lg.info("添加人工客服失败!!!");
				TTUtil.formatReturn(resultJSON, 1, "添加人工客服失败!!!");
			}
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/stafflist", method = RequestMethod.POST)
	@ResponseBody
	public void stafflist(HttpServletRequest request, HttpServletResponse response, User user, Integer page,
			Integer per) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || null == page || null == per) {
			lg.info("查看客户名下客服接口传递参数异常!!!");
			TTUtil.formatReturn(resultJSON, 404, "查看客户名下客服接口传递参数异常!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		page = (page - 1) * per;
		int userID = user.getId();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("per", per);
		map.put("page", page);
		map.put("userID", userID);
		// 把用户ID做为客服的父ID,计算用户名下客服数量
		Integer count = userService.selectStaffCountByParentID(userID);
		JSONObject temp = new JSONObject();
		if (null != count) {
			if (count == 0) {
				List<User> list = new ArrayList<User>();
				temp.put("count", 0);
				temp.put("list", list);
				resultJSON.put("data", temp);
				lg.info("此接口没有数据!!!");
				TTUtil.formatReturn(resultJSON, 405, "此接口没有数据!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			if (count <= page) {
				lg.info("此接口传递页码有问题!!!");
				TTUtil.formatReturn(resultJSON, 406, "此接口传递页码有问题!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			// 用户ID做为客服的父ID,分页查看用户名下客服
			List<User> users = userService.selectStaffListByParentID(map);
			temp.put("count", count);
			temp.put("list", users);
			resultJSON.put("data", temp);
			lg.info("查看客户名下客服 成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "查看客户名下客服 成功!!!");
		} else {
			lg.info("查看客户名下客服 失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "查看客户名下客服 失败!!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody // 用户端登录接口
    public void login(HttpServletRequest request, HttpServletResponse response, User user) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (StringUtils.isAnyBlank(user.getAccount(), user.getPassword())) {
            lg.info("登录接口传递的用户名或密码不能为空!!!");
            TTUtil.formatReturn(resultJSON, 404, "参数错误");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        // 根据用户的账号和密码,查找用户是否存在或信息是否正确
        User account = userService.findUserInfoByAccAndPwd(user);
        if (null == account) {
            lg.info("用户名或密码错误!!!");
            TTUtil.formatReturn(resultJSON, 1, "用户名或密码错误");
        } else {
            switch (account.getIsActive()) {
                case 0:
                    lg.info("该账号已经关闭,请联系客服处理!!!");
                    TTUtil.formatReturn(resultJSON, 403, "该账号已经被关闭,请联系客服处理!!!");
                    break;
                case 2:
                    lg.info("该账号没有激活,请联系客服激活!!!");
                    TTUtil.formatReturn(resultJSON, 402, "该账号没有激活,请联系客服激活!!!");
                    break;
                case 3:
                    lg.info("该账号已经到期,请联系客服激活!!!");
                    TTUtil.formatReturn(resultJSON, 401, "该账号已经到期,请联系客服激活!!!");
                    break;
                default:
                    if (null != account.getValidTime()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                        Date date = new Date();
                        try {
                            date = sdf.parse(account.getValidTime());
                        } catch (ParseException e) {
                            lg.info(TTUtil.appendString(account, "-->账户有效日期存在异常,请处理"));
                            TTUtil.formatReturn(resultJSON, 400, "该账号状态存在异常,请联系客服处理!!!");
                        }
                        if (date.before(new Date())) {
                            lg.info("该账号已经到期,请联系客服激活!!!");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("userID", account.getId());
                            Integer count = userService.updateUserStatusByID(map);
                            if (null != count && count > 0) {
                                lg.info("经过计算,用户现在已经到期,现将isActive已修改为<3>");
                            } else {
                                lg.info("用户已经到期,但isActive没有修改,<<<<<<!!!!!!>>>>>>");
                            }
                            TTUtil.formatReturn(resultJSON, 401, "该账号已经到期,请联系客服激活!!!");
                        } else {
                            lg.info(TTUtil.appendString(account.getAccount(), "-->账户登录成功!!!"));
                            //判断话术一键关闭和开启的状态，级状态是否一致
                            List<Project> list = iProjectService.findProjectByUserId(account.getId());
                            int getswitchstatus = 0;
                            int switchstatus = 1;
                            if (list.size() > 0) {
                                getswitchstatus = list.get(0).getSwitchStatus();
                                for (Project project : list) {
                                    if (project.getSwitchStatus() != getswitchstatus) {
                                        switchstatus = 0;
                                        lg.info("用户话术状态不一致");
                                        resultJSON.put("message", "用户话术状态不一致");
                                    }
                                }
                            }
                            if (getswitchstatus != switchstatus) {
                                switchstatus = 0;
                            }
                            resultJSON.put("switchstatus", switchstatus);
                            resultJSON.put("data", account);
                            resultJSON.put("isHaveSip", 0);
							resultJSON.put("isHaveGateway", 0);
                            List<Integer> typeList = portService.findGatewayTypeBuUserId(account.getId());
                            if(typeList != null && typeList.size() != 0) {
                            	for (Integer integer : typeList) {
									if(integer == 2) {
										resultJSON.put("isHaveSip", 1);//1表示有sip线路，0表示没有
									}else if(integer == 1) {
										resultJSON.put("isHaveGateway", 1);//1表示有网关，0表示没有
									}
								}
                            }
                            TTUtil.formatReturn(resultJSON, 0, "登录成功!");
                        }


                    } else {
                        lg.info("该账号到期日期存在异常,请联系客服处理!!!");
                        TTUtil.formatReturn(resultJSON, 400, "该账号状态存在异常,请联系客服处理!!!");
                    }
                    break;
            }
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

	@RequestMapping(value = "/chpwd", method = RequestMethod.POST)
	@ResponseBody
	public void chpwd(HttpServletRequest request, HttpServletResponse response, User user, String newpwd) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (StringUtils.isAnyBlank(user.getAccount(), user.getPassword(), newpwd)) {
			lg.info("修改密码接口传递的用户名或新/旧密码不能为空!!!");
			TTUtil.formatReturn(resultJSON, 404, "修改密码接口传递的用户名或新/旧密码不能为空!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		if (newpwd.equals(user.getPassword())) {
			lg.info("修改密码接口传递的新旧密码不能相同!!!");
			TTUtil.formatReturn(resultJSON, 403, "修改密码接口传递的新旧密码不能相同!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		if (newpwd.length() < 6 || newpwd.length() > 12) {
			lg.info("密码位数在6~12位之间,请重试!!!");
			TTUtil.formatReturn(resultJSON, 402, "密码位数在6~12位之间,请重试!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("account", user.getAccount());
		map.put("oldpwd", user.getPassword());
		map.put("newpwd", newpwd);
		// 根据用户名和旧密码,设置新密码
		Integer updateCount = userService.updateUserPassword(map);
		if (null != updateCount && updateCount > 0) {
			lg.info(TTUtil.appendString(user.getAccount(), "-->密码修改成功!!"));
			TTUtil.formatReturn(resultJSON, 0, "用户密码修改成功,请使用新密码登录");
		} else {
			lg.info(TTUtil.appendString(user.getAccount(), "-->密码修改失败!!"));
			TTUtil.formatReturn(resultJSON, 1, "旧密码错误");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}
	/*** 
	* @Param: [request, response, userId 用户Id]
	* @return: void 
	* @Author: FuJacKing
	* @Description:  
	*/ 
    @RequestMapping(value = "/istransport", method = RequestMethod.POST)
    @ResponseBody
    public void istransport(HttpServletRequest request, HttpServletResponse response,Integer userId){
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if(null==userId){
            TTUtil.formatReturn(resultJSON, 404, "用户ID不能为空");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        List<Port> portList= portService.findPortListByUserId(userId, -1, 2);
        boolean flag=false;
        if (portList.size()>0) {
            flag=true;
        }
        resultJSON.put("isany", flag);
        TTUtil.sendDataByIOStream(response, resultJSON);
    }


}
