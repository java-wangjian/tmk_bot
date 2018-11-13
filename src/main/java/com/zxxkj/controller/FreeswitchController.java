package com.zxxkj.controller;

import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.model.Gateway;
import com.zxxkj.model.User;
import com.zxxkj.service.FreeSwitchService;
import com.zxxkj.service.IGatewayService;
import com.zxxkj.service.IUserService;

import exception.FSException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zxxkj.service.impl.FreeSwitchServiceImpl.getParamsAndWork;
import static com.zxxkj.util.ConstantUtil.FREESWITCH_ACTION;
import static com.zxxkj.util.HTTPUtil.sendGet;
import static com.zxxkj.util.Utils.isEmpty;


@Controller
public class FreeswitchController {
    private static final Logger LOGGER = Logger.getLogger(FreeswitchController.class);

    @Resource
    private FreeSwitchService freeSwitchService;

    @Resource
    private IUserService iUserService;

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Resource
    private IGatewayService gatewayService;

    // 上传文件存储目录
    public static final String UPLOAD_DIRECTORY = "upload";


    //外呼响应
    @RequestMapping(value = "/resp", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getResponseResp(HttpServletRequest request) {
        LOGGER.info(request.getParameter("customerPhone") + "resp-----------" +request.getQueryString());
        String result = null;
        try {
        	result = freeSwitchService.freeSwitchResp(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
        return result;
    }

    //全程录音
    @RequestMapping(value = "/record_call", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getResponseRecord_ack(HttpServletRequest request) {
    	LOGGER.info(request.getParameter("customerPhone") + "record_call-----------" +request.getQueryString());
    	String result = null;
    	try {
    		result = freeSwitchService.freeSwitchRecordCall(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
    	return result;
    }

    //分段录音
    @RequestMapping(value = "/recordChunk", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getResponseRecordTrunk(HttpServletRequest request) {
        String name = RandomStringUtils.randomNumeric(50) + ".mp3";
        String recordChunkName = name;
        String params = "<recordChunkName>" + recordChunkName + "</recordChunkName> \n"+
                "<RECORD_STEREO>false</RECORD_STEREO>"+
                "<RECORD_READ_ONLY>true</RECORD_READ_ONLY>";
        String work = "<recordChunk file=\"" + name + "\" name=\"recordChunk\" action=\"" + FREESWITCH_ACTION + "/recordChunkAck\"></recordChunk>";
        LOGGER.info(request.getParameter("customerPhone") + "recordChunk-----------" +request.getQueryString());
        return getParamsAndWork(params, work);
    }

    //分段录音
    @RequestMapping(value = "/recordChunkAck", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getResponseRecordAck(HttpServletRequest request) {
        String work = "<continue action=\"" + FREESWITCH_ACTION + "/silenceStream\"></continue>\n";
        LOGGER.info(request.getParameter("customerPhone") + "recordChunkAck-----------" +request.getQueryString());
        return getParamsAndWork(null, work);
    }

    @RequestMapping(value = "/recordChunkAck", method = RequestMethod.POST, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getRecordCallTrunkAct(HttpServletRequest request) {
        try {
			freeSwitchService.freeSwitchUploadRecordChunkAckFile(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
        String work = "<continue></continue>";
        LOGGER.info(request.getParameter("customerPhone") + "recordChunkAck-----------" +request.getQueryString());
        return getParamsAndWork(null, work);
    }

    //首次播放录音
    @RequestMapping(value = "/recordCall_ack", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getResponseIvr(HttpServletRequest request) {
    	LOGGER.info(request.getParameter("customerPhone") + "recordCall_ack-----------" +request.getQueryString());
        String result = null;
    	try {
			result = freeSwitchService.freeSwitchFirstPlayBack(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
    	return result;
    }

    //播放录音
    @RequestMapping(value = "/playback_ack", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getResponseIvrAck(HttpServletRequest request) {
    	LOGGER.info(request.getParameter("customerPhone") + "playback_ack-----------" +request.getQueryString());
        String result = null;
    	try {
    		result = freeSwitchService.freeSwitchPlayBack(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
    	return result;
    }

    @RequestMapping(value = "/recordCall_ack", method = RequestMethod.POST, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getRecordCallAct(HttpServletRequest request) {
    	LOGGER.info(request.getParameter("customerPhone") + "recordCall_ack-----------" +request.getQueryString());
        try {
			freeSwitchService.freeSwitchUploadRecordCallFile(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
        String work = "<break></break>";
        return getParamsAndWork(null, work);
    }

    @RequestMapping(value = "silenceStream", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getSilence_stream(HttpServletRequest request) {
    	LOGGER.info(request.getParameter("customerPhone") + "silenceStream-----------" +request.getQueryString());
    	String result = null;
    	try {
    		result = freeSwitchService.freeswitchSilenceStream(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
    	return result;
    }

    @RequestMapping(value = "silenceStreamBusy", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getSilenceStreamBusy(HttpServletRequest request) {
    	LOGGER.info(request.getParameter("customerPhone") + "silenceStreamBusy-----------" +request.getQueryString());
    	String result = null;
    	try {
    		result = freeSwitchService.freeswitchSilenceStreamBusy(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
    	return result;
    }

    @RequestMapping(value = "hangup", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getHangUp(HttpServletRequest request) {
    	LOGGER.info(request.getParameter("customerPhone") + "hangup-----------" +request.getQueryString());
    	String result = null;
    	try {
			result = freeSwitchService.freeswitchHangUp(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
    	return result;
    }

    @RequestMapping(value = "busy", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getBusy(HttpServletRequest request) {
    	LOGGER.info(request.getParameter("customerPhone") + "busy-----------" +request.getQueryString());
    	String result = null;
    	try {
    		result = freeSwitchService.freeswitchBusy(request);
		} catch (FSException e) {
			e.printStackTrace();
		}
    	return result;
    }

    //呼叫转移
    @RequestMapping(value = "/dial", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
    @ResponseBody
    public String getDial(HttpServletRequest request) {
        String params = "<ringback>${cn-ring}</ringback> \n"+
                "<transfer_ringback>local_stream://moh</transfer_ringback>"+
                "<instant_ringback>true</instant_ringback>";
        String dialPhone = null;
        LOGGER.info("呼叫转移：" + request.getQueryString());
        Integer n = (Integer) redisCacheUtil.getCacheObject("outbound");
        String ports = request.getParameter("dialPorts");
        String auth = (String) redisCacheUtil.getCacheObject("auth");
        String pwd = (String) redisCacheUtil.getCacheObject("pwd");
        Integer gatewayId = Integer.valueOf(request.getParameter("gatewayId"));
        
        Gateway gateway = null;
        if (null == auth && null == pwd) {
            gateway = gatewayService.findGatewayInfoByGatewayId(gatewayId);
        }
        String param="info_type=imei,imsi,iccid,smsc,type,number,reg,slot,callstate,signal,gprs";
        if(!isEmpty(ports)) {
        	 param="port=" + ports + "&&info_type=imei,imsi,iccid,smsc,type,number,reg,slot,callstate,signal,gprs";
        }
        List<Map<String, String>> mapList=new ArrayList<>();
        if (!isEmpty(gateway.getUrl())){
            mapList = sendGet(gateway.getUrl(),param , gateway.getAuth(), gateway.getPwd());
        }

        Integer prifix = null;
        if (Boolean.valueOf(request.getParameter("exiting"))) {
            String work = "<hangup cause=\"主动挂机\"></hangup>";
            if (null != n && n > 0) {
                redisCacheUtil.setCacheObjectTimeOut("outbound", n - 1, 60);
            }
            return getParamsAndWork(null, work);
        }

        if (null != n && n > 0) {
            redisCacheUtil.setCacheObjectTimeOut("outbound", n + 1, 60);
        }
        Integer userId = Integer.valueOf(request.getParameter("userId"));
        LOGGER.info("转接的号码：-----------------------"+dialPhone);
        dialPhone = getDialPhone(userId);
        LOGGER.info("转接的号码：-----------------------"+dialPhone);
        if (mapList.size() > 0) {
            prifix = 1000 + Integer.valueOf(mapList.get(0).get("port"));
            dialPhone = "b" + prifix + dialPhone;
        }else if (!isEmpty(request.getParameter("gateName"))){
            dialPhone=request.getParameter("gateName")+dialPhone;
        }
        redisCacheUtil.setCacheObjectTimeOut("dial" + request.getParameter("Caller-Caller-ID-Number"), 1, 60);
        String work = "<dial Dialplan=\"XML\" context=\"default\" caller-id-number=\"7777\" caller-id-name=\"HTTAPI\" action=\"" + FREESWITCH_ACTION + "/dial\">" + dialPhone + "</dial>\n";
        LOGGER.info("转接userList:" + dialPhone);
        return getParamsAndWork(params, work);
    }

    public String getDialPhone(Integer userId) {
        String dialPhone = null;
        String dialPhones = (String) redisCacheUtil.getCacheObject("dialPhone" + userId);
        String[] strDialPhone = null;

        if (null == dialPhones) {
            Map<String, Object> map = new HashMap<>();
            map.put("userID", userId);
            List<User> userList = iUserService.findOnStaffPhoneList(map);
            LOGGER.info("转接userList:          "+userList.size());
            if (null == userList||userList.size()==0) {
                return null;
            }
            for (User user : userList) {
                dialPhones += user.getPhone() + ",";
            }
            dialPhones = dialPhones.replace("null", "");
            redisCacheUtil.setCacheObjectTimeOut("dialPhone" + userId, dialPhones, 60 * 12);
        }
        if (null != dialPhones) {
            strDialPhone = dialPhones.split(",");
        }
        for (String str : strDialPhone) {
            if (!isEmpty(str)) {
                dialPhone = str;
                dialPhones = dialPhones.replace(str, "");
                if (isEmpty(dialPhones.replace(",", ""))) {
                    redisCacheUtil.delete("dialPhone" + userId);
                } else {
                    redisCacheUtil.setCacheObjectTimeOut("dialPhone" + userId, dialPhones, 60 * 12);
                }
                break;
            }
        }
        if (isEmpty(dialPhone)) {
            return getDialPhone(userId);
        }
        return dialPhone;
    }
}
