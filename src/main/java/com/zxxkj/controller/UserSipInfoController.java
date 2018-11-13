package com.zxxkj.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Gateway;
import com.zxxkj.model.Port;
import com.zxxkj.service.FinanceService;
import com.zxxkj.service.IGatewayService;
import com.zxxkj.service.IPortService;
import com.zxxkj.util.TTUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *@Copyright 北京知行信科技有限公司
 *Author: FuJacKing
 *@Date 2018/9/18 11:45
 *Description 根据UserId查询网关信息，用户查看
 */
@Controller
@RequestMapping("/userlines")
public class UserSipInfoController {
    private static final Logger LOGGER = Logger.getLogger(UserSipInfoController.class);
    @Resource
    private IGatewayService gatewayService;
    @Resource
    private IPortService portService;
    @Resource
    private FinanceService financeService;

    @RequestMapping("/sipinfo")
    public void getUserSipInfo(HttpServletRequest request, HttpServletResponse response, Integer userId) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(userId)) {
            LOGGER.info("UserId不能为空");
            TTUtil.formatReturn(resultJSON, 404, "UserId不能为空");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        //根据UserId查询网关信息
        List<Gateway> gatewayList = gatewayService.findGatewayListByUserId(userId);
        JSONArray sipJsonArray = new JSONArray();
        for (Gateway gateway : gatewayList) {
            JSONObject json = new JSONObject();
            Integer gatewayId = gateway.getId();
            json.put("gatewayNumbers", gateway.getGatewayNumbers());
            json.put("url", gateway.getUrl());
            json.put("auth", gateway.getAuth());
            json.put("pwd", gateway.getPwd());
            json.put("gatewayId", gatewayId);
            json.put("type",  gateway.getType());
            if (gateway.getType() == 2) {
                Map<String, Object> map = new HashMap();
                map.put("user_id", userId);
                map.put("sip_id", gatewayId);
                //消费总记录
                Double xf = 0.000;
                xf = financeService.selectUserCostTotalData(map);
                if (null == xf) {
                    xf = 0.000;
                } else {
                    xf.toString();
                }
                //充值总记录
                Map<String, Object> cz = financeService.seleceUserTotalMoney(map);
                String chongzhi = "";
                if (cz == null) {
                    chongzhi = "0.000";
                } else {
                    if (null == cz.get("totalMoney")) {
                        chongzhi = "0.000";
                    } else {
                        chongzhi = cz.get("totalMoney").toString();
                    }
                }
                BigDecimal bxf = new BigDecimal(xf).setScale(3, BigDecimal.ROUND_HALF_UP);
                BigDecimal bcz = new BigDecimal(chongzhi).setScale(3, BigDecimal.ROUND_HALF_UP);
                BigDecimal balanceMoney = bcz.subtract(bxf).setScale(3, BigDecimal.ROUND_DOWN);
                Map<String, Object> map2 = new HashMap();
                map2.put("new_sip_balance", balanceMoney);
                map2.put("userId", userId);
                map2.put("sipId", gatewayId);
                financeService.updateUserSipDataByRecordId(map2);
                json.put("balanceMoney", balanceMoney);
                json.put("unitPrice", gateway.getUnitPrice() == null ? 0 : gateway.getUnitPrice());
                json.put("leftover", gateway.getLeftover() == null ? 0 : gateway.getLeftover());
            }
            Integer porttype=0;
            List<Port> portList = portService.findPortListByUserId(userId, gateway.getId(),porttype);
            JSONArray callPortList = new JSONArray();
            JSONArray transferPortList = new JSONArray();
            for (Port port : portList) {
                JSONObject portJSON = new JSONObject();
                portJSON.put("port", port.getPort());
                portJSON.put("userId", String.valueOf(userId));
                if (port.getType() == 1) {
                    callPortList.add(portJSON);
                } else {
                    transferPortList.add(portJSON);
                }
            }
            json.put("callPortList", callPortList);
            json.put("transferPortList", transferPortList);
            sipJsonArray.add(json);
        }
        LOGGER.info("用户查看SIP线路成功查看成功");
        resultJSON.put("data", sipJsonArray);
        TTUtil.formatReturn(resultJSON, 0, "查看用户线路成功");
        TTUtil.sendDataByIOStream(response, resultJSON);
    }
}
