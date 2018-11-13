package com.zxxkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.service.IPlanAllService;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.ParameterProperties;
import com.zxxkj.util.TTUtil;
import com.zxxkj.util.TransportUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 *@Copyright 北京知行信科技有限公司
 *Author: FuJacKing
 *@Date 2018/11/6 15:37
 *Description /**查询所有当前计划
 */
@Controller
@RequestMapping("/plans")
public class PlanAllController {
    private static final Logger LOGGER = Logger.getLogger(PlanAllController.class);
    @Resource
    private IPlanAllService planAllService;
    @RequestMapping(value = "/findNowPlansList", method = RequestMethod.POST)
    @ResponseBody
    public void findNowPlanList(HttpServletRequest request, HttpServletResponse response, int planStatus,
                                int curPage, String searchText,  String startTimeStr, String endTimeStr,int adminId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        JSONArray JSONArr = new JSONArray();
        int total = findHistoryPlan(curPage,  startTimeStr, endTimeStr, searchText, planStatus, JSONArr,adminId);
        result.put("total", total);
        result.put("list", JSONArr);
        TTUtil.sendDataByIOStream(response, result);
    }

    //查找历史计划
    private int findHistoryPlan(int curPage,  String startTimeStr, String endTimeStr,
                                String searchText, int planStatus, JSONArray JSONArr,int adminId) {
        //添加时间筛选参数
        Date startTimes = null;
        Date endTimes = null;
        try {
            if (!StringUtils.isBlank(startTimeStr)) {
                startTimes = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(startTimeStr);
            }
            if (!StringUtils.isBlank(endTimeStr)) {
                endTimes = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(endTimeStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int pageCount = Integer.parseInt(ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "normalCount"));
        int start = (curPage - 1) * pageCount;

        int total = planAllService.findPlansNowTotalList(planStatus, searchText, startTimes, endTimes,adminId);

        List<Map<String, Object>> nowPlanMapList = planAllService.findNowPlansList(planStatus, searchText,
                start, pageCount, startTimes, endTimes,adminId);

        for (Map<String, Object> map : nowPlanMapList) {
            int customerCount = (map.get("customerCount") == null ? 0 : (int) map.get("customerCount"));
            int thoughCount = (map.get("thoughCount") == null ? 0
                    : Integer.valueOf(map.get("thoughCount").toString()));
            JSONArray timeArr = getTimeRang((map.get("timeStr") == null ? "[]" : map.get("timeStr").toString()));
            map.put("planId", (map.get("planId") == null ? "" : map.get("planId")));
            map.put("userId", (map.get("userId") == null ? "" : map.get("userId")));
            map.put("excuteTime", map.get("excuteTime").toString().subSequence(0, 19));
            map.put("updateTime", (map.get("updateTime") == null ? map.get("excuteTime").toString().subSequence(0, 19) : map.get("updateTime").toString().subSequence(0, 19)));
            map.put("endTime",
                    (map.get("endTime") == null ? "" : map.get("endTime").toString().subSequence(0, 19)));
            map.put("addTime",
                    (map.get("addTime") == null ? "" : map.get("addTime").toString().subSequence(0, 19)));
            map.put("sourceTimeStr", map.get("timeStr") == null ? "[]" : map.get("timeStr").toString());
            map.put("timeStr", timeArr);
            map.put("projectId", (map.get("projectId") == null ? "" : map.get("projectId")));
            map.put("projectName", (map.get("projectName") == null ? "" : map.get("projectName")));
            map.put("customerCount", customerCount);
            map.put("noThoughCount", customerCount - thoughCount);
            map.put("isInterrupt", (map.get("isInterrupt") == null ? 0 : map.get("isInterrupt")));
            map.put("isTransfer", (map.get("isTransfer") == null ? 0 : map.get("isTransfer")));
            map.put("isSendSMS", (map.get("isSendSMS") == null ? 2 : map.get("isSendSMS")));
            map.put("transferGrade", (map.get("transferGrade") == null ? "" : map.get("transferGrade")));
            map.put("excuteCount", (map.get("transferGrade") == null ? 0 : map.get("excuteCount")));
            map.put("account", (map.get("account") == null ? 0 : map.get("account")));
            JSONArr.add(JSON.toJSON(map));
        }

        return total;
    }

    private static JSONArray getTimeRang(String timeStr) {
        List<String> timeArr = new ArrayList<String>();
        JSONArray jsonArr = new JSONArray();
        List<Integer> timeList = TransportUtil.stringTransportListForId(timeStr);
        int size = timeList.size();
        if (size == 0) {
            String workStartDateTimeStr = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "startTime");
            String workEndDateTimeStr = ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "endTime");
            int start = Integer.valueOf(workStartDateTimeStr.substring(0, 2));
            int end = Integer.valueOf(workEndDateTimeStr.substring(0, 2));
            int tmp = start;
            while (tmp >= start && tmp < end) {
                timeList.add(tmp);
                tmp = tmp + 1;
            }
        }

        size = timeList.size();
        if (size > 0) {
            while (size != 0) {
                int mul = 0;
                List<Integer> list = new ArrayList<Integer>();

                for (int i = 0; i < timeList.size(); i++) {
                    int aa = timeList.get(i) - i;
                    if (i == 0) {
                        int tmp = aa;
                        mul = tmp;
                    }

                    if (aa == mul && i < timeList.size()) {
                        list.add(timeList.get(i));

                    } else {
                        timeArr.add(list.get(0) + ":00-" + (list.get(list.size() - 1) + 1) + ":00");
                        timeList.removeAll(list);
                        break;
                    }
                }
                if (timeList.equals(list) && timeList.size() > 0) {
                    timeArr.add(list.get(0) + ":00-" + (list.get(list.size() - 1) + 1) + ":00");
                    timeList.clear();
                }
                size = timeList.size();
            }
            for (String str : timeArr) {
                jsonArr.add(str);
            }
        }
        return jsonArr;
    }


}
