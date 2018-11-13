package com.zxxkj.dao;

import java.util.List;
import java.util.Map;

public interface FinanceMapper {
    /***
     * @Param: [map]
     * @return: int
     * @Author: FuJacKing
     * @Description:查询充值记录条数
     */
    int selectUserRechargeRecordCount(Map<String, Object> map);

    /***
     * @Param: [map] 当前用户ID
     * @return: java.util.List<java.util.Map                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               <                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               java.lang.String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               java.lang.Object>>
     * @Author: FuJacKing
     * @Description: 查询当前用户下线路信息
     */
    List<Map<String, Object>> selectUserAllSipDataList(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:根据sipid和userid充值当前线路
     */
    Integer updateUserSipDataByRecordId(Map<String, Object> map);


    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:更新充值成功记录表
     */
    Integer insertUserRechargeRecordByAdminData(Map<String, Object> map);

    /***
     * @Param: [map] 查询条件
     * @return:
     * @Author: FuJacKing
     * @Description: 计算代理商为用户充值总金额
     */

    Map<String, Object> seleceUserTotalMoney(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description: 插入一条运营商为企业充值的记录
     */
    Integer insertUserBalanceMoneyByAdminData(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:判断是否是第一次充值
     */
    Integer isRechargeFirst(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:消费明细查询
     */
    Integer insertUserConsumRecord(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description: 查询消费总额
     */
    Double selectUserCostTotalData(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: double
     * @Author: FuJacKing
     * @Description: 代理商为用户充值记录数据
     */

    List<Map<String, Object>> selectUserRechargeRecordList(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return: int
     * @Author: FuJacKing
     * @Description:查询消费明细条数
     */

    int selectUserCostRecordListCount(Map<String, Object> map);

    /***
     * @Param: [map]
     * @return:
     * @Author: FuJacKing
     * @Description: 分页查询消费明细列表
     */

    List<Map<String, Object>> selectUserCostRecordList(Map<String, Object> map);

    /*** 
    * @Param: [map]
    * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 
    * @Author: FuJacKing
    * @Description:   按条件查询下拉菜单数据(满足删除的线路账单数据，显示在查询条件里)
    */ 
    List<Map<String,Object>> selcectSipLine(Map<String,Object> map);
}
