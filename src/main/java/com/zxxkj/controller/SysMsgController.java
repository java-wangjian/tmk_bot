package com.zxxkj.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zxxkj.model.Mail;
import com.zxxkj.service.MailService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.SysMsg;
import com.zxxkj.model.User;
import com.zxxkj.service.IUserService;
import com.zxxkj.service.SysMsgService;
import com.zxxkj.util.TTUtil;

@Controller
@RequestMapping("/msg")
public class SysMsgController {

	@Resource
	private SysMsgService sysMsgService;
	@Resource
	private IUserService userService;
	@Resource
	private MailService mailService;
	private static final Logger lg = Logger.getLogger(SysMsgController.class);

	@RequestMapping(value = "/feedbacklist", method = RequestMethod.POST)
	@ResponseBody
	public void feedbacklist(HttpServletRequest request, HttpServletResponse response, User user,Integer page,Integer per) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || null == page || null == per) {
			lg.info("查看意见反馈接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "查看意见反馈接口传递参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		page = (page - 1) * per;
		int userID = user.getId();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userID", userID);
		// 查询用户一共提交了多少条用户反馈
		Integer count = sysMsgService.selectFeedbackCountByUserID(map);
		if (null != count) {
			if (count == 0) {
				lg.info("此接口没有数据!!!");
				TTUtil.formatReturn(resultJSON, 405, "没有数据!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			if (count <= page) {
				lg.info("此接口传递页码有问题!!!");
				TTUtil.formatReturn(resultJSON, 406, "此接口传递页码有问题!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			map.put("page", page);
			map.put("per", per);
			// 根据用户ID,分页查询用户提交的意见反馈列表
			List<SysMsg> msgList = sysMsgService.selectFeedbackMsgList(map);
			JSONObject temp = new JSONObject();
			temp.put("count", count);
			temp.put("list", msgList);
			resultJSON.put("data", temp);
			TTUtil.formatReturn(resultJSON, 0, "意见反馈接口 查看成功!!!");
		}else {
			lg.info("意见反馈接口 查看失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "意见反馈接口 查看失败");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/feedback", method = RequestMethod.POST)
	@ResponseBody
	public void feedback(HttpServletRequest request, HttpServletResponse response, User user, SysMsg msg) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || null == msg.getContent() || null == msg.getCreateTime()
				|| 0 == msg.getMsgType()) {
			lg.info("意见反馈接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "意见反馈接口传递参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		int userID = user.getId();
        User feedBackUser = userService.findUserInfoByUserId(userID);
        String account = feedBackUser.getAccount();
        msg.setUserID(userID);
		// 向数据库插入用户提交的意见反馈
		Integer count = sysMsgService.insertFeedbackMsg(msg);
		lg.info(count);
		if (null != count && count > 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Role", 0);
            Mail mailSetting = mailService.selectRole0Info(map);
            map.put("Role", 1);
            List<Mail> mailList = mailService.selectSalesInfoList(map);
            if (mailList.size() > 0) {
                for (Mail mail : mailList) {
                    String content = String.format("反馈人：%s    \n反馈内容：%s",account,msg.getContent());
                    String title = "您收到一个 意见反馈!!!";
                    TTUtil.sendMail(mailSetting.getHost(), mail.getMail(), mailSetting.getMail(), title,
                            content, mailSetting.getUsername(), mailSetting.getPassword());
                    lg.info("给市场部人员发送邮件成功!!!");
                }
            } else {
                lg.info("没有设置市场部人员邮箱,不做发送操作!!!");
            }
			lg.info("反馈成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "反馈成功!!!");
		} else {
			TTUtil.formatReturn(resultJSON, 1, "反馈失败!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/batchRead", method = RequestMethod.POST)
	@ResponseBody
	public void batchRead(HttpServletRequest request, HttpServletResponse response, String msgIDs, User user) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || null == msgIDs) {
			lg.info("系统消息批量已读接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		int userID = user.getId();
		List<String> tempMsgIDList = TTUtil.string2list(msgIDs);
		if (null == tempMsgIDList || tempMsgIDList.size() < 1) {
			lg.info("系统消息批量已读接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		List<Integer> msgIdList = new ArrayList<Integer>();
		for (String msgid : tempMsgIDList) {
			msgid = msgid.replace("\"", "");
			Integer intMsgID = Integer.valueOf(msgid);
			msgIdList.add(intMsgID);
		}
		Integer temp = tempMsgIDList.size();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msgIDs", msgIdList);
		map.put("userID", userID);
		// 通过系统消息ID的集合,批量设置消息状态 为已读
		Integer count = sysMsgService.batchReadSysMsgByMsgIDList(map);
		if (null != count && count > 0) {
			TTUtil.formatReturn(resultJSON, 0, TTUtil.appendString(temp, "条数据批量已读成功!!!"));
		} else {
			TTUtil.formatReturn(resultJSON, 1, TTUtil.appendString(temp, "条数据批量已读失败!!!"));
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
	@ResponseBody
	public void batchDelete(HttpServletRequest request, HttpServletResponse response, String msgIDs, User user) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || null == msgIDs) {
			lg.info("查看未读的系统接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		int userID = user.getId();
		List<String> tempMsgIDList = TTUtil.string2list(msgIDs);
		if (null == tempMsgIDList || tempMsgIDList.size() < 1) {
			lg.info("查看未读的系统接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		List<Integer> msgIdList = new ArrayList<Integer>();
		for (String msgid : tempMsgIDList) {
			msgid = msgid.replace("\"", "");
			Integer intMsgID = Integer.valueOf(msgid);
			msgIdList.add(intMsgID);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msgIDs", msgIdList);
		map.put("userID", userID);
		// 通过系统消息ID的集合,批量删除系统消息
		Integer count =  sysMsgService.batchDeleteSysMsgByMsgIDList(map);
		if (null != count && count > 0) {
			lg.info("批量删除成功!!!");
			TTUtil.formatReturn(resultJSON, 0, TTUtil.appendString(tempMsgIDList.size(), "条数据批量删除成功!!!"));
		} else {
			lg.info("批量删除失败!!!");
			TTUtil.formatReturn(resultJSON, 1, TTUtil.appendString(tempMsgIDList.size(), "条数据批量删除失败!!!"));
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return ;
	}

    @RequestMapping(value = "/unreadcount", method = RequestMethod.POST)
    @ResponseBody
    public void unread(HttpServletRequest request, HttpServletResponse response, User user) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (user.getId() < 1 || null == user.getCreateTime()) {
            lg.info("查看未读的系统接口传递参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "参数错误");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Integer userID = user.getId();
        String createTime = user.getCreateTime();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userID", userID);
        map.put("createTime", createTime);
        // 从消息表中初始所有系统消息,筛选符合条件的系统消息,返回消息ID的集合
        List<Integer> msgIdList = sysMsgService.findAllSysMsgIdListByContidation(map);
        for (Integer tmpMsgId : msgIdList) {
            map.put("msgID", tmpMsgId);
            // 根据消息ID判断消息在中间表中是否存在,初始化中间表
            Integer status = sysMsgService.findMsgStatusByMsgIDAndUserID(map);
            if (null == status) {
                map.put("msgID", tmpMsgId);
                map.put("userID", userID);
                sysMsgService.insertMsgIDConnUserID(map);
            }
        }
        // 从中间表中,计算这个UserID名下未读消息的数量
        Integer count = sysMsgService.selectUnreadMsgCountByUserID(userID);
        if (null != count) {
            lg.info("计算未读消息数量成功!!!");
            resultJSON.put("count", count);
            TTUtil.formatReturn(resultJSON, 0, "查询成功");
        } else {
            lg.info("计算未读消息数量失败!!!");
            resultJSON.put("count", 0);
            TTUtil.formatReturn(resultJSON, 1, "查询失败");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public void list(HttpServletRequest request, HttpServletResponse response, User user, Integer page, Integer per) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (user.getId() < 1 || null == page || null == per || null == user.getCreateTime()) {
            lg.info("查看系统消息接口传递参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "参数错误");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        page = (page - 1) * per;
        int userID = user.getId();
        JSONObject temp = new JSONObject();
        String createTime = user.getCreateTime();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("createTime", createTime);
        map.put("userID", userID);
        // 返回符合条件的所有系统消息ID集合
        List<Integer> msgIdList = sysMsgService.findAllSysMsgIdListByContidation(map);
        if (null == msgIdList || msgIdList.size() == 0) {
            lg.info("此接口没有数据!!!");
            temp.put("count", 0);
            temp.put("list", new ArrayList<SysMsg>());
            resultJSON.put("data", temp);
            TTUtil.formatReturn(resultJSON, 405, "没有数据!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        map.put("page", page);
        map.put("per", per);
        Integer count = sysMsgService.selectMsgMapListSizeByCondetion(map);
        List<Map<String, Object>> msgMapList = sysMsgService.selectMsgMapListByCondetion(map);
        if (count == 0) {
            temp.put("list",new  ArrayList<>());
            temp.put("count",0);
            resultJSON.put("data", temp);
            TTUtil.formatReturn(resultJSON, 0, "查询成功!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Collections.sort(msgMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                if ((Integer)o1.get("status") > (Integer) o2.get("status")) {
                    return  1;
                }
                return -1;
            }
        });
        temp.put("list", msgMapList);
        temp.put("count", count);
        resultJSON.put("data", temp);
        TTUtil.formatReturn(resultJSON, 0, "查询成功!");
        TTUtil.sendDataByIOStream(response, resultJSON);
    }

    @RequestMapping(value = "/insertMsg", method = RequestMethod.POST)
    @ResponseBody
    public void insertMsg(HttpServletRequest request, HttpServletResponse response, Integer userId,String content,String title) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(userId,content,title)) {
            lg.info("参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "参数错误");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> map = TTUtil.getParamMap();
        map.put("user_id", userId);
        map.put("content", content);
        map.put("title", title);
        Integer effect = sysMsgService.insertPointMsgByUserId(map);
    }

}
