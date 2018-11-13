package com.zxxkj.controller;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Port;
import com.zxxkj.service.IGatewayService;
import com.zxxkj.service.IPortService;
import com.zxxkj.util.TTUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *@Copyright 北京知行信科技有限公司
 *Author: FuJacKing
 *@Date 2018/9/11 10:54
 *Description 单个线路的修改，删除
 */
@Controller
@RequestMapping("/gateways")
public class GatewaySingleContrller {
    private static final Logger LOGGER = Logger.getLogger(GatewaySingleContrller.class);
    @Resource
    private IGatewayService gatewayService;
    @Resource
    private IPortService portService;

    /***
     * @Param: [request, response, userId]
     * @return: java.lang.String
     * @Author: FuJacKing
     * @Description: 修改用户线路信息或网关信息
     */
    @RequestMapping(value = "/updateProject", method = RequestMethod.POST)
    @ResponseBody
    public String updateProject(HttpServletRequest request, HttpServletResponse response, Integer userId, Integer gatewayType, Integer gatewayId, String url, String auth, String pwd, String portOnStr, String transProtStr, Integer callCount) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        //返回数据
        JSONObject result = new JSONObject();
        //接收前端数据，进行数据校验，根据线路类型，校验不同的参数
        if (TTUtil.isAnyNull(userId, gatewayType, gatewayId)) {
            LOGGER.info("接口请求参数错误!");
            result = TTUtil.formatReturn(result, 404, "接口请求参数错误");
            return result.toJSONString();
        } else if (gatewayType == 1) {
            if (TTUtil.isAnyNull(url, auth, pwd, portOnStr)) {
                LOGGER.info("网关请求参数错误!");
                result = TTUtil.formatReturn(result, 404, "网关请求参数错误");
                return result.toJSONString();
            }
        } else if (gatewayType == 2) {
            if (TTUtil.isAnyNull(callCount)) {
                result = TTUtil.formatReturn(result, 404, "SIP线路请求参数错误");
                return result.toJSONString();
            }
        }

        //业务处理
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("gatewayId", gatewayId);
//        int deleteCount = portService.deletePortByUserId(param);
        List<Port> portList = new ArrayList<Port>();
       
        //网关
        if (gatewayType == 1) {
            //判断端口的类型
            String[] portOn = portOnStr.substring(1, portOnStr.length() - 1).split(",");
            String[] transProt = transProtStr.substring(1, transProtStr.length() - 1).split(",");

            if((portOn[0]==null||portOn[0].equals("")&&(transProt[0]==null||transProt[0].equals("")))){
                result = TTUtil.formatReturn(result, 404, "网关请求参数错误");
                return result.toJSONString();
            }

            Integer updateflag = gatewayService.updateGatewayByGatewayId(gatewayId, url, auth, pwd);
            if (updateflag != 0) {
                //网关拨打端口数据更新
            	int deleteCount = portService.deletePortByUserId(userId, gatewayId);
            	LOGGER.info("用户[ " + userId + " ]删除了之前的[ " + deleteCount + " ]个端口");

                if (!(portOn[0]==null||portOn[0].equals(""))) {

                    for (String porton : portOn) {
                        Port port = new Port();
                        port.setPort(Integer.parseInt(porton));
                        port.setGatewayId(gatewayId);
                        port.setType(1);
                        port.setUserId(userId);
                        portList.add(port);
                    }
                }

                //网关转接端口数据更新

                if (!(transProt[0]==null||transProt[0].equals(""))) {
//                	List<Integer> transferPortList = portService.findTracferPortByUserId(userId, 2);
                    for (String porton : transProt) {
                        Port port = new Port();
                        port.setPort(Integer.parseInt(porton));
                        port.setGatewayId(gatewayId);
                        port.setType(2);
                        port.setUserId(userId);
                        portList.add(port);
                    }
                }
            }
        }
        //Sip线路
        if (gatewayType == 2) {
            Port port = new Port();
            port.setPort(callCount);
            port.setGatewayId(gatewayId);
            port.setType(1);
            port.setUserId(userId);
            portList.add(port);
        }
        
        //批量添加端口
        Integer batchAddCount = null;
        if (portList.size() > 0) {
            batchAddCount = portService.batchAddPort(portList);
            LOGGER.info("用户[ " + userId + " ]新加了[ " + batchAddCount + "]个端口");
            result.put("result", 0);
        }
        return result.toJSONString();
    }
    @RequestMapping(value = "/deleteProject", method = RequestMethod.POST)
    @ResponseBody
    public String deleteProject(HttpServletRequest request, HttpServletResponse response, int userId, Integer gatewayId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        //返回数据
        JSONObject result = new JSONObject();

        int deleteCount = portService.deletePortByUserId(userId, gatewayId);
        result.put("result", 0);
        result.put("delcount", deleteCount);
        return result.toJSONString();
    }
}
