package com.eastrobot.sweepbot.web;

import java.security.MessageDigest;

import org.apache.struts2.convention.annotation.Action;

import com.opensymphony.xwork2.ActionSupport;

@Action
public class TestAction{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String test(){
	      System.out.println("进入TestAction");
	      return "success111";
	 }
    
}
