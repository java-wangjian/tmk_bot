package com.zxxkj.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Customer;
import com.zxxkj.service.ICallRecordService;
import com.zxxkj.service.ICustomerAndPlanService;
import com.zxxkj.service.ICustomerService;
import com.zxxkj.service.IPlanService;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.ExcelUtil;
import com.zxxkj.util.ParameterProperties;
import com.zxxkj.util.TTUtil;
import com.zxxkj.util.TransportUtil;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private static final Logger LOGGER = Logger.getLogger(CustomerController.class);

	@Resource
	private ICustomerService customerService;
	@Resource
	private ICallRecordService callRecordService;
	@Resource
	private IPlanService planService;
	@Resource
	private ICustomerAndPlanService customerAndPlanService;

	public static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	public static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	public static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	public static boolean isExcel2003(String filePath) {
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
			if (POIFSFileSystem.hasPOIFSHeader(bis)) {
				LOGGER.info(2003);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 依据内容判断是否为excel2007及以上
	 */
	public static boolean isExcel2007(String filePath) {
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
			if (POIXMLDocument.hasOOXMLHeader(bis)) {
				LOGGER.info(2007);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@RequestMapping(value = "/batchInsert", method = RequestMethod.POST)
	@ResponseBody
	public String importCustomer(HttpServletRequest request, HttpServletResponse response, Customer customer) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		int userId = 0;
		if (!ServletFileUpload.isMultipartContent(request)) {
			// 如果不是则停止
			PrintWriter writer = null;
			try {
				writer = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writer.println("Error: 表单必须包含 enctype=multipart/form-data");
			writer.flush();
			return null;
		}
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
		try {
			// 解析请求的内容提取文件数据
			List<FileItem> formItems = upload.parseRequest(request);
			if (formItems != null && formItems.size() > 0) {
				// 迭代表单数据
				for (FileItem item : formItems) {
					if (item.isFormField()) {
						// 参数名
						@SuppressWarnings("unused")
						String fieldName = item.getFieldName();
						// 参数值
						userId = Integer.parseInt(item.getString("UTF-8"));
					}

					// 处理不在表单中的字段
					if (!item.isFormField()) {
						if (!ExcelUtil.validateExcel(item.getName())) {// 验证文件名是否合格
							result.put("result", 1);// 上传文件不是excel格式
							LOGGER.info(userId + " 上传文件的格式不正确 ");
						}
						boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
						if (ExcelUtil.isExcel2007(item.getName())) {
							isExcel2003 = false;
						}
						InputStream input = new ByteArrayInputStream(item.get());

						List<String> customerPhoneList = new ArrayList<String>();
						String batchNo = System.currentTimeMillis() + RandomStringUtils.randomNumeric(5);
						Integer maxImportCount = ParameterProperties.getIntegerValue(ConstantUtil.SETTING_FILEPATH, "importExcelCount");
						JSONObject resultExcel = readExcelValue(input, isExcel2003, userId, customerPhoneList, batchNo, maxImportCount);
                        int total = resultExcel.getInteger("count");
                        if(total > 60000) {
                        	
                        	result.put("result", 4);//超过导入数量限制
                        	result.put("maxImportCount", maxImportCount);
                        	return result.toJSONString();
                        }
                        List<Customer> customerList = JSONObject.parseArray(resultExcel.getJSONArray("list").toString(), Customer.class);
                        Integer insertCount = 0;
                        int size = customerList.size();
                        if (size > 0) {
                        	int count = size/500;
                        	if((size % 500) != 0) {
                        		count = count + 1;
                        	}
                        	for (int i = 0; i < count; i++) {
                        		List<Customer> insertCustomerList = new ArrayList<Customer>();
                        		for (int j = 0; j < 500; j++) {
                        			if(customerList.size() > 0) {
                        				insertCustomerList.add(customerList.get(0));
                        				customerList.remove(0);
                        			}
                        		}
//                        		customerList.removeAll(insertCustomerList);
                        		Integer addCount = customerService.addCustomers(insertCustomerList);
                        		if(addCount == null) {
                        			result.put("result", 3);// 数据不正确，请检查数据
                        			break;
                        		}
                        		insertCustomerList.clear();
                        		insertCount = insertCount + addCount;
							}
                            if (insertCount == size) {
                                result.put("result", 0);// 导入成功
                            }
                        } else {
                            result.put("result", 2);
                            LOGGER.info("该文件已导入过");
                        }
						result.put("total", total);
						result.put("haveCount", total - insertCount);
						result.put("insertCount", insertCount);
						result.put("batchNo", batchNo);
                        LOGGER.info(String.format("Excel中有%d条数据，成功导入%d条数据，有%d条重复或错误", total,insertCount,total - insertCount));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getMessage());
        }
		return result.toJSONString();
	}

	@RequestMapping(value = "/addCustomer", method = RequestMethod.POST)
	@ResponseBody
	public String addCustomer(HttpServletRequest request, HttpServletResponse response, Customer customer) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		Integer DBcustomerCount = customerService.findCustomerByUserIdAndPhone(customer);
		if (DBcustomerCount == 0) {
			int count = customerService.addCustomer(customer);
			if (count > 0) {
				result.put("code", 0);
				result.put("result", customer.getId());
				LOGGER.info(customer.getUserId() + " 添加了单个用户 ，用户id是: " + customer.getId());
			}
			return result.toJSONString();
		}
		LOGGER.info(customer.getUserId() + " 添加的用户已存在 ");
		result.put("code", 1);
		return result.toJSONString();
	}

	@RequestMapping(value = "/findCustomer", method = RequestMethod.POST)
	@ResponseBody
	public void findCustomer(HttpServletRequest request, HttpServletResponse response, int userId,
			String customerPhone, String startTimeStr, String endTimeStr, int curPage) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		String customerName = null;

		JSONArray customerArr = new JSONArray();
		Date startTime = null;
		Date endTime = null;
		int count = Integer.parseInt(ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "siHaiPerPagecount"));
		int start = (curPage - 1) * count;

		try {
			if (!StringUtils.isBlank(startTimeStr)) {
				startTime = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(startTimeStr);
			}
			if (!StringUtils.isBlank(endTimeStr)) {
				endTime = ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(endTimeStr);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (!customerPhone.matches("[0-9]+")) {
			customerName = customerPhone;
			customerPhone = "0";
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("customerPhone", customerPhone);
		param.put("customerName", customerName);
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		param.put("start", start);
		param.put("count", count);
		param.put("userId", userId);
		List<Map<String, Object>> restltMapList = customerService.findCustomerByPhoneOrDateTimeRangOrGradeMap(param);
		
		int total = customerService.findCountByUserId(param);
		
		for (Map<String, Object> map : restltMapList) {
			map.put("addTime", map.get("addTime").toString().substring(0, 19));
			map.put("customerName", (map.get("customerName") == null ? "" : map.get("customerName").toString()));
			customerArr.add(JSON.toJSON(map));
		}
		LOGGER.info(userId + " 查询了所有客户列表");
		result.put("customerList", customerArr);
		result.put("total", total);
		TTUtil.sendDataByIOStream(response, result);
	}

	/**
	 * 批量删除客户
	 * 
	 * @param request
	 * @param response
	 * @param idStr
	 * @return
	 */
	@RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
	@ResponseBody
	public String batchDeleteUser(HttpServletRequest request, HttpServletResponse response, int userId, String idStr) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		List<Integer> idStrList = TransportUtil.stringTransportListForId(idStr);
		result = deleteCustomerByIdList(idStrList, userId);

		return result.toJSONString();
	}

	private void excel1(XSSFWorkbook wb, XSSFSheet sheet) {
		XSSFRow row = sheet.createRow((int) 0);
		XSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFont(font);
		XSSFCell cell = row.createCell(0);
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
		sheet.setColumnWidth(4, (int) (50 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(5);
		cell.setCellValue("系统评级");
		sheet.setColumnWidth(5, (int) (50 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(6);
		cell.setCellValue("意向评级");
		sheet.setColumnWidth(6, (int) (50 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(7);
		cell.setCellValue("通话具体时间");
		sheet.setColumnWidth(7, (int) (80 * 100));
		cell.setCellStyle(style);
		cell = row.createCell(8);
		cell.setCellValue("通话持续时长(s)");
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
//		cell = row.createCell(12);
//		cell.setCellValue("导出次数");
//		sheet.setColumnWidth(12, (int) (50 * 100));
//		cell.setCellStyle(style);
		cell = row.createCell(12);
		cell.setCellValue("远程文件地址");
		sheet.setColumnWidth(12, (int) (100 * 100));
		cell.setCellStyle(style);
	}

	@RequestMapping(value = "/exportExcel", method = RequestMethod.GET)
    @ResponseBody
    public void exportExcel(HttpServletRequest request, HttpServletResponse response, int planId, int userId,
                            String planName, String projectName, String gradeListStr, String duringTimeRang, String startTimeStr,
                            String endTimeStr, String searchPhone) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=GBK");
        JSONArray JSONArr = new JSONArray();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String account = planService.selectPlanCreateAccountByPlanID(planId);
        if (StringUtils.isAnyBlank(account)) {
            account = "";
        }
        if (StringUtils.isNotBlank(searchPhone)) {
            paramMap.put("cutomerPhone", searchPhone);
        }
        List<Integer> gradeList = TransportUtil.stringTransportListForId(gradeListStr);
        if (gradeList.size() == 0) {
            gradeList = null;
        }
        Integer startDuringTime = null;
        Integer endDuringTime = null;

        if (duringTimeRang.contains("-")) {
            String[] duringTimeArr = duringTimeRang.split("-");
            startDuringTime = Integer.parseInt(duringTimeArr[0]);
            endDuringTime = Integer.parseInt(duringTimeArr[1]);
        } else {
            if (!("").equals(duringTimeRang)) {
                int time = Integer.parseInt(duringTimeRang);
                if (time <= 10) {
                    endDuringTime = time;
                } else if (time >= 30) {
                    startDuringTime = time;
                }
            }
        }
        
        Date startDate = null;
        Date endDate = null;
        try {
			startDate = (("").equals(startTimeStr) ? null
					: ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(startTimeStr + " 00:00:00"));
			endDate = (("").equals(endTimeStr) ? null
					: ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(endTimeStr + " 23:59:59"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        List<Map<String, Object>> customerList = null;
        paramMap.put("planId", planId);
        paramMap.put("userId", userId);
        paramMap.put("gradeList", gradeList);
        paramMap.put("startDuringTime", startDuringTime);
        paramMap.put("endDuringTime", endDuringTime);
        paramMap.put("startTimeStr", startDate);
        paramMap.put("endTimeStr", endDate);
        customerList = customerAndPlanService.findCustomerListByPlanIdByExcel(paramMap);
        JSONObject visitGradeJSON = new JSONObject();
        List<Map<String, Object>> visitGradeMap = customerAndPlanService.findCustomerLastVisitGrade(paramMap);
        for (Map<String, Object> map : visitGradeMap) {
            visitGradeJSON.put(String.valueOf(map.get("customerId")), map.get("visitGrade"));
        }
        for (Map<String, Object> map : customerList) {
            String customerGrade = "";
            String visitGrade = "";
            if (visitGradeJSON.containsKey(String.valueOf(map.get("customerId")))) {
                Integer tempVisitGrade = visitGradeJSON.getInteger(String.valueOf(map.get("customerId")));
                visitGrade = TTUtil.changeGrade(tempVisitGrade);
            }
            if (null != map.get("grade")) {
                customerGrade = TTUtil.changeGrade((Integer) map.get("grade"));
            } else {
                customerGrade = "";
            }
            map.put("visitGrade", visitGrade);
            map.put("account", (map.get("account") == null ? "" : map.get("account")));
            map.put("callrecordId", (map.get("callrecordId") == null ? "" : map.get("callrecordId")));
            map.put("datetime", (map.get("datetime") == null ? "" : map.get("datetime")));
            map.put("exportCount", (map.get("exportCount") == null ? 0 : map.get("exportCount")));
            map.put("durationTime", (map.get("durationTime") == null ? 0 : map.get("durationTime")));
            map.put("fileID", (map.get("fileID") == null ? "" : map.get("fileID")));
            map.put("customerGrade", customerGrade);
            map.put("customerNote", (map.get("customerNote") == null ? "" : map.get("customerNote")));
            map.put("customerId", (map.get("customerId") == null ? "" : map.get("customerId")));
            map.put("customerName", (map.get("customerName") == null ? "" : map.get("customerName")));
            map.put("customerPhone", (map.get("customerPhone") == null ? "" : map.get("customerPhone")));
            map.put("customerCompany", (map.get("customerCompany") == null ? "" : map.get("customerCompany")));
            JSONArr.add(JSON.toJSON(map));
        }
        
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(projectName);
        XSSFRow row;
        excel1(wb, sheet);
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        for (int i = 0; i < JSONArr.size(); i++) {
            JSONObject temp = JSONArr.getJSONObject(i);
            row = sheet.createRow((int) i + 1);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(String.valueOf(i + 1));
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue(planName);
            cell = row.createCell(2);
            cell.setCellStyle(style);
            cell.setCellValue(account);
            cell = row.createCell(3);
            cell.setCellStyle(style);
            cell.setCellValue(temp.getString("customerName"));
            cell = row.createCell(4);
            cell.setCellStyle(style);
            cell.setCellValue(temp.getString("customerPhone"));
            cell = row.createCell(5);
            cell.setCellStyle(style);
            cell.setCellValue(temp.getString("customerGrade"));
            cell = row.createCell(6);
            cell.setCellStyle(style);
            cell.setCellValue(temp.getString("visitGrade"));
            cell = row.createCell(7);
            cell.setCellStyle(style);
            cell.setCellValue(temp.getString("datetime").replace(".0", ""));
            cell = row.createCell(8);
            cell.setCellStyle(style);
            if (-1 != temp.getInteger("durationTime")) {
                cell.setCellValue(temp.getInteger("durationTime"));
            }
            cell = row.createCell(9);
            cell.setCellStyle(style);
            cell.setCellValue(projectName);
            cell = row.createCell(10);
            cell.setCellStyle(style);
            cell.setCellValue(temp.getString("customerCompany"));
            cell = row.createCell(11);
            cell.setCellStyle(style);
            cell.setCellValue(temp.getString("customerNote"));
            cell = row.createCell(12);
            cell.setCellStyle(style);
            cell.setCellValue(temp.getString("fileID"));
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
            LOGGER.info("将虚拟表格写入到字节数组输出流 成功!!!");
        } catch (IOException e1) {
            LOGGER.info("将虚拟表格写入到字节数组输出流 异常!!!");
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
                        "attachment;filename=" + new String((ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(new Date()) + ".xls").getBytes(), "iso-8859-1"));
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + new String((ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.format(new Date()) + ".xls").getBytes(), "UTF8"));
            }
            response.addHeader("Access-Control-Allow-Origin", "*");
            out = response.getOutputStream();
            LOGGER.info("将文件数据放到servlet响应回传浏览器 成功");
        } catch (Exception e1) {
            LOGGER.info("将文件数据放到servlet响应回传浏览器 异常");
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
            byte[] buff = new byte[512];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            LOGGER.info("字节流循环读取 成功!!!");
        } catch (Exception e) {
            LOGGER.info("字节流循环读取 异常!!!");
            e.printStackTrace();
            TTUtil.formatReturn(resultJSON, 400, "字节流循环读取 异常!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                    LOGGER.info("字符输入流关闭 成功!!!");
                } catch (IOException e) {
                    LOGGER.info("字符输入流关闭 失败!!!");
                    e.printStackTrace();
                    TTUtil.formatReturn(resultJSON, 400, "字符输入流关闭 失败!!!");
                    TTUtil.sendDataByIOStream(response, resultJSON);
                    return;
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                    LOGGER.info("字符输出流关闭 成功!!!");
                } catch (IOException e) {
                    LOGGER.info("字符输出流关闭 失败!!!");
                    e.printStackTrace();
                    TTUtil.formatReturn(resultJSON, 400, "字符输出流关闭 失败!!!");
                    TTUtil.sendDataByIOStream(response, resultJSON);
                    return;
                }
            }
            LOGGER.info("通话记录导出Excel成功!!!");
        }
        return;

    }

	@RequestMapping(value = "/findCustomerByPlanId", method = RequestMethod.POST)
	@ResponseBody
	public void findCustomerByPlanId(HttpServletRequest request, HttpServletResponse response, int planId, int userId,
			int planStatus, String gradeListStr, String duringTimeRang, String startTimeStr, String endTimeStr,
			String searchPhone, int curPage) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();
		JSONArray JSONArr = new JSONArray();
		int pageCount = Integer.parseInt(ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "normalCount"));
		int start = (curPage - 1) * pageCount;
		Long cutomerPhone = null;
		Date startDate = null;
		Date endDate = null;
		if (!("").equals(searchPhone)) {
			cutomerPhone = Long.parseLong(searchPhone);
		}
		List<Integer> gradeList = TransportUtil.stringTransportListForId(gradeListStr);
		if (gradeList.size() == 0) {
			gradeList = null;
		}
		Integer startDuringTime = null;
		Integer endDuringTime = null;
		if (duringTimeRang.contains("-")) {
			String[] duringTimeArr = duringTimeRang.split("-");
			startDuringTime = Integer.parseInt(duringTimeArr[0]);
			endDuringTime = Integer.parseInt(duringTimeArr[1]);
		} else {
			if (!("").equals(duringTimeRang)) {
				int time = Integer.parseInt(duringTimeRang);
				if (time <= 10) {
					endDuringTime = time - 1;
				} else if (time >= 30) {
					startDuringTime = time + 1;
				}
			}
		}
		try {
			startDate = (("").equals(startTimeStr) ? null
					: ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(startTimeStr + " 00:00:00"));
			endDate = (("").equals(endTimeStr) ? null
					: ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(endTimeStr + " 23:59:59"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("start", start);
		paramMap.put("pageCount", pageCount);
		paramMap.put("cutomerPhone", (cutomerPhone == null ? cutomerPhone : String.valueOf(cutomerPhone)));

		List<Map<String, Object>> customerList = null;
		int total = 0;
		List<Integer> customerIds = new ArrayList<Integer>();

		paramMap.put("planId", planId);
		paramMap.put("userId", userId);
		paramMap.put("gradeList", gradeList);
		paramMap.put("startDuringTime", startDuringTime);
		paramMap.put("endDuringTime", endDuringTime);
		paramMap.put("startTimeStr", startDate);
		paramMap.put("endTimeStr", endDate);
		customerList = customerAndPlanService.findCustomerListByPlanId(paramMap);
		if(StringUtils.isAllBlank(duringTimeRang, startTimeStr, endTimeStr, searchPhone) && ("[]").equals(gradeListStr)) {
			total = planService.findPlanInfoByPlanId(planId).getCustomerCount();
		}else if(StringUtils.isAllBlank(duringTimeRang, startTimeStr, endTimeStr, searchPhone) && !("[]").equals(gradeListStr)) {
			total = callRecordService.findCountByGradeList(gradeList, planId);
		}else {
			total = customerAndPlanService.findCustomerListSizeByPlanId(paramMap);
		}
		for (Map<String, Object> map : customerList) {
			int customerId = (map.get("customerId") == null ? 0 : (int) map.get("customerId"));
			customerIds.add(customerId);
			map.put("customerId", customerId);
			map.put("customerPhone", (map.get("customerPhone") == null ? "" : map.get("customerPhone")));
			map.put("customerName", (map.get("customerName") == null ? "" : map.get("customerName")));
			map.put("fileID", (map.get("fileID") == null ? "" : map.get("fileID")));
			map.put("grade", (map.get("grade") == null ? "" : map.get("grade")));
			map.put("callStatus", (map.get("status") == null ? 0 : map.get("status")));
			map.put("isTransfer", (map.get("isTransfer") == null ? "" : map.get("isTransfer")));// 1表示未转接；2表示已转接
			map.put("callrecordId", (map.get("callrecordId") == null ? "" : map.get("callrecordId")));
			map.put("customerCompany", (map.get("customerCompany") == null ? "" : map.get("customerCompany")));
			map.put("datetime", (map.get("datetime") == null ? "" : map.get("datetime").toString().subSequence(0, 19)));
			
			if (gradeList == null && ("").equals(duringTimeRang) && ("").equals(startTimeStr) 
					&& cutomerPhone == null && ("").equals(endTimeStr)) {
				JSONArr.add(JSON.toJSON(map));
			}else if(cutomerPhone != null && gradeList == null && startDuringTime == null && endDuringTime == null && startDate == null && endDate == null){
				JSONArr.add(JSON.toJSON(map));
			}else {
				if(map.get("grade") != null && !("").equals(map.get("grade")) && !("").equals(map.get("customerPhone"))) {
					JSONArr.add(JSON.toJSON(map));
				}
			}
		}
		result.put("total", total);
		result.put("list", JSONArr);
		LOGGER.info("用户 " + userId + " 查询了计划 " + planId + " 下的客户列表");
		TTUtil.sendDataByIOStream(response, result);
	}

	@RequestMapping(value = "/manyDelete", method = RequestMethod.POST)
	@ResponseBody
	public String deleteManyCustomers(HttpServletRequest request, HttpServletResponse response, int userId, int count) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		List<Customer> customerList = customerService.findCustomerListByUserIdAndCount(userId, count);
		List<Integer> idStrList = new ArrayList<Integer>();
		for (Customer customer : customerList) {
			idStrList.add(customer.getId());
		}
		if (idStrList.size() > 0) {
			result = deleteCustomerByIdList(idStrList, userId);
			return result.toJSONString();
		}
		result.put("result", 2);
		return result.toJSONString();
	}

	private JSONObject deleteCustomerByIdList(List<Integer> idStrList, int userId) {
		JSONObject result = new JSONObject();

		int deleteCount = customerService.batchDeleteUser(idStrList, 1);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userID", userId);
		map.put("customerIDS", idStrList);
		// Integer deleteRecordeCount =
		// callRecordService.betchDeleteCallRecordListByUserIDAndCustomerIDS(map);
		if (deleteCount == idStrList.size() /* && deleteRecordeCount != null */) {
			result.put("result", 0);
			LOGGER.info(userId + " 删除了 " + deleteCount + " 条数据");
			return result;
		}
		result.put("result", 1);
		return result;
	}

	/**
	 * 读取Excel里面客户的信息
	 * 
	 * @return
	 */
	private JSONObject readExcelValue(InputStream is, boolean isExcel2003, int userId, List<String> customerPhoneList, String batchNo, Integer maxImportCount) {
	    Integer dataCount = 0;
	    JSONObject resultExcel = new JSONObject();
	    List<String> repeatPhoneList = new ArrayList<String>();
		Workbook wb = null;
		
		try {
			// wb = WorkbookFactory.create(is);
			if (POIFSFileSystem.hasPOIFSHeader(is)) {
				wb = new HSSFWorkbook(is);
			}
			if (POIXMLDocument.hasOOXMLHeader(is)) {
				wb = new XSSFWorkbook(OPCPackage.open(is));
			}
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
		}

		// 得到第一个shell
		Sheet sheet = wb.getSheetAt(0);
		// 得到Excel的行数
		int totalRows = sheet.getPhysicalNumberOfRows();
		if(totalRows > maxImportCount) {
			
			dataCount = totalRows;
		}else {
			// 得到Excel的列数(前提是有行数)
			int totalCells = 0;
			if (totalRows > 1 && sheet.getRow(0) != null) {
				totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
			}
			List<Customer> customerList = new ArrayList<Customer>();
			// 循环Excel行数
			for (int r = 1; r < totalRows; r++) {
				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				String phone = null;
				Customer customer = new Customer();
				// 循环Excel的列
				for (int c = 0; c < totalCells; c++) {
					Cell cell = row.getCell(c);
					if (null != cell) {
						if (c == 0) {
							// 如果是纯数字,比如你写的是25,cell.getNumericCellValue()获得是25.0,通过截取字符串去掉.0获得25
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								String name = String.valueOf(cell.getNumericCellValue());
								customer.setCompany(name.substring(0, name.length() - 2 > 0 ? name.length() - 2 : 1));
							} else {
								customer.setCompany(cell.getStringCellValue());
							}
						} else if (c == 1) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								String sex = String.valueOf(cell.getNumericCellValue());
								customer.setCustomerName(sex.substring(0, sex.length() - 2 > 0 ? sex.length() - 2 : 1));
							} else {
								customer.setCustomerName(cell.getStringCellValue());
							}
						} else if (c == 2) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								DecimalFormat df = new DecimalFormat("#");
								phone = df.format(cell.getNumericCellValue());
							} else {
								phone = StringUtils.trim(cell.getStringCellValue().replaceAll("[^0-9]", ""));
							}
							customer.setCustomerPhone(phone);
						} else if (c == 3) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								String note = String.valueOf(cell.getNumericCellValue());
								customer.setNote(note.substring(0, note.length() - 2 > 0 ? note.length() - 2 : 1));
							} else {
								customer.setNote(cell.getStringCellValue());
							}
						}
						customer.setUserId(userId);
					}
				}
				customer.setBatchNo(batchNo);
				customer.setAddTime(new Date());
				dataCount ++;
				if(StringUtils.isNotBlank(phone) && !repeatPhoneList.contains(phone) && phone.length() == 11 && (phone.startsWith("1") || phone.startsWith("0"))) {
					repeatPhoneList.add(phone);
					customerList.add(customer);
				}
			}
			resultExcel.put("list", customerList);
		}
        resultExcel.put("count", dataCount);
		return resultExcel;
	}

	/**
	 * 根据手机号增加其通话次数
	 *
	 * @param phone
	 * @return
	 */
	@RequestMapping(value = "/updateCallCountByPhone", method = RequestMethod.POST)
	@ResponseBody
	public String updateCallCountByPhone(HttpServletRequest request, HttpServletResponse response, long phone) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject result = new JSONObject();

		int updateCount = customerService.updateCallCountByPhone(phone);

		if (updateCount > 0) {
			result.put("result", 0);
			return result.toJSONString();
		}
		result.put("result", 1);
		return result.toJSONString();
	}
}
