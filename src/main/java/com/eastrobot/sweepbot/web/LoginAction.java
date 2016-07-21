package com.eastrobot.sweepbot.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eastrobot.sweepbot.model.UserLogin;
import com.eastrobot.sweepbot.service.LoginService;
import com.eastrobot.sweepbot.service.UserService;
import com.eastrobot.sweepbot.common.BaseAction;

public class LoginAction extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4566903014123125802L;
	
	@Autowired
	private LoginService loginService;
	@Autowired
	private UserService userService;
	Logger logger = LoggerFactory.getLogger("sweepManager");
	public void login() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		String userName = getRequest().getParameter("userName");
		String userPwd = getRequest().getParameter("userPwd");
		String userRole = "";
		String userId = "";
		String msg = "";
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		if(loginService.checkUser(userName, userPwd)){
			map.put("i_type", "success");
			session.setAttribute("isLogin", "0");
			session.setAttribute("userName", userName);
			List<UserLogin> list = new ArrayList<UserLogin>();
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("userName", userName);
			condition.put("userPwd", userPwd);
			try {
//				userRole = loginService.getUserRole(userName);
				list = userService.getUserListLogin(condition);
				if(list.size() > 0){
					UserLogin user = list.get(0);
					userRole = user.getUserRole()+"";
					userId = user.getId();
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("用户：" + userName + ",在" + new Date() + "获取角色信息失败，错误信息："+e.getMessage());
			}
			session.setAttribute("userRole", userRole);
			session.setAttribute("userId", userId);
			try {
				logger.info("用户：" + userName + ",在" + new Date() + "登录成功。");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try {
				logger.info("用户：" + userName + ",在" + new Date() + "登录失败。");
			} catch (Exception e) {
				e.printStackTrace();
			}
			session.setAttribute("isLogin", "1");
			map.put("i_type", "error");
			map.put("i_msg", "用户名或密码错误！");
		}
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

}
