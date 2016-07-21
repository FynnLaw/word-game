package com.eastrobot.sweepbot.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.eastrobot.sweepbot.model.RomManager;
import com.eastrobot.sweepbot.service.OperateHistoryService;
import com.eastrobot.sweepbot.service.RomManagerService;
import com.eastrobot.sweepbot.common.BaseAction;

public class UpLoadFileAction extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1679523149413390544L;
	private ServletContext sc;
	private String savePath="/uploadFile";
	
	// 上传文件域对象
	private File uploadFile; // 与上传属性框的name保持一致   //上传的文件，在extjs 中对应 xtype:'fileuploadfield',  name:'uploadFile'  
	// 上传文件名
	private String uploadFileFileName;
	
	@Autowired
	private RomManagerService romManagerService;
	@Autowired
	private OperateHistoryService operateHistoryService;
	public void uploadFile() throws IOException {
		HttpSession session=getRequest().getSession();
		String msg = null;
		String uploadType = getRequest().getParameter("uploadType");
		Map<String, String> map = new HashMap<String, String>();
		if ("1".equals(uploadType)) {
			RomManager romManager = new RomManager();
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
			InputStream in = null;
			byte[] data = null;
			try {
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
					}
				}
			}
			if (msg == null) {
				if (!romManagerService.isRepeat(romManager)) {
					romManagerService.insertRomManager(romManager);
				} else {
					msg = "版本号：" + romManager.getVersion() + "已存在,请删除后再操作";
				}
			}
			operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "5", msg,msg==null?1:0);
		}else{
			String tempFileDir = getRequest().getSession().getServletContext().getRealPath("/uploadFile");
			String body = "";
			String ext = "";
			String newName  = "";
			Date date = new Date();
			int pot = getUploadFileFileName().lastIndexOf(".");
			if (pot != -1) {
				body = date.getTime() + "";
				ext = getUploadFileFileName().substring(pot);
			} else {
				body = (new Date()).getTime() + "";
				ext = "";
			}
			newName = body + ext;
			
			File tempDir = new File(tempFileDir);
			if(!tempDir.isDirectory()){
				tempDir.mkdir();
			}
			File tempFile = new File(tempDir, newName);
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			}
			FileUtils.copyFile(getUploadFile(), tempFile);
			map.put("filePath", tempFileDir	+File.separator+ newName);
			map.put("fileName", newName);
		}
		
		if(msg == null){
			map.put("i_type", "success");
			map.put("success", "true");
			map.put("i_msg", msg);
		}else{
			map.put("i_type", "error");
			map.put("success", "true");
			map.put("i_msg", msg);
		}
		ObjectMapper mapper = new ObjectMapper();
		this.printData(getResponse(), mapper.writeValueAsString(map));

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
