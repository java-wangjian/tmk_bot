package com.zxxkj.controller;

import java.util.HashMap;
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
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Statictics;
import com.zxxkj.model.User;
import com.zxxkj.service.StaticticsService;
import com.zxxkj.util.TTUtil;

@Controller
@RequestMapping("/statictics")
public class StaticticsController {

    private static final Logger lg = Logger.getLogger(StaticticsController.class);
    @Resource
    private StaticticsService staticticsService;

    @RequestMapping(value = "/custompic", method = RequestMethod.POST)
    @ResponseBody
    public void custompic(HttpServletRequest request, HttpServletResponse response, User user, String startDate,
                          String endDate) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (user.getId() < 1 || StringUtils.isAnyBlank(startDate, endDate)) {
            lg.info("查看区间统计数据 接口传递参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "查看区间统计数据 接口传递参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        int userID = user.getId();
        List<String> calList = TTUtil.calendarFormat(startDate, endDate);
        if (calList.size() > 32) {
            lg.info("查看区间统计数据 传递日期大于1个月!!!");
            TTUtil.formatReturn(resultJSON, 1, "查看区间统计数据 传递日期大于1个月!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        JSONObject data = new JSONObject(true);
        for (String cal : calList) {
            JSONObject temp = new JSONObject();
            JSONObject json = new JSONObject();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userID", userID);
            map.put("date", cal);
            // 根据用户ID和日期,返回当日统计的详细信息
            Statictics s = staticticsService.selectDataByUserIDAndDate(map);
            if (null == s) {
                temp.put("A", 0);
                temp.put("B", 0);
                temp.put("C", 0);
                temp.put("D", 0);
                temp.put("E", 0);
                temp.put("F", 0);
                temp.put("callCount", 0);
                temp.put("callduration", 0);
            } else {
                json = (JSONObject) JSON.toJSON(s);
                temp.put("A", json.getInteger("customerA"));
                temp.put("B", json.getInteger("customerB"));
                temp.put("C", json.getInteger("customerC"));
                temp.put("D", json.getInteger("customerD"));
                temp.put("E", json.getInteger("customerE"));
                temp.put("F", json.getInteger("customerF"));
                temp.put("callCount", json.getInteger("callCount"));
                temp.put("callduration", json.getInteger("duratTotal"));
            }
            data.put(cal, temp);
        }
        lg.info("查看日期区间数据统计成功!!!");
        resultJSON.put("data", data);
        TTUtil.formatReturn(resultJSON, 0, "查看成功!!!");
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/singledata", method = RequestMethod.POST)
    @ResponseBody
    public void singledata(HttpServletRequest request, HttpServletResponse response, User user, String dateStr) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (user.getId() < 1 || StringUtils.isAnyBlank(dateStr) || dateStr.length() != 10) {
            lg.info("查看当日数据 接口传递参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "查看当日数据 接口传递参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        int userID = user.getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userID", userID);
        map.put("date", dateStr);
        JSONObject json = new JSONObject();
        // 根据用户ID和日期,返回当日统计的详细信息
        Statictics s = staticticsService.selectDataByUserIDAndDate(map);
        if (null == s) {
            JSONObject temp = new JSONObject();
            temp.put("date", dateStr);
            temp.put("customerA", 0);
            temp.put("customerB", 0);
            temp.put("customerC", 0);
            temp.put("customerD", 0);
            temp.put("customerE", 0);
            temp.put("customerF", 0);
            temp.put("connCount", 0);
            temp.put("callCount", 0);
            temp.put("duratTotal", 0);
            temp.put("toStaffCount", 0);
            temp.put("refuseCount", 0);
            temp.put("gt30", 0);
            temp.put("lt10gt5", 0);
            temp.put("missCount", 0);
            resultJSON.put("data", temp);
            TTUtil.formatReturn(resultJSON, 1, "没有当日的数据!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        } else {
            json = (JSONObject) JSON.toJSON(s);
            resultJSON.put("data", json);
            lg.info("查看单日数据统计成功!!!");
            TTUtil.formatReturn(resultJSON, 0, "查看单一数据成功!!!");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public void singledata(Integer userId, Integer tos) {
        Integer duratTotal = 100;
        Integer toStaffCount = tos;//
        Integer callCount = 1;
        Integer status = 1;
        Integer customers = 5;
        staticticsService.insertStatictics(duratTotal, toStaffCount, callCount, status, customers, userId);
    }

}
