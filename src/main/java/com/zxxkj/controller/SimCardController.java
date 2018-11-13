package com.zxxkj.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.SimCard;
import com.zxxkj.model.User;
import com.zxxkj.service.SimCardService;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.HTTPUtil;
import com.zxxkj.util.TTUtil;

@Controller
@RequestMapping("/simcard")
public class SimCardController {

	@Resource
	private SimCardService simCardService;
	private static final Logger lg = Logger.getLogger(SimCardController.class);
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public void list(HttpServletRequest request, HttpServletResponse response, User user, Integer page, Integer per) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || null == page || null == per) {
			lg.info("手机卡列表接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "手机卡列表接口传递参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		int userID = user.getId();
		page = (page - 1) * per;
		// 根据用户的ID,查询用户名下端口的数量,方便分页
		Integer count = simCardService.selectCountFromSimCardTable(user);
		if (null != count) {
			if (count == 0) {
				lg.info("此接口没有数据!!!");
				TTUtil.formatReturn(resultJSON, 405, "此接口没有数据!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("page", page);
			map.put("id", userID);
			map.put("per", per);
			// 根据用户ID,分页显示用户名下手机卡的列表
//			List<SimCard> simCardList = simCardService.selectListFromSimCardTable(map);
			List<Map<String,Object>> listMap = simCardService.selectPortsInfo(map);
			JSONObject disposePortInfo = new JSONObject();
			JSONObject authsJSON = new JSONObject();
			List<String> ports;
			for (Map<String, Object> tempMap : listMap) {
				Integer type = (Integer) tempMap.get("type");
				if(type == null) {
					break;
				}
				String gatewayUrl = (String) tempMap.get("gatewayUrl");
				String gatewayNode = (String) tempMap.get("gatewayNode");
				String auth = (String) tempMap.get("auth");
				String pwd = (String) tempMap.get("pwd");
				String port = (String) tempMap.get("port");
				if (1 == type && StringUtils.isNoneBlank(gatewayNode,gatewayUrl,auth,pwd) && null != port) {
					Object object = disposePortInfo.get(gatewayNode + "<->" + gatewayUrl);
					if (null != object) {
						ports = (List<String>) disposePortInfo.get(gatewayNode + "<->" + gatewayUrl);
					} else {
						ports = new ArrayList<String>();
					}
					authsJSON.put(gatewayUrl, auth+":"+pwd);
					ports.add(port);
					disposePortInfo.put(gatewayNode + "<->" + gatewayUrl, ports);
				}
			}
			Set<String> keySet = disposePortInfo.keySet();
			JSONObject portJSON = new JSONObject();
			for (String nodeAndUrl : keySet) {
				JSONArray portArray = disposePortInfo.getJSONArray(nodeAndUrl);
				String portsStr = portArray.toString();
				portsStr = portsStr.replace("[", "");
				portsStr = portsStr.replace("\"", "");
				portsStr = portsStr.replace("]", "");
				List<String> kkkPort = TTUtil.string2list(portsStr);
				String url = nodeAndUrl;
				int index = url.indexOf("<->");
				url = url.substring(index + 3);
				if (StringUtils.isNotBlank(url)) {
					JSONObject responseStr = TTUtil.getPortInfo(url,
							"port=" + portsStr + "&info_type=type,number,reg,callstate,signal",authsJSON.getString(url));
					String tempNode = nodeAndUrl;
					index = tempNode.indexOf("<->");
					tempNode = tempNode.substring(0, index);
					if (null != responseStr && 200 == responseStr.getInteger("error_code")) {
						JSONArray infoArray = responseStr.getJSONArray("info");
						for (Object object : infoArray) {
							JSONObject temp = (JSONObject) object;
							portJSON.put(tempNode + "<->" + temp.getInteger("port"), temp);
						}
					} else {
						for (String tempKKK : kkkPort) {
							portJSON.put(tempNode + "<->" + tempKKK, null);
						}
					}
				}
			}
			for (Map<String, Object> tempMap : listMap) {
                Integer type = (Integer) tempMap.get("type");
                if (null != type) {
                    if (1 == type) {
                        String gatewayNode = (String) tempMap.get("gatewayNode");
                        String port = (String) tempMap.get("port");
                        JSONObject json = portJSON.getJSONObject(gatewayNode + "<->" + port);
                        if (StringUtils.isNoneBlank(gatewayNode,port)) {
                            if (null == json) {
                                tempMap.put("status",1);
                                tempMap.put("now",1);
                                tempMap.put("signal",0);
                                tempMap.put("phone",0L);
                            }else {
                                String reg = json.getString("reg");
                                String callstate = json.getString("callstate");
                                /**
                                 * 没有SIM：-2
                                 * 正常状态：0 (1:空闲，0:使用中)
                                 * 未知状态：1
                                 */
                                switch (reg) {
                                    case "NO_SIM":
                                        tempMap.put("status",-2);
                                        tempMap.put("now",-2);
                                        break;
                                    case "REGISTER_OK":
                                        tempMap.put("status",0);
                                        if ("Idle".equals(callstate)) {
                                            tempMap.put("now",1);
                                        } else {
                                            tempMap.put("now",0);
                                        }
                                        break;
                                    default:
                                        tempMap.put("status",1);
                                        if (1 == type) {
                                            tempMap.put("now",0);
                                        } else {
                                            tempMap.put("now",1);
                                        }
                                        break;
                                }
                                tempMap.put("signal",json.getInteger("signal"));
                                Long phone = json.getLong("number");
                                if (null != phone) {
                                    String tempPhone = String.valueOf(phone);
                                    int index = tempPhone.indexOf("1");
                                    tempPhone = tempPhone.substring(index);
                                    phone = Long.valueOf(tempPhone);
                                } else {
                                    phone = 0L;
                                }
                                tempMap.put("phone",phone);
                            }
                        }
                    } else {
                        // type：2
                        //线路呼叫状态now, 查询出未null状态改为3(默认可使用)
                        tempMap.put("status",0);
                        tempMap.put("now",3);
                        tempMap.put("signal",0);
                        tempMap.put("phone",0L);
                    }
                } else {
                    // type:空
                    tempMap.put("status",1);
                    tempMap.put("now",1);
                    tempMap.put("signal",0);
                    tempMap.put("phone",0L);
                }
            }
			JSONObject temp = new JSONObject();
			temp.put("count", count);
			temp.put("list", listMap);
			resultJSON.put("data", temp);
			lg.info("查看Sim卡列表成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "手机卡列表 查看  成功!!!");
		} else {
			lg.info("查看Sim卡列表失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "手机卡列表 查看 失败!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

//	@RequestMapping(value = "/restart", method = RequestMethod.POST)
//	@ResponseBody
//	public void restart(HttpServletRequest request, HttpServletResponse response, SimCard sim) {
//		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
//		if (null == sim.getId()) {
//			lg.info("重启Sim卡端口接口传递参数错误!!!");
//			TTUtil.formatReturn(resultJSON, 404, "重启Sim卡端口接口传递参数错误!!!");
//			TTUtil.sendDataByIOStream(response, resultJSON);
//			return;
//		}
//		lg.info("手机卡的重启端口!!!");
//		TTUtil.formatReturn(resultJSON, 0, "手机卡重启端口");
//		TTUtil.sendDataByIOStream(response, resultJSON);
//		return;
//	}

//	@RequestMapping(value = "/search", method = RequestMethod.POST)
//	@ResponseBody
//	public void search(HttpServletRequest request, HttpServletResponse response, User user, String param, Integer page,
//			Integer per) {
//		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
//		if (user.getId() < 1 || StringUtils.isAnyBlank(param) || null == page || null == per) {
//			lg.info("搜索Sim卡开关接口传递参数错误!!!");
//			TTUtil.formatReturn(resultJSON, 404, "搜索Sim卡开关接口传递参数错误!!!");
//			TTUtil.sendDataByIOStream(response, resultJSON);
//			return;
//		}
//		page = (page - 1) * per;
//		Integer userID = user.getId();
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("param", param);
//		map.put("per", per);
//		map.put("userID", userID);
//		// 根据参数,计算模糊搜索到的符合参数的手机号数量
//		Integer count = simCardService.selectCountPhoneNum(map);
//		if (null != count) {
//			if (count == 0) {
//				lg.info("此接口没有数据!!!");
//				TTUtil.formatReturn(resultJSON, 405, "此接口没有数据!!!");
//				TTUtil.sendDataByIOStream(response, resultJSON);
//				return;
//			}
//			if (count <= page) {
//				lg.info("此接口传递页码有问题!!!");
//				TTUtil.formatReturn(resultJSON, 406, "此接口传递页码有问题!!!");
//				TTUtil.sendDataByIOStream(response, resultJSON);
//				return;
//			}
//			map.put("page", page);
//			// 根据参数,模糊搜索手机卡,分页返回手机卡列表
//			List<SimCard> simCardList = simCardService.selectListPhoneNum(map);
//			JSONObject temp = new JSONObject();
//			temp.put("count", count);
//			temp.put("list", simCardList);
//			resultJSON.put("data", temp);
//			lg.info("搜索SIM卡接口 查看 成功!!!");
//			TTUtil.formatReturn(resultJSON, 0, "搜索SIM卡接口 查看 成功!!!");
//		} else {
//			lg.info("搜索SIM卡接口 查看失败!!!");
//			TTUtil.formatReturn(resultJSON, 1, "搜索SIM卡接口 查看失败!!!");
//		}
//		TTUtil.sendDataByIOStream(response, resultJSON);
//		return;
//	}

	// @RequestMapping(value = "/switch", method = RequestMethod.POST)
	// @ResponseBody
	// public void switchSimCard(HttpServletRequest request, HttpServletResponse
	// response, SimCard simCard) {
	// JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
	// if (null == simCard.getIsActive() || null == simCard.getId()) {
	// lg.info("修改Sim卡开关接口传递参数错误!!!");
	// TTUtil.formatReturn(resultJSON, 404, "修改Sim卡开关接口传递参数错误!!!");
	// TTUtil.sendDataByIOStream(response, resultJSON);
	// return;
	// }
	// // 根据手机卡的ID,调节手机卡的激活开关
	// Integer count = simCardService.switchSimCardInfo(simCard);
	// if (null != count && count > 0) {
	// lg.info("Sim卡开关修改成功!!!");
	// TTUtil.formatReturn(resultJSON, 0, "Sim卡开关修改成功!!!");
	// } else {
	// lg.info("Sim卡开关修改失败!!!");
	// TTUtil.formatReturn(resultJSON, 1, "Sim卡开关修改失败!!!");
	// }
	// TTUtil.sendDataByIOStream(response, resultJSON);
	// return;
	// }

	// @RequestMapping(value = "/edit", method = RequestMethod.POST)
	// @ResponseBody
	// public void edit(HttpServletRequest request, HttpServletResponse response,
	// SimCard simCard) {
	// JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
	// if (StringUtils.isAnyBlank(simCard.getPrefix()) || null == simCard.getId()) {
	// lg.info("修改Sim卡接口传递参数错误!!!");
	// TTUtil.formatReturn(resultJSON, 404, "修改Sim卡接口传递参数错误");
	// TTUtil.sendDataByIOStream(response, resultJSON);
	// return;
	// }
	// // 根据手机卡的ID,修改手机卡的信息
	// Integer count = simCardService.updateSimCardInfo(simCard);
	// if (null != count && count > 0) {
	// lg.info("修改Sim卡信息 成功!!!");
	// TTUtil.formatReturn(resultJSON, 0, "修改Sim卡信息 成功!!!");
	// } else {
	// lg.info("修改Sim卡信息 失败!!!");
	// TTUtil.formatReturn(resultJSON, 1, "修改Sim卡信息 失败!!!");
	// }
	// TTUtil.sendDataByIOStream(response, resultJSON);
	// return;
	// }

//	@RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
//	@ResponseBody
//	public void batchDelete(HttpServletRequest request, HttpServletResponse response, String simCardIDs, User user) {
//		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
//		if (user.getId() < 1 || null == simCardIDs) {
//			lg.info("批量删除Sim卡接口传递参数错误!!!");
//			TTUtil.formatReturn(resultJSON, 404, "批量删除Sim卡接口传递参数错误!!!");
//			TTUtil.sendDataByIOStream(response, resultJSON);
//			return;
//		}
//		int userID = user.getId();
//		List<String> simIDs = TTUtil.string2list(simCardIDs);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("simIDs", simIDs);
//		map.put("userID", userID);
//		// 根据手机卡ID的集合,批量删除手机卡
//		Integer count = simCardService.batchDeleteSimCard(map);
//		if (null != count && count > 0) {
//			lg.info("批量删除Sim卡成功");
//			TTUtil.formatReturn(resultJSON, 0, "批量删除Sim卡成功!!!");
//		} else {
//			lg.info("批量删除Sim卡失败");
//			TTUtil.formatReturn(resultJSON, 1, "批量删除Sim卡失败!!!");
//		}
//		TTUtil.sendDataByIOStream(response, resultJSON);
//	}

}