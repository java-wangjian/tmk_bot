package com.zxxkj.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.util.Auth;
import com.zxxkj.model.Admin;
import com.zxxkj.model.Gateway;
import com.zxxkj.model.Keyword;
import com.zxxkj.model.Port;
import com.zxxkj.model.Project;
import com.zxxkj.model.Record;
import com.zxxkj.model.RemoteGateway;
import com.zxxkj.model.User;
import com.zxxkj.remoteDao.RemoteAdminDao;
import com.zxxkj.remoteDao.RemoteGatewayDao;
import com.zxxkj.service.IAdminService;
import com.zxxkj.service.IGatewayService;
import com.zxxkj.service.IKeywordService;
import com.zxxkj.service.IPortService;
import com.zxxkj.service.IProjectService;
import com.zxxkj.service.IRecordService;
import com.zxxkj.service.IUserService;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.ParameterProperties;
import com.zxxkj.util.SortUtil;
import com.zxxkj.util.TTUtil;
import com.zxxkj.util.TransportUtil;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger LOGGER = Logger.getLogger(AdminController.class);

    @Resource
    private IAdminService adminService;
    @Resource
    private IUserService userService;
    @Resource
    private IProjectService projectService;
    @Resource
    private IRecordService recordService;
    @Resource
    private IKeywordService keywordService;
    @Resource
    private IGatewayService gatewayService;
    @Resource
    private IPortService portService;
    @Resource
    private RemoteGatewayDao remoteGatewayDao;
    @Resource
    private RemoteAdminDao remoteAdminDao;

    /**
     * 添加后台管理员
     *
     * @param request
     * @param respons
     * @param content
     * @return
     */
    @RequestMapping(value = "/addAdmin", method = RequestMethod.POST)
    @ResponseBody
    public String addAdmin(HttpServletRequest request, HttpServletResponse response, Admin admin) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        adminService.addAdmin(admin);

        result.put("result", admin.getId());
        return Integer.toString(admin.getId());
    }

    @RequestMapping(value = "/addProject", method = RequestMethod.POST)
    @ResponseBody
    public void addProject(HttpServletRequest request, HttpServletResponse response, Project project) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (project.getUserId() < 1 || StringUtils.isAnyBlank(project.getProjectName())) {
            LOGGER.info("单独添加一个项目 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "单独添加一个项目 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        String projectName = project.getProjectName();
        int userId = project.getUserId();
        Project DBproject = projectService.findProjectByUserIdAndprojectName(projectName, userId);
        int projectId = 0;
        if (DBproject == null) {
            projectService.addProject(project);
            projectId = project.getId();
            if (projectId < 1) {
                LOGGER.info("添加项目失败!!!");
                TTUtil.formatReturn(resultJSON, 404, "添加项目失败!!!");
                TTUtil.sendDataByIOStream(response, resultJSON);
                return;
            }
            JSONObject temp = new JSONObject();
            temp.put("projectID", projectId);
            LOGGER.info("新加项目成功， projectId为：" + projectId);
            resultJSON.put("data", temp);
            TTUtil.formatReturn(resultJSON, 0, "添加项目成功!");
        } else {
            projectId = DBproject.getId();
            LOGGER.info("该用户名下已经存在同名项目,项目ID为" + projectId);
            TTUtil.formatReturn(resultJSON, 403, "此用户已经存在同名项目!");
        }
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    /**
     * 新加contactPerson,contactPhone,activeTime字段
     *
     * @param request
     * @param response
     * @param user
     * @param projectName
     * @param gateway
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ResponseBody
    public void addUser(HttpServletRequest request, HttpServletResponse response, User user,
                        String gatewayArrStr) {
        JSONObject resultJSON = TTUtil.setDomainAndCreateReturn(response);
        if (TTUtil.isAnyNull(user.getAccount(), user.getCompany(), user.getPassword(), user.getValidTime(), user.getAdminId(),
                user.getContactPerson(), user.getContactPhone(), user.getActiveTime(), user.getCity())) {
            LOGGER.info("添加一个普通用户接口 参数错误!!!");
            TTUtil.formatReturn(resultJSON, 404, "添加一个普通用户接口 参数错误!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        Integer adminId = user.getAdminId();
        Admin admin = remoteAdminDao.findAdminByAdminId(adminId);
        try {
            if ((ConstantUtil.YYYY_MM_DD_SDF.parse(user.getValidTime())).after(admin.getValidTime())) {
                TTUtil.formatReturn(resultJSON, 405, "用户的到期时间不能在代理商的到期时间晚");
                TTUtil.sendDataByIOStream(response, resultJSON);
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        User DBUser = userService.findUserByAccount(user.getAccount());
        int userId = 0;
        if (DBUser == null) {
            userService.addUser(user);
            userId = user.getId();
            if (userId < 1) {
                LOGGER.info("添加一个普通用户接口 添加用户失败!!!");
                TTUtil.formatReturn(resultJSON, 404, "添加一个普通用户接口 添加用户失败!!!");
                TTUtil.sendDataByIOStream(response, resultJSON);
                return;
            }
            LOGGER.info("新加用户成功,该userId为：" + userId);
        } else {
            userId = DBUser.getId();
            LOGGER.info("用户添加失败,用户名已经存在,该用户的ID为:" + userId);
            TTUtil.formatReturn(resultJSON, 402, "添加用户失败,用户账号已经存在!!!");
            TTUtil.sendDataByIOStream(response, resultJSON);
            return;
        }
        resultJSON.put("userId", userId);
        List<Port> portList = new ArrayList<Port>();
        JSONArray gatewayArr = JSON.parseArray(gatewayArrStr);
        for (int i = 0; i < gatewayArr.size(); i++) {
            JSONObject json = gatewayArr.getJSONObject(i);
            Integer gatewayType = json.getInteger("gatewayType");
            Integer gatewayId = json.getInteger("gatewayId");
            String gatewayURL = null;
            String auth = null;
            String pwd = null;
            if (gatewayType == 1) {
                gatewayURL = json.getString("gatewayURL");
                auth = json.getString("auth");
                pwd = json.getString("pwd");

                List<Integer> callPortList = (List<Integer>) json.get("callPortList");
                List<Integer> transferPortList = (List<Integer>) json.get("transferPortList");
                for (Integer integer : callPortList) {
                    Port port = new Port();
                    port.setPort(integer);
                    port.setGatewayId(gatewayId);
                    port.setType(1);
                    port.setUserId(userId);
                    portList.add(port);
                }
                for (Integer integer : transferPortList) {
                    Port port = new Port();
                    port.setPort(integer);
                    port.setGatewayId(gatewayId);
                    port.setType(2);
                    port.setUserId(userId);
                    portList.add(port);
                }
            } else if (gatewayType == 2) {
                Port port = new Port();
                port.setPort(json.getInteger("callCount"));
                port.setGatewayId(gatewayId);
                port.setType(1);
                port.setUserId(userId);
                portList.add(port);
            }
            Integer updateflag = gatewayService.updateGatewayByGatewayId(gatewayId, gatewayURL, auth, pwd);
            LOGGER.info("用户 [" + userId + "] 修改了 [" + updateflag + "] 个网关，id为 [ " + gatewayId + "]");
        }
        Integer batchAddCount = null;
        if (portList.size() > 0) {
            batchAddCount = portService.batchAddPort(portList);
        }
        LOGGER.info("为用户 [" + userId + "] 批量添加了 [" + batchAddCount + "] 个端口");
        TTUtil.sendDataByIOStream(response, resultJSON);
        return;
    }

    /**
     * 添加用户
     *
     * @param request
     * @param respons
     * @param account         用户账号
     * @param company         企业名称
     * @param password        用户密码
     * @param validTimeStr    账号到期时间
     * @param voiceAndkeyword 录音和关键字的json串
     * @param projectName     项目名
     * @return
     */
    @RequestMapping(value = "/addUser1", method = RequestMethod.POST)
    @ResponseBody
    public void addUser1(HttpServletRequest request, HttpServletResponse response, User user, String validTimeStr,
                         String voiceAndkeyword) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        int userId = 0;

        if (!StringUtils.isAnyBlank(user.getAccount(), user.getCompany(), user.getPassword(), validTimeStr,
                voiceAndkeyword)) {
            // 添加用户
            user.setValidTime(validTimeStr);
            User DBUser = userService.findUserByAccount(user.getAccount());
            if (DBUser == null) {
                userService.addUser(user);
                userId = user.getId();
                LOGGER.info("新加用户成功， userId为：" + userId);
            } else {
                userId = DBUser.getId();
                LOGGER.info("该用户已存在，查询其userId为：" + userId);
            }

            // 添加项目
            JSONArray voiceAndkeywordArr = (JSONArray) JSONArray.parse(voiceAndkeyword);
            result = addVoiceAndKW(voiceAndkeywordArr, userId);
			/*for (int i = 0; i < voiceAndkeywordArr.size(); i++) {
				JSONObject json = voiceAndkeywordArr.getJSONObject(i);
				String projectName = json.getString("projectName");
				JSONArray voiceAndKw = json.getJSONArray("voiceAndKw");
				int projectId = 0;
				
				Project DBproject = projectService.findProjectByUserIdAndprojectName(projectName, userId);
				if (DBproject == null) {
					Project project = new Project();
					project.setProjectName(projectName);
					project.setUserId(userId);
					projectService.addProject(project);
					projectId = project.getId();
					result.put("projectId", projectId);
					LOGGER.info("新加项目成功， projectId为：" + projectId);
				} else {
					projectId = DBproject.getId();
					LOGGER.info("该项目已存在，查询其projectId为：" + projectId);
				}
				projectIdList.add(projectId);
				
				for (int j = 0; j < voiceAndKw.size(); j++) {
					JSONObject SunJson = voiceAndKw.getJSONObject(j);
					List<String> list = (List<String>) SunJson.get("kws");

					Record record = new Record();
					List<Keyword> keywordList = new ArrayList<Keyword>();

					String voiceUrl = SunJson.getString("voice");
					record.setUrl(voiceUrl);
					record.setUserId(userId);
					record.setProjectId(projectId);
					recordService.addRecord(record);
					int recordId = record.getId();
					LOGGER.info("添加录音成功,recordId为：" + recordId);
					for (String kw : list) {
						Keyword keyword = new Keyword();
						keyword.setKeyword(kw);
						keyword.setProjectId(projectId);
						keyword.setUserId(userId);
						keyword.setRecordId(recordId);
						keywordList.add(keyword);
					}
					keywordService.addKeywords(keywordList);
					int keywordCount = keywordService.addKeywords(keywordList);
					LOGGER.info("添加" + recordId + "录音的关键字数量为：" + keywordCount);
					if (keywordCount < keywordList.size()) {
						LOGGER.info("添加" + recordId + "录音的关键字时出现错误");
					}
				}
			}
			

			result.put("userId", userId);
			result.put("projectIdList", projectIdList);*/
            // 为项目添加录音和关键字
            result.put("code", 0);
        } else {
            LOGGER.info("参数异常,此次请求的参数为: [" + " account:" + user.getAccount() + ",company:" + user.getCompany()
                    + "password:" + user.getPassword() + ",validTime" + validTimeStr + ",voiceAndkeyword:"
                    + voiceAndkeyword + " ]");
            result.put("code", 1);
        }
        TTUtil.sendDataByIOStream(response, result);
    }

    /**
     * 修改项目中的录音和关键字
     *
     * @param request
     * @param response
     * @param
     * @param
     * @param userId
     * @return
     */
    @RequestMapping(value = "/updateProject", method = RequestMethod.POST)
    @ResponseBody
    public String updateProject(HttpServletRequest request, HttpServletResponse response, int userId, String validTime,
                                User user) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String activeTime = user.getActiveTime();
        int updatValidTimeCount = userService.updateValidTime(userId, validTime);
        if (updatValidTimeCount == 1) {
            try {
                if (new Date().getTime() > ConstantUtil.YYYY_MM_DD_SDF.parse(validTime).getTime()) {//到期
                    user.setIsActive(3);
                } else if (new Date().getTime() < ConstantUtil.YYYY_MM_DD_SDF.parse(activeTime).getTime()) {//未激活
                    user.setIsActive(2);
                } else {//正常使用
                    user.setIsActive(1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            LOGGER.info("修改了 " + userId + " 的到期时间");
        }
        user.setId(userId);
        int updateCount = userService.updateUserInfoByUserId(user);
        LOGGER.info("updatValidTimeCount: " + "" + "");
        if (updatValidTimeCount == 1 && updateCount != 0) {
            result.put("result", 0);
        } else {
            result.put("result", 1);
        }
        return result.toJSONString();
    }

    /**
     * 生成十位随机账号（大小写字母 + 数字）
     *
     * @param request
     * @param respons
     * @return
     */
    @RequestMapping(value = "/getAccount", method = RequestMethod.POST)
    @ResponseBody
    public String getAccount(HttpServletRequest request, HttpServletResponse response, int adminId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        boolean flag = false;
        String account = null;

        while (!flag) {
            String a = RandomStringUtils.randomNumeric(5);
            String b = RandomStringUtils.randomAlphabetic(5);
            String c = b.substring(0, 1).toUpperCase();
            String d = b.substring(1, 5).toLowerCase();
            account = c + d + a;
            User user = userService.findUserByAccount(account);
            if (user == null) {
                flag = true;
            }
        }
        LOGGER.info(adminId + " 生成了新账号：" + account);
        result.put("result", account);
        return result.toJSONString();
    }

    /**
     * 修改账号的激活状态
     *
     * @param request
     * @param response
     * @param userId
     * @param isActive
     * @return
     */
    @RequestMapping(value = "/active", method = RequestMethod.POST)
    @ResponseBody
    public String active(HttpServletRequest request, HttpServletResponse response, int userId, int isActive) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        int updateCount = 0;
        User user = userService.findUserInfoByUserId(userId);
        if (1 == isActive) {
            try {
                if (new Date().getTime() > ConstantUtil.YYYY_MM_DD_HH_MM_SS_SDF.parse(user.getValidTime()).getTime()) {
                    updateCount = userService.updateActiveById(userId, 3);
                } else if (new Date().getTime() < ConstantUtil.YYYY_MM_DD_SDF.parse(user.getActiveTime()).getTime()) {
                    updateCount = userService.updateActiveById(userId, 2);
                } else {
                    updateCount = userService.updateActiveById(userId, 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            updateCount = userService.updateActiveById(userId, 0);
        }

        if (updateCount > 0) {
            result.put("result", 0);
            LOGGER.info(userId + " 改变了账号状态 ");
            return result.toJSONString();
        }
        result.put("result", 1);
        return result.toJSONString();
    }

    /**
     * 重置密码
     *
     * @param request
     * @param response
     * @param admainName
     * @return
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public String resetPassword(HttpServletRequest request, HttpServletResponse response, int userId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        int updateCount = userService.updatePassword(userId, "123456");
        if (updateCount > 0) {
            result.put("result", 0);
            LOGGER.info(userId + " 修改了密码");
            return result.toJSONString();
        }
        result.put("result", 1);
        return result.toJSONString();
    }

    /**
     * 删除录音以及相应的关键字
     *
     * @param request
     * @param response
     * @param recordId
     * @return
     */
    @RequestMapping(value = "/deletVoicAndKw", method = RequestMethod.POST)
    @ResponseBody
    public String deletVoicAndKw(HttpServletRequest request, HttpServletResponse response, int adminId, int recordId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        int updateRecordCount = recordService.deleteRecordById(recordId);
        int updateKWCount = keywordService.deleteKeywordByRecordId(recordId);
        if (updateRecordCount > 0 || updateKWCount > 0) {
            result.put("result", 0); // 录音以及关键字都删除成功
            LOGGER.info(adminId + " 删除了 " + updateRecordCount + " 条录音, " + updateKWCount + " 个关键字");
            return result.toJSONString();
        } else {
            LOGGER.info(adminId + " 删除录音 " + recordId + " 失败");
            result.put("result", 1);// 删除失败
        }
        return result.toJSONString();
    }

    @RequestMapping(value = "/findUserList", method = RequestMethod.POST)
    @ResponseBody
    public void findUserList(HttpServletRequest request, HttpServletResponse response, User user, int curPage) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        JSONArray userArr = new JSONArray();
        String account = user.getAccount();
        if (("").equals(account)) {
            account = null;
        }
        int adminId = user.getAdminId();
        int count = Integer.parseInt(ParameterProperties.getValue(ConstantUtil.SETTING_FILEPATH, "normalCount"));
        int start = (curPage - 1) * count;
        int total = userService.findCountByAdminId(adminId);
        List<User> userList = userService.findUserListByAdminId(adminId, start, count, account);
        List<Integer> userIdList = new ArrayList<Integer>();
        for (User DBUser : userList) {
            int userId = DBUser.getId();
            userIdList.add(userId);
        }

        if (userIdList.size() > 0) {
            List<Project> projectList = projectService.findProjectByUserIdList(userIdList);
            for (User DBUser : userList) {
                JSONObject userJson = (JSONObject) JSON.toJSON(DBUser);
                JSONArray projectArr = new JSONArray();
                for (Project project : projectList) {
                    if (DBUser.getId() == project.getUserId()) {
                        projectArr.add(JSON.toJSON(project));
                    }
                }
                userJson.put("project", projectArr);
                userJson.put("createTime", userJson.get("createTime").toString().subSequence(0, 19));
                userJson.put("validTime", userJson.get("validTime").toString().subSequence(0, 19));
                if (DBUser.getIsActive() == 0) {
                    userJson.put("isStart", 0);
                } else {
                    userJson.put("isStart", 1);
                }

                userArr.add(userJson);
            }
        }

        LOGGER.info(adminId + " 查询了用户列表");
        result.put("total", total);
        result.put("list", userArr);
        TTUtil.sendDataByIOStream(response, result);
    }

    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    @ResponseBody
    public String getToken(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();

        String bucket = ConstantUtil.QINIUYUN_BUCKE_RECORDE;
        Auth auth = Auth.create(ConstantUtil.QINIUYUN_ACCESS_KEY, ConstantUtil.QINIUYUN_SECRET_KEY);
        String upToken = auth.uploadToken(bucket);

        result.put("result", upToken);
        LOGGER.info("请求得token: " + upToken);
        return result.toJSONString();
    }

    /**
     * 根据adminId查询该用户下的网关编号和对应的端口号
     *
     * @param request
     * @param response
     * @param adminId
     */
    @RequestMapping(value = "/getGatewayAndPort", method = RequestMethod.POST)
    @ResponseBody
    public void getGatewayAndPort(HttpServletRequest request, HttpServletResponse response, Integer adminId) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        JSONArray resultArr = new JSONArray();
        List<RemoteGateway> remoteGatewayList = remoteGatewayDao.findRemoteGatewayInfoByAdminId(adminId);
        //int deleteGatewayCount = gatewayService.deleteGatewayByAdminId(adminId);
        //LOGGER.info("用户[ " + adminId + " ]删除了之前的[ " + deleteGatewayCount + " ]个网关");
        List<Gateway> gatewayList = gatewayService.findGatewayByAdminId(adminId);
        List<Integer> gatewayIdList = new ArrayList<Integer>();
        List<String> gatewayNumbersList = new ArrayList<String>();
        for (Gateway gateway : gatewayList) {
            gatewayIdList.add(gateway.getId());
            gatewayNumbersList.add(gateway.getGatewayNumbers());
        }

        for (RemoteGateway remoteGateway : remoteGatewayList) {
            JSONObject result = new JSONObject();
            JSONArray portOnArr = new JSONArray();
            String gatewayNode = remoteGateway.getGatewayNode();
            String portOnStr = remoteGateway.getPortOn();
            Integer type = remoteGateway.getType();
            String gatewaySn = null;
            String[] portOn = null;

            Gateway gatewayDB = null;
            Gateway gateway = new Gateway();
            gateway.setAdminId(adminId);
            gateway.setGatewayNumbers(gatewayNode);
            gateway.setPort_no(portOnStr);
            if (type == 2) {
                gatewaySn = remoteGateway.getGatewaySn();
            }
            gateway.setGateway_sn(gatewaySn);
            gateway.setType(type);
            Integer gatewayId = null;
            if (!gatewayNumbersList.contains(gatewayNode)) {

                gatewayService.addGateway(gateway);
                gatewayId = gateway.getId();
            } else {
                gatewayDB = gatewayService.findGatewayByGatewayNumbers(gatewayNode, adminId);
                gatewayId = gatewayDB.getId();
                gatewayService.updateGatewayNumAndPortAndTypeByGatewayId(gatewayId, gatewayNode, portOnStr, type, gatewaySn);
                gatewayIdList.remove(gatewayId);
            }
            List<Integer> portNoList = new ArrayList<Integer>();
            List<Port> portList = null;
            Integer soldPort = null;

            result.put("gatewayType", type);
            if (type == 1) {
                portOn = portOnStr.substring(1, portOnStr.length() - 1).split(",");
                for (int i = 0; i < portOn.length; i++) {
                    portNoList.add(Integer.valueOf(portOn[i]));
                }
                portList = portService.findPortListByGatewayId(gatewayId);
                List<Integer> ports = new ArrayList<Integer>();
                for (Port port : portList) {
                    JSONObject portJSON = new JSONObject();
                    int portNo = port.getPort();
                    if (portNoList.contains(portNo)) {
                        ports.add(portNo);
                    } else {
                        break;
                    }
                    portJSON.put("userId", String.valueOf(port.getUserId()));
                    portJSON.put("port", portNo);
                    portOnArr.add(portJSON);
                }
                portNoList.removeAll(ports);
                soldPort = ports.size();
                for (Integer portNo : portNoList) {
                    JSONObject portJSON = new JSONObject();
                    portJSON.put("userId", "");
                    portJSON.put("port", portNo);
                    portOnArr.add(portJSON);
                }
                result.put("portList", SortUtil.PortSort(portOnArr));
            } else if (type == 2) {
                Integer haveUsedCount = portService.findHaveUsedCountByGatewayId(gatewayId);
                soldPort = haveUsedCount;
                result.put("total", portOnStr);
                result.put("haveUsedCount", Integer.valueOf(portOnStr) - (haveUsedCount == null ? 0 : haveUsedCount));
            }
            if (soldPort != null) {
                boolean isUpdate = remoteGatewayDao.updateSortPortByAdminId(adminId, gatewayNode, soldPort);
                if (isUpdate) {
                    LOGGER.info("成功修改远程该代理商已卖出的数量");
                }
            }
            result.put("gateway", gatewayNode);
            result.put("gatewayId", gatewayId);
            if (gatewayDB == null) {
                result.put("url", "");
                result.put("auth", "");
                result.put("pwd", "");
            } else {
                result.put("url", (gatewayDB.getUrl() == null ? "" : gatewayDB.getUrl()));
                result.put("auth", (gatewayDB.getAuth() == null ? "" : gatewayDB.getAuth()));
                result.put("pwd", (gatewayDB.getPwd() == null ? "" : gatewayDB.getPwd()));
            }
            resultArr.add(result);
        }
//		Integer bathaddCount = gatewayService.bathAddGateways(gatewayList);
        Integer bathDeleteCount = null;
        if (gatewayIdList != null && gatewayIdList.size() > 0) {
            bathDeleteCount = gatewayService.bathDeleteGateway(gatewayIdList);
        }
        LOGGER.info("批量删除了[ " + bathDeleteCount + " ]个网关");
        TTUtil.sendJSONArrDataByIOStream(response, resultArr);
    }

    private JSONObject addVoiceAndKW(JSONArray voiceAndkeywordArr, int userId) {
        JSONObject result = new JSONObject();

        List<Integer> projectIdList = new ArrayList<Integer>();
        for (int i = 0; i < voiceAndkeywordArr.size(); i++) {
            JSONObject json = voiceAndkeywordArr.getJSONObject(i);
            String projectName = json.getString("projectName");
            JSONArray voiceAndKw = json.getJSONArray("voiceAndKw");
            int projectId = 0;

            Project DBproject = projectService.findProjectByUserIdAndprojectName(projectName, userId);
            if (DBproject == null) {
                Project project = new Project();
                project.setProjectName(projectName);
                project.setUserId(userId);
                projectService.addProject(project);
                projectId = project.getId();
                result.put("projectId", projectId);
                LOGGER.info("新加项目成功， projectId为：" + projectId);
            } else {
                projectId = DBproject.getId();
                LOGGER.info("该项目已存在，查询其projectId为：" + projectId);
            }
            projectIdList.add(projectId);

            for (int j = 0; j < voiceAndKw.size(); j++) {
                JSONObject SunJson = voiceAndKw.getJSONObject(j);
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) SunJson.get("kws");

                Record record = new Record();
                List<Keyword> keywordList = new ArrayList<Keyword>();

                String voiceUrl = SunJson.getString("voice");
                record.setUrl(voiceUrl);
                record.setUserId(userId);
                record.setProjectId(projectId);
                recordService.addRecord(record);
                int recordId = record.getId();
                LOGGER.info("添加录音成功,recordId为：" + recordId);
                for (String kw : list) {
                    Keyword keyword = new Keyword();
                    keyword.setKeyword(kw);
                    keyword.setProjectId(projectId);
                    keyword.setUserId(userId);
                    keyword.setRecordId(recordId);
                    keywordList.add(keyword);
                }
                int keywordCount = keywordService.addKeywords(keywordList);
                LOGGER.info("添加" + recordId + "录音的关键字数量为：" + keywordCount);
                if (keywordCount < keywordList.size()) {
                    LOGGER.info("添加" + recordId + "录音的关键字时出现错误");
                }
            }
        }
        result.put("userId", userId);
        result.put("projectIdList", projectIdList);
        LOGGER.info(userId + " 新加了 " + projectIdList.size() + " 个项目");
        return result;
    }

}
