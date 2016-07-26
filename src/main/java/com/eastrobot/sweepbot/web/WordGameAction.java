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
			wordGameService.insertWordGame(name);
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
