package com.zxxkj.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.CallDetail;
import com.zxxkj.model.CallRecord;
import com.zxxkj.model.RecordHistory;
import com.zxxkj.model.User;
import com.zxxkj.service.CallDetailService;
import com.zxxkj.service.ICallRecordService;
import com.zxxkj.service.IProjectService;
import com.zxxkj.service.IUserService;
import com.zxxkj.service.IVisitService;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.TTUtil;
import com.zxxkj.util.TransportUtil;

@Controller
@RequestMapping("/callrecord")
public class CallRecordController {

	private static final Logger lg = Logger.getLogger(CallRecordController.class);

	@Resource
	private CallDetailService callDetailService;
	@Resource
	private ICallRecordService callRecordService;
	@Resource
	private IUserService userService;
	@Resource
	private IProjectService projectService;
	@Resource
	private IVisitService visitService;

	@RequestMapping(value = "/detailExcel", method = RequestMethod.GET)
	@ResponseBody // 导出通话记录的对话详情
	public void detailExcel(HttpServletRequest request, HttpServletResponse response, CallRecord callRecord) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (TTUtil.isAnyNull(callRecord.getId())) {
			lg.info("导出通话记录的对话详情接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "导出通话记录的对话详情接口传递参数错误!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = new HashMap<>();
		Integer callRecordID = callRecord.getId();
		map.put("CallRecordID", callRecordID);
		// 根据通话记录的idID，查找通话记录对话的详情
		List<CallDetail> list = callDetailService.selectCallRecordDetailByCallRecordID(map);
		if (list.size() > 0) {
			HSSFWorkbook wb = new HSSFWorkbook();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日-HH时mm分ss秒");
			Date date = new Date();
			String fileName = sdf.format(date);
			HSSFSheet sheet = wb.createSheet(String.valueOf(callRecord.getCustomerPhone()));
			HSSFRow row;
			detailExcel(wb, sheet);
			for (int i = 0; i < list.size(); i++) {
				row = sheet.createRow((int) i + 1);
				row.createCell(0).setCellValue(String.valueOf(i + 1));
				row.createCell(1).setCellValue((StringUtils.isAnyBlank(list.get(i).getFileURL()) ? ("<暂未获取到录音文件>")
						: (list.get(i).getFileURL())));
				row.createCell(2).setCellValue((StringUtils.isAnyBlank(list.get(i).getFileWord()) ? ("<环境嘈杂，暂未获取文字>")
						: (list.get(i).getFileWord())));
				row.createCell(3).setCellValue((StringUtils.isAnyBlank(list.get(i).getRecordURL()) ? ("<暂未获取到录音文件>")
						: (list.get(i).getRecordURL())));
				row.createCell(4).setCellValue((StringUtils.isAnyBlank(list.get(i).getRecordWord()) ? ("<环境嘈杂，暂未获取文字>")
						: (list.get(i).getRecordWord())));
				row.createCell(5).setCellValue(list.get(i).getDatetime());
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				wb.write(os);
				lg.info("将虚拟表格写入到字节数组输出流 成功!!!");
			} catch (IOException e1) {
				lg.info("将虚拟表格写入到字节数组输出流 异常!!!");
				e1.printStackTrace();
				TTUtil.formatReturn(resultJSON, 401, "将虚拟表格写入到字节数组输出流 异常!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			response.reset();
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			ServletOutputStream out = null;
			try {
				String userAgent = request.getHeader("user-agent");
				if (userAgent != null && userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0 
						 || userAgent.indexOf("Safari") >= 0) {
					response.setHeader("Content-Disposition",
							"attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
				} else {
					response.setHeader("Content-Disposition",
							"attachment;filename=" + new String((fileName + ".xls").getBytes(), "UTF8"));
				}
				response.addHeader("Access-Control-Allow-Origin", "*");
				out = response.getOutputStream();
				lg.info("将文件数据放到servlet响应回传浏览器 成功");
			} catch (Exception e1) {
				lg.info("将文件数据放到servlet响应回传浏览器 异常");
				e1.printStackTrace();
				TTUtil.formatReturn(resultJSON, 400, "将文件数据放到servlet响应回传浏览器 异常");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				bis = new BufferedInputStream(is);
				bos = new BufferedOutputStream(out);
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
				lg.info("字节流循环读取 成功!!!");
			} catch (Exception e) {
				lg.info("字节流循环读取 异常!!!");
				e.printStackTrace();
				TTUtil.formatReturn(resultJSON, 400, "字节流循环读取 异常!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			} finally {
				if (bis != null) {
					try {
						bis.close();
						lg.info("字符输入流关闭 成功!!!");
					} catch (IOException e) {
						lg.info("字符输入流关闭 失败!!!");
						e.printStackTrace();
						TTUtil.formatReturn(resultJSON, 400, "字符输入流关闭 失败!!!");
						TTUtil.sendDataByIOStream(response, resultJSON);
						return;
					}
				}
				if (bos != null) {
					try {
						bos.close();
						lg.info("字符输出流关闭 成功!!!");
					} catch (IOException e) {
						lg.info("字符输出流关闭 失败!!!");
						e.printStackTrace();
						TTUtil.formatReturn(resultJSON, 400, "字符输出流关闭 失败!!!");
						TTUtil.sendDataByIOStream(response, resultJSON);
						return;
					}
				}
			}
		} else {
			resultJSON.put("data", list);
			TTUtil.formatReturn(resultJSON, -1, "查看通话记录对话详情接口成功,但没有数据!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
	}

	// 创建通话记录的对话详情表头
	private void detailExcel(HSSFWorkbook wb, HSSFSheet sheet) {
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCell cell = row.createCell(0);
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFont(font);
		cell.setCellValue("序号");
		sheet.setColumnWidth(0, (int) (15 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("客户录音");
		sheet.setColumnWidth(1, (int) (50 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("客户文字");
		sheet.setColumnWidth(2, (int) (80 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("机器人录音");
		sheet.setColumnWidth(3, (int) (50 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue("机器人文字");
		sheet.setColumnWidth(4, (int) (80 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(5);
		cell.setCellValue("时间");
		sheet.setColumnWidth(5, (int) (50 * 100));
		cell.setCellStyle(style);
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@ResponseBody // 根据通话记录的ID，返回通话记录详情，彼此对话
	public void detail(HttpServletRequest request, HttpServletResponse response, CallRecord callRecord) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (null == callRecord.getId()) {
			lg.info("查看通话记录对话详情接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "查看通话记录对话详情接口传递参数错误!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Integer callRecordID = callRecord.getId();
		map.put("CallRecordID", callRecordID);
		// 根据通话记录的idID，查找通话记录对话的详情
		List<CallDetail> list = callDetailService.selectCallRecordDetailByCallRecordID(map);
		if (list.size() > 0) {
			for (CallDetail callDetail : list) {
				if (StringUtils.isAnyBlank(callDetail.getFileURL())) {
					callDetail.setFileURL("");
				}
				if (StringUtils.isAnyBlank(callDetail.getFileWord())) {
					callDetail.setFileWord("");
				}
				if (StringUtils.isAnyBlank(callDetail.getRecordURL())) {
					callDetail.setRecordURL("");
				}
				if (StringUtils.isAnyBlank(callDetail.getRecordWord())) {
					callDetail.setRecordWord("");
				}
			}
			resultJSON.put("data", list);
			TTUtil.formatReturn(resultJSON, 0, "查看通话记录对话详情接口成功!!!");
		} else {
			resultJSON.put("data", list);
			TTUtil.formatReturn(resultJSON, -1, "查看通话记录对话详情接口成功,但没有数据!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/history", method = RequestMethod.POST)
	@ResponseBody // 查看通话记录导出历史接口
	public void history(HttpServletRequest request, HttpServletResponse response, User user, Integer page,
			Integer per) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || null == page || null == per || page < 1) {
			lg.info("查看通话记录导出历史接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "查看通话记录导出历史接口传递参数错误!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		int userID = user.getId();
		map.put("userID", userID);
		page = (page - 1) * per;
		map.put("page", page);
		map.put("per", per);
		// 查询通话记录导出历史的总条数
		Integer count = callRecordService.selectExportHistoryCountByUserID(map);
		JSONObject temp = new JSONObject();
		if (null != count) {
			if (count == 0) {
				lg.info("查询记录为空!!!");
				List<RecordHistory> list = new ArrayList<RecordHistory>();
				temp.put("list", list);
				temp.put("count", 0);
				resultJSON.put("data", resultJSON);
				TTUtil.formatReturn(resultJSON, 405, "查询记录为空!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			// 查询通话记录导出历史的List
			List<RecordHistory> list = callRecordService.selectExportHistoryListByUserID(map);
			for (RecordHistory recordHistory : list) {
				recordHistory.setContent("");
				String levels = recordHistory.getLevels().replace("[", "");
				levels = levels.replace("]", "");
				levels = levels.replace(" ", "");
				recordHistory.setLevels(levels);
				String projects = recordHistory.getProjects().replace("[", "");
				projects = projects.replace("]", "");
				projects = projects.replace(" ", "");
				recordHistory.setProjects(projects);
			}
			temp.put("count", count);
			temp.put("list", list);
			resultJSON.put("data", temp);
			lg.info("通话记录导出历史 查看成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "通话记录导出历史 查看成功!!!");
		} else {
			lg.info("通话记录导出历史 查看失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "通话记录导出历史 查看失败!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/export", method = RequestMethod.POST)
    @ResponseBody // 导出通话记录 接口
    public void exportCallRecord(HttpServletRequest request, HttpServletResponse response, Integer id, 
    		String idList, Integer startSecond, Integer endSecond, String param, String startDate, 
    		String endDate, String levels) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=GBK");
        JSONObject resultJSON = new JSONObject();
        if (StringUtils.isAnyBlank(idList)) {
            lg.info("导出通话记录 接口传递参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "导出通话记录 接口传递参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        
        List<Integer> ids = null;
        if(idList == null || ("[]").equals(idList)) {
        	
        	Map<String, Object> paramMap = new HashMap<String, Object>();
        	paramMap.put("userID", id);
        	if (null != startSecond && null != endSecond && endSecond > startSecond) {
        		paramMap.put("startSecond", startSecond);
        		paramMap.put("endSecond", endSecond);
    		}

        	if (StringUtils.isNoneBlank(startDate, endDate)) {
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    			Date bt = null;
    			Date et = null;
    			try {
    				bt = sdf.parse(startDate);
    				et = sdf.parse(endDate);
    				Calendar cal = Calendar.getInstance();
    				cal.setTime(et);
    				cal.add(Calendar.DATE, 1);
    				et = cal.getTime();
    				endDate = sdf.format(et);
    			} catch (ParseException e) {
    				lg.info("多条件查找通话记录接口传递日期格式错误!!!");
    				TTUtil.formatReturn(resultJSON, 401, "多条件查找通话记录接口传递日期格式错误!!!!!!");
    				TTUtil.sendDataByIOStream(response, resultJSON);
    				return;
    			}
    			if (et.before(bt)) {
    				lg.info("多条件查找通话记录接口结束日期在开始日期之前!!!");
    				TTUtil.formatReturn(resultJSON, 402, "多条件查找通话记录接口结束日期在开始日期之前!!!");
    				TTUtil.sendDataByIOStream(response, resultJSON);
    				return;
    			}
    			List<String> calList = TTUtil.calendarFormat(startDate, endDate);
    			if (calList.size() > 31) {
    				lg.info("多条件查找通话记录接口 超过可筛选最长日期区间!!!");
    				TTUtil.formatReturn(resultJSON, 407, "多条件查找通话记录接口 超过可筛选最长日期区间!!!");
    				TTUtil.sendDataByIOStream(response, resultJSON);
    				return;
    			}
        	}
        	paramMap.put("startDate", startDate);
        	paramMap.put("endDate", endDate);
			if (StringUtils.isNoneBlank(param)) {
				paramMap.put("param", param);
			}
			if (StringUtils.isNotBlank(levels)) {
				List<String> levelList = TransportUtil.stringTransportList(levels);
				if (levelList.size() > 0) {
					paramMap.put("levels", levelList);
				}
			}
        	ids = callRecordService.selectAllIDsByMultiple(paramMap);
        }else {
        	ids = TransportUtil.stringTransportListForId(idList);
        }
		
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userID", id);
        // 查看是否是特殊用户
        int flag = userService.selectUserFlagStatusByUserId(map);
        HSSFWorkbook wb = new HSSFWorkbook();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日-HH时mm分ss秒");
        Date date = new Date();
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        date = ca.getTime();
        String fileName = sdf.format(date);
        HSSFSheet sheet = wb.createSheet("通话记录");
        HSSFRow row;
        excel(wb, sheet,flag);

        if (null == ids || ids.size() < 1) {
            lg.info("查询记录为空!!!");
            TTUtil.formatReturn(resultJSON, 405, "查询记录为空!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
       
        if (ids.size() > 5000) {
            lg.info("导出通话记录 数据量超过5000!!!");
            TTUtil.formatReturn(resultJSON, 406, "导出通话记录 数据量超过5000!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        map.put("ids", ids);
        // 根据ID的集合,返回通话记录的List,写入Excel文件
        List<Map<String, Object>> list = callRecordService.selectCallRecordListByIdListForExcel(map);
        if (list.size() < 1) {
            lg.info("没有找到通话记录,不做导出操作!!!");
            TTUtil.formatReturn(resultJSON, 405, "没有找到通话记录,不做导出操作!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        List<Map<String,Object>> visitList = visitService.selectVisitGradeByUserId(map);
        JSONObject visitJSON = new JSONObject();
        if (visitList.size() > 0) {
            for (Map<String, Object> map2 : visitList) {
                visitJSON.put(String.valueOf(map2.get("customerId")), map2.get("visitGrade"));
            }
        }
        String customerGrade;
        for (Map<String, Object> map2 : list) {
            Integer customerId = (Integer) map2.get("customerId");
            if (null != visitJSON.getInteger(String.valueOf(customerId))) {
                map2.put("visitGrade", TTUtil.changeGrade(visitJSON.getInteger(String.valueOf(customerId))));
            } else {
                map2.put("visitGrade", "");
            }
            if (null != map2.get("grade")) {
                customerGrade = TTUtil.changeGrade((Integer) map2.get("grade"));
            } else {
                customerGrade = "";
            }
            map2.put("account", (map2.get("account") == null ? "" : map2.get("account")));
            if (flag == 1 && null != map2.get("callrecordId")) {
                Map<String, Object> tmpMap = new HashMap<>();
                tmpMap.put("CallRecordID", map2.get("callrecordId"));
                List<CallDetail> detailMapList = callDetailService.selectCallRecordDetailByCallRecordID(tmpMap);
                if (null != detailMapList && detailMapList.size() > 0) {
                    StringBuffer detail = new StringBuffer();
                    for (CallDetail callDetail : detailMapList) {
                        if ("开场白".equals(callDetail.getFileWord())) {
                            continue;
                        }
                        detail.append(callDetail.getFileWord()).append("\n").append(callDetail.getRecordWord());
                    }
                    map2.put("detail", detail);
                }
            }
            map2.put("datetime", (map2.get("datetime") == null ? "" : ((String)map2.get("datetime"))));
//			map2.put("exportCount", (map2.get("exportCount") == null ? 0 : map2.get("exportCount")));
            map2.put("durationTime", (map2.get("durationTime") == null ? 0 : map2.get("durationTime")));
            map2.put("fileID", (map2.get("fileID") == null ? "" : map2.get("fileID")));
            map2.put("customerGrade", customerGrade);
            map2.put("customerNote", (map2.get("customerNote") == null ? "" : map2.get("customerNote")));
            map2.put("customerId", (map2.get("customerId") == null ? "" : map2.get("customerId")));
            map2.put("customerName", (map2.get("customerName") == null ? "" : map2.get("customerName")));
            map2.put("customerPhone", (map2.get("customerPhone") == null ? "" : map2.get("customerPhone")));
            map2.put("customerCompany", (map2.get("customerCompany") == null ? "" : map2.get("customerCompany")));
        }
        Set<String> levelSet = new TreeSet<String>();
        int minDurat = 0;
        Set<Integer> duratSet = new TreeSet<Integer>();
        Set<String> projectSet = new HashSet<String>();
        String startDateTime = null;
        String endDateTime = null;
        SimpleDateFormat tempSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HSSFCellStyle style = wb.createCellStyle();
        HSSFCellStyle style2 = wb.createCellStyle();
        HSSFDataFormat format = wb.createDataFormat();
        style2.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> mmm = list.get(i);
            row = sheet.createRow((int) i + 1);
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(String.valueOf(i + 1));
            String customerName = (String) mmm.get("customerName");
            String customerNote = (String) mmm.get("customerNote");
            String planName = (String) mmm.get("planName");
            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue(planName);
            cell = row.createCell(2);
            cell.setCellStyle(style);
            cell.setCellValue((String) mmm.get("account"));
            cell = row.createCell(3);
            cell.setCellStyle(style);
            cell.setCellValue(customerName);
            cell = row.createCell(4);
            cell.setCellStyle(style);
            cell.setCellValue(String.valueOf(mmm.get("customerPhone")));
            cell = row.createCell(5);
            cell.setCellStyle(style);
            cell.setCellValue((String) mmm.get("customerGrade"));
            String tempDBDateTime = (String) mmm.get("datetime");
            String subDateTimeStr = tempDBDateTime;
            if (StringUtils.isNotBlank(subDateTimeStr)) {
                subDateTimeStr = subDateTimeStr.substring(0, 19);
            }
            if (StringUtils.isAnyBlank(startDateTime)) {
                startDateTime = tempDBDateTime;
            } else {
                Date dbDate = null;
                Date tempDate = null;
                try {
                    dbDate = tempSDF.parse(tempDBDateTime);
                    tempDate = tempSDF.parse(startDateTime);
                } catch (ParseException e) {
                    lg.info("日期格式转换异常!!!");
                }
                if (dbDate.before(tempDate)) {
                    startDateTime = tempDBDateTime;
                }
            }
            if (StringUtils.isAnyBlank(endDateTime)) {
                endDateTime = tempDBDateTime;
            } else {
                Date dbDate = null;
                Date tempDate = null;
                try {
                    dbDate = tempSDF.parse(tempDBDateTime);
                    tempDate = tempSDF.parse(endDateTime);
                } catch (ParseException e) {
                    lg.info("日期格式转换异常!!!");
                }
                if (dbDate.after(tempDate)) {
                    endDateTime = tempDBDateTime;
                }
            }
            Integer durationTime = (Integer) mmm.get("durationTime");
            cell = row.createCell(6);
            cell.setCellStyle(style);
            cell.setCellValue(String.valueOf(mmm.get("visitGrade")));
            if (minDurat == 0 && durationTime > 0) {
                minDurat = durationTime;
            }
            if (durationTime < minDurat && 0 != durationTime) {
                minDurat = durationTime;
            }
            duratSet.add(durationTime);
            String projectName = (String) mmm.get("projectName");
            cell = row.createCell(7);
            cell.setCellStyle(style2);
            Date date2 = null;
            try {
                date2 = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(subDateTimeStr);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            cell.setCellValue(date2);
            projectSet.add(projectName);
            cell = row.createCell(10);
            cell.setCellStyle(style);
            cell.setCellValue((Integer) mmm.get("exportCount"));
            String customerCompany = (String) mmm.get("customerCompany");
            levelSet.add((String) mmm.get("customerGrade"));
            cell = row.createCell(8);
            cell.setCellStyle(style);
            cell.setCellValue(durationTime);
            cell = row.createCell(9);
            cell.setCellStyle(style);
            cell.setCellValue((String) mmm.get("projectName"));
            cell = row.createCell(10);
            cell.setCellStyle(style);
            cell.setCellValue(customerCompany);
            cell = row.createCell(11);
            cell.setCellStyle(style);
            cell.setCellValue(customerNote);
//			cell = row.createCell(12);
//			cell.setCellStyle(style);
//			cell.setCellValue((Integer) mmm.get("exportCount"));
            cell = row.createCell(12);
            cell.setCellStyle(style);
            cell.setCellValue((String) mmm.get("fileID"));
            if (flag == 1 && null != mmm.get("detail")) {
                cell = row.createCell(13);
                cell.setCellStyle(style);
                cell.setCellValue( mmm.get("detail").toString());
            }
        }
        // 根据通话记录ID的集合,对符合要求的记录导出次数 +1
//		Integer count = callRecordService.incrExportCountByIDList(map);
//		if (null != count && count > 0) {
//			lg.info("对通话记录导出次数 +1 成功!!!");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        String dateInterval = String.format("%s~%s", startDateTime, endDateTime);
        map.put("projects", projectSet.toString());
        map.put("minDuart", minDurat);
        map.put("durats", duratSet.toString());
        map.put("levels", levelSet.toString());
        map.put("dateInterval", dateInterval);
        map.put("content", "");
        map.put("count", list.size());
        map.put("time", time);
        // 插入导出通话记录的详情到服务器,方便查询通话记录导出
        Integer count1 = callRecordService.insertExportHistoryContent(map);
        if (null != count1 && count1 > 0) {
            lg.info("写入通话记录导出历史的信息到服务器 成功!!!");
        } else {
            lg.info("写入通话记录导出历史的信息到服务器 失败!!!");
            TTUtil.formatReturn(resultJSON, 401, "写入通话记录导出历史的信息到服务器 失败!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
//		} else {
//			lg.info("对通话记录导出次数加1 失败!!!");
//			TTUtil.formatReturn(resultJSON, 402, "对通话记录导出次数加1 失败!!!");
//			TTUtil.sendDataByIOStream(response, resultJSON);
//			return;
//		}
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
            lg.info("将虚拟表格写入到字节数组输出流 成功!!!");
        } catch (IOException e1) {
            lg.info("将虚拟表格写入到字节数组输出流 异常!!!");
            e1.printStackTrace();
            TTUtil.formatReturn(resultJSON, 401, "将虚拟表格写入到字节数组输出流 异常!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        ServletOutputStream out = null;
        try {
            String userAgent = request.getHeader("user-agent");
            if (userAgent != null && userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0
                    || userAgent.indexOf("Safari") >= 0) {
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + new String((fileName + ".xls").getBytes(), "UTF8"));
            }
            response.addHeader("Access-Control-Allow-Origin", "*");
            out = response.getOutputStream();
            lg.info("将文件数据放到servlet响应回传浏览器 成功");
        } catch (Exception e1) {
            lg.info("将文件数据放到servlet响应回传浏览器 异常");
            e1.printStackTrace();
            TTUtil.formatReturn(resultJSON, 400, "将文件数据放到servlet响应回传浏览器 异常");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            lg.info("字节流循环读取 成功!!!");
        } catch (Exception e) {
            lg.info("字节流循环读取 异常!!!");
            e.printStackTrace();
            TTUtil.formatReturn(resultJSON, 400, "字节流循环读取 异常!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                    lg.info("字符输入流关闭 成功!!!");
                } catch (IOException e) {
                    lg.info("字符输入流关闭 失败!!!");
                    e.printStackTrace();
                    TTUtil.formatReturn(resultJSON, 400, "字符输入流关闭 失败!!!");
                    TTUtil.sendDataByIOStream(response, resultJSON);
                    return;
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                    lg.info("字符输出流关闭 成功!!!");
                } catch (IOException e) {
                    lg.info("字符输出流关闭 失败!!!");
                    e.printStackTrace();
                    TTUtil.formatReturn(resultJSON, 400, "字符输出流关闭 失败!!!");
                    TTUtil.sendDataByIOStream(response, resultJSON);
                    return;
                }
            }
            lg.info("通话记录导出Excel成功!!!");
        }
    }

	@RequestMapping(value = "/multiple", method = RequestMethod.POST)
	@ResponseBody // 多条件搜索通话记录
	public void multiple(HttpServletRequest request, HttpServletResponse response, Integer startSecond,
			Integer endSecond, String param, String startDate, String endDate, String levels, Integer userID,
			Integer page, Integer per) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (null == userID || null == page || null == per) {
			lg.info("多条件查找通话记录接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "多条件查找通话记录接口传递参数错误!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		page = (page - 1) * per;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("per", per);
		map.put("page", page);
		map.put("userID", userID);
		if (null != startSecond && null != endSecond && endSecond < startSecond) {
			lg.info("多条件查找通话记录 传递通话时长参数 接口错误!!!");
			TTUtil.formatReturn(resultJSON, 403, "多条件查找通话记录接口 传递通话时长参数 格式错误!!!!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		if (null != startSecond && null != endSecond && endSecond > startSecond) {
			map.put("startSecond", startSecond);
			map.put("endSecond", endSecond);
		}
		if (StringUtils.isNoneBlank(startDate, endDate)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date bt = null;
			Date et = null;
			try {
				bt = sdf.parse(startDate);
				et = sdf.parse(endDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(et);
				cal.add(Calendar.DATE, 1);
				et = cal.getTime();
				endDate = sdf.format(et);
			} catch (ParseException e) {
				lg.info("多条件查找通话记录接口传递日期格式错误!!!");
				TTUtil.formatReturn(resultJSON, 401, "多条件查找通话记录接口传递日期格式错误!!!!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			if (et.before(bt)) {
				lg.info("多条件查找通话记录接口结束日期在开始日期之前!!!");
				TTUtil.formatReturn(resultJSON, 402, "多条件查找通话记录接口结束日期在开始日期之前!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			List<String> calList = TTUtil.calendarFormat(startDate, endDate);
			if (calList.size() > 31) {
				lg.info("多条件查找通话记录接口 超过可筛选最长日期区间!!!");
				TTUtil.formatReturn(resultJSON, 407, "多条件查找通话记录接口 超过可筛选最长日期区间!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			map.put("startDate", startDate);
			map.put("endDate", endDate);
		}
		if (StringUtils.isNoneBlank(param)) {
			map.put("param", param);
		}
		if (StringUtils.isNotBlank(levels)) {
			List<String> levelList = TransportUtil.stringTransportList(levels);
			if (levelList.size() > 0) {
				map.put("levels", levelList);
			}
		}
		// 根据多变条件,查询符合要求记录条数
		Integer count = callRecordService.selectRecordCountByMultiple(map);
		JSONObject temp = new JSONObject();
		if (null != count) {
			List<Integer> idList = new ArrayList<Integer>();
			if (count == 0) {
				lg.info("查询记录为空!!!");
				temp.put("count", 0);
				temp.put("list", idList);
				resultJSON.put("data", temp);
				TTUtil.formatReturn(resultJSON, 405, "查询记录为空!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			if (count <= page) {
				lg.info("此接口传递页码有问题!!!");
				TTUtil.formatReturn(resultJSON, 406, "此接口传递页码有问题!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			// 根据多变条件,查询符合要求记录的集合
//			List<Integer> ids = callRecordService.selectAllIDsByMultiple(map);
//			for (CallRecord callRecord : ids) {
//				Integer tempID = callRecord.getId();
//				idList.add(tempID);
//			}
			List<CallRecord> list = callRecordService.selectListByMultiple(map);
			for (CallRecord callRecord : list) {
				if (StringUtils.isAnyBlank(callRecord.getCustomerName())) {
					callRecord.setCustomerCompany("");
					callRecord.setCustomerName("");
					callRecord.setCustomerNote("");
				}
				if (StringUtils.isAnyBlank(callRecord.getPlanName())) {
					callRecord.setPlanName("");
				}
				if (null != callRecord.getIsTransfer() && 2 == callRecord.getIsTransfer()) {
					callRecord.setStatus(4);
				}
			}
//			temp.put("idList", idList);
			temp.put("count", count);
			temp.put("list", list);
			resultJSON.put("data", temp);
			lg.info("多条件查找通话记录接口 成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "多条件查找通话记录接口 成功!!!");
		} else {
			lg.info("多条件查找通话记录接口 失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "多条件查找通话记录接口 失败!!!!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
	@ResponseBody //
	public void batchDelete(HttpServletRequest request, HttpServletResponse response, String callRecordIDs, User user) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (null == callRecordIDs || user.getId() < 1) {
			lg.info("批量删除通话记录接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "批量删除通话记录接口传递参数错误!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		int userID = user.getId();
		List<String> IDs = TTUtil.string2list(callRecordIDs);
		if (null == IDs || IDs.size() < 1) {
			lg.info("批量删除通话记录接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "参数错误");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		List<Integer> msgIdList = new ArrayList<Integer>();
		for (String msgid : IDs) {
			msgid = msgid.replace("\"", "");
			Integer intMsgID = Integer.valueOf(msgid);
			msgIdList.add(intMsgID);
		}
		Integer temp = IDs.size();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", msgIdList);
		map.put("userID", userID);
		// 根据UserID和通话记录集合,批量删除通话记录
		Integer count = callRecordService.batchDeleteCallRecord(map);
		if (null != count && count > 0) {
			lg.info("批量删除通话记录成功!!!");
			TTUtil.formatReturn(resultJSON, 0, TTUtil.appendString(temp, "条数据删除成功!!!"));
		} else {
			lg.info("批量删除通话记录失败!!!");
			TTUtil.formatReturn(resultJSON, 1, TTUtil.appendString(temp, "条数据删除失败!!!"));
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public void list(HttpServletRequest request, HttpServletResponse response, User user, Integer page, Integer per) {
		JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
		if (user.getId() < 1 || null == page || null == per) {
			lg.info("查看通话记录列表接口传递参数错误!!!");
			TTUtil.formatReturn(resultJSON, 404, "查看通话记录列表接口传递参数错误!!!");
			TTUtil.sendDataByIOStream(response, resultJSON);
			return;
		}
		int id = user.getId();
		page = (page - 1) * per;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		// 根据用户的ID,返回用户名下有多少条通话记录
		Integer count = callRecordService.selectCallRecordCountByUserID(user);
		if (null != count) {
			map.put("page", page);
			map.put("per", per);
			if (count == 0) {
				lg.info("查询记录为空!!!");
				TTUtil.formatReturn(resultJSON, 405, "查询记录为空!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			if (count <= page) {
				lg.info("此接口传递页码有问题!!!");
				TTUtil.formatReturn(resultJSON, 406, "此接口传递页码有问题!!!");
				TTUtil.sendDataByIOStream(response, resultJSON);
				return;
			}
			// 根据用户的ID,查看用户名下通话记录列表详情
			List<CallRecord> list = callRecordService.selectCallRecordListByUserID(map);
			for (CallRecord callRecord : list) {
				if (StringUtils.isAnyBlank(callRecord.getCustomerName())) {
					callRecord.setCustomerCompany("");
					callRecord.setCustomerName("");
					callRecord.setCustomerNote("");
				}
				if (StringUtils.isAnyBlank(callRecord.getPlanName())) {
					// callRecord.setPlanName("<信息已删除!>");
					callRecord.setPlanName("");
				}
				if (null != callRecord.getIsTransfer() && 2 == callRecord.getIsTransfer()) {
					callRecord.setStatus(4);
				}
			}
			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			for (CallRecord callRecord : list) {
				Object json = JSON.toJSON(callRecord);
				jsonList.add((JSONObject) json);
			}
			JSONObject temp = new JSONObject();
			temp.put("count", count);
			temp.put("list", jsonList);
			resultJSON.put("data", temp);
			lg.info("通话记录列表查看成功!!!");
			TTUtil.formatReturn(resultJSON, 0, "通话记录列表查看成功!!!");
		} else {
			lg.info("通话记录列表查看失败!!!");
			TTUtil.formatReturn(resultJSON, 1, "通话记录列表查看失败!!!");
		}
		TTUtil.sendDataByIOStream(response, resultJSON);
		return;
	}

	 private void excel(HSSFWorkbook wb, HSSFSheet sheet, int flag) {
	        HSSFRow row = sheet.createRow((int) 0);
	        HSSFFont font = wb.createFont();
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        HSSFCellStyle style = wb.createCellStyle();
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	        style.setFont(font);
	        HSSFCell cell = row.createCell(0);
	        cell.setCellValue("序号");
	        sheet.setColumnWidth(0, (int) (23 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(1);
	        cell.setCellValue("计划名称");
	        sheet.setColumnWidth(1, (int) (40 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(2);
	        cell.setCellValue("计划创建者");
	        sheet.setColumnWidth(2, (int) (50 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(3);
	        cell.setCellValue("客户姓名");
	        sheet.setColumnWidth(3, (int) (50 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(4);
	        cell.setCellValue("客户电话");
	        sheet.setColumnWidth(4, (int) (45 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(5);
	        cell.setCellValue("系统评级");
	        sheet.setColumnWidth(5, (int) (40 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(6);
	        cell.setCellValue("意向评级");
	        sheet.setColumnWidth(6, (int) (40 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(7);
	        cell.setCellValue("通话具体时间");
	        sheet.setColumnWidth(7, (int) (70 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(8);
	        cell.setCellValue("通话时长(s)");
	        sheet.setColumnWidth(8, (int) (50 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(9);
	        cell.setCellValue("所使用的项目模板");
	        sheet.setColumnWidth(9, (int) (50 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(10);
	        cell.setCellValue("客户所属公司");
	        sheet.setColumnWidth(10, (int) (80 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(11);
	        cell.setCellValue("客户备注");
	        sheet.setColumnWidth(11, (int) (80 * 100));
	        cell.setCellStyle(style);
	        cell = row.createCell(12);
	        cell.setCellValue("远程文件地址");
	        sheet.setColumnWidth(12, (int) (100 * 100));
	        cell.setCellStyle(style);
	        if (flag == 1) {
	            cell = row.createCell(13);
	            cell.setCellValue("对话详情");
	            sheet.setColumnWidth(13, (int) (100 * 100));
	            cell.setCellStyle(style);
	        }
	    }

	@RequestMapping(value = "/findLastCallRecordInfo", method = RequestMethod.POST)
	@ResponseBody
	public void findLastCallRecordInfo(HttpServletRequest request, HttpServletResponse response, int userId,
			int customerId) {
		response.addHeader("Access-Control-Allow-Origin", "*");

		Map<String, Object> resultMap = callRecordService.findLastCallRecordByCustomerID(customerId);

		if (resultMap != null) {
			resultMap.put("datetime",
					resultMap.get("datetime") == null ? "" : resultMap.get("datetime").toString().subSequence(0, 19));
			resultMap.put("durationTime", resultMap.get("durationTime") == null ? 0 : resultMap.get("durationTime"));
			resultMap.put("callSignal", resultMap.get("callSignal") == null ? 0 : resultMap.get("callSignal"));
			resultMap.put("fileID", resultMap.get("fileID") == null ? "" : resultMap.get("fileID"));
			resultMap.put("customerPhone", resultMap.get("customerPhone") == null ? 0 : resultMap.get("customerPhone"));
			resultMap.put("customerGrade", resultMap.get("customerGrade") == null ? 0 : resultMap.get("customerGrade"));
			resultMap.put("projectName", resultMap.get("projectName") == null ? "" : resultMap.get("projectName"));
			resultMap.put("phone", resultMap.get("phone") == null ? 0 : resultMap.get("phone"));
			resultMap.put("callRecordId", resultMap.get("callRecordId") == null ? 0 : resultMap.get("callRecordId"));
			lg.info(userId + " 查询了 " + customerId + " 的最后一次通话信息");
		} else {
			resultMap = new HashMap<String, Object>();
			resultMap.put("datetime", "暂无");
			resultMap.put("durationTime", 0);
			resultMap.put("callSignal", "暂无");
			resultMap.put("fileID", "暂无");
			resultMap.put("customerPhone", "暂无");
			resultMap.put("customerGrade", 0);
			resultMap.put("projectName", "暂无");
			resultMap.put("phone", "暂无");
			resultMap.put("callRecordId", -1);
			lg.info(customerId + " 没有通话记录");
		}
		TTUtil.sendDataByIOStream(response, (JSONObject) JSON.toJSON(resultMap));
	}

	@RequestMapping(value = "/findCallRecordByCallRecordId", method = RequestMethod.POST)
	@ResponseBody
	public void findCallRecordByCallRecordId(HttpServletRequest request, HttpServletResponse response, int userId,
			int callRecordId,int planId,int customerId) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
        Map<String, Object> map = TTUtil.getParamMap();
        map.put("callRecordId",callRecordId);
        map.put("planId",planId);
        map.put("customerId",customerId);
        Map<String, Object> callRecord = callRecordService.findCallRecordByCallRecordId(map);

		if (callRecord != null) {
			result = (JSONObject) JSON.toJSON(callRecord);
			result.put("datetime", callRecord.get("datetime").toString().substring(0, 19));
		} else {
			result.put("result", 1);
		}
		TTUtil.sendDataByIOStream(response, result);
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	@ResponseBody
	public void test(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
        
		List<CallRecord> call = new ArrayList<CallRecord>();
		CallRecord call1 = new CallRecord();
		call1.setId(976910);
		call1.setCustomerGrade(5);
		call1.setDurationTime(6);
		call1.setFileID("http://qiniu.91tmk.com1");
		call1.setStatus(2);
		
		CallRecord call2 = new CallRecord();
		call2.setId(976909);
		call2.setCustomerGrade(4);
		call2.setDurationTime(30);
		call2.setFileID("http://qiniu.91tmk.com2");
		call2.setStatus(2);
		
		call.add(call1);call.add(call2);
		
		callRecordService.updateCallRecorde(call);
	}
}
