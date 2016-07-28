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

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.eastrobot.sweepbot.common.BaseAction;
import com.eastrobot.sweepbot.model.CategoryTreeBean;
import com.eastrobot.sweepbot.model.WordGame;
import com.eastrobot.sweepbot.model.WordGameContent;
import com.eastrobot.sweepbot.service.OperateHistoryService;
import com.eastrobot.sweepbot.service.WordGameService;

public class WordGameAction extends BaseAction {
	private static final long serialVersionUID = -1036748998286563677L;
	@Autowired
	private WordGameService wordGameService;

	@Autowired
	private OperateHistoryService operateHistoryService;

	/**
	 * author:FynnLaw time:2016-7-26下午2:08:20 description:取得游戏列表
	 */
	public void getWordGameList() {
		List<WordGame> wordGameList = new ArrayList<WordGame>();
		wordGameList = wordGameService.queryWordGameList();
		writeJson(wordGameList);
	}

	/**
	 * author:FynnLaw time:2016-7-26下午2:09:03 description:添加新游戏
	 */
	public void addWordGame() throws JsonGenerationException,
			JsonMappingException, IOException {
		HttpSession session = getRequest().getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map map = new HashMap<String, String>();
		String msg = null;

		String name = getRequest().getParameter("name");
		try {
			// 插入新游吸渓
			int gameId = wordGameService.insertWordGame(name);
			// 新增游戏后默认添加第一题
			WordGameContent wordGameContent = new WordGameContent();
			wordGameContent.setSerialNo("1");
			wordGameContent.setTitle("1");
			wordGameContent.setGameId(String.valueOf(gameId));
			wordGameContent.setParentSerialNo("0");
			wordGameContent.setEnd(true);
			wordGameService.insertWordGameContent(wordGameContent);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if (msg == null) {
			map.put("i_type", "success");
			map.put("i_msg", "");
		} else {
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(), (String) session
				.getAttribute("userId"), "16", msg, msg == null ? 1 : 0);
		this.printData(getResponse(), mapper.writeValueAsString(map));

	}

	/**
	 * author:FynnLaw time:2016-7-26下午2:09:23 description:删除游戏
	 */
	public void deleteWordGame() throws JsonGenerationException,
			JsonMappingException, IOException {
		HttpSession session = getRequest().getSession();
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

		if (msg == null) {
			map.put("i_type", "success");
			map.put("i_msg", "");
		} else {
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		operateHistoryService.insertOH(getRequest(), (String) session
				.getAttribute("userId"), "16", msg, msg == null ? 1 : 0);
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}

	/**
	 * author:FynnLaw time:2016-7-26下午2:42:23 description:返回游戏题树
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public void getWordGameTree() throws JsonGenerationException,
			JsonMappingException, IOException {
		HttpSession session = getRequest().getSession();
		ObjectMapper mapper = new ObjectMapper();

		List<CategoryTreeBean> wordGameContentList;
		String id = getRequest().getParameter("id");
		wordGameContentList = wordGameService.queryWordGameContentListById(id);
		// 如果题列表为空,返回一个root

		Map<String, CategoryTreeBean> map = new LinkedHashMap<String, CategoryTreeBean>();
		Map<String, CategoryTreeBean> map1 = new LinkedHashMap<String, CategoryTreeBean>();
		for (CategoryTreeBean t : wordGameContentList) {// list转换成map
			map.put(t.getId(), t);
			map1.put(t.getId(), t);
		}
		CategoryTreeBean c1 = null;
		CategoryTreeBean c2 = null;
		Iterator it = map.keySet().iterator();// 遍历map
		while (it.hasNext()) {
			c1 = new CategoryTreeBean();
			c1 = map.get(it.next());
			if (c1.getId() == null || "null".equals(c1.getId())) {// 第一级节点

			} else {
				if (map1.containsKey(c1.getParent_id())) {//
					c2 = new CategoryTreeBean();
					c2 = map1.get(c1.getParent_id());
					if (c2.getChildren() != null) {
						c2.getChildren().add(c1);
					} else {
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
			newList.add((CategoryTreeBean) map.get(i.next()));
		}
		writeJson(newList);
	}

	
	public void queryWordGameContent(){
		WordGameContent wordGameContent = new WordGameContent();
		String gameId = getRequest().getParameter("gameId");
		String questionId = getRequest().getParameter("questionId");
		try {
			wordGameContent = wordGameService.queryWordGameContentById(gameId,questionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		writeJson(wordGameContent);
	}
	
	
	/**
	 * author:FynnLaw time:2016-7-28上午9:29:42 description:
	 */
	public void editWordGameContent() {
		WordGameContent wordGameContent = new WordGameContent();
		
		wordGameContent.setSerialNo(getRequest().getParameter("serialNo"));
		wordGameContent.setTitle(getRequest().getParameter("title"));
		wordGameContent.setContent(getRequest().getParameter("content"));
		wordGameContent.setOption0(getRequest().getParameter("option0"));
		wordGameContent.setOption1(getRequest().getParameter("option1"));
		wordGameContent.setOption2(getRequest().getParameter("option2"));
		wordGameContent.setOption3(getRequest().getParameter("option3"));
		wordGameContent.setOption4(getRequest().getParameter("option4"));
		wordGameContent.setEndMessage(getRequest().getParameter("endMessage"));
		wordGameContent.setGameId(getRequest().getParameter("wordGameId"));
		
		/**
		 * 1、首先将自身update
		 */
		wordGameService.updateWordGameContent(wordGameContent);
		
		/**
		 * 2、处理option0~option4
		 * 1)值为1,则为无效,不插入子节点,不做处理
		 * 2)值为2,则为进入下一题,生成子节点
		 * 3)值为3,则为进入指定题
		 */
		List<String> optionList = new ArrayList<String>();
		optionList.add(wordGameContent.getOption0());
		optionList.add(wordGameContent.getOption1());
		optionList.add(wordGameContent.getOption2());
		optionList.add(wordGameContent.getOption3());
		optionList.add(wordGameContent.getOption4());
		
		//是否将本题end改为false的标志
		boolean flag = false;
		
		for(int i=0;i<optionList.size();i++){
			String option = optionList.get(i);
			if(option.equals("2")){//生成子节点
				flag = true;
				WordGameContent wordGameContentSon =  new WordGameContent();
				wordGameContentSon.setSerialNo(wordGameContent.getSerialNo() + i);
				wordGameContentSon.setGameId(wordGameContent.getGameId());
				wordGameContentSon.setParentSerialNo(wordGameContent.getSerialNo());
				wordGameContentSon.setEnd(true);
				
				wordGameService.insertWordGameContent(wordGameContentSon);
			}else if(option.equals("3")){//进入指定题
				flag = true;
			}
		}
		
		if(flag){
			//将自身end改为false
			wordGameContent.setEnd(false);
			wordGameService.updateWordGameContent(wordGameContent);
		}
	}

	/**
	 * 功能：公共方法用于响应前台请求
	 * 
	 * @param response
	 * @param data
	 */
	private void printData(HttpServletResponse response, String data) {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					response.getOutputStream(), "UTF-8"));
			out.println(data);
			out.close();
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
