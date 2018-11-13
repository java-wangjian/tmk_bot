package com.zxxkj.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Gateway;
import com.zxxkj.model.Port;
import com.zxxkj.service.IGatewayService;
import com.zxxkj.service.IPortService;
import com.zxxkj.util.TTUtil;

@Controller
@RequestMapping("/gateway")
public class GatewayController {

	private static final Logger LOGGER = Logger.getLogger(GatewayController.class);
	
	@Resource
	private IGatewayService gatewayService;
	@Resource
	private IPortService portService;

    @RequestMapping(value="/getUserSipData", method= RequestMethod.POST)
    @ResponseBody
    public void getUserSipData(HttpServletRequest request, HttpServletResponse response, Integer userId){
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(userId)) {
            LOGGER.info("参数错误");
            TTUtil.formatReturn(resultJSON, 404, "参数错误");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> map = TTUtil.getParamMap();
        map.put("user_id", userId);
        List<Map<String, Object>> listMap = gatewayService.selectUserSipData(map);
        if (null == listMap || listMap.size() == 0) {
            resultJSON.put("data", new ArrayList<>());
            TTUtil.formatReturn(resultJSON, 0, "成功");
        }
        resultJSON.put("data", listMap);
        TTUtil.sendDataByIOStream(response, resultJSON);
    }
	
	@RequestMapping(value="/getUserGatewayAndPortList", method= RequestMethod.POST)
	@ResponseBody
	public void getUserGatewayAndPortList(HttpServletRequest request, HttpServletResponse response, int userId){
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject resultJSON = new JSONObject();
		JSONArray normalGatewayList = new JSONArray();
		List<Gateway> gatewayList = gatewayService.findGatewayListByUserId(userId);
		if(gatewayList != null && gatewayList.size() > 0) {
			for (Gateway gateway : gatewayList) {
				Integer type = gateway.getType();
				JSONObject gatewayAndPortJSON = new JSONObject();
				gatewayAndPortJSON = new JSONObject();
				gatewayAndPortJSON.put("gateway", gateway.getGatewayNumbers());
				gatewayAndPortJSON.put("gatewayType", type);
				gatewayAndPortJSON.put("gatewayId", gateway.getId());
				normalGatewayList.add(gatewayAndPortJSON);
			}
		}
		resultJSON.put("normalGatewayList", normalGatewayList);
		LOGGER.info("用户< " + userId + " >查询了可用的网关和端口");
		TTUtil.sendDataByIOStream(response, resultJSON);
	}
	
	@RequestMapping(value="/findGatewayInfo", method= RequestMethod.POST)
	@ResponseBody
	public String findGatewayInfo(HttpServletRequest request, HttpServletResponse response, Integer gatewayId, Integer type,
			Integer userId){
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		JSONObject gatewayAndPortJSON = new JSONObject();
		Gateway gateway = gatewayService.findGatewayInfoByGatewayId(gatewayId);
		//1,表示拨打端口，2，表示转接端口
		Integer porttype=1;
		List<Port> portList = portService.findPortListByUserId(userId, gatewayId,porttype);
		if(type == 1) {
			StringBuffer portsStr = new StringBuffer();
			if(portList != null && portList.size() > 0) {
				for (int i = 0; i < portList.size(); i++) {
					Integer port = portList.get(i).getPort();
					portsStr.append(port);
					if(i != (portList.size()-1)) {
						portsStr.append(",");
					}
				}
				LOGGER.info("开始查询网关[ " + gateway.getGatewayNumbers() +" ]的端口信息");
				String url = gateway.getUrl().replaceAll(" ", "");
				JSONObject responseStr = TTUtil.getPortInfo(url,
						"port=" + portsStr + "&info_type=type,number,reg,callstate,signal",gateway.getAuth() + ":" + gateway.getPwd());
				if(responseStr == null) {
					gatewayAndPortJSON.put("portList", 1);//网关信息异常(url,账户或密)
				}else {
					JSONArray portInfoArr = responseStr.getJSONArray("info");
					LOGGER.info("查询结束");
					for (int i = 0; i < portInfoArr.size(); i++) {
						JSONObject portInfo = portInfoArr.getJSONObject(i);
						for (Port port : portList) {
							if(port.getPort() == portInfo.getInteger("port")) {
								portInfo.put("type", port.getType());
								portInfo.remove("number");
								portInfo.remove("callstate");
								portInfo.remove("signal");
							}
						}
						if(2 == Integer.valueOf(portInfo.get("type").toString())) {
							portInfoArr.remove(portInfo);
						}
					}
					LOGGER.info("返回信息");
					gatewayAndPortJSON.put("portList", portInfoArr);
				}
			}
		}else if(type == 2) {
			int callCount = portList.get(0).getPort();
			gatewayAndPortJSON.put("callCount", callCount);
		}else {
			LOGGER.info("网关类型为空");
		}
		
		return gatewayAndPortJSON.toJSONString();
	}
}
