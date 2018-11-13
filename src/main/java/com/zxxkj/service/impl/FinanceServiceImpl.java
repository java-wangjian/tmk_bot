package com.zxxkj.service.impl;

import com.zxxkj.controller.FinanceController;
import com.zxxkj.dao.FinanceMapper;
import com.zxxkj.service.FinanceService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("financeService")
public class FinanceServiceImpl implements FinanceService {
    @Resource
    private FinanceMapper financeMapper;
    private static final Logger loger = Logger.getLogger(FinanceServiceImpl.class);
    private Object obj = new Object();

    /***
     * @Param: [map] 当前用户ID
     * @return: java.lang.Object>
     * @Author: FuJacKing
     * @Description: 查询当前用户下线路信息
     */
    @Override
    public List<Map<String, Object>> selectUserAllSipDataList(Map<String, Object> map) {
        return financeMapper.selectUserAllSipDataList(map);
    }


    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:根据sipid和userid充值用户当前线路
     */
    @Override
    public Integer updateUserSipDataByRecordId(Map<String, Object> map) {
        return financeMapper.updateUserSipDataByRecordId(map);
    }


    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:Sip 代理商为用户线路充值记录
     */
    @Override
    public Integer insertUserRechargeRecordByAdminData(Map<String, Object> map) {
        return financeMapper.insertUserRechargeRecordByAdminData(map);
    }

    /***
     * @Param: [map] 查询条件
     * @return: int
     * @Author: FuJacKing
     * @Description: 用户的充值记录，分页查看，线路查看，时间查看
     */
    @Override
    public int selectUserRechargeRecordCount(Map<String, Object> map) {
        return financeMapper.selectUserRechargeRecordCount(map);
    }

    /***
     * @Param: [map] 查询条件
     * @return:
     * @Author: FuJacKing
     * @Description: 计算代理商为用户充值总金额
     */
    @Override
    public Map<String, Object> seleceUserTotalMoney(Map<String, Object> map) {
        return financeMapper.seleceUserTotalMoney(map);
    }

    /***
     * @Param: [map] 查询条件
     * @return: java.lang.Object>
     * @Author: FuJacKing
     * @Description: 插入一条代理商为用户第一次充值的记录
     */
    @Override
    public Integer insertUserBalanceMoneyByAdminData(Map<String, Object> map) {
        return financeMapper.insertUserBalanceMoneyByAdminData(map);
    }

    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:判断是否是第一次充值
     */
    @Override
    public Integer isRechargeFirst(Map<String, Object> map) {
        return financeMapper.isRechargeFirst(map);
    }


    /***
     * @Param: [map]
     * @return: int
     * @Author: FuJacKing
     * @Description: 计算消费明细列表条数
     */
    @Override
    public int selectUserCostRecordListCount(Map<String, Object> map) {
        return financeMapper.selectUserCostRecordListCount(map);
    }

    /***
     * @Param: [map]
     * @return:
     * @Author: FuJacKing
     * @Description: 消费明细列表查询
     */

    @Override
    public List<Map<String, Object>> selectUserCostRecordList(Map<String, Object> map) {
        return financeMapper.selectUserCostRecordList(map);
    }


    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:分页查询企业用户充值记录
     */


    @Override
    public List<Map<String, Object>> selectUserRechargeRecordList(Map<String, Object> map) {
        return financeMapper.selectUserRechargeRecordList(map);
    }

    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description: 消费明细表数据插入
     */
    @Override
    public Integer insertUserConsumRecord(Map<String, Object> map) {
        //获取当前你线路的通话时长，
        String strlt = map.get("longTime").toString();
        BigDecimal dlt = new BigDecimal(strlt);
        BigDecimal seconds = new BigDecimal(60);
        BigDecimal longTime = dlt.divide(seconds, 0, BigDecimal.ROUND_UP);
        //查询当前SIP线路信息，包括余额
        List<Map<String, Object>> balanceListMap = balanceListMap = financeMapper.selectUserAllSipDataList(map);
        //数据库已有记录
        if (balanceListMap.size() == 1) {
            Map<String, Object> tempMap = balanceListMap.get(0);
            //获取当前余额
            String struserBalance = (tempMap.get("balanceMoney") == null ? "0" : tempMap.get("balanceMoney")).toString();
            BigDecimal userBalance = new BigDecimal(struserBalance);
            //获取当前线路时长
            String strleftover = (tempMap.get("leftover") == null ? 0 : tempMap.get("leftover")).toString();
            BigDecimal leftover = new BigDecimal(strleftover);
            //获取当前线路单价
            String strunitPrice = (tempMap.get("unitPrice") == null ? "0" : tempMap.get("unitPrice")).toString();
            BigDecimal unitPrice = new BigDecimal(strunitPrice);
//                    Map<String, Object> map1 = new HashMap<>();
//                    map1.put("userId", map.get("userId"));
            //计算本次消费金额
            BigDecimal amountMoney = unitPrice.multiply(longTime);
            //扣除后所剩金额
            BigDecimal new_sip_balance = userBalance.subtract(amountMoney);
            //消费金额与现有余额相减并更新
//                    map1.put("new_sip_balance", new_sip_balance);
//                    map1.put("unitPrice", unitPrice);
            //扣除后所剩时长-将时长转换成秒
            BigDecimal bLongTime = longTime.multiply(seconds);
            BigDecimal bleftover = leftover.subtract(bLongTime);
            //拨打时长与现有时长相减并更新
//                    map1.put("leftover", bleftover);
//                    map1.put("sipId", map.get("sipId"));
            //修改线路余额
//            Integer flag = -1;
//            flag = financeMapper.updateUserSipDataByRecordId(map1);
            //补充--单价
            map.put("unitPrice", unitPrice);
            //补充消费消费金额
            map.put("amountMany", amountMoney);
            map.put("new_sip_balance", new_sip_balance);
            return financeMapper.insertUserConsumRecord(map);
        } else {
            loger.info("未找到相关的充值记录信息");
        }


        return 0;
    }

    /***
     * @Param: [map]
     * @return: java.lang.Double
     * @Author: FuJacKing
     * @Description: 消费记录总额
     */

    @Override
    public Double selectUserCostTotalData(Map<String, Object> map) {
        return financeMapper.selectUserCostTotalData(map);
    }

    /***
     * @Param: [map]
     * @Author: FuJacKing
     * @Description: 按条件查询下拉菜单数据(满足删除的线路账单数据 ， 显示在查询条件里)
     */
    @Override
    public List<Map<String, Object>> selcectSipLine(Map<String, Object> map) {
        return financeMapper.selcectSipLine(map);
    }
}
