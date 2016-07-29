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
		Map<String, CategoryTreeBean> wordGameContentMap = new HashMap<String, CategoryTreeBean>();

		for (int i = 0; i < wordGameContentList.size(); i++) {
			CategoryTreeBean category = wordGameContentList.get(i);
			wordGameContentMap.put(category.getId(), category);
		}

		Iterator it = wordGameContentMap.keySet().iterator();
		while (it.hasNext()) {
			CategoryTreeBean son = wordGameContentMap.get(it.next());
			String parentId = son.getParent_id();
			// 第一题没有父亲
			if (!parentId.equals("0")) {
				CategoryTreeBean parent = wordGameContentMap.get(parentId);

				if (parent.getChildren() != null) {
					parent.getChildren().add(son);
				} else {
					List<CategoryTreeBean> childrens = new ArrayList<CategoryTreeBean>();
					childrens.add(son);
					parent.setChildren(childrens);
				}
			}
		}
		List<CategoryTreeBean> result = new ArrayList<CategoryTreeBean>();
		result.add(wordGameContentMap.get("1"));
		writeJson(result);
	}

	public void queryWordGameContent() {
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
	
	public void editWordGameName(){
		String gameId = getRequest().getParameter("gameId");
		String wordGameName = getRequest().getParameter("wordGameName");
		WordGame wordGame = new WordGame();
		wordGame.setId(gameId);
		wordGame.setName(wordGameName);
		wordGameService.updateWordGame(wordGame);
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
		 * 1、处理option0~option4 
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

		// 本题是否为叶子题
		boolean flag = true;

		for (int i = 0; i < optionList.size(); i++) {
			String option = optionList.get(i);
			
			if(option != null && option.equals("1")){//无效
				switch (i) {
				case 0:
					wordGameContent.setOption0(null);
					wordGameService.deleteOneGameContentById(wordGameContent.getGameId(),wordGameContent.getOption0());
					break;
				case 1:
					wordGameContent.setOption1(null);
					wordGameService.deleteOneGameContentById(wordGameContent.getGameId(),wordGameContent.getOption1());
					break;
				case 2:
					wordGameContent.setOption2(null);
					wordGameService.deleteOneGameContentById(wordGameContent.getGameId(),wordGameContent.getOption2());
					break;
				case 3:
					wordGameContent.setOption3(null);
					wordGameService.deleteOneGameContentById(wordGameContent.getGameId(),wordGameContent.getOption3());
					break;
				case 4:
					wordGameContent.setOption4(null);
					wordGameService.deleteOneGameContentById(wordGameContent.getGameId(),wordGameContent.getOption4());
					break;
				default:
					break;
				}
				
			}else if (option != null && option.equals("2")) {// 生成子节点
				
				WordGameContent wordGameContentSon = new WordGameContent();
				wordGameContentSon.setSerialNo(wordGameContent.getSerialNo()+ i);
				wordGameContentSon.setTitle(wordGameContent.getSerialNo() + i);
				wordGameContentSon.setGameId(wordGameContent.getGameId());
				wordGameContentSon.setParentSerialNo(wordGameContent.getSerialNo());
				wordGameContentSon.setEnd(true);
				
				// 将子题serialNo与父题option对应起来
				switch (i) {
				case 0:
					wordGameContent.setOption0(wordGameContentSon.getSerialNo());
					break;
				case 1:
					wordGameContent.setOption1(wordGameContentSon.getSerialNo());
					break;
				case 2:
					wordGameContent.setOption2(wordGameContentSon.getSerialNo());
					break;
				case 3:
					wordGameContent.setOption3(wordGameContentSon.getSerialNo());
					break;
				case 4:
					wordGameContent.setOption4(wordGameContentSon.getSerialNo());
					break;
				default:
					break;
				}
				
				//先删除父题option对应的子题
				wordGameService.deleteOneGameContentById(wordGameContent.getGameId(), wordGameContentSon.getSerialNo());
				// 再插入子题
				wordGameService.insertWordGameContent(wordGameContentSon);
				flag = false;
				
			} else if (option != null && option.equals("3")) {// 进入指定题
				flag = false;
			} else {//只要有内容就说明父题不是叶子题
				flag = false;
			} 
		}

		// 将自身end进行修改
		wordGameContent.setEnd(flag);

		/**
		 * 2、首先将自身update
		 */
		wordGameService.updateWordGameContent(wordGameContent);
	}

	public void deleteContentTree() throws JsonGenerationException,
			JsonMappingException, IOException {
		HttpSession session = getRequest().getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map map = new HashMap<String, String>();
		String msg = null;

		String gameId = getRequest().getParameter("gameId");
		try {
			// 删除所有题目
			wordGameService.deleteGameContentById(gameId);
			// 新增游戏后默认添加第一题
			WordGameContent wordGameContent = new WordGameContent();
			wordGameContent.setSerialNo("1");
			wordGameContent.setTitle("1");
			wordGameContent.setGameId(gameId);
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
