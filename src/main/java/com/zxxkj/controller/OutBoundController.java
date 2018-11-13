package com.zxxkj.controller;

import com.zxxkj.model.Plan;
import com.zxxkj.service.ICallRecordService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;


@Controller
public class OutBoundController {
    private static final Logger LOGGER = Logger.getLogger(OutBoundController.class);

    @Resource
    private ICallRecordService iCallRecordService;

    //外呼响应
    @RequestMapping(value = "/execute",method = RequestMethod.GET)
    @ResponseBody
    public void executeOutBound()  {
        Plan plan=new Plan();
        plan.setId(1);
        plan.setUserId(1);
        plan.setProjectId(1);
//        iCallRecordService.outbound(plan);
    }
}
