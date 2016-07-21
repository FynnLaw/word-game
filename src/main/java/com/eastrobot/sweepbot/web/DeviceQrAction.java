package com.eastrobot.sweepbot.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.eastrobot.robotface.domain.RobotResponseEx;
import com.eastrobot.sweepbot.common.BaseAction;
import com.eastrobot.sweepbot.domain.SweepBot;
import com.eastrobot.sweepbot.model.DeviceBind;
import com.eastrobot.sweepbot.model.DeviceVersion;
import com.eastrobot.sweepbot.model.RomManager;
import com.eastrobot.sweepbot.model.SweepBotBean;
import com.eastrobot.sweepbot.service.DeviceQrService;
import com.eastrobot.sweepbot.service.OperateHistoryService;
import com.eastrobot.sweepbot.service.RomManagerService;
import com.eastrobot.sweepbot.util.Page;

public class DeviceQrAction extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7741502142464680599L;
	@Autowired
	private RomManagerService romManagerService;
	@Autowired
	private DeviceQrService deviceQrService;
	@Autowired
	private OperateHistoryService operateHistoryService;
	// 上传文件域对象
	private File uploadFile; // 与上传属性框的name保持一致   //上传的文件，在extjs 中对应 xtype:'fileuploadfield',  name:'uploadFile'  
	// 上传文件名
	private String uploadFileFileName;
	
	static Logger logger = LoggerFactory.getLogger("sweepmanager");
	
	public void list(){
		String msg = "";
		ObjectMapper mapper = new ObjectMapper();
		try{
			Map<String,String> condition = new HashMap<String, String>();
			String romVersion = "请输入版本号".equals(getRequest().getParameter("version"))?null:getRequest().getParameter("version");
			String createDateStart = getRequest().getParameter("createDateStart");
			String createDateEnd = getRequest().getParameter("createDateEnd");
			String editDateStart = getRequest().getParameter("editDateStart");
			String editDateEnd = getRequest().getParameter("editDateEnd");
			String start = getRequest().getParameter("start");
			
			String limit = "20";
			if (romVersion != null && !"".equals(romVersion.trim())) {
				condition.put("romVersion", romVersion);
			}
			if (createDateStart != null && !"".equals(createDateStart.trim())) {
				condition.put("createDateStart", createDateStart);
			}
			if (createDateEnd != null && !"".equals(createDateEnd.trim())) {
				condition.put("createDateEnd", createDateEnd);
			}
			if (editDateStart != null && !"".equals(editDateStart.trim())) {
				condition.put("editDateStart", editDateStart);
			}
			if (editDateEnd != null && !"".equals(editDateEnd.trim())) {
				condition.put("editDateEnd", editDateEnd);
			}
			if (start != null && !"".equals(start.trim())) {
				condition.put("start", start);
			}
			if (limit != null && !"".equals(limit.trim())) {
				condition.put("limit", limit);
			}
			List<RomManager> romManagerList = null;
			List<RomManager> romManagerListCount = null;
			try {
				romManagerList = romManagerService.findRMList(condition);
				romManagerListCount = romManagerService.countRMList(condition);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
			String totalCount = "0";
			if(romManagerListCount != null){
				totalCount = romManagerListCount.size()+"";
			}
			StringBuffer sb = new StringBuffer("");
			sb.append("{'totalCount':'" + totalCount + "','products':[");
			if(romManagerList != null){
				for(int i=0;i<romManagerList.size();i++){
					sb.append(mapper.writeValueAsString(romManagerList.get(i)));
					if((i+1) == romManagerList.size()){
						
					}else{
						sb.append(",");
					}
				}
			}
			sb.append("]}");
			this.printData(getResponse(), sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteRomById(){
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		String id = getRequest().getParameter("id");
		try {
			if (StringUtils.isNotBlank(id)) {
				romManagerService.deleteById(id);
			} else {
				msg = "参数异常";
			}
			Map<String, String> map = new HashMap<String, String>();
			if (msg == null) {
				map.put("i_type", "success");
				map.put("i_msg", "");
			} else {
				map.put("i_type", "error");
				map.put("i_msg", "保存失败：" + msg);
			}
			operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "7", msg,msg==null?1:0);
			this.printData(getResponse(), mapper.writeValueAsString(map));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void qrBind() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		String deviceIdW = getRequest().getParameter("deviceIdW");
		int start1 = getRequest().getParameter("start")==null?0:Integer.parseInt(getRequest().getParameter("start"));
		int pageSize = Integer.parseInt(getRequest().getParameter("limit"));
		List<DeviceBind> dblist = null;
		try {
			List<DeviceBind> dblistC = deviceQrService.getDeviceBindCountBydiw(deviceIdW);
			String totalCount = "0";
			if(dblistC != null){
				totalCount = dblistC.size()+"";
			}
			dblist = deviceQrService.getDeviceBindBydiw(deviceIdW,start1,pageSize);
			StringBuffer sb = new StringBuffer("");
			sb.append("{'totalCount':'" + totalCount + "','products':[");
			if(dblist != null){
				for(int i=0;i<dblist.size();i++){
					sb.append(mapper.writeValueAsString(dblist.get(i)));
					if((i+1) == dblist.size()){
					}else{
						sb.append(",");
					}
				}
			}
			sb.append("]}"); 
			this.printData(getResponse(), sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void updateRom() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		String id = getRequest().getParameter("id");
		if (id != null && !"".equals(id)) {
			RomManager romManager = new RomManager();
			romManager.setId(id);
			String romVersion = getRequest().getParameter("romVersion");
			String romType = getRequest().getParameter("romType");
			String romComment = getRequest().getParameter("romComment");
			String temp = "";
			if("可选更新".equals(romType)){
				temp = "0";
			}else{
				temp = "1";
			}
			romManager.setType(temp);
			romManager.setVersion(romVersion);
			String tmp = romComment;
			Pattern pattern=Pattern.compile("(\r\n|\r|\n|\n\r)");
			//正则表达式的匹配一定要是这样，单个替换\r|\n的时候会错误
			Matcher matcher=pattern.matcher(tmp);
			String newString=matcher.replaceAll("<br>");
			romManager.setComment(newString);
			romManager.setOriginalRomName(uploadFileFileName);
			if (uploadFile != null && uploadFile.exists()) {
				InputStream in = null;
				try {
					byte[] data = null;
					in = new FileInputStream(uploadFile);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					IOUtils.copy(in, bos);
					data = bos.toByteArray();
					romManager.setContent(data);
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
				}finally{
					if(in != null){
						try {
							in.close();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			}
			if (msg == null) {
				if (!romManagerService.isRepeat(romManager)) {
					romManagerService.updateRMById(romManager);
				} else {
					msg = "版本号：" + romManager.getVersion() + "已存在,请删除后再操作";
				}
			}
		}else{
			msg = "保存失败";
		}
		Map<String, String> map = new HashMap<String, String>();
		if(msg == null){
			map.put("i_type", "success");
			map.put("success", "true");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("success", "true");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "6", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	public void getDeviceInfo(){
		String msg = null;
		try{
			Map<String,String> condition = new HashMap<String, String>();
			String wxDeviceId = getRequest().getParameter("wxDeviceId");//微信设备ID
			String qrTicket = getRequest().getParameter("qrTicket");//设备二维码ticket
			String deviceId = getRequest().getParameter("deviceId");//设备ID
			String model = getRequest().getParameter("model");//扫地机型号
			String version = getRequest().getParameter("version");//版本号
			String status = getRequest().getParameter("status");//在线状态
			String qrticketisUse = getRequest().getParameter("qrticketisUse");//微信二维码是否已使用
			String createDateStart = getRequest().getParameter("createDateStart");
			String createDateEnd = getRequest().getParameter("createDateEnd");
			String start = getRequest().getParameter("start");
			String limit = getRequest().getParameter("limit");
			if (StringUtils.isNotEmpty(wxDeviceId)) {
				condition.put("wxDeviceId", wxDeviceId);
			}
			if (StringUtils.isNotEmpty(qrTicket)) {
				condition.put("qrTicket", qrTicket);
			}
			if (StringUtils.isNotEmpty(deviceId)) {
				condition.put("deviceId", deviceId);
			}
			if (StringUtils.isNotEmpty(model)) {
				condition.put("model", model);
			}
			if (StringUtils.isNotEmpty(version)) {
				condition.put("version", version);
			}
			if (StringUtils.isNotEmpty(status)) {
				condition.put("status", status);
			}
			if (StringUtils.isNotEmpty(qrticketisUse)) {
				condition.put("qrticketisUse", qrticketisUse);
			}
			if (StringUtils.isNotEmpty(createDateStart)) {
				condition.put("createDateStart", createDateStart);
			}
			if (StringUtils.isNotEmpty(createDateEnd)) {
				condition.put("createDateEnd", createDateEnd);
			}
			if (StringUtils.isNotEmpty(start)) {
				condition.put("start", start);
			}
			if (StringUtils.isNotEmpty(limit)) {
				condition.put("limit", limit);
			}
			String outPut = "";
			Page<SweepBot> page = null;
			try {
				page = romManagerService.getDeviceList(condition,true);
//				outPut = romManagerService.getDeviceListTest();//调试方法
				JSONObject jsonObject = new JSONObject();
				jsonObject = JSONObject.fromObject(page);
				outPut = jsonObject.toString();
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
			this.printData(getResponse(), outPut);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void importData() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		String filePath = getRequest().getParameter("filePath");
		Map<String, String> mapsize = new HashMap<String, String>();
		try {
			mapsize = deviceQrService.importAndCheckDevcieQr(filePath);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
//		Map<String, String> map = new HashMap<String, String>();
		if(msg == null){
			mapsize.put("i_type", "success");
			mapsize.put("i_msg", "");
		}else{
			mapsize.put("i_type", "error");
			mapsize.put("i_msg", "导入失败："+msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "4", msg,msg==null?1:0);
		mapper = new ObjectMapper();
		this.printData(getResponse(), mapper.writeValueAsString(mapsize));
	}
	
	public void getVersionTs() throws JsonGenerationException, JsonMappingException, IOException{
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		List<DeviceVersion> list = new ArrayList<DeviceVersion>();
		list = deviceQrService.getVersion();
		StringBuffer sb = new StringBuffer("");
		sb.append("{'totalCount':'" + list.size()+ "','products':[");
		if(list != null){
			for(int i=0;i<list.size();i++){
				sb.append(mapper.writeValueAsString(list.get(i)));
				if((i+1) == list.size()){
				}else{
					sb.append(",");
				}
			}
		}
		sb.append("]}");
		this.printData(getResponse(), sb.toString());
	}
	/*
	 * 推送升级
	 */
	public void PushData() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		/*
		 * 推送升级
		 */
		List<String> deviceIds = null;//前台获取
		String dids = getRequest().getParameter("dids");
		String version = getRequest().getParameter("version");
		
		if(StringUtils.isNotBlank(dids)){
			deviceIds = analysisStr(dids, ",");
			dids = dids.replaceAll(",","','");
			dids = "'"+dids+"'";
		}else{
			msg = "参数为空";
		}
		if(msg == null){
			try {
				romManagerService.updatePushUpdate(dids, version);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
		}
		if (msg == null) {
			try {
				msg = romManagerService.PushData(deviceIds);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "1", msg,msg==null?1:0);
		mapper = new ObjectMapper();
		this.printData(getResponse(), mapper.writeValueAsString(map));
	
	}
	public void setAutoUpdate() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String dids = getRequest().getParameter("dids");
		String val = getRequest().getParameter("val");//1:自动  0 ：非自动
//		List<String> didList = analysisStr(dids, "$");
		String t = dids.substring(dids.length()-1, dids.length());
		if(",".equals(t)){
			dids = dids.substring(0, dids.length() -1);
		}
		dids = dids.replaceAll(",","','");
		dids = "'"+dids+"'";
		if(StringUtils.isNotBlank(dids)){
			try {
				romManagerService.updateAutoUp(dids,val);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", "更新失败："+msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "3", msg,msg==null?1:0);
		mapper = new ObjectMapper();
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	public void refreshStatus() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		try {
			msg = romManagerService.updateDeviceStatus();
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "2", msg,msg==null?1:0);
		mapper = new ObjectMapper();
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	public void getVersion() throws JsonGenerationException, JsonMappingException, IOException{
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		List<DeviceVersion> list = new ArrayList<DeviceVersion>();
		list = deviceQrService.getVersion();
		StringBuffer sb = new StringBuffer("");
		DeviceVersion dv = new DeviceVersion();
		dv.setName("全部");
		dv.setValue("0");
		sb.append("{'totalCount':'" + list.size()+ "','products':[");
		sb.append(mapper.writeValueAsString(dv));
		if(list != null){
			sb.append(",");
			for(int i=0;i<list.size();i++){
				sb.append(mapper.writeValueAsString(list.get(i)));
				if((i+1) == list.size()){
				}else{
					sb.append(",");
				}
			}
		}
		sb.append("]}");
		this.printData(getResponse(), sb.toString());
	}
	/*
	 * 批量生成二维码
	 */
	public void generatQr() throws IOException{
		HttpSession session=getRequest().getSession();
		String num = getRequest().getParameter("num").trim();
		StringBuilder sb = new StringBuilder();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		int i = 0;
		try {
			i = Integer.parseInt(num);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if(msg == null){
			try {
				msg = deviceQrService.generatQr(i, sb);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
			if(msg == null){//刷新缓存
				try {
					msg = romManagerService.updateDeviceStatus();
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
			}
		}
		
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "本次共生成二维码"+sb+"个");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "17", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
		
	}
	/*
	 * 单独设备解绑解绑
	 */
	public void forceunbind() throws IOException{
		HttpSession session = getRequest().getSession(true);
		String userIds = getRequest().getParameter("userIds").trim();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		String flag =null;
		RobotResponseEx r = null;
		StringBuilder sb = new StringBuilder("");
		if(StringUtils.isNotBlank(userIds)){
			try {
				msg = romManagerService.unbindDevice(userIds,sb,"");
			} catch (Exception e) {
				e.printStackTrace();
				flag = e.getMessage();
			}
		}else{
			flag = "参数为空";
		}
		if(flag == null){
			if(StringUtils.isBlank(msg)){
				msg = "操作成功！";
			}
			map.put("i_type", "success");
			map.put("i_msg", msg);
		}else{
			map.put("i_type", "error");
			map.put("i_msg", flag);
		}
		if(flag == null){
			flag = msg;
		}
		operateHistoryService.insertOH(getRequest(),(String) session.getAttribute("userId"), "8",flag, "0".equals(sb.toString()) ? 1 : 0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	/*
	 * 该设备号下所有设备解绑解绑
	 */
	public void forceunbindall() throws IOException{
		String deviceWIds = getRequest().getParameter("deviceWIds").trim();
		Map<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		String msg = null;
		String flag = null;
		HttpSession session = getRequest().getSession(true); 
		RobotResponseEx r  = null;
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(deviceWIds)) {
			if(",".equals(deviceWIds.substring(deviceWIds.length()-1, deviceWIds.length()))){
				deviceWIds = deviceWIds.substring(0, deviceWIds.length() - 1 );
				deviceWIds = deviceWIds.replaceAll(",","','");
			}
			try {
				String userIds = "";
				try {
					userIds = romManagerService.getUserIdByDeviceW(deviceWIds);
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
				if(msg == null && StringUtils.isNotBlank(userIds)){
					try {
						msg = romManagerService.unbindDevice(userIds,sb,"");
					} catch (Exception e) {
						e.printStackTrace();
						flag = e.getMessage();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				flag = e.getMessage();
			}
		}else{
			flag = "参数为空";
		}
		if(flag == null){
			if(StringUtils.isBlank(msg)){
				msg = "操作成功！";
			}
			map.put("i_type", "success");
			map.put("i_msg", msg);
		}else{
			map.put("i_type", "error");
			map.put("i_msg", flag);
		}
		if(flag == null){
			flag = msg;
		}
		operateHistoryService.insertOH(getRequest(),(String) session.getAttribute("userId"), "8",flag, "0".equals(sb.toString()) ? 1 : 0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	
	
	public void exportData() throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		HttpSession session = getRequest().getSession(true); 
		String msg = null;
		Map<String,String> condition = new HashMap<String, String>();
		String wxDeviceId = getRequest().getParameter("wxDeviceId");//微信设备ID
		String qrTicket = getRequest().getParameter("qrTicket");//设备二维码ticket
		String deviceId = getRequest().getParameter("deviceId");//设备ID
		String model = getRequest().getParameter("model");//扫地机型号
		String version = getRequest().getParameter("version");//版本号
		String status = getRequest().getParameter("status");//在线状态
		String qrticketisUse = getRequest().getParameter("qrticketisUse");//微信二维码是否已使用
		String createDateStart = getRequest().getParameter("createDateStart");
		String createDateEnd = getRequest().getParameter("createDateEnd");
		String start = getRequest().getParameter("start");
		String limit = getRequest().getParameter("limit");
		if (StringUtils.isNotEmpty(wxDeviceId)) {
			condition.put("wxDeviceId", wxDeviceId);
		}
		if (StringUtils.isNotEmpty(qrTicket)) {
			condition.put("qrTicket", qrTicket);
		}
		if (StringUtils.isNotEmpty(deviceId)) {
			condition.put("deviceId", deviceId);
		}
		if (StringUtils.isNotEmpty(model)) {
			condition.put("model", model);
		}
		if (StringUtils.isNotEmpty(version)) {
			condition.put("version", version);
		}
		if (StringUtils.isNotEmpty(status)) {
			condition.put("status", status);
		}
		if (StringUtils.isNotEmpty(qrticketisUse)) {
			condition.put("qrticketisUse", qrticketisUse);
		}
		if (StringUtils.isNotEmpty(createDateStart)) {
			condition.put("createDateStart", createDateStart);
		}
		if (StringUtils.isNotEmpty(createDateEnd)) {
			condition.put("createDateEnd", createDateEnd);
		}
		if (StringUtils.isNotEmpty(start)) {
			condition.put("start", start);
		}
		if (StringUtils.isNotEmpty(limit)) {
			condition.put("limit", limit);
		}
		Page<SweepBot> page = null;
		try {
			page = romManagerService.getDeviceList(condition,false);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		File file = null;
		if (msg == null) {
			String fileDir = getRequest().getSession().getServletContext().getRealPath("/uploadFile");
			File file1 = new File(fileDir);
			if (!file1.exists()) {
				file1.mkdir();
			}
			String fileName = getGeneralFileName("设备数据导出", "xls");
//			getRequest().getSession().setAttribute("export_device_file",fileName);
			file = new File(fileDir, fileName);
			msg = deviceQrService.writeExcelData(file, page);
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_file_name", file.getName());
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "18", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	/*
	 * 更新设备数据
	 */
	public void updateDevice() throws JsonGenerationException, JsonMappingException, IOException{
		Map<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		HttpSession session = getRequest().getSession(true); 
		String msg = null;
		String deviceInfo = getRequest().getParameter("deviceInfo");
		try {
			msg = deviceQrService.updateDevice(deviceInfo);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "操作成功！");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "19", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	
	public void deleteDevice() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String dids = getRequest().getParameter("dids");
		String tmpDeviceId = dids;
		String deviceWIds = getRequest().getParameter("deviceWids");
		String t = dids.substring(dids.length()-1, dids.length());
		if(",".equals(t)){
			dids = dids.substring(0, dids.length() -1);
		}
		dids = dids.replaceAll(",","','");
		dids = "'"+dids+"'";
		if(msg == null){
			StringBuilder sb = new StringBuilder();
			if (StringUtils.isNotBlank(deviceWIds)) {
				if(",".equals(deviceWIds.substring(deviceWIds.length()-1, deviceWIds.length()))){
					deviceWIds = deviceWIds.substring(0, deviceWIds.length() - 1 );
					deviceWIds = deviceWIds.replaceAll(",","','");
				}
				try {
					String userIds = "";
					try {
						userIds = romManagerService.getUserIdByDeviceW(deviceWIds);
					} catch (Exception e) {
						e.printStackTrace();
						msg = e.getMessage();
					}
					if(msg == null){
						if (StringUtils.isNotBlank(dids)) {
							if (StringUtils.isNotBlank(dids.replaceAll("'",""))) {
								try {
									msg = deviceQrService.deleteDevice(dids, deviceWIds);
								} catch (Exception e) {
									e.printStackTrace();
									msg = e.getMessage();
								}
							}
						} else {
							msg = "参数为空";
						}
						try {
							msg = romManagerService.unbindDevice(userIds,sb,tmpDeviceId);
						} catch (Exception e) {
							e.printStackTrace();
							msg = e.getMessage();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
			}else{
				msg = "参数为空";
			}
		}
		if (msg == null) {
//		if (true) {
//			if (StringUtils.isNotBlank(dids)) {
//				if (StringUtils.isNotBlank(dids.replaceAll("'",""))) {
//					try {
//						msg = deviceQrService.deleteDevice(dids, deviceWIds);
//					} catch (Exception e) {
//						e.printStackTrace();
//						msg = e.getMessage();
//					}
//				}
//			} else {
//				msg = "参数为空";
//			}
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "20", msg,msg==null?1:0);
		mapper = new ObjectMapper();
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	private String getGeneralFileName(String title, String ext) {
		return String.format("%s-%s.%s", title, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
				ext);
	}
	
	public List<String> analysisStr(String value,String flag){
		List<String> list = new ArrayList<String>();
		try {
			if (StringUtils.isNotBlank(value)) {
				String[] tmp = null;
				tmp = value.split(flag);
				for (String str : tmp) {
					list.add(str);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 功能：公共方法用于响应前台请求
	 * @param response
	 * @param data
	 */
	private void printData(HttpServletResponse response, String data) {
		try {
//			System.out.println(data);
			response.setContentType("text/html;charset=utf-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
			out.println(data);
			out.close();
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadFileFileName() {
		return uploadFileFileName;
	}

	public void setUploadFileFileName(String uploadFileFileName) {
		this.uploadFileFileName = uploadFileFileName;
	}
	
}
