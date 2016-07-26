package com.eastrobot.sweepbot.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.eastrobot.sweepbot.common.BaseAction;
import com.eastrobot.sweepbot.model.WordGame;
import com.eastrobot.sweepbot.service.OperateHistoryService;
import com.eastrobot.sweepbot.service.WordGameService;

public class WordGameAction extends BaseAction {
	private static final long serialVersionUID = -1036748998286563677L;
	@Autowired
	private WordGameService wordGameService;
	
	@Autowired
	private OperateHistoryService operateHistoryService;
	
	public void getWordGameList() {
		List<WordGame> wordGameList = new ArrayList<WordGame>();
		wordGameList = wordGameService.queryWordGameList();
		writeJson(wordGameList);
	}
	
	public void addWordGame() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map map = new HashMap<String, String>();
		String msg = null;
		
		String name = getRequest().getParameter("name");
		try {
			wordGameService.insertWordGame(name);
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
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "16", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
		
	}
	
	public void deleteWordGame() throws JsonGenerationException, JsonMappingException, IOException{
		HttpSession session=getRequest().getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map map = new HashMap<String, String>();
		String msg = null;
		
		String id = getRequest().getParameter("id");
		try {
			wordGameService.deleteWordGame(id);
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
		operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "16", msg,msg==null?1:0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	/**
	 * 功能：公共方法用于响应前台请求
	 * @param response
	 * @param data
	 */
	private void printData(HttpServletResponse response, String data) {
		try {
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
