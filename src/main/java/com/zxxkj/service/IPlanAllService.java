package com.zxxkj.service;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 *@Copyright 北京知行信科技有限公司
 *Author: FuJacKing
 *@Date 2018/11/6 15:48
 *Description 查询当前所有计划
 */
public interface IPlanAllService {
    /***
    * @Param: [planStatus, searchText, startTime, endTime]
    * @return: int
    * @Author: FuJacKing
    * @Description: 查询总数，用于分页
    */
    int findPlansNowTotalList(int planStatus, String searchText , Date startTime, Date endTime, int adminId);
    /*** 
    * @Param: [planStatus, searchText, start, pageCount, startTime, endTime]
    * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 
    * @Author: FuJacKing
    * @Description:  查询当前所有计划
    */ 
    List<Map<String,Object>> findNowPlansList(int planStatus, String searchText, int start, int pageCount,Date startTime,Date endTime,int adminId);

}
