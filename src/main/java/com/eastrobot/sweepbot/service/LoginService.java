package com.eastrobot.sweepbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.eastrobot.sweepbot.util.Md5Util;

@Service
public class LoginService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("sweepManager");
	public boolean checkUser(String userName,String userPwd){
		String md5Pwd = Md5Util.generatePassword(userPwd);
		int i = jdbcTemplate.queryForInt("select count(1) from user_login where user_name = ? and user_password = ? and is_delete = 0 ", userName,md5Pwd);
		return i>0;
	}
	public String getUserRole(String userName){
		String userRole = "";
		userRole = jdbcTemplate.queryForInt("select max(user_role) from user_login where user_name = ?", userName)+"";
		return userRole;
	}
	
}
