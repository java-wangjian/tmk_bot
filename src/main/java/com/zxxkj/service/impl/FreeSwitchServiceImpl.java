package com.zxxkj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.cache.RedisCacheUtil;
import com.zxxkj.dao.CustomerAndPlanMapper;
import com.zxxkj.dao.CustomerMapper;
import com.zxxkj.dao.GatewayMapper;
import com.zxxkj.model.*;
import com.zxxkj.service.*;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.QiniuUtil;

import com.zxxkj.util.TTUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zxxkj.controller.CustomerController.MAX_FILE_SIZE;
import static com.zxxkj.controller.CustomerController.MAX_REQUEST_SIZE;
import static com.zxxkj.controller.CustomerController.MEMORY_THRESHOLD;
import static com.zxxkj.controller.FreeswitchController.UPLOAD_DIRECTORY;
import static com.zxxkj.util.ConstantUtil.FREESWITCH_ACTION;
import static com.zxxkj.util.ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF;
import static com.zxxkj.util.SipStatusCodeUtil.returnStatus;
import static com.zxxkj.util.Utils.deleteFile;
import static com.zxxkj.util.Utils.isEmpty;

@Service
public class FreeSwitchServiceImpl implements FreeSwitchService {
	private static final Logger LOGGER = Logger.getLogger(FreeSwitchServiceImpl.class);

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Resource
	private ProjectDataService projectDataService;

	@Resource
	private ICallRecordService iCallRecordService;

	@Resource
	private ICustomerService iCustomerService;

	@Resource
	private CustomerMapper customerMapper;

	@Resource
	private CallDetailService callDetailService;

//	@Resource
//	private StaticticsService staticticsService;

	@Autowired
	private GatewayMapper gatewayMapper;

	@Autowired
	private SMSService smsService;
	
	@Autowired
	private CustomerAndPlanMapper customerAndPlanMapper;

	@SuppressWarnings("unchecked")
	@Override
	public String freeSwitchResp(HttpServletRequest request) {
		LOGGER.info("外呼响应rest");
		Integer userId = Integer.valueOf(request.getParameter("userId"));
		String gateWayType = request.getParameter("gateWayType");
		String gateName = request.getParameter("gateName");
		String customerPhone = null;
		String planId = request.getParameter("planId");
		String customerId = request.getParameter("customerId");
		CallRecord callRecord = (CallRecord) redisCacheUtil.getCacheObject(userId + "_"+ planId + "_" + customerId);
		LOGGER.info("callRecord实例：" + callRecord.toString());
		String work = "<continue action=\"" + FREESWITCH_ACTION + "/record_call\"></continue>\n";
		LOGGER.info(request.getParameter("customerPhone") + "cause-------------------------------"
				+ (!isEmpty(request.getParameter("cause")) ? returnStatus(request.getParameter("cause")) : null));

//		if (!isEmpty(request.getParameter("Caller-Caller-ID-Number"))) {
//			customerPhone = request.getParameter("Caller-Caller-ID-Number");
//			iCustomerService.updateCallCountByPhone(Long.parseLong(customerPhone));// bug 待调整
//		}
		if (!isEmpty(request.getParameter("userId")) && !isEmpty(request.getParameter("exiting"))
				&& request.getParameter("exiting").equals("true")) {
			String thread_uuid = request.getParameter("thread_uuid");
//			recordService.updateCallRecordStatusById(Integer.valueOf(request.getParameter("callRecordId")), 2, 0, 6);
			callRecord.setStatus(2);
			callRecord.setDurationTime(0);
			callRecord.setCustomerGrade(6);
			
			redisCacheUtil.setCacheObjectTimeOut("transferGrade" + thread_uuid, 6, 60);
//			createStatictics();
//            staticticsService.insertStatictics(null, null, 1, 2, null, userId);
			work = "<break></break>";
			// 判断是否需要重复拨打
			boolean tr = false;
			if (!isEmpty(request.getParameter("cause")) && returnStatus(request.getParameter("cause"))) {
				Integer dialingThePhoneAgainNum = (Integer) redisCacheUtil
						.getCacheObject("dialingThePhoneAgainNum" + thread_uuid);
				if (dialingThePhoneAgainNum == null) {
					dialingThePhoneAgainNum = 0;
				}
				tr = true;
				redisCacheUtil.setCacheObjectTimeOut("dialingThePhoneAgainNum" + thread_uuid,
						dialingThePhoneAgainNum + 1, 60 * 8);
				redisCacheUtil.setCacheObjectTimeOut(request.getParameter("thread_uuid") + "aiDial", "wait", 10);
				if (dialingThePhoneAgainNum < 2) {
//					iCustomerService.updateIsCallById(Integer.parseInt(request.getParameter("customerId")), 0);
//					customerAndPlanMapper.updateIsCallByPlanIdAndCustomerId(Integer.valueOf(customerId), Integer.valueOf(planId), 0);
				}
			} else {
				redisCacheUtil.setCacheObjectTimeOut(request.getParameter("thread_uuid") + "aiDial", "over", 10);
			}

			if (!isEmpty(gateWayType) && gateWayType.equals("sipLine")) {
				Integer concurrencyNumber = (Integer) redisCacheUtil.getCacheObject("concurrencyNumber" + gateName);

				if (null == concurrencyNumber) {
					concurrencyNumber = 1;
				}
				if (tr) {
					redisCacheUtil.setCacheObjectTimeOut(request.getParameter("thread_uuid") + "aiDial", "over", 10);
				}
				redisCacheUtil.setCacheObjectTimeOut("concurrencyNumber" + gateName, concurrencyNumber - 1, 1);
			}

			LOGGER.info("break.............................................................");
		} else if (!isEmpty(request.getParameter("callRecordId"))) {
//			recordService.updateCallRecordStatusById(Integer.valueOf(request.getParameter("callRecordId")), 1, 1, 5);
			callRecord.setStatus(1);
			callRecord.setDurationTime(1);
			callRecord.setCustomerGrade(5);
			LOGGER.info("用户接通了电话,通话记录的id为[ " + callRecord.getId() + " ]");
		}else {
			LOGGER.info("既没有接通，也没有生成通话记录，属于异常");
		}

		redisCacheUtil.setCacheObject(userId + "_"+ planId + "_" + customerId, callRecord);
		return getParamsAndWork(null, work);
	}

	@Override
	public String freeSwitchRecordCall(HttpServletRequest request) {
		LOGGER.info("record call...");
		String param = "<RECORD_STEREO>true</RECORD_STEREO> \n" + "<RECORD_READ_ONLY >false</RECORD_READ_ONLY > \n"
						+ "<set_audio_level>read 1</set_audio_level>";
		String work = "<recordCall name=\"recordCall_name\" file=\"" + request.getParameter("Caller-Caller-ID-Number")
				+ "/" + RandomStringUtils.randomNumeric(50) + ".mp3" + "\" action=\"" + FREESWITCH_ACTION
				+ "/recordCall_ack\"></recordCall>";
		return getParamsAndWork(param, work);
	}

	// 第一次播放录音
	@Override
	public String freeSwitchFirstPlayBack(HttpServletRequest request) {
		long startTime = System.currentTimeMillis();
		setRedisUtil("startTime" + request.getParameter("session_id"), System.currentTimeMillis(), 10);
		LOGGER.info(request.getParameter("customerPhone") + "first playback...");
		String params = getTTSEngine();
		String customerPhone = request.getParameter("Caller-Caller-ID-Number");
		String thread_uuid = request.getParameter("thread_uuid");
		redisCacheUtil.setCacheObjectTimeOut("transferGrade" + thread_uuid, 3, 60);
		redisCacheUtil.setCacheObjectTimeOut("projectDatasMain" + thread_uuid, 1, 60);// 表示第几流程
		LOGGER.info("projectDatasMain" + customerPhone + "  "
				+ redisCacheUtil.getCacheObject("projectDatasMain" + thread_uuid));
		Integer projectId = Integer.valueOf(request.getParameter("projectId"));
		Integer callRecordId = Integer.valueOf(request.getParameter("callRecordId"));

		ProjectData projectData = projectDataService.selectProjectDateFirstByProjectId(projectId, 1);
		if (null != projectData && !isEmpty(projectData.getFileID1())) {
			String work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/recordChunk\" file=\""
					+ projectData.getFileID1() + "\"   >\n" + "</playback>\n";
			long endTime = System.currentTimeMillis();
			LOGGER.info("开场白消耗时间：" + (endTime - startTime) + "ms");
			createCalldetail(1, callRecordId, projectData.getFileID1(), projectData.getContent1(), null, "开场白");
			return getParamsAndWork(null, work);
		} else {
			String work = "<playback name=\"inputid\" file=\"say:" + projectData.getContent1()
					+ ",,,,,,,,,,\" action=\"" + FREESWITCH_ACTION + "/recordChunk\"  >" + "</playback>\n";
			return getParamsAndWork(params, work);
		}
	}

	@Override
	public String freeSwitchPlayBack(HttpServletRequest request) {
		Long startTime = System.currentTimeMillis();
		String freeswitchResult = null;
		String customerPhone = request.getParameter("Caller-Caller-ID-Number");
		Integer callRecordId = Integer.valueOf(request.getParameter("callRecordId"));
		Integer userId = Integer.valueOf(request.getParameter("userId"));
		Integer planId = Integer.valueOf(request.getParameter("planId"));
		LOGGER.info(request.getParameter("customerPhone") +  "-------" + request.getQueryString());
		// Integer planId = Integer.valueOf(request.getParameter("planId"));
		// Integer customerId = Integer.valueOf(request.getParameter("customerId"));
		String thread_uuid = request.getParameter("thread_uuid");
		LOGGER.info("xfly result:  " + request.getParameter("inputid"));
		Map<String, Object> map = new HashMap<>();

		Integer projectDatasMainCustomerPhoneNum = (Integer) redisCacheUtil
				.getCacheObject("projectDatasMain" + thread_uuid); // 第几流程
		Integer transferGrade = (Integer) redisCacheUtil.getCacheObject("transferGrade" + thread_uuid);
		LOGGER.info("projectDatasMain" + thread_uuid + " ** "
				+ redisCacheUtil.getCacheObject("projectDatasMain" + thread_uuid));
		if (null == projectDatasMainCustomerPhoneNum) {
			projectDatasMainCustomerPhoneNum = 2;
		}

		if (null == transferGrade) {
			if (projectDatasMainCustomerPhoneNum < 3) {
				transferGrade = 3;
			} else {
				transferGrade = 2;
			}

		}

		map.put("ProjectID", request.getParameter("projectId"));
		map.put("role", 1);
		map.put("projectDatasMainCustomerPhone", projectDatasMainCustomerPhoneNum);

		ProjectData projectDatasMain = projectDataService.shotProjectDataMainByProjectID(map);
		Integer projectDatasMainNum = projectDataService.searchProjectDatadMainsNum(map);
		map.remove("role");
		String inputId = request.getParameter("inputid");
		LOGGER.info("inputId: " + inputId);
		JSONObject jsonObject = JSON.parseObject(inputId);
		String result = getCustomerString(jsonObject);
		if (!isEmpty(result)) {
			if (projectDatasMainCustomerPhoneNum >= projectDatasMainNum) {// 主流程结束
				freeswitchResult = getEndProjectDatasSpecial(map, result, customerPhone, callRecordId, userId,
						thread_uuid, planId);
			}
			if (null != projectDatasMain) {
				if (getRedisUtil("multiWheelSession" + thread_uuid + projectDatasMainCustomerPhoneNum) >= 3) {
					freeswitchResult = getProjectDatasSpecial(map, result, customerPhone, callRecordId,
							projectDatasMainCustomerPhoneNum, projectDatasMainNum, thread_uuid);
				}
				if (isEmpty(freeswitchResult)) {
					freeswitchResult = getMainstreamProcedure(thread_uuid, projectDatasMain, result, customerPhone,
							projectDatasMainCustomerPhoneNum, callRecordId);
				}
			}
			if (isEmpty(freeswitchResult) && null != projectDatasMain) {
				freeswitchResult = getProjectDatasAuxiliary(map, result, callRecordId, customerPhone,
						projectDatasMainCustomerPhoneNum, thread_uuid);
			}
			if (isEmpty(freeswitchResult) && null != projectDatasMain) {
				freeswitchResult = multiWheelSession(map, result, callRecordId, projectDatasMainCustomerPhoneNum,
						thread_uuid);
			}
			if (isEmpty(freeswitchResult)) {
				freeswitchResult = getProjectDatasSpecial(map, result, customerPhone, callRecordId,
						projectDatasMainCustomerPhoneNum, projectDatasMainNum, thread_uuid);
			}
			if (isEmpty(freeswitchResult) && null != projectDatasMain) {
				setRedisUtil("projectDatasMain" + thread_uuid, projectDatasMainCustomerPhoneNum + 1, 10);
				if (transferGrade != 1 && transferGrade != 4 && transferGrade != 5) {
					if (projectDatasMainCustomerPhoneNum >= 3) {
						setRedisUtil("transferGrade" + thread_uuid, 2, 60);
					} else if (projectDatasMainCustomerPhoneNum < 3) {
						setRedisUtil("transferGrade" + thread_uuid, 3, 60);
					}
				}
				setRedisUtil("multiWheelSession" + thread_uuid, 0, 10);// 清空多轮会话次数
				freeswitchResult = getPlayBack(projectDatasMain);
				createCalldetail(1, callRecordId, projectDatasMain.getFileID1(), projectDatasMain.getContent1(), null,
						result);
			} else if (isEmpty(freeswitchResult) && (null == projectDatasMain) && projectDatasMainNum > 0) {// 主流程结束，转特殊问题
				setRedisUtil("transferGrade" + thread_uuid, 1, 10);
				freeswitchResult = getProjectDatasSpecial(map, result, customerPhone, callRecordId,
						projectDatasMainCustomerPhoneNum, projectDatasMainNum, thread_uuid);
			}
		} else {
			// 未识别问题待修改
			LOGGER.info("未识别日志：customer" + customerPhone + "  callRecordId:  " + callRecordId);
			if (null == result) {
				freeswitchResult = getProjectDatasUnrecognized(map, customerPhone, callRecordId, thread_uuid);
			} else {
				freeswitchResult = getProjectDatasNoSpeech(map, customerPhone, callRecordId, thread_uuid);
			}

			if (isEmpty(freeswitchResult) && null != projectDatasMain) {
				setRedisUtil("projectDatasMain" + thread_uuid, projectDatasMainCustomerPhoneNum + 1, 10);
				freeswitchResult = getPlayBack(projectDatasMain);
				createCalldetail(1, callRecordId, projectDatasMain.getFileID1(), projectDatasMain.getContent1(), null,
						result);
			} else if (isEmpty(freeswitchResult)) {
				freeswitchResult = getProjectDatasUnrecognized(map, customerPhone, callRecordId, thread_uuid);
			}
		}
		LOGGER.info("getParamsAndWork:" + getParamsAndWork(getParam(), freeswitchResult));
		Long endTime = System.currentTimeMillis();
		LOGGER.info("通话消耗时间：" + (endTime - startTime) / 1000 + "s");
		return getParamsAndWork(getParam(), freeswitchResult);
	}

	@Override
	public void freeSwitchUploadRecordCallFile(HttpServletRequest request) {

		Integer customerId = null;
		Integer projectDatasMain = null;
		Integer transferGrade = null;

		Map<String, String> map = upload(request);
		String customerPhone = null;
		customerPhone = map.get("Caller-Caller-ID-Number");
		customerId = Integer.valueOf(map.get("customerId"));
		String str_key = "startTime" + map.get("session_id");
		Integer talkingTime = getTalkingTime(str_key);
		String thread_uuid = map.get("thread_uuid");

		if (null != redisCacheUtil.getCacheObject("projectDatasMain" + thread_uuid)) {
			projectDatasMain = (Integer) redisCacheUtil.getCacheObject("projectDatasMain" + thread_uuid);
		}
		if (null != redisCacheUtil.getCacheObject("transferGrade" + thread_uuid)) {
			transferGrade = (Integer) redisCacheUtil.getCacheObject("transferGrade" + thread_uuid);
		}
		
		try {
			if (null != projectDatasMain && null != transferGrade) {
				projectDatasMain = (Integer) redisCacheUtil.getCacheObject("projectDatasMain" + thread_uuid);
				transferGrade = (Integer) redisCacheUtil.getCacheObject("transferGrade" + thread_uuid);
				if (projectDatasMain >= 3 && transferGrade != 1 && transferGrade != 4 && transferGrade != 5) {
					transferGrade = 2;
				}
				
				if (null != projectDatasMain && projectDatasMain < 3 && null != transferGrade && transferGrade != 1
						&& transferGrade != 2 && transferGrade != 4 && transferGrade != 5) {
					transferGrade = 3;
				}
				if (null != projectDatasMain && projectDatasMain == 1) {
					transferGrade = 5;
				}
				String uploadPath = map.get("filePath");
				String filename = map.get("fileName");
				Integer planId = Integer.valueOf(map.get("planId"));
				Integer userId = Integer.valueOf(map.get("userId"));
				LOGGER.info(request.getParameter("customerPhone") + "-------------planId:" + map.get("planId"));
				// Integer planId = Integer.valueOf(map.get("planId"));
				createCallRecord(talkingTime, map, uploadPath, filename, transferGrade, thread_uuid, customerId, userId, planId);
				Integer isTransfer = getRedisUtil("dial" + thread_uuid);
//                staticticsService.insertStatictics(talkingTime, isTransfer, 1, 1, transferGrade, Integer.valueOf(map.get("userId")));
                LOGGER.info("send sms" + map.get("isSendSMS") + "-------------" + map.get("isSendSMS"));
				if (!isEmpty(map.get("isSendSMS")) && Integer.valueOf(map.get("isSendSMS")) == 1) {
					sendSms(Integer.valueOf(map.get("gatewayId")), customerPhone, Integer.valueOf(map.get("projectId")),
							transferGrade);
				}
				LOGGER.info("  " + map.get("exiting"));
				if (map.get("exiting").equals("true")) {
					
					redisCacheUtil.setCacheObjectTimeOut("customerId" + customerId, 1, 60);
					String gateWayType = request.getParameter("gateWayType");
					String gateName = request.getParameter("gateName");
					if (!isEmpty(gateWayType) && gateWayType.equals("sipLine")) {
						Integer concurrencyNumber = (Integer) redisCacheUtil.getCacheObject("concurrencyNumber" + gateName);
						if (null == concurrencyNumber) {
							concurrencyNumber = 1;
						}
						redisCacheUtil.setCacheObjectTimeOut("concurrencyNumber" + gateName, concurrencyNumber - 1, 1);
					}
					redisCacheUtil.delete("dial" + thread_uuid);
					redisCacheUtil.delete("projectDatasMain" + thread_uuid);
					redisCacheUtil.delete("transferGrade" + thread_uuid);
					if (null != getRedisUtil("outbound") && getRedisUtil("outbound") <= 0) {
						redisCacheUtil.delete("outbound");
					}
				}
				if(deleteFile(uploadPath)) {
					LOGGER.info(uploadPath + " 删除成功");
				}else {
					LOGGER.info(uploadPath + " 删除失败");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			redisCacheUtil.setCacheObjectTimeOut(map.get("thread_uuid") + "aiDial", "over", 10);
		}
	}

	@Override
	public void freeSwitchUploadRecordChunkAckFile(HttpServletRequest request) {
		Map<String, Object> callRecordMap = new HashMap<>();
		Map<String, String> map = upload(request);
		Integer callRecordId = Integer.valueOf(map.get("callRecordId"));
		String uploadPath = map.get("filePath");
		String filename = map.get("fileName");
		String fileUrl = QiniuUtil.upload(uploadPath, filename, ConstantUtil.QINIUYUN_BUCKE_RECORDE);
		
		try {
			if(deleteFile(uploadPath)) {
				LOGGER.info(uploadPath + " 删除成功");
			}else {
				LOGGER.info(uploadPath + " 删除失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("删除文件 " + uploadPath + "出现错误");
		}
		callRecordMap.put("CallRecordID", callRecordId);
		LOGGER.info(request.getParameter("customerPhone") + "分段录音上传：" + fileUrl);
		List<CallDetail> callDetailList = callDetailService.selectCallRecordDetailByCallRecordID(callRecordMap);
		for (int i = 0; i < callDetailList.size(); i++) {
			if (i == 0) {
				continue;
			}
			if (isEmpty(callDetailList.get(i).getFileURL())) {
				callRecordMap.put("fileURL", fileUrl);
				callRecordMap.put("callDetailId", callDetailList.get(i).getId());
				LOGGER.info("fileURL:      " + fileUrl + "             callDetailId:          "
						+ callDetailList.get(i).getId());
				callDetailService.updateFileUrlByCallDetailId(callRecordMap);
				break;
			}
		}
		LOGGER.info("删除文件：----------" + filename);
	}

	@Override
	public String freeswitchSilenceStream(HttpServletRequest request) {
		System.out.println("播放静音流文件7s");// default|mandarin
		String work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION
				+ "/playback_ack\" file=\"silence_stream://4000\" input-timeout=\"1000\" asr-engine=\"iflyrest\" asr-grammar=\"{accent=sms8k}default\" >\n"
				+ "</playback>\n";
		String param = "<sessionId>" + "zxx" + request.getParameter("session_id") + "</sessionId> \n";
		return getParamsAndWork(param, work);
	}

	@Override
	public String freeswitchSilenceStreamBusy(HttpServletRequest request) {
		System.out.println("播放静音流文件7s");// {accept="sms8k",barge-in="true",aue="raw"}
		String work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION
				+ "/busy\" file=\"silence_stream://4000\" input-timeout=\"1000\" asr-engine=\"iflyrest\" asr-grammar=\"{accent=sms8k}default\" >\n"
				+ "</playback>\n";
		String param = "<sessionId>" + "zxx" + request.getParameter("session_id") + "</sessionId> \n";
		return getParamsAndWork(param, work);
	}

	@Override
	public String freeswitchHangUp(HttpServletRequest request) {
		System.out.println("挂机");
		String work = "<hangup cause=\"主动挂机\"></hangup>";
		return getParamsAndWork(null, work);
	}

	@Override
	public String freeswitchBusy(HttpServletRequest request) {
		Integer callRecordId = Integer.valueOf(request.getParameter("callRecordId"));
		String inputId = request.getParameter("inputid");
		LOGGER.info(request.getParameter("customerPhone") + "inputId: " + inputId);
		JSONObject jsonObject = JSON.parseObject(inputId);
		String result = getCustomerString(jsonObject);
		String thread_uuid = request.getParameter("thread_uuid");
		LOGGER.info("xfly result:  " + request.getParameter("inputid"));
		String fileId2 = String.valueOf(redisCacheUtil.getCacheObject("busy" + thread_uuid));
		String content = String.valueOf(redisCacheUtil.getCacheObject("busyContent" + thread_uuid));
		redisCacheUtil.delete("busy" + thread_uuid);
		redisCacheUtil.delete("busyContent" + thread_uuid);
		String work = null;
		work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/hangup\" file=\"" + fileId2
				+ "\"  digit-timeout=\"1000\"  input-timeout=\"1000\" >\n" + "</playback>\n";
		createCalldetail(1, callRecordId, fileId2, content, null, result);
		return getParamsAndWork(getParam(), work);
	}

	public static String getParamsAndWork(String params, String work) {
		String str = "<document type=\"xml/freeswitch-httapi\">\n" + "<variables>\n" + params + "</variables>\n"
				+ "<work>\n" + work + "</work>\n" + "</document>";
		return str;
	}

	public static String getTTSEngine() {
		String params = "<tts_engine>iflyrest</tts_engine>\n" + "<tts_voice>xiaoyan</tts_voice>\n";
		return params;
	}

	public static String getParam() {
		return "<var1>1234</var1> \n" + "<var2>" + String.valueOf(System.currentTimeMillis()) + "</var2>\n";
	}

	public void setRedisUtil(String key, Object value, long time) {
		redisCacheUtil.setCacheObjectTimeOut(key, value, time);
	}

	public Integer getRedisUtil(String key) {
		Integer result = (Integer) redisCacheUtil.getCacheObject(key);
		if (result == null) {
			result = 0;
		}
		return result;
	}

	// 主流程话术
	public String getMainstreamProcedure(String thread_uuid, ProjectData projectData, String result,
			String customerPhone, Integer projectDatasMainCustomerPhoneNum, Integer callRecordId) {
		String work = null;
		System.out.println("-----------------------" + result);
		String[] keywords = projectData.getKeyword().split(" ");
		Integer multiWheelSessionOwnTimes = getRedisUtil(
				"multiWheelSession" + thread_uuid + projectDatasMainCustomerPhoneNum);
		if (multiWheelSessionOwnTimes >= 3) {
			setRedisUtil("projectDatasMain" + thread_uuid, projectDatasMainCustomerPhoneNum + 1, 10);
			if (getRedisUtil("transferGrade" + thread_uuid) != 1 && getRedisUtil("transferGrade" + thread_uuid) != 4
					&& getRedisUtil("transferGrade" + thread_uuid) != 5) {
				if (getRedisUtil("projectDatasMain" + thread_uuid) >= 3) {
					setRedisUtil("transferGrade" + thread_uuid, 2, 60);
				} else if (getRedisUtil("projectDatasMain" + thread_uuid) < 3) {
					setRedisUtil("transferGrade" + thread_uuid, 3, 60);
				}
			}
			setRedisUtil("multiWheelSession" + thread_uuid, 0, 10);
			createCalldetail(1, callRecordId, projectData.getFileID1(), projectData.getContent1(), null, result);
			work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/recordChunk\"file=\""
					+ projectData.getFileID1() + "\"  digit-timeout=\"1000\"  input-timeout=\"1000\" >\n"
					+ "</playback>\n";
		} else {
			for (String keyword : keywords) {
				if (!isEmpty(result) && result.contains(keyword)) {
					setRedisUtil("projectDatasMain" + thread_uuid, projectDatasMainCustomerPhoneNum + 1, 10);
					if (getRedisUtil("transferGrade" + thread_uuid) != 1
							&& getRedisUtil("transferGrade" + thread_uuid) != 4
							&& getRedisUtil("transferGrade" + thread_uuid) != 5) {
						if (getRedisUtil("projectDatasMain" + thread_uuid) >= 3) {
							setRedisUtil("transferGrade" + thread_uuid, 2, 60);
						} else if (getRedisUtil("projectDatasMain" + thread_uuid) < 3) {
							setRedisUtil("transferGrade" + thread_uuid, 3, 60);
						}
					}
					setRedisUtil("multiWheelSession" + thread_uuid, 0, 10);
					createCalldetail(1, callRecordId, projectData.getFileID1(), projectData.getContent1(), null,
							result);
					work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/recordChunk\"file=\""
							+ projectData.getFileID1() + "\"  digit-timeout=\"1000\"  input-timeout=\"1000\" >\n"
							+ "</playback>\n";
					return work;
				}
			}
		}

		return work;
	}

	// 辅助问题话术
	public String getProjectDatasAuxiliary(Map<String, Object> map, String result, Integer callRecordId,
			String customerPhone, Integer projectDatasMainCustomerPhoneNum, String thread_uuid) {
		String work = null;
		map.put("role", 2);
		List<ProjectData> projectDatasAuxiliary = projectDataService.shotProjectDatasByProjectID(map);
		map.remove("role");
		for (ProjectData projectData1 : projectDatasAuxiliary) {
			String[] keywordsAuxiliary = projectData1.getKeyword().split(" ");
			for (String kw : keywordsAuxiliary) {
				if (!isEmpty(result) && result.contains(kw)) {
					Integer multiWheelSessionOwnTimes = getRedisUtil(
							"multiWheelSession" + thread_uuid + projectDatasMainCustomerPhoneNum);
					redisCacheUtil.setCacheObjectTimeOut(
							"multiWheelSession" + thread_uuid + projectDatasMainCustomerPhoneNum,
							multiWheelSessionOwnTimes + 1, 10);
					work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/recordChunk\"file=\""
							+ projectData1.getFileID1() + "\"   digit-timeout=\"1000\"  input-timeout=\"1000\" >\n"
							+ "</playback>\n";
					createCalldetail(1, callRecordId, projectData1.getFileID1(), projectData1.getContent1(), null,
							result);
					return work;
				}
			}
		}
		return work;
	}

	// 多轮话术
	public String multiWheelSession(Map<String, Object> map, String result, Integer callRecordId,
			Integer projectDatasMainCustomerPhoneNum, String thread_uuid) {
		String work = null;
		map.put("role", 3);
		List<ProjectData> projectDatasMultiWheelSession = projectDataService.shotProjectDatasByProjectID(map);
		map.remove("role");
		for (ProjectData projectData2 : projectDatasMultiWheelSession) {
			String[] keywordsSpecial = projectData2.getKeyword().split(" ");
			for (String kw : keywordsSpecial) {
				if (!isEmpty(result) && result.contains(kw)) {
					Integer multiWheelSessionNumProjectDateId = getRedisUtil(
							"multiWheelSession" + thread_uuid + projectData2.getId());
					Integer multiWheelSessionOwnTimes = getRedisUtil(
							"multiWheelSession" + thread_uuid + projectDatasMainCustomerPhoneNum);
					String fileID = projectData2.getFileID1();
					String content = projectData2.getContent1();
					redisCacheUtil.setCacheObjectTimeOut("multiWheelSession" + thread_uuid + projectData2.getId(),
							multiWheelSessionNumProjectDateId + 1, 10);
					redisCacheUtil.setCacheObjectTimeOut(
							"multiWheelSession" + thread_uuid + projectDatasMainCustomerPhoneNum,
							multiWheelSessionOwnTimes + 1, 10);

					if (multiWheelSessionNumProjectDateId % 3 == 1 && !isEmpty(projectData2.getFileID2())) {
						fileID = projectData2.getFileID2();
						content = projectData2.getContent2();
					} else if (multiWheelSessionNumProjectDateId % 3 == 2 && !isEmpty(projectData2.getFileID3())) {
						fileID = projectData2.getFileID3();
						content = projectData2.getContent3();
					}
					work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/recordChunk\"file=\"" + fileID
							+ "\"  digit-timeout=\"1000\"  input-timeout=\"1000\" >\n" + "</playback>\n";
					createCalldetail(1, callRecordId, fileID, content, null, result);
					return work;
				}
			}
		}
		return work;
	}

	// 特殊问题库
	public String getProjectDatasSpecial(Map<String, Object> map, String result, String customerPhone,
			Integer callRecordId, Integer projectDatasMainCustomerPhoneNum, Integer projectDatasMainNum,
			String thread_uuid) {
		String work = null;
		map.put("role", 4);
		List<ProjectData> projectDatasSpecial = projectDataService.shotProjectDatasByProjectID(map);
		map.remove("role");
		for (ProjectData projectData2 : projectDatasSpecial) {
			String[] keywordsSpecial = projectData2.getKeyword().split(" ");
			String named = projectData2.getNamed();
			String fileID = projectData2.getFileID1();
			String content = projectData2.getContent1();
			String str = "recordChunk";
			boolean result_kw = false;
			for (String kw : keywordsSpecial) {
				if (result.contains(kw)) {
					result_kw = true;
				}
			}
			if (projectDatasMainCustomerPhoneNum < projectDatasMainNum
					&& (projectData2.getNamed().contains("结束语-邀约失败") || projectData2.getNamed().contains("结束语-邀约成功"))) {
				continue;
			}
			if (!isEmpty(result) && result_kw) {
				if (!isEmpty(named) && named.contains("忙") && getRedisUtil("transferGrade" + thread_uuid) > 1) {
					redisCacheUtil.setCacheObjectTimeOut("transferGrade" + thread_uuid, 4, 60);
				}

				Integer specialProblemsTimes = getRedisUtil("specialProblems" + thread_uuid + projectData2.getId());
				if (projectData2.getNamed().contains("拒绝")) {
					setRedisUtil("specialProblems" + thread_uuid + projectData2.getId(), specialProblemsTimes + 1, 10);
					if (getRedisUtil("transferGrade" + thread_uuid) != 1
							&& getRedisUtil("transferGrade" + thread_uuid) != 4) {
						redisCacheUtil.setCacheObjectTimeOut("transferGrade" + thread_uuid, 5, 60);
					}
				}

				if ((specialProblemsTimes % 3 == 1) && !isEmpty(projectData2.getFileID2())) {
					fileID = projectData2.getFileID2();
					content = projectData2.getContent2();
				} else if (specialProblemsTimes % 3 == 2 && !isEmpty(projectData2.getFileID3())) {
					fileID = projectData2.getFileID3();
					content = projectData2.getContent3();
				}

				if (projectData2.getNamed().contains("忙")) {
					fileID = projectData2.getFileID1();
					content = projectData2.getContent1();
					str = "silenceStreamBusy";
					setRedisUtil("busy" + thread_uuid, projectData2.getFileID2(), 10);
					setRedisUtil("busyContent" + thread_uuid, projectData2.getContent2(), 10);
				} else if (specialProblemsTimes >= 1 && projectData2.getNamed().contains("拒绝")) {
					str = "hangup";
					setRedisUtil("specialProblems" + thread_uuid + projectData2.getId(), 0, 10);
				}

				work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/" + str + "\" file=\"" + fileID
						+ "\"  digit-timeout=\"1000\"  input-timeout=\"1000\" >\n" + "</playback>\n";
				createCalldetail(1, callRecordId, fileID, content, null, result);
				if (!isEmpty(fileID)) {
					return work;
				}
			}
		}
		return work;
	}

	// 邀约成功or邀约失败
	public String getEndProjectDatasSpecial(Map<String, Object> map, String result, String customerPhone,
			Integer callRecordId, Integer userId, String thread_uuid, Integer planId) {
		String work = null;
		map.put("role", 4);
		map.put("named", "结束语-邀约");
		List<ProjectData> projectDatasSpecial = projectDataService.shotProjectDatasByProjectID(map);
		map.remove("named");
		map.remove("role");
		for (int i = 0; i < projectDatasSpecial.size(); i++) {
			ProjectData projectData = projectDatasSpecial.get(i);
			String[] keywordsSpecial = projectData.getKeyword().split(" ");
			String fileID = projectData.getFileID1();
			String content = projectData.getContent1();
			String str = "hangup";
			boolean result_kw = false;
			for (String kw : keywordsSpecial) {
				if (result.contains(kw)) {
					result_kw = true;
					break;
				}
			}
			if (result_kw) {
				if (projectData.getNamed().contains("邀约失败")) {
					LOGGER.info("邀约失败:" + fileID + "  " + content);
					setRedisUtil("specialProblems" + thread_uuid + projectData.getId(), 0, 10);
					redisCacheUtil.setCacheObjectTimeOut("transferGrade" + thread_uuid, 2, 60);
				} else {
					for (ProjectData pd : projectDatasSpecial) {
						if (pd.getNamed().contains("邀约成功")) {
							fileID = pd.getFileID1();
							content = pd.getContent1();
							break;
						}
					}
					if (getRedisUtil("planId_" + planId + "_isTransfer") == 1) {
						str = "dial";
						setRedisUtil("dial" + thread_uuid, 1, 10);
					} else {
						str = "hangup";
					}
					setRedisUtil("specialProblems" + thread_uuid + projectData.getId(), 0, 10);
					redisCacheUtil.setCacheObjectTimeOut("transferGrade" + thread_uuid, 1, 60);
				}
			} else if (projectDatasSpecial.size() > 0 && i == projectDatasSpecial.size() - 1) {
				for (ProjectData pd : projectDatasSpecial) {
					if (pd.getNamed().contains("邀约成功")) {
						fileID = pd.getFileID1();
						content = pd.getContent1();
						break;
					}
				}
				if (getRedisUtil("planId_" + planId + "_isTransfer") == 1) {
					str = "dial";
					setRedisUtil("dial" + thread_uuid, 1, 10);
				} else {
					str = "hangup";
				}
				setRedisUtil("specialProblems" + thread_uuid + projectData.getId(), 0, 10);
				redisCacheUtil.setCacheObjectTimeOut("transferGrade" + thread_uuid, 1, 60);
			} else {
				continue;
			}

			work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/" + str + "\" file=\"" + fileID
					+ "\"  digit-timeout=\"1000\"  input-timeout=\"1000\" >\n" + "</playback>\n";
			createCalldetail(1, callRecordId, fileID, content, null, result);
			return work;
		}

		return work;
	}

	// 未识别结果查询
	public String getProjectDatasUnrecognized(Map<String, Object> map, String customerPhone, Integer callRecordId,
			String thread_uuid) {
		String work = null;
		map.put("role", 4);
		map.put("named", "未识别");
		Integer projectDatasMainCustomerPhoneNum = getRedisUtil("projectDatasMain" + thread_uuid);
		List<ProjectData> projectDatasSpecial = projectDataService.shotProjectDatasByProjectID(map);
		map.remove("named");
		map.remove("role");
		if (projectDatasSpecial.size() > 0) {
			Integer unidentifiedTimes = getRedisUtil(
					"unidentifiedTimes" + thread_uuid + projectDatasMainCustomerPhoneNum);
			setRedisUtil("unidentifiedTimes" + thread_uuid + projectDatasMainCustomerPhoneNum, unidentifiedTimes + 1,
					5);
			String fileID = projectDatasSpecial.get(0).getFileID1();
			String content = projectDatasSpecial.get(0).getContent1();
			if ((unidentifiedTimes == 1 || unidentifiedTimes % 3 == 1)
					&& !isEmpty(projectDatasSpecial.get(0).getFileID2())) {
				fileID = projectDatasSpecial.get(0).getFileID2();
				content = projectDatasSpecial.get(0).getContent2();
			} else if ((unidentifiedTimes == 2 || unidentifiedTimes % 3 == 2)
					&& !isEmpty(projectDatasSpecial.get(0).getFileID3())) {
				fileID = projectDatasSpecial.get(0).getFileID3();
				content = projectDatasSpecial.get(0).getContent3();
			}
			String str = "recordChunk";
			if (unidentifiedTimes >= 2) {
				setRedisUtil("unidentifiedTimes" + thread_uuid + projectDatasMainCustomerPhoneNum, 0, 5);
				str = "hangup";
			}
			work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/" + str + "\" file=\"" + fileID
					+ "\"  digit-timeout=\"1000\"  input-timeout=\"1000\" >\n" + "</playback>\n";
			createCalldetail(1, callRecordId, fileID, content, null, "未识别");
			return work;
		}
		return work;
	}

	// 未讲话结果查询
	public String getProjectDatasNoSpeech(Map<String, Object> map, String customerPhone, Integer callRecordId,
			String thread_uuid) {
		String work = null;
		map.put("role", 4);
		map.put("named", "未讲话");
		Integer projectDatasMainCustomerPhoneNum = getRedisUtil("projectDatasMain" + thread_uuid);
		List<ProjectData> projectDatasSpecial = projectDataService.shotProjectDatasByProjectID(map);
		map.remove("named");
		map.remove("role");
		if (projectDatasSpecial.size() > 0) {
			Integer unidentifiedTimes = getRedisUtil("noSpeechTimes" + thread_uuid + projectDatasMainCustomerPhoneNum);
			setRedisUtil("noSpeechTimes" + thread_uuid, unidentifiedTimes + 1, 10);
			setRedisUtil("noSpeechTimes" + thread_uuid + projectDatasMainCustomerPhoneNum, unidentifiedTimes + 1, 10);
			String fileID = projectDatasSpecial.get(0).getFileID1();
			String content = projectDatasSpecial.get(0).getContent1();
			if ((unidentifiedTimes == 1 || unidentifiedTimes % 3 == 1)
					&& !isEmpty(projectDatasSpecial.get(0).getFileID2())) {
				fileID = projectDatasSpecial.get(0).getFileID2();
				content = projectDatasSpecial.get(0).getContent2();
			} else if ((unidentifiedTimes == 2 || unidentifiedTimes % 3 == 2)
					&& !isEmpty(projectDatasSpecial.get(0).getFileID3())) {
				fileID = projectDatasSpecial.get(0).getFileID3();
				content = projectDatasSpecial.get(0).getContent3();
			}
			String str = "recordChunk";
			if (unidentifiedTimes >= 2) {
				str = "hangup";
			}
			work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/" + str + "\" file=\"" + fileID
					+ "\"  digit-timeout=\"1000\"  input-timeout=\"1000\" >\n" + "</playback>\n";
			createCalldetail(1, callRecordId, fileID, content, null, "未识别");
			return work;
		}
		return work;
	}

	public static String getCustomerString(JSONObject jsonObject) {
		StringBuffer str_result_buffer = new StringBuffer();
		if (null == jsonObject) {
			return null;
		}
		JSONArray result = jsonObject.getJSONArray("result");
		if (result == null) {
			return null;
		}
		for (int i = 0; i < result.size(); i++) {
			str_result_buffer.append(result.getString(i));
		}
		return str_result_buffer.toString();
	}

	public static String getPlayBack(ProjectData projectData) {
		String work = "<playback name=\"inputid\" action=\"" + FREESWITCH_ACTION + "/recordChunk\"file=\""
				+ projectData.getFileID1() + "\"  >\n" + "</playback>\n";
		return work;
	}

	public String getDial(String customerPhone) {
		redisCacheUtil.setCacheObjectTimeOut("dial" + customerPhone, 1, 60 * 12);

		String work = "<dial caller-id-number=\"7777\" action=\"" + FREESWITCH_ACTION + "/dial\">b15003428788</dial>\n";
		return work;
	}

	public String getTTSPlayBack(String customerPhone) {
		Integer unrecognizedNumOfTimes = 0;// 未识别次数
		if (null == redisCacheUtil.getCacheObject("unrecognizedNumOfTimes" + customerPhone)) {
			redisCacheUtil.setCacheObjectTimeOut("unrecognizedNumOfTimes" + customerPhone, 1, 60);
		}
		unrecognizedNumOfTimes = (null != redisCacheUtil.getCacheObject("unrecognizedNumOfTimes" + customerPhone)
				? (Integer) redisCacheUtil.getCacheObject("unrecognizedNumOfTimes" + customerPhone)
				: 1);
		redisCacheUtil.setCacheObjectTimeOut("unrecognizedNumOfTimes" + customerPhone, unrecognizedNumOfTimes + 1, 60);
		String work = "<playback  name=\"inputid\" file=\"say:你好、我没听清楚、请再说一遍,可以么+\" action=\"" + FREESWITCH_ACTION
				+ "/recordChunk\"   digit-timeout=\"1000\" input-timeout=\"1000\" >" + "</playback>\n";
		return work;
	}

	public void createCallRecord(Integer talkTime, Map<String, String> map, String path, String filename, Integer grade,
			String thread_uuid, Integer customerId, Integer userId, Integer planId) {
		Integer callRecordId = Integer.valueOf(map.get("callRecordId"));
		Integer transferGrade = (Integer) redisCacheUtil.getCacheObject("transferGrade" + thread_uuid);
		Map<String, Object> map1 = new HashMap<>();
		CallRecord callRecord = (CallRecord) redisCacheUtil.getCacheObject(userId + "_"+ planId + "_" + customerId);
		// if (null != transferGrade && transferGrade == 6) {
		// callRecord.setStatus(2);
		// } else {
		// callRecord.setStatus(1);
		// }
		if (null == grade) {
			grade = 6;
		}
		callRecord.setDurationTime(talkTime);
		callRecord.setCustomerGrade(transferGrade);
		LOGGER.info("七牛云返回上传路劲：" + QiniuUtil.upload(path, filename, ConstantUtil.QINIUYUN_BUCKE_RECORDE));
		callRecord.setFileID(QiniuUtil.upload(path, filename, ConstantUtil.QINIUYUN_BUCKE_RECORDE));
		callRecord.setId(callRecordId);
		callRecord.setCustomerGrade(grade);
		map1.put("CallRecord", callRecord);
		LOGGER.info("grade*******  " + grade);
		customerMapper.updateGradeByCustomerId(customerId, grade);
//		iCallRecordService.insertAfterUpdateStatus(map1);
		redisCacheUtil.setCacheObject(userId + "_" + planId + "_" + customerId, callRecord);
	}

	public Integer getTalkingTime(String startTimeKey) {
		Long startTime = (Long) redisCacheUtil.getCacheObject(startTimeKey);
		Integer talkTime = 1;
		if (startTime != null) {
			LOGGER.info("startTime :" + startTime + "------------startTimeKey:" + startTimeKey);
			talkTime = Math.toIntExact((System.currentTimeMillis() - startTime) / 1000);
			if (talkTime == 0) {
				talkTime = 1;
			}
		}
		LOGGER.info("通话时长：" + talkTime);
		return talkTime;
	}

	public void createCalldetail(Integer role, Integer callRecordId, String recordURL, String recordWord,
			String fileURL, String fileWord) {
		CallDetail callDetail = new CallDetail();
		Map<String, Object> map = new HashMap<>();
		callDetail.setCallrecordID(callRecordId);
		callDetail.setRole(role);
		callDetail.setDatetime(YYYY_MM_DD_HH_MM_SS_SDF.format(new Date()));
		callDetail.setRecordURL(recordURL);
		callDetail.setRecordWord(recordWord);
		callDetail.setFileURL(fileURL);
		callDetail.setFileWord(fileWord);
		map.put("CallDetail", callDetail);
		callDetailService.insertCallDetailData(map);
	}

//	public void createStatictics(Integer duratTotal, Integer toStaffCount, Integer callCount, Integer status,
//			Integer customer, Integer userId) {
//		Map<String, Object> map = new HashMap<>();
//		Statictics statictics = new Statictics();
//		statictics.setDuratTotal(duratTotal);
//		if (null != toStaffCount && toStaffCount == 0) {
//			toStaffCount = null;
//		}
//		statictics.setToStaffCount(toStaffCount);
//		statictics.setCallCount(callCount);
//		statictics.setUserId(userId);
//		if (null != duratTotal) {
//			if (duratTotal <= 10) {
//				statictics.setLt10gt5(1);
//			} else if (duratTotal >= 30) {
//				statictics.setGt30(1);
//			}
//		}
//		map.put("statictics", statictics);
//		map.put("customer", customer);
//		map.put("userID", userId);
//		map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//		map.put("status", status);
////		staticticsService.insertStatictics(map);
//	}

	public Map<String, String> upload(HttpServletRequest request) {
		System.out.println("upload file");
		String filename = null;
		Map<String, String> map = new HashMap<>();
		String customerPhone = null;

		// 配置上传参数
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		// 设置临时存储目录
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);

		// 设置最大文件上传值
		upload.setFileSizeMax(MAX_FILE_SIZE);

		// 设置最大请求值 (包含文件和表单数据)
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// 中文处理
		upload.setHeaderEncoding("UTF-8");

		// 构造临时路径来存储上传的文件
		// 这个路径相对当前应用的目录
		String uploadPath = request.getServletContext().getRealPath("./") + UPLOAD_DIRECTORY;//

		System.out.println(uploadPath);
		// 如果目录不存在则创建
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			// 解析请求的内容提取文件数据
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0) {
				// 迭代表单数据
				for (FileItem item : formItems) {
					map.put(item.getFieldName(), item.getString("utf-8"));
					// 处理不在表单中的字段
					if (!item.isFormField()) {
						filename = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + filename;
						File storeFile = new File(filePath);
						// 在控制台输出文件的上传路径
						System.out.println(filePath);
						map.put("filePath", filePath);
						map.put("fileName", filename);
						// 保存文件到硬盘
						item.write(storeFile);
						request.setAttribute("message", "文件上传成功!");
					}
				}
			}
		} catch (Exception ex) {
			request.setAttribute("message", "错误信息: " + ex.getMessage());
		}
		return map;
	}

	public void sendSms(Integer gateWayId, String phone, Integer projectId, Integer transfergrade) {
		Map<String, Object> map = new HashMap<>();
		String grade = "F";
		if (transfergrade == 1) {
			grade = "A";
		} else if (transfergrade == 2) {
			grade = "B";
		} else if (transfergrade == 3) {
			grade = "C";
		} else if (transfergrade == 4) {
			grade = "D";
		} else if (transfergrade == 5) {
			grade = "E";
		} else if (transfergrade == 6) {
			grade = "F";
		}
		map.put("projectId", projectId);
		map.put("grade", grade);
		if (!isEmpty(phone) && phone.length() == 15) {
			phone = phone.substring(4, 15);
		}
		SMS sms = smsService.getSmsContentByProjectIdAndGrade(map);
		Gateway gateway = gatewayMapper.findGatewayInfoByGatewayId(gateWayId);

		LOGGER.info("sendSms+++++++++++++" + gateway.getUrl() + "   " + gateway.getAuth() + "  " + gateway.getPwd()
				+ "  " + phone + "     " + sms.getContent());
		TTUtil.sendSms(gateway.getUrl(), gateway.getAuth(), gateway.getPwd(), phone, sms.getContent());
	}
}
