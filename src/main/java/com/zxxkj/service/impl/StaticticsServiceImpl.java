package com.zxxkj.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.zxxkj.dao.StaticticsMapper;
import com.zxxkj.model.Statictics;
import com.zxxkj.service.StaticticsService;

@Service
public class StaticticsServiceImpl implements StaticticsService {

    @Resource
    private StaticticsMapper staticticsDao;
    private static final Logger lg = Logger.getLogger(StaticticsServiceImpl.class);

    /**
     * 根据用户ID和日期,返回当日统计的详细信息
     */
    @Override
    public Statictics selectDataByUserIDAndDate(Map<String, Object> map) {
        return staticticsDao.selectDataByUserIDAndDate(map);
    }

    /***
     * @Param: [duratTotal,通话时长 toStaffCount, 转接数 callCount,总呼出量 status 状态（1 为已接听，2, 未接听）  customer AI意向级默认F, userId，用户ID]
     * @return: java.lang.Integer
     * @Author: FuJacKing
     * @Description:
     */
    @Override
    public Integer insertStatictics(Integer duratTotal, Integer toStaffCount, Integer callCount, Integer status,
                                    Integer customer, Integer userId) {
        Map<String, Object> map = new HashMap<>();
        Statictics statictics = new Statictics();
        statictics.setDuratTotal(duratTotal);
        if (null != toStaffCount && toStaffCount == 0) {
            toStaffCount = null;
        }
        statictics.setToStaffCount(toStaffCount);
        statictics.setCallCount(callCount);
        statictics.setUserId(userId);
        if (null != duratTotal) {
            if (duratTotal <= 10&&duratTotal>=1) {
                statictics.setLt10gt5(1);
            } else if (duratTotal >= 30) {
                statictics.setGt30(1);
            }
        }

        //底下为原始代码
        String dateStr = (String) map.get("date");
        if (null == userId) {
            lg.info("对数据统计修改时,userID和dateStr为空!!!");
            return 0;
        }
        map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        map.put("statictics", statictics);
        map.put("customer", customer);
        map.put("status", status);
        map.put("userId", userId);
        Integer count2 = staticticsDao.selectInitDateDate(map);
        if (null == count2) {
            lg.info("根据userID和dateStr查找数据库异常!!!");
            return 0;
        } else {
            if (0 == count2) {
                lg.info("数据库里没有今日的记录!!!");
                Integer count1 = staticticsDao.initDateData(map);
                if (null != count1 && count1 > 0) {
                    lg.info("初始化一条数据 成功!!!");
                } else {
                    lg.info("初始化一条数据 失败!!!");
                }
            }
        }
        Integer count3 = staticticsDao.updateStaticticsInfo(map);
        if (null != count3) {
            if (0 == count3) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

}
