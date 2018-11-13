package com.zxxkj.controller;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.service.FinanceService;
import com.zxxkj.service.SysMsgService;
import com.zxxkj.util.TTUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/finance")
public class FinanceController {
    @Resource
    private FinanceService financeService;
    private static final Logger lg = Logger.getLogger(FinanceController.class);

    /***
     * @Param: [request, response, userId 用户Id, sipId sip线路ID, unitPrice 单价金额  RechargePrice充值金额,保留三位小数]
     * @return: void
     * @Author: FuJacKing
     * @Description: 代理商为用户--充值
     */
    @RequestMapping(value = "/rechargeSipBalanceToUser", method = RequestMethod.POST)
    @ResponseBody
    public void rechargeSipBalanceToUser(HttpServletRequest request, HttpServletResponse response, Integer userId, Integer adminId, Integer sipId, String unitPrice, String rechargePrice, String sipName) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(userId, adminId, sipId, unitPrice, rechargePrice, sipName)) {
            lg.info("参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        //收集前端传递的参数
        Map<String, Object> map = TTUtil.getParamMap();
        map.put("userId", userId);
        map.put("sipId", sipId);
        map.put("gatewayType", 2);
        //单价-小数保留3位-5为舍弃
        BigDecimal vunitPrice = new BigDecimal(unitPrice).setScale(3, BigDecimal.ROUND_HALF_DOWN);
        //预计充值金额
        BigDecimal temprechargePrice = new BigDecimal(rechargePrice).setScale(3, BigDecimal.ROUND_HALF_DOWN);
        //收集第一次充值所用的参数
        //计算充值时常-分钟
        BigDecimal rechargeTime = temprechargePrice.divide(vunitPrice, 0, BigDecimal.ROUND_DOWN);
        BigDecimal seconds = new BigDecimal(60);
        //计算实际充值金额
        BigDecimal vrechargePrice = rechargeTime.multiply(vunitPrice);
        map.put("unitPrice", vunitPrice);
        map.put("rechargePrice", vrechargePrice);
        //换算为秒
        map.put("lengthtime", rechargeTime.multiply(seconds));
        //第一次充值，将插入一条记录
        Integer effect = 0;
        Integer isFirst = financeService.isRechargeFirst(map);
        //查询用户下,当前线路余额，判断是否是第一次充值
        if (isFirst == 0) {
            effect = financeService.insertUserBalanceMoneyByAdminData(map);
        } else if (isFirst == 1) {
            //查询余额信息
            List<Map<String, Object>> balanceListMap = financeService.selectUserAllSipDataList(map);

            if (balanceListMap.size() == 1) {
                //数据库已有记录
                Map<String, Object> tempMap = balanceListMap.get(0);
                //获取当前余额
                String struserBalance = (tempMap.get("balanceMoney") == null ? 0 : tempMap.get("balanceMoney")).toString();
                BigDecimal userBalance = new BigDecimal(struserBalance);
                //获取当前线路时长
                String strleftover = (tempMap.get("leftover") == null ? 0 : tempMap.get("leftover")).toString();
                BigDecimal leftover = new BigDecimal(strleftover);
                //获取当前线路名称
                sipName = (tempMap.get("sip_name") == null ? 0 : tempMap.get("sip_name")).toString();
                if (null == userBalance) {
                    lg.info("当前用户线路余额查询失败");
                    TTUtil.formatReturn(resultJSON, 403, "当前用户线路余额查询失败");
                    TTUtil.sendDataByIOStream(response, resultJSON);
                    return;
                }
                //将充值额度与现有余额相加并更新.
                BigDecimal new_sip_blance = userBalance.add(vrechargePrice);
                BigDecimal rechargeAfterTime = leftover.add(rechargeTime);
                map.put("new_sip_balance", new_sip_blance);
                map.put("leftover", rechargeAfterTime);
                //修改线路余额
                effect = financeService.updateUserSipDataByRecordId(map);
            }
        } else {
            lg.info("SIP线路充值信息异常");
            TTUtil.formatReturn(resultJSON, 403, "SIP线路充值信息异常");
            return;
        }
        //充值成功，更新充值轨迹表
        if (1 == effect) {
            lg.info("充值成功");
            map.put("adminId", adminId);
            map.put("sipName", sipName);
            financeService.insertUserRechargeRecordByAdminData(map);
            //更新充值轨迹表
            TTUtil.formatReturn(resultJSON, 0, "充值成功");
        } else {
            lg.info("充值失败");
            TTUtil.formatReturn(resultJSON, 1, "充值失败");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
    }


    /***
     * @Param: [request, response, userId, page, per, sipId, startDate, endDate]
     * @return: void
     * @Author: FuJacKing
     * @Description: 用户的充值记录，分页查看，线路查看，时间查看
     */
    @RequestMapping(value = "/userRechargeRecord", method = RequestMethod.POST)
    @ResponseBody
    public void userRechargeRecord(HttpServletRequest request, HttpServletResponse response, Integer userId, Integer adminId, Integer page, Integer per, Integer sipId,
                                   String startDate, String endDate) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(userId, page, per)) {
            lg.info("参数错误");
            TTUtil.formatReturn(resultJSON, 404, "参数错误");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> map = TTUtil.getParamMap();
        map.put("user_id", userId);
        page = (page - 1) * per;
        map.put("page", page);
        map.put("per", per);
        if (null != sipId) {
            map.put("sip_id", sipId);
        }
        if (StringUtils.isNoneBlank(startDate, endDate)) {
            map.put("start_date", startDate);
            map.put("end_date", endDate);
        }
        //计算条数
        int rechargeRecordCount = financeService.selectUserRechargeRecordCount(map);
        //计算代理商为用户充值总金额
        Map<String, Object> balanceMap = financeService.seleceUserTotalMoney(map);
        if (null == balanceMap) {
            balanceMap = new HashMap<>();
            balanceMap.put("totalBalance", 0);
            balanceMap.put("totalMoney", 0);
        }
        //查询企业用户充值记录
        List<Map<String, Object>> listMap = financeService.selectUserRechargeRecordList(map);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", balanceMap);
        if (listMap.size() < 1) {
            jsonObject.put("list", new ArrayList<Map<String, Object>>());
        } else {
            jsonObject.put("list", listMap);
        }
        lg.info("查看企业用户充值记录成功");
        jsonObject.put("count", rechargeRecordCount);
        resultJSON.put("data", jsonObject);
        TTUtil.formatReturn(resultJSON, 0, "查看企业用户充值记录成功");
        TTUtil.sendDataByIOStream(response, resultJSON);
    }

    /***
     * @Param: [request, response, userId, page, per, sipId, startDate, endDate, prev]
     * @return: void
     * @Author: FuJacKing
     * @Description: 查询用户消费记录
     */
    @RequestMapping(value = "/userCostRecord", method = RequestMethod.POST)
    @ResponseBody
    public void userCostRecord(HttpServletRequest request, HttpServletResponse response, Integer userId, Integer page, Integer per, Integer sipId,
                               String startDate, String endDate, Integer prev) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(userId, page, per, prev)) {
            lg.info("参数错误");
            TTUtil.formatReturn(resultJSON, 404, "参数错误");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }

        Map<String, Object> map = TTUtil.getParamMap();
        map.put("user_id", userId);
        page = (page - 1) * per;
        map.put("page", page);
        map.put("per", per);
        if (null != sipId) {
            map.put("sip_id", sipId);
        }
        if (StringUtils.isNoneBlank(startDate, endDate)) {
            map.put("start_date", startDate);
            map.put("end_date", endDate);
        }

        int count = financeService.selectUserCostRecordListCount(map);
        JSONObject temp = new JSONObject();


        List<Map<String, Object>> listMap = financeService.selectUserCostRecordList(map);
        Double totalMoney = financeService.selectUserCostTotalData(map);
        if (null == totalMoney) {
            temp.put("totalMoney", 0);
        }
        temp.put("list", listMap);
        temp.put("total", count);
        temp.put("totalMoney", totalMoney);
        resultJSON.put("data", temp);
        lg.info("查看用户消费历史成功");
        TTUtil.formatReturn(resultJSON, 0, "查看用户消费历史成功");
        TTUtil.sendDataByIOStream(response, resultJSON);
    }


    /***
     * @Param: [request, response, userId]
     * @return: void
     * @Author: FuJacKing
     * @Description: 按条件查询下拉菜单数据(满足删除的线路账单数据 ， 显示在查询条件里)
     */
    @RequestMapping(value = "/getSelcectSipLine", method = RequestMethod.POST)
    @ResponseBody
    public void getSelcectSipLine(HttpServletRequest request, HttpServletResponse response, Integer userId) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(userId)) {
            lg.info("userId不能为空");
            TTUtil.formatReturn(resultJSON, 404, "userId不能为空");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> map = TTUtil.getParamMap();
        map.put("user_id", userId);
        List<Map<String, Object>> listMap = financeService.selcectSipLine(map);
        if (null == listMap || listMap.size() == 0) {
            resultJSON.put("data", new ArrayList<>());
            TTUtil.formatReturn(resultJSON, 0, "成功");
        }
        resultJSON.put("data", listMap);
        TTUtil.sendDataByIOStream(response, resultJSON);
    }


    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public void test(HttpServletRequest request, HttpServletResponse response, Integer userId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("linkPhone", 15003428788L);
        param.put("sipId", 139);
        param.put("sipName", "vos3");
        param.put("callTime", new Date());
        param.put("longTime", 39);
        param.put("userId", userId);
        financeService.insertUserConsumRecord(param);
    }
}
