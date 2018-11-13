package com.zxxkj.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zxxkj.service.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxkj.model.Gateway;
import com.zxxkj.model.Keyword;
import com.zxxkj.model.Port;
import com.zxxkj.model.Project;
import com.zxxkj.model.ProjectData;
import com.zxxkj.model.Record;
import com.zxxkj.model.RemoteGateway;
import com.zxxkj.model.SMS;
import com.zxxkj.model.User;
import com.zxxkj.remoteDao.RemoteGatewayDao;
import com.zxxkj.util.ExcelUtil;
import com.zxxkj.util.QiniuUtil;
import com.zxxkj.util.SortUtil;
import com.zxxkj.util.TTUtil;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private static final Logger LOGGER = Logger.getLogger(ProjectController.class);

    @Resource
    private IProjectService projectService;
    @Resource
    private IRecordService recordService;
    @Resource
    private IKeywordService keyWordService;
    @Resource
    private ProjectDataService projectdataService;
    @Resource
    private IPortService portService;
    @Resource
    private IGatewayService gatewayService;
    @Resource
    private IUserService userService;
    @Resource
    private RemoteGatewayDao remoteGatewayDao;
    @Resource
    private FinanceService financeService;

    public static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
    public static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    public static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
    private static final String suffix = ".mp3";


    // 修复开始

    @RequestMapping(value = "/projectGrade", method = RequestMethod.POST)
    @ResponseBody // 测试方法: 获取项目中哪些用户级别已经使用
    public void projectGrade(HttpServletRequest request, HttpServletResponse response, Integer projectId, Integer userId) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (null == projectId || null == userId) {
            LOGGER.info("必选参数为空!!!");
            TTUtil.formatReturn(resultJSON, 404, "必选参数为空!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> paramMap = TTUtil.getParamMap();
        paramMap.put("projectId", projectId);
        paramMap.put("userId", userId);
        List<SMS> tempGradeList = projectService.selectProjectSelectedGrade(paramMap);
        if (null == tempGradeList) {
            LOGGER.info("这个模板没有设置过标签!!!");
            resultJSON.put("grades", new HashSet<String>());
            TTUtil.formatReturn(resultJSON, 0, "这个模板没有设置过标签!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Set<String> tempReturnGradeSet = new HashSet<String>();
        Set<String> returnGradeSet = new HashSet<String>();
        for (SMS sms : tempGradeList) {
            String tempGradeStr = sms.getGrade();
            List<String> gradeList = TTUtil.string2list(tempGradeStr);
            if (null != gradeList && gradeList.size() > 0) {
                tempReturnGradeSet.addAll(gradeList);
            }
        }
        for (String string : tempReturnGradeSet) {
            string = string.replace("\"", "");
            returnGradeSet.add(string);
        }
        resultJSON.put("grades", returnGradeSet);
        LOGGER.info("这个模板设置过标签！！！");
        TTUtil.formatReturn(resultJSON, 1, "这个模板设置过如下标签！！！");
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/allName", method = RequestMethod.POST)
    @ResponseBody // 测试方法: 简单获取项目名
    public void allName(HttpServletRequest request, HttpServletResponse response, Integer userId) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (null == userId) {
            LOGGER.info("必选参数为空!!!");
            TTUtil.formatReturn(resultJSON, 404, "必选参数为空!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> paramMap = TTUtil.getParamMap();
        paramMap.put("userId", userId);
        List<Project> projects = projectService.selectAllProjectNameByUserID(paramMap);
        if (null != projects) {
            LOGGER.info("获取用户的所有项目信息成功！！！");
            resultJSON.put("list", projects);
            TTUtil.formatReturn(resultJSON, 0, "成功");
        } else {
            LOGGER.info("获取用户的所有项目信息失败！！！");
            resultJSON.put("list", new ArrayList<Project>());
            TTUtil.formatReturn(resultJSON, 1, "失败");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/fixedData", method = RequestMethod.POST)
    @ResponseBody // 固定的特殊问题库初始化及编辑
    public void fixedData(HttpServletRequest request, HttpServletResponse response, ProjectData projectData) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(projectData.getProjectID(), projectData.getNamed(), projectData.getRole(),
                projectData.getLevel()) || 4 != projectData.getRole() || 0 != projectData.getLevel()) {
            LOGGER.info("初始化及编辑固定的特殊问题项目数据 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "初始化及编辑固定的特殊问题项目数据 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ProjectData", projectData);
        int exist = projectdataService.selectFixedDataExist(map);
        if (0 == exist) {
            // 数据库中没有这个固定数据，采取插入的方式
            if (TTUtil.isAnyNull(projectData.getContent1(), projectData.getFileID1())) {
                LOGGER.info("插入 固定的特殊问题项目数据 数据错误!!!");
                TTUtil.formatReturn(resultJSON, 403, "插入 固定的特殊问题项目数据 数据错误!!!");
                TTUtil.sendDataByIOStream(response, resultJSON);
                return;
            }
            Integer effect = projectdataService.insertProjectDataByPOJO(map);
            if (null != effect && effect > 0) {
                // 数据插入成功，返回项目数据的ID给前端
                LOGGER.info("插入 固定的特殊问题项目数据 成功!!!");
                resultJSON.put("data", projectData.getId());
                TTUtil.formatReturn(resultJSON, 0, "插入 固定的特殊问题项目数据 成功!!!");
            } else {
                // 数据插入失败，请重试
                LOGGER.info("插入 固定的特殊问题项目数据 失败!!!");
                TTUtil.formatReturn(resultJSON, 1, "插入 固定的特殊问题项目数据 失败!!!");
            }
        } else {
            // 数据库中存在这个固定数据，采取更新的方式
            if (TTUtil.isAnyNull(projectData.getContent1(), projectData.getFileID1())) {
                LOGGER.info("插入 固定的特殊问题项目数据 数据错误!!!");
                TTUtil.formatReturn(resultJSON, 403, "插入 固定的特殊问题项目数据 数据错误!!!");
                TTUtil.sendDataByIOStream(response, resultJSON);
                return;
            }
            int effect = projectdataService.updateProjectDataByProjectIDAndNamedAndRole(map);
            if (effect > 0) {
                LOGGER.info("对固定的特殊问题项目数据 修改成功!!!");
                TTUtil.formatReturn(resultJSON, 0, "对固定的特殊问题项目数据 修改成功!!!");
            } else {
                LOGGER.info("对固定的特殊问题项目数据 修改失败!!!");
                TTUtil.formatReturn(resultJSON, 1, "对固定的特殊问题项目数据 修改失败!!!");
            }
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/deleteAll", method = RequestMethod.POST)
    @ResponseBody // 在删除项目时调用的接口,对项目数据做假删除操作
    public void deleteAll(HttpServletRequest request, HttpServletResponse response, ProjectData projectData) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(projectData.getProjectID())) {
            LOGGER.info("删除整个项目数据 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "删除整个项目数据 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ProjectData", projectData);
        Integer effectCount = projectdataService.deleteAllProjectDataByProjectDataID(map);
        if (null != effectCount && effectCount > 0) {
            LOGGER.info("删除整个项目数据  成功!!!");
            TTUtil.formatReturn(resultJSON, 0, "删除整个项目数据  成功!!!");
        } else {
            LOGGER.info("删除整个项目数据   失败!!!");
            TTUtil.formatReturn(resultJSON, 1, "删除整个项目数据  失败!!!");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/deleteData", method = RequestMethod.POST)
    @ResponseBody // 根据项目ID和数据ID,真正删除项目数据
    public void deleteData(HttpServletRequest request, HttpServletResponse response, ProjectData projectData) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(projectData.getProjectID(), projectData.getId())) {
            LOGGER.info("删除项目数据 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "删除项目数据 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        // List<String> ids = TTUtil.string2list(idList);
        // if (null == ids || ids.size() < 1) {
        // LOGGER.info("删除项目数据 参数错误!!!");
        // TTUtil.formatReturn(resultJSON, 404, "删除项目数据 参数错误!!!");
        // TTUtil.sendDataByIOStream(response, resultJSON);
        // return;
        // }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ProjectData", projectData);
        // map.put("ids", ids);
        Integer effectCount = projectdataService.deleteProjectDataByProjectDataID(map);
        if (null != effectCount && effectCount > 0) {
            LOGGER.info("删除项目数据  成功!!!");
            TTUtil.formatReturn(resultJSON, 0, "删除项目数据  成功!!!");
        } else {
            LOGGER.info("删除项目数据   失败!!!");
            TTUtil.formatReturn(resultJSON, 1, "删除项目数据  失败!!!");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/updateData", method = RequestMethod.POST)
    @ResponseBody // 根据项目ID和数据ID,来修改数据的详情
    public void updateData(HttpServletRequest request, HttpServletResponse response, ProjectData projectData) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(projectData.getId(), projectData.getProjectID())) {
            LOGGER.info("更新项目数据 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "更新项目数据 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        projectData.setDatetime(new Date());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ProjectData", projectData);
        Integer effectCount = projectdataService.updateProjectDataByProjectDataID(map);
        if (null != effectCount && effectCount > 0) {
            LOGGER.info("项目数据修改 成功!!!");
            TTUtil.formatReturn(resultJSON, 0, "项目数据修改 成功!!!");
        } else {
            LOGGER.info("项目数据修改  失败!!!");
            TTUtil.formatReturn(resultJSON, 1, "项目数据修改 失败!!!");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/roleData", method = RequestMethod.POST)
    @ResponseBody // 根据库名及项目ID,查找所有数据
    public void roleData(HttpServletRequest request, HttpServletResponse response, ProjectData projectData,
                         Integer role) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(projectData.getProjectID(), role)) {
            LOGGER.info("项目数据列表 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "项目数据列表 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Integer projectID = projectData.getProjectID();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("position", role);
        map.put("ProjectID", projectID);
        List<ProjectData> projectDataList = projectdataService.selectPositionProjectDataListByProjectID(map);
        if (projectDataList.size() > 0) {
            if (3 == role) {
                for (ProjectData POJO : projectDataList) {
                    JSONArray array = new JSONArray();
                    String fileID1 = POJO.getFileID1();
                    String fileID2 = POJO.getFileID2();
                    String fileID3 = POJO.getFileID3();
                    String content1 = POJO.getContent1();
                    String content2 = POJO.getContent2();
                    String content3 = POJO.getContent3();
                    JSONObject temp1 = new JSONObject();
                    temp1.put("fileID", fileID1);
                    temp1.put("content", content1);
                    array.add(temp1);
                    if (StringUtils.isNotBlank(fileID2) && StringUtils.isNotBlank(content2)) {
                        JSONObject temp2 = new JSONObject();
                        temp2.put("fileID", fileID2);
                        temp2.put("content", content2);
                        array.add(temp2);
                    }
                    if (StringUtils.isNotBlank(fileID3) && StringUtils.isNotBlank(content3)) {
                        JSONObject temp3 = new JSONObject();
                        temp3.put("fileID", fileID3);
                        temp3.put("content", content3);
                        array.add(temp3);
                    }
                    POJO.setBackup(array.toString());
                }
            }
            resultJSON.put("data", projectDataList);
            TTUtil.formatReturn(resultJSON, 0, "数据查询成功!!!");
        } else {
            TTUtil.formatReturn(resultJSON, 1, "数据查询失败!!!");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/shotData", method = RequestMethod.POST)
    @ResponseBody // 在项目中添加项目数据(命名/关键字/录音文件/录音内容)
    public void shotData(HttpServletRequest request, HttpServletResponse response, ProjectData projectData) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(projectData.getId())) {
            LOGGER.info("项目数据列表 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "项目数据列表 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Integer id = projectData.getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        ProjectData projectPOJO = projectdataService.shotProjectDataByProjectID(map);
        if (null != projectPOJO) {
            if (3 == projectPOJO.getRole()) {
                JSONArray array = new JSONArray();
                String fileID1 = projectPOJO.getFileID1();
                String fileID2 = projectPOJO.getFileID2();
                String fileID3 = projectPOJO.getFileID3();
                String content1 = projectPOJO.getContent1();
                String content2 = projectPOJO.getContent2();
                String content3 = projectPOJO.getContent3();
                JSONObject temp1 = new JSONObject();
                temp1.put("fileID", fileID1);
                temp1.put("content", content1);
                array.add(temp1);
                if (StringUtils.isNotBlank(fileID2) && StringUtils.isNotBlank(content2)) {
                    JSONObject temp2 = new JSONObject();
                    temp2.put("fileID", fileID2);
                    temp2.put("content", content2);
                    array.add(temp2);
                }
                if (StringUtils.isNotBlank(fileID3) && StringUtils.isNotBlank(content3)) {
                    JSONObject temp3 = new JSONObject();
                    temp3.put("fileID", fileID2);
                    temp3.put("content", content2);
                    array.add(temp3);
                }
                projectPOJO.setBackup(array.toString());
            }
            resultJSON.put("data", projectPOJO);
            TTUtil.formatReturn(resultJSON, 0, "数据查询成功!!!");
        } else {
            TTUtil.formatReturn(resultJSON, 1, "数据查询失败!!!");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/dataList", method = RequestMethod.POST)
    @ResponseBody // 在项目中添加项目数据(命名/关键字/录音文件/录音内容)
    public void dataList(HttpServletRequest request, HttpServletResponse response, ProjectData projectData) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(projectData.getProjectID())) {
            LOGGER.info("项目数据列表 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "项目数据列表 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Integer projectID = projectData.getProjectID();
        JSONObject temp = new JSONObject();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ProjectID", projectID);
        List<ProjectData> projectDataList = projectdataService.selectProjectDataListByProjectID(map);
        List<ProjectData> data1 = new ArrayList<ProjectData>();
        List<ProjectData> data2 = new ArrayList<ProjectData>();
        List<ProjectData> data3 = new ArrayList<ProjectData>();
        for (ProjectData POJO : projectDataList) {
            switch (POJO.getRole()) {
                case 2:
                    data2.add(POJO);
                    break;
                case 3:
                    data3.add(POJO);
                    break;
                default:
                    data1.add(POJO);
                    break;
            }
        }
        temp.put("data1", data1);
        temp.put("data2", data2);
        temp.put("data3", data3);
        resultJSON.put("data", temp);
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/insertData", method = RequestMethod.POST)
    @ResponseBody // 在项目中添加项目数据(命名/关键字/录音文件/录音内容)
    public void insertData(HttpServletRequest request, HttpServletResponse response, ProjectData projectData) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);

        projectData.setDatetime(new Date());
        if (TTUtil.isAnyNull(projectData.getNamed(), projectData.getRole(), projectData.getKeyword(),
                projectData.getDatetime(), projectData.getContent1(), projectData.getProjectID(),
                projectData.getFileID1(), projectData.getLevel())) {
            LOGGER.info("在项目中添加项目数据 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "在项目中添加项目数据 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        if (1 == projectData.getRole() && 0 == projectData.getLevel()) {
            LOGGER.info("在项目中添加项目数据 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "在项目中添加项目数据 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ProjectData", projectData);
        Integer count = projectdataService.selectNodeCountByPOJO(map);
        if (null != count && 1 == projectData.getRole() && count > 9) {
            LOGGER.info("在项目中添加项目数据时,主流程已经达到最大值!!!");
            TTUtil.formatReturn(resultJSON, 403, "在项目中添加项目数据时,主流程已经达到最大值!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        } else if (null != count && 3 == projectData.getRole() && count > 19) {
            LOGGER.info("在项目中添加项目数据时,特殊流程已经达到最大值!!!");
            TTUtil.formatReturn(resultJSON, 402, "在项目中添加项目数据时,特殊流程已经达到最大值!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Integer effectCount = projectdataService.insertProjectDataByPOJO(map);
        if (null != effectCount && effectCount > 0) {
            resultJSON.put("id", projectData.getId());
            LOGGER.info("项目数据插入成功!!!");
            TTUtil.formatReturn(resultJSON, 0, "插入成功!!!");
        } else {
            LOGGER.info("项目数据插入失败!!!");
            TTUtil.formatReturn(resultJSON, 1, "插入失败!!!");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    @RequestMapping(value = "/batchInsertData", method = RequestMethod.POST)
    @ResponseBody // 在项目中添加项目数据(命名/关键字/录音文件/录音内容)
    public void batchInsertData(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        int projectId = 0;
        int adminId = 0;
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
            return;
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
            List<FileItem> removeItems = new ArrayList<FileItem>();
            if (formItems != null && formItems.size() > 0) {
                FileItem excelItem = null;

                // 迭代表单数据
                for (FileItem item : formItems) {
                    if (item.isFormField()) {
                        // 参数名
                        String fieldName = item.getFieldName();
                        // 参数值
                        if (fieldName.equals("adminId")) {
                            adminId = Integer.parseInt(item.getString("UTF-8"));
                        } else if (fieldName.equals("projectId")) {
                            projectId = Integer.parseInt(item.getString("UTF-8"));
                        }
                        removeItems.add(item);
                    }

                    // 处理不在表单中的字段
                    if (!item.isFormField()) {
                        if (ExcelUtil.validateExcel(item.getName())) {// 验证文件名是否合格
                            excelItem = item;
                            removeItems.add(item);
                            break;
                        }

                    }
                }

                boolean flag = formItems.removeAll(removeItems);
                if (flag) {
                    LOGGER.info("formItems删除成功 ");
                }
                if (excelItem == null) {

                    TTUtil.sendJSONDataByIOStream(response, 412, "无excel或excel文件为空", result);
                    return;
                }
                if (formItems.size() == 0) {

                    TTUtil.sendJSONDataByIOStream(response, 412, "只有excel，没有话术", result);
                    return;
                }
                InputStream input = new ByteArrayInputStream(excelItem.get());
                boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
                if (ExcelUtil.isExcel2007(excelItem.getName())) {
                    isExcel2003 = false;
                }

                List<ProjectData> projectDataList = readExcelValue(input, isExcel2003, adminId, projectId, formItems);

                if (projectDataList.size() == formItems.size()) {
                    Integer insertCount = projectdataService.batchInsertProjectData(projectDataList);
                    if (insertCount > 0) {
                        LOGGER.info("添加了" + insertCount + " 条话术");
                        TTUtil.sendJSONDataByIOStream(response, 200, "导入成功", result);
                        return;
                    }
                } else {
                    LOGGER.info("请检查excel文件中的文件名和mp3文件名，或数量是否对应");
                    TTUtil.sendJSONDataByIOStream(response, 412, "请检查excel文件中的文件名和mp3文件名，或数量是否对应", result);
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getMessage());
        }
    }

    // 修复结束

    @RequestMapping(value = "/deleteProject", method = RequestMethod.POST)
    @ResponseBody
    public String deleteProject(HttpServletRequest request, HttpServletResponse response, int adminId, int projectId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        int deleteProjectCount = projectService.deleteProject(projectId);
        int deleteRecordCount = recordService.deleteRecordByProjectId(projectId);
        int deleteKeywordCount = keyWordService.deleteKeywordByProjectId(projectId);
        if (deleteProjectCount != 0 || deleteRecordCount != 0 || deleteKeywordCount != 0) {
            result.put("result", 0);
            LOGGER.info(adminId + " 删除了id为 " + projectId + " 的项目,以及其对应的 " + deleteRecordCount + " 条录音和 "
                    + deleteKeywordCount + "个关键字");
            return result.toJSONString();
        }
        result.put("result", 1);
        return result.toJSONString();
    }

    @RequestMapping(value = "/findRecordAndKeywordByProjectId", method = RequestMethod.POST)
    @ResponseBody
    public void findRecordByProjectId(HttpServletRequest request, HttpServletResponse response, Record record) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONArray resultArr = new JSONArray();
        int projectId = record.getProjectId();

        List<Record> recordList = recordService.findRecordByProjectId(projectId);
        for (Record DBrecord : recordList) {
            JSONObject result = new JSONObject();
            JSONArray keyWordArr = new JSONArray();
            int recordId = DBrecord.getId();
            List<Keyword> keywordList = keyWordService.findRecordByProjectId(recordId);
            for (Keyword keyword : keywordList) {
                keyWordArr.add(keyword.getKeyword());
            }
            result.put("id", recordId);
            result.put("voice", DBrecord.getUrl());
            result.put("kws", keyWordArr);
            resultArr.add(result);
        }

        TTUtil.sendJSONArrDataByIOStream(response, resultArr);
    }

    @RequestMapping(value = "/findProjectByUserId", method = RequestMethod.POST)
    @ResponseBody
    public void findProjectByUserId(HttpServletRequest request, HttpServletResponse response, int userId, int adminId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject resultJSON = new JSONObject();
        JSONArray projectArr = new JSONArray();
        JSONArray gatewayAndPortArr = new JSONArray();

        List<Project> projectList = projectService.findProjectByUserId(userId);
        for (Project project : projectList) {
            JSONObject projectJSON = new JSONObject();
            JSONArray voicedArr = new JSONArray();
            int projectId = project.getId();
            List<Record> recordList = recordService.findRecordByProjectId(projectId);
            for (Record DBrecord : recordList) {
                JSONObject result = new JSONObject();
                JSONArray keyWordArr = new JSONArray();
                int recordId = DBrecord.getId();
                List<Keyword> keywordList = keyWordService.findRecordByProjectId(recordId);
                for (Keyword keyword : keywordList) {
                    keyWordArr.add(keyword.getKeyword());
                }
                result.put("id", recordId);
                result.put("voice", DBrecord.getUrl());
                result.put("kws", keyWordArr);
                voicedArr.add(result);
            }
            projectJSON.put("id", projectId);
            projectJSON.put("projectName", project.getProjectName());
            projectJSON.put("voiceAndKw", voicedArr);
            projectJSON.put("switchStatus", project.getSwitchStatus());
            projectArr.add(projectJSON);
            LOGGER.info("id 为" + projectId + " 的项目有 " + projectArr.size() + " 条录音及关键字");
        }
        LOGGER.info("id为 " + userId + " 的用户查询了已拥有的 " + projectList.size() + " 个项目");

        List<Gateway> gatewayList = gatewayService.findGatewayListByUserId(userId);
        for (Gateway gateway : gatewayList) {

            JSONObject json = new JSONObject();
            Integer gatewayId = gateway.getId();
            json.put("gatewayNumbers", gateway.getGatewayNumbers());
            json.put("url", gateway.getUrl());
            json.put("auth", gateway.getAuth());
            json.put("pwd", gateway.getPwd());
            json.put("gatewayId", gatewayId);
            //剩余话费直减扣除方式
            if (gateway.getType() == 2) {
                Map<String, Object> map = new HashMap();
                map.put("user_id", userId);
                map.put("sip_id", gatewayId);
                //消费总记录
                Double xf = 0.000;
                xf = financeService.selectUserCostTotalData(map);
                if (null == xf) {
                    xf = 0.000;
                } else {
                    xf.toString();
                }
                //充值总记录
                Map<String, Object> cz = financeService.seleceUserTotalMoney(map);
                String chongzhi = "";
                if (cz == null) {
                    chongzhi = "0.000";
                } else {
                    if (null == cz.get("totalMoney")) {
                        chongzhi = "0.000";
                    } else {
                        chongzhi = cz.get("totalMoney").toString();
                    }
                }
                BigDecimal bxf = new BigDecimal(xf).setScale(3, BigDecimal.ROUND_HALF_UP);
                BigDecimal bcz = new BigDecimal(chongzhi).setScale(3, BigDecimal.ROUND_HALF_UP);
                BigDecimal balanceMoney = bcz.subtract(bxf).setScale(3, BigDecimal.ROUND_DOWN);
                Map<String, Object> map2 = new HashMap();
                map2.put("new_sip_balance", balanceMoney);
                map2.put("userId", userId);
                map2.put("sipId", gatewayId);
                financeService.updateUserSipDataByRecordId(map2);
                json.put("balanceMoney", balanceMoney);
//            json.put("balanceMoney", gateway.getBalanceMoney()==null?0:gateway.getBalanceMoney());
                json.put("unitPrice", gateway.getUnitPrice() == null ? 0 : gateway.getUnitPrice());
                json.put("leftover", gateway.getLeftover() == null ? 0 : gateway.getLeftover());
            }
            Integer porttype=0;
            List<Port> portList = portService.findPortListByUserId(userId, gateway.getId(),porttype);
            JSONArray callPortList = new JSONArray();
            JSONArray transferPortList = new JSONArray();
            for (Port port : portList) {
                JSONObject portJSON = new JSONObject();
                portJSON.put("port", port.getPort());
                portJSON.put("userId", String.valueOf(userId));
                if (port.getType() == 1) {
                    callPortList.add(portJSON);
                } else {
                    transferPortList.add(portJSON);
                }
            }
            JSONArray portOnArr = new JSONArray();
            RemoteGateway remoteGateway = remoteGatewayDao.findRemoteGatewayInfoByAdminIdAndGatewayNode(adminId, gateway.getGatewayNumbers());
            if (remoteGateway == null) {
                LOGGER.info("该代理商远程没有该网关权限");
                break;
            }
            Integer type = remoteGateway.getType();
            json.put("gatewayType", type);
            if (type == 1) {
                String portOnStr = remoteGateway.getPortOn();
                String[] portOn = portOnStr.substring(1, portOnStr.length() - 1).split(",");
                List<Integer> portNoList = new ArrayList<Integer>();
                for (int i = 0; i < portOn.length; i++) {
                    portNoList.add(Integer.valueOf(portOn[i]));
                }

                List<Port> allPortList = portService.findPortListByGatewayId(gatewayId);
                List<Integer> ports = new ArrayList<Integer>();
                for (Port port : allPortList) {
                    JSONObject portJSON = new JSONObject();
                    int portNo = port.getPort();
                    if (portNoList.contains(portNo)) {
                        ports.add(portNo);
                    }
                    portJSON.put("userId", String.valueOf(port.getUserId()));
                    portJSON.put("port", portNo);
                    portOnArr.add(portJSON);
                }
                portNoList.removeAll(ports);
                for (Integer portNo : portNoList) {
                    JSONObject portJSON = new JSONObject();
                    portJSON.put("userId", "");
                    portJSON.put("port", portNo);
                    portOnArr.add(portJSON);
                }
                json.put("callPortList", callPortList);
                json.put("transferPortList", transferPortList);
                json.put("allPort", SortUtil.PortSort(portOnArr));
            } else if (type == 2) {
                List<Port> portlist = portService.findPortListByUserId(userId, gatewayId,porttype);
                Integer haveUsedCount = portService.findHaveUsedCountByGatewayId(gatewayId);
                json.put("callCount", portlist.get(0).getPort());
                json.put("total", Integer.valueOf(gateway.getPort_no()));
                json.put("haveUsedCount", Integer.valueOf(gateway.getPort_no()) - (haveUsedCount == null ? 0 : haveUsedCount));
            }
            gatewayAndPortArr.add(json);
        }

        User user = userService.findUserInfoByUserId(userId);
        resultJSON.put("projectList", projectArr);
        resultJSON.put("gatewayAndPortArr", gatewayAndPortArr);
        resultJSON.put("validTime", user.getValidTime().substring(0, 10));
        resultJSON.put("activeTime", user.getActiveTime().substring(0, 10));
        resultJSON.put("company", user.getCompany());
        resultJSON.put("contactPhone", (user.getContactPhone() == 0 ? "" : user.getContactPhone()));
        resultJSON.put("account", user.getAccount());
        resultJSON.put("contactPerson", (user.getContactPerson() == null ? "" : user.getContactPerson()));
        resultJSON.put("city", (user.getCity() == null ? "" : user.getCity()));

        TTUtil.sendDataByIOStream(response, resultJSON);
    }

    @RequestMapping(value = "/findProjectNameByUserId", method = RequestMethod.POST)
    @ResponseBody
    public void findProjectNameByUserId(HttpServletRequest request, HttpServletResponse response, int userId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONArray resultArr = new JSONArray();

        List<Project> projectList = projectService.findProjectByUserId(userId);
        for (Project project : projectList) {
            resultArr.add(JSON.toJSON(project));
        }
        TTUtil.sendJSONArrDataByIOStream(response, resultArr);
    }

    @RequestMapping(value = "/addProject", method = RequestMethod.POST)
    @ResponseBody // 在删除项目时调用的接口,对项目数据做假删除操作
    public void addProject(HttpServletRequest request, HttpServletResponse response, String projectName, int userId) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        int projectId = 0;
        Project DBproject = projectService.findProjectByUserIdAndprojectName(projectName, userId);
        if (DBproject == null) {
            Project project = new Project();
            project.setProjectName(projectName);
            project.setUserId(userId);
            projectService.addProject(project);
            projectId = project.getId();
            if (projectId < 1) {
                LOGGER.info("添加一个普通用户接口 添加项目失败!!!");
                TTUtil.formatReturn(resultJSON, 404, "添加一个普通用户接口 添加项目失败!!!");
                TTUtil.sendDataByIOStream(response, resultJSON);
                return;
            }
            //JSONObject temp = new JSONObject();
            //temp.put("userID", userId);
            //temp.put("projectID", projectId);
            LOGGER.info("新加项目成功， projectId为：" + projectId);
            resultJSON.put("projectID", projectId);
            TTUtil.formatReturn(resultJSON, 0, "添加用户并设置默认项目成功!");
            TTUtil.sendDataByIOStream(response, resultJSON);
        } else {
            projectId = DBproject.getId();
            LOGGER.info("该用户名下已经存在同名项目,项目ID为" + projectId);
            TTUtil.formatReturn(resultJSON, 403, "此用户已经存在同名项目!");
            TTUtil.sendDataByIOStream(response, resultJSON);
        }
    }

    /***
     * @Param: [request, response, switchstatus, userId]
     * @return: void
     * @Author: FuJacKing
     * @Description: 话术模版开关，一键关闭和开启
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public String updateStatus(HttpServletRequest request, HttpServletResponse response, Integer switchstatus, Integer userId) {
        JSONObject result = TTUtil.setDomainAndCreateReturn(response);
        Integer counts = 0;
        Project project = new Project();
        project.setUserId(userId);
        project.setSwitchStatus(switchstatus);
        counts = projectService.upDateSwitchStatus(project);
        result.put("result", 1);
        if (counts > 0) {
            result.put("result", 0);
        }
        return result.toJSONString();
    }

    /**
     * 读取Excel里面客户的信息
     *
     * @return
     */
    private List<ProjectData> readExcelValue(InputStream is, boolean isExcel2003, int adminId, int projectID,
                                             List<FileItem> formItems) {
        Integer dataCount = 0;
        Workbook wb = null;
        try {
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
        // 得到Excel的列数(前提是有行数)
        int totalCells = 0;
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        List<ProjectData> projectDataList = new ArrayList<ProjectData>();
        // 循环Excel行数
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            ProjectData projectData = new ProjectData();
            // 循环Excel的列
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if (null != cell) {
                    if (c == 0) {
                        // 如果是纯数字,比如你写的是25,cell.getNumericCellValue()获得是25.0,通过截取字符串去掉.0获得25
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            String named = String.valueOf(cell.getNumericCellValue());
                            projectData.setNamed(named.substring(0, named.length() - 2 > 0 ? named.length() - 2 : 1));
                        } else {
                            projectData.setNamed(cell.getStringCellValue());
                        }
                    } else if (c == 1) {
                        String fileID1 = null;
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            String fileName = String.valueOf(cell.getStringCellValue()) + suffix;
                            for (FileItem item : formItems) {
                                System.out.println(fileName + "------" + item.getName());
                                if ((fileName).equals(item.getName())) {
                                    fileID1 = QiniuUtil.uploadByte(item.get(), System.currentTimeMillis() + "$$" + fileName, "");
                                    break;
                                }
                            }

                        } else {
                            fileID1 = cell.getStringCellValue();
                        }
                        projectData.setFileID1(fileID1);
                    } else if (c == 2) {
                        String keyword = null;
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            keyword = String.valueOf(cell.getStringCellValue());
                        } else {
                            keyword = cell.getStringCellValue();
                        }
                        projectData.setKeyword(keyword.replaceAll("\\s{2,}", " "));
                    } else if (c == 3) {
                        String content1 = null;
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            content1 = String.valueOf(cell.getStringCellValue());
                            projectData.setContent1(content1);
                        } else {
                            content1 = cell.getStringCellValue();
                        }
                        projectData.setContent1(content1);
                    }
                    projectData.setProjectID(projectID);
                    projectData.setLevel(0);
                    projectData.setRole(2);
                    projectData.setDatetime(new Date());
                }
            }
            dataCount++;
            projectDataList.add(projectData);
        }
        return projectDataList;
    }
}
