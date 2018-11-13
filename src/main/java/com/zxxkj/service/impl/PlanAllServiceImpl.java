package com.zxxkj.service.impl;

import com.zxxkj.dao.PlanAllMapper;
import com.zxxkj.service.IPlanAllService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 *@Copyright 北京知行信科技有限公司
 *Author: FuJacKing
 *@Date 2018/11/6 16:02
 *Description 查询所有计划实现类
 */
@Service("planAllService")
public class PlanAllServiceImpl implements IPlanAllService {
    private static final Logger LOGGER = Logger.getLogger(PlanAllServiceImpl.class);
    @Resource
    private PlanAllMapper planAllDao;
    @Override
    public int findPlansNowTotalList(int planStatus, String searchText, Date startTime, Date endTime,int adminId) {
        return planAllDao.findPlansTotalList(planStatus, searchText ,startTime, endTime,adminId);
    }

    @Override
    public List<Map<String, Object>> findNowPlansList(int planStatus, String searchText, int start, int pageCount, Date startTime, Date endTime,int adminId) {
        return planAllDao.findNowPlansList(planStatus, searchText, start, pageCount, startTime,endTime,adminId);
    }
}
