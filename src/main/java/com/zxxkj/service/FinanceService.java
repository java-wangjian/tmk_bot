package com.zxxkj.service;

import java.util.List;
import java.util.Map;

public interface FinanceService {
    /***
     * @Param: [map]
     * @return:
     * @Author: FuJacKing
     * @Description:根据sipid和userid查询当前线路
     */
    List<Map<String, Object>> selectUserAllSipDataList(Map<String, Object> map);

    /***
     * @Param: [map]查询信息
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:根据sipid和userid充值当前线路
     *
     */
    Integer updateUserSipDataByRecordId(Map<String, Object> map);


    /***
     * @Param: [map] 充值数据
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:代理商为用户充值成功记录表
     *
     */
    Integer insertUserRechargeRecordByAdminData(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: int
     * @Author: FuJacKing
     * @Description: 计算企业用户充值记录总条数，方便分页
     */
    int selectUserRechargeRecordCount(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.util.Map<java.lang.String                               ,                               java.lang.Object>
     * @Author: FuJacKing
     * @Description:代理商为用户充值总额
     */
    Map<String, Object> seleceUserTotalMoney(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.util.Map<java.lang.String                               ,                               java.lang.Object>
     * @Author: FuJacKing
     * @Description:代理商是否第一次为用户充值
     */
    Integer isRechargeFirst(Map<String, Object> map);



    /**
     * 分页查询企业用户的充值记录，返回ListMap
     */
    List<Map<String, Object>> selectUserRechargeRecordList(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:消费明细表数据插入
     */
    Integer insertUserConsumRecord(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.util.Map<java.lang.String       ,       java.lang.Object>
     * @Author: FuJacKing
     * @Description:查询消费总额
     */
    Double selectUserCostTotalData(Map<String, Object> map);

    /***
     * @Param: [map] 查询条件
     * @return: java.util.Map<java.lang.String       ,           java.lang.Object>
     * @Author: FuJacKing
     * @Description: 插入一条代理商为用户第一次充值的记录
     */
    Integer insertUserBalanceMoneyByAdminData(Map<String, Object> map);


    /***
     * @Param: [map]
     * @return: int
     * @Author: FuJacKing
     * @Description: 查询消费明细列表条数
     */
    int selectUserCostRecordListCount(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return:
     * @Author: FuJacKing
     * @Description: 分页查询消费明细列表
     */
    List<Map<String, Object>> selectUserCostRecordList(Map<String, Object> map);


    List<Map<String,Object>> selcectSipLine(Map<String,Object> map);



}
