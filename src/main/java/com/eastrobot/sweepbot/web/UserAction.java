package com.eastrobot.sweepbot.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.eastrobot.sweepbot.common.BaseAction;
import com.eastrobot.sweepbot.model.CategoryTreeBean;
import com.eastrobot.sweepbot.model.CategoryTreeBeanCk;
import com.eastrobot.sweepbot.model.UserLogin;
import com.eastrobot.sweepbot.model.UserRole;
import com.eastrobot.sweepbot.model.ModuleStoreBean;
import com.eastrobot.sweepbot.service.LoginService;
import com.eastrobot.sweepbot.service.OperateHistoryService;
import com.eastrobot.sweepbot.service.UserService;
import com.eastrobot.sweepbot.util.Md5Util;
import com.eastrobot.sweepbot.util.ToolUtils;

public class UserAction extends BaseAction{
	private static final long serialVersionUID = -1036748998286563677L;
	@Autowired
	private LoginService loginService;
	@Autowired
	private UserService userService;
	@Autowired
	private OperateHistoryService operateHistoryService;
	
	public void getTree(){
		HttpSession session=getRequest().getSession();
		String userRole = (String)session.getAttribute("userRole");
		List<CategoryTreeBean> l = new ArrayList<CategoryTreeBean>();
		l = userService.getTreeList(userRole);
		Map<String,CategoryTreeBean> map = new LinkedHashMap<String,CategoryTreeBean>(); 
		Map<String,CategoryTreeBean> map1 = new LinkedHashMap<String,CategoryTreeBean>(); 
		for(CategoryTreeBean t:l){//list转换成map
			map.put(t.getId(), t);
			map1.put(t.getId(), t);
		}
		CategoryTreeBean c1 = null;
		CategoryTreeBean c2 = null;
		Iterator it = map.keySet().iterator();//遍历map
		while (it.hasNext()) {
			c1 = new CategoryTreeBean();
			c1 = map.get(it.next());
			if(c1.getId() == null ||"null".equals(c1.getId())){//第一级节点
				
			}else{
				if(map1.containsKey(c1.getParent_id())){//
					c2 = new CategoryTreeBean();
					c2 = map1.get(c1.getParent_id());
					if(c2.getChildren() != null){
						c2.getChildren().add(c1);
					}else{
						List<CategoryTreeBean> childrens = new ArrayList<CategoryTreeBean>();
						childrens.add(c1);
						c2.setChildren(childrens);
					}
					map1.remove(c1.getId());
				}
			}
		}
		List<CategoryTreeBean> newList = new ArrayList<CategoryTreeBean>();
		Iterator i = map1.keySet().iterator();
		while (i.hasNext()) {
			newList.add((CategoryTreeBean)map.get(i.next()));
		}
		writeJson(newList);
	}
	public void getAuthTree(){
		String roleId = (String)getRequest().getParameter("roleId");
		List<CategoryTreeBeanCk> l = new ArrayList<CategoryTreeBeanCk>();
		l = userService.getTreeCKListAuth(roleId);
		Map<String,CategoryTreeBeanCk> map = new LinkedHashMap<String,CategoryTreeBeanCk>(); 
		Map<String,CategoryTreeBeanCk> map1 = new LinkedHashMap<String,CategoryTreeBeanCk>(); 
		for(CategoryTreeBeanCk t:l){//list转换成map
			map.put(t.getId(), t);
			map1.put(t.getId(), t);
		}
		CategoryTreeBeanCk c1 = null;
		CategoryTreeBeanCk c2 = null;
		Iterator it = map.keySet().iterator();//遍历map
		while (it.hasNext()) {
			c1 = new CategoryTreeBeanCk();
			c1 = map.get(it.next());
			if(c1.getId() == null ||"null".equals(c1.getId())){//第一级节点
				
			}else{
				if(map1.containsKey(c1.getParent_id())){//
					c2 = new CategoryTreeBeanCk();
					c2 = map1.get(c1.getParent_id());
					if(c2.getChildren() != null){
						c2.getChildren().add(c1);
					}else{
						List<CategoryTreeBeanCk> childrens = new ArrayList<CategoryTreeBeanCk>();
						childrens.add(c1);
						c2.setChildren(childrens);
					}
					map1.remove(c1.getId());
				}
			}
		}
		List<CategoryTreeBeanCk> newList = new ArrayList<CategoryTreeBeanCk>();
		Iterator i = map1.keySet().iterator();
		while (i.hasNext()) {
			newList.add((CategoryTreeBeanCk)map.get(i.next()));
		}
		writeJson(newList);
	}
	public void updateUserAuth() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String menuIds = getRequest().getParameter("ids");
		String roleId = getRequest().getParameter("roleId");
		HttpSession session=getRequest().getSession();
		String menuId[] = menuIds.split(",");//
		String msg = null;
		if(ToolUtils.isNotBlank(menuIds) && ToolUtils.isNotBlank(roleId)){
			try {
				userService.DeleteRoleAuth(roleId);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
			if (msg == null) {
				for (String temId : menuId) {
					try {
						userService.addRoleAuth(roleId, temId);
					} catch (Exception e) {
						e.printStackTrace();
						msg = e.getMessage();
					}
				}
			}
		}else{//没有任何权限
			msg = "参数有误";
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "16", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	public void updatePwd() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		HttpSession session=getRequest().getSession();
		String oldpwd = getRequest().getParameter("oldpwd");
		String newpwd = getRequest().getParameter("newpwd");
		String userName = (String)session.getAttribute("userName");
		String msg = null;
		if(!loginService.checkUser(userName, oldpwd)){
			msg = "原密码输入错误";
		}else{
			try {
				userService.updatePwd(userName, newpwd);
			} catch (Exception e) {
				e.printStackTrace();
				msg = "密码重置错误："+e.getMessage();
			}
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "9", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	public void listUser() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String,String> condition = new HashMap<String, String>();
		String userName = getRequest().getParameter("userName");
		String userRole = getRequest().getParameter("userRole");
		String start = getRequest().getParameter("start");
		String limit = getRequest().getParameter("limit");
		
		String msg = null;
		if(StringUtils.isNotBlank(userName)){
			condition.put("userName", userName);
		}
		if(StringUtils.isNotBlank(userRole)){
			if(!"0".equals(userRole) && !"全部".equals(userRole)){//2 :全部
				condition.put("userRole", userRole);
			}
		}
		if (StringUtils.isNotBlank(start)) {
			condition.put("start", start);
		}
		if (StringUtils.isNotBlank(limit)) {
			condition.put("limit", limit);
		}
		List<UserLogin> list = null;
		int count = 0;
		try {
			list = userService.getUserList(condition);
			count = userService.getUserListCount(condition);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		StringBuffer sb = new StringBuffer("");
		sb.append("{'totalCount':'" + count + "','products':[");
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
	
	public void listRole() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String,String> condition = new HashMap<String, String>();
		String roleName = getRequest().getParameter("roleName");
		String start = getRequest().getParameter("start");
		String limit = getRequest().getParameter("limit");
		
		String msg = null;
		if(StringUtils.isNotBlank(roleName)){
			condition.put("roleName", roleName);
		}
		if (StringUtils.isNotBlank(start)) {
			condition.put("start", start);
		}
		if (StringUtils.isNotBlank(limit)) {
			condition.put("limit", limit);
		}
		List<UserRole> list = null;
		StringBuilder sb = new StringBuilder();
		try {
			list = userService.getRoleList(condition, sb);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		StringBuffer sbs = new StringBuffer("");
		sbs.append("{'totalCount':'" + sb + "','products':[");
		if(list != null){
			for(int i=0;i<list.size();i++){
				sbs.append(mapper.writeValueAsString(list.get(i)));
				if((i+1) == list.size()){
				}else{
					sbs.append(",");
				}
			}
		}
		sbs.append("]}");
		this.printData(getResponse(), sbs.toString());
	}
	
	public void deleteRole() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String roleId = getRequest().getParameter("roleId");
		String sessionRoleId = (String)session.getAttribute("userRole");
		String msg = null;
		if(sessionRoleId.equals(roleId)){
			msg = "当前角色不能删除!";
		}
		if (msg  == null) {
			if (StringUtils.isNotBlank(roleId)) {
				try {
					userService.deleteRole(roleId);
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
				if (msg == null) {
					try {
						userService.deleteUserByRoleId(roleId);
					} catch (Exception e) {
						e.printStackTrace();
						msg = e.getMessage();
					}
				}
			} else {
				msg = "参数为空！";
			}
		}
		if (msg == null) {
			map.put("i_type", "success");
			map.put("i_msg", "");
		} else {
			map.put("i_type", "error");
			map.put("i_msg", "操作失败：" + msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "15", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	public void deleteUser() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String msg = null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String userId = getRequest().getParameter("userId");
		String sessionUserID = (String)session.getAttribute("userId");
		if(sessionUserID.equals(userId)){
			msg = "当前用户不能删除！";
		}
		if (msg == null) {
			if (StringUtils.isNotBlank(userId)) {
				try {
					userService.deleteUser(userId);
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
			} else {
				msg = "参数为空！";
			}
		}
		if (msg == null) {
			map.put("i_type", "success");
			map.put("i_msg", "");
		} else {
			map.put("i_type", "error");
			map.put("i_msg", "删除失败：" + msg);
		}
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "12", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	public void editRole() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String roleId = getRequest().getParameter("roleId");
		String roleName = getRequest().getParameter("roleName");
		String msg = null;
		if(!ToolUtils.isNotBlank(roleName)){
			msg = "参数有误!";
		}
		if(msg == null){
			if(ToolUtils.isNotBlank(roleId)){//roleId 不为空，编辑角色
				if(!userService.isExitRole(roleName)){//检查是否存在重复角色名
					try {
						userService.editRole(roleId,roleName);
					} catch (Exception e) {
						e.printStackTrace();
						msg = e.getMessage();
					}
				}else{
					msg = roleName + "用户名已存在";
				}
				operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "13", msg,msg==null?1:0);
			}else{//添加用户
				if(!userService.isExitRole(roleName)){//检查是否存在重复角色名
					try {
						userService.addRole(roleName);
					} catch (Exception e) {
						e.printStackTrace();
						msg = e.getMessage();
					}
				}else{
					msg = roleName + "用户名已存在";
				}
				operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "14", msg,msg==null?1:0);
			}
		}
		if (msg == null) {
			map.put("i_type", "success");
			map.put("i_msg", "");
		} else {
			map.put("i_type", "error");
			map.put("i_msg", "保存失败：" + msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	public void editUser() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String userId = getRequest().getParameter("userId");
		String userName = getRequest().getParameter("userName");
		String userPwd = getRequest().getParameter("userPwd");
		String userRole = getRequest().getParameter("userRole");
		String msg = null;
		if(!ToolUtils.isNotBlank(userName) || !ToolUtils.isNotBlank(userPwd) || !ToolUtils.isNotBlank(userRole)){
			msg = "参数有误!";
		}
		int role = 1;
		if(msg == null ){
			try {
				role = Integer.parseInt(userRole);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
		}
		if (msg == null) {
			if (!StringUtils.isNotBlank(userId) && !"null".equals(userId)) {//添加用户
				if (!userService.isExitUser(userName,"")) {//检查是否存在userName
					
					if (msg == null) {
						try {
							userPwd = Md5Util.generatePassword(userPwd);
							userService.addUser(userName, userPwd, role);
						} catch (Exception e) {
							e.printStackTrace();
							msg = e.getMessage();
						}
					}
				} else {
					msg = userName + "用户名已存在";
				}
				operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "10", msg,msg==null?1:0);
			} else {//编辑用户
				if (!userService.isExitUser(userName,userId)) {
					UserLogin user = new UserLogin();
					if ("******".equals(userPwd)) {//密码没有修改
					} else {
						userPwd = Md5Util.generatePassword(userPwd);
						user.setUserPassword(userPwd);
					}
					user.setUserName(userName);
					user.setUserRole(role + "");
					user.setId(userId);
					try {
						userService.editUser(user);
					} catch (Exception e) {
						e.printStackTrace();
						msg = e.getMessage();
					}
				}else{
					msg = userName + "用户名已存在";
				}
				operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "11", msg,msg==null?1:0);
			}
		}
		if (msg == null) {
			map.put("i_type", "success");
			map.put("i_msg", "");
		} else {
			map.put("i_type", "error");
			map.put("i_msg", "保存失败：" + msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}

	public void getRole() throws JsonGenerationException, JsonMappingException, IOException{
		String msg = null;
		String isAll = getRequest().getParameter("all");
		ObjectMapper mapper = new ObjectMapper();
		List<ModuleStoreBean> list = new ArrayList<ModuleStoreBean>();
		list = userService.getRole();
		StringBuffer sb = new StringBuffer("");
		ModuleStoreBean dv = new ModuleStoreBean();
		dv.setText("全部");
		dv.setValue("0");
		sb.append("{'totalCount':'" + list.size()+ "','products':[");
		if ("1".equals(isAll)) {
			sb.append(mapper.writeValueAsString(dv));
		}
		if(list != null){
			if ("1".equals(isAll)) {
				sb.append(",");
			}
			for(int i=0;i<list.size();i++){
				sb.append(mapper.writeValueAsString(list.get(i)));
				if((i+1) == list.size()){
				}else{
					sb.append(",");
				}
			}
		}
		sb.append("]}");
//		System.out.println(sb);
		this.printData(getResponse(), sb.toString());
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
}
