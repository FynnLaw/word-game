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
			String originParentId = son.getParent_id();
			String parentId = null;
			String[] parentIdArray = originParentId.split("\\|");
			if(parentIdArray.length <= 1){//只有一个时，就是单父亲的情况
				parentId = parentIdArray[0];
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
			}else{//有多个父亲的情况
				// 第一个父亲是亲生父亲
				parentId = parentIdArray[0];
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
				
				CategoryTreeBean specialSon = new CategoryTreeBean();
				specialSon.setId(son.getId());
				specialSon.setText(son.getText()+"*");
				specialSon.setLeaf(true);
				//后面的父亲都是养父
				for(int i=1;i<parentIdArray.length;i++){
					CategoryTreeBean parent = wordGameContentMap.get(parentIdArray[i]);

					if (parent.getChildren() != null) {
						parent.getChildren().add(specialSon);
					} else {
						List<CategoryTreeBean> childrens = new ArrayList<CategoryTreeBean>();
						childrens.add(specialSon);
						parent.setChildren(childrens);
					}
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
		wordGameContent.setEndMessage(getRequest().getParameter("endMessage"));
		wordGameContent.setGameId(getRequest().getParameter("wordGameId"));

		
		
		String option0 = getRequest().getParameter("option0");
		String option1 = getRequest().getParameter("option1");
		String option2 = getRequest().getParameter("option2");
		String option3 = getRequest().getParameter("option3");
		String option4 = getRequest().getParameter("option4");

		/**
		 * 1、处理option0~option4 
		 * 1)值为1,则为无效,不插入子节点,不做处理 
		 * 2)值为2,则为进入下一题,生成子节点
		 * 3)值为3,则为进入指定题
		 */
		List<String> optionList = new ArrayList<String>();
		optionList.add(option0);
		optionList.add(option1);
		optionList.add(option2);
		optionList.add(option3);
		optionList.add(option4);

		// 本题是否为叶子题
		boolean flag = true;

		for (int i = 0; i < optionList.size(); i++) {
			String option = optionList.get(i);
			
			WordGameContent wordGameContentSon = new WordGameContent();
			wordGameContentSon.setSerialNo(wordGameContent.getSerialNo()+ i);
			wordGameContentSon.setTitle(wordGameContent.getSerialNo() + i);
			wordGameContentSon.setGameId(wordGameContent.getGameId());
			wordGameContentSon.setParentSerialNo(wordGameContent.getSerialNo());
			wordGameContentSon.setEnd(true);
			
			if(option != null && option.equals("select1")){//无效
				
				//如果是从新子题改为无效,则只需删除子题(因为已经没有父题指向它了)
				//如果是从指定子题改为无效,则只需更改子题的parentSerialNo
				String sonSerialNo = "";
				switch (i) {
				case 0:
					wordGameContent.setOption0(null);
					sonSerialNo = wordGameContent.getOption0();
					break;
				case 1:
					wordGameContent.setOption1(null);
					sonSerialNo = wordGameContent.getOption0();
					break;
				case 2:
					wordGameContent.setOption2(null);
					sonSerialNo = wordGameContent.getOption0();
					break;
				case 3:
					wordGameContent.setOption3(null);
					sonSerialNo = wordGameContent.getOption0();
					break;
				case 4:
					wordGameContent.setOption4(null);
					sonSerialNo = wordGameContent.getOption0();
					break;
				default:
					break;
				}
				
				//先查出子题
				WordGameContent sonWordGameContent = wordGameService.queryWordGameContentById(wordGameContent.getGameId(), sonSerialNo);
				if(sonWordGameContent == null){
					continue;
				}
				
				String originParentSerialNo = sonWordGameContent.getParentSerialNo();
				String[] parentSerialNoArray = originParentSerialNo.split("\\|");
				
				if(parentSerialNoArray.length <= 1){
					//删除子题(包括没有子题的情况)
					wordGameService.deleteOneGameContentById(wordGameContent.getGameId(),sonSerialNo);
				}else{//更改子题的parentSerialNo
					String updatedParentSerialNo = parentSerialNoArray[0];
					//数组第一个肯定是亲生父亲,所以不需要考虑
					for(int index =1;index<parentSerialNoArray.length;index++){
						if(!parentSerialNoArray[index].equals(wordGameContent.getSerialNo())){
							updatedParentSerialNo = updatedParentSerialNo + "|" + parentSerialNoArray[index];
						}
					}
					
					sonWordGameContent.setParentSerialNo(updatedParentSerialNo);
					wordGameService.updateWordGameContent(sonWordGameContent);
					
				}
				
			}else if (option != null && option.equals("select2")) {// 生成子节点
				
				WordGameContent oldWordGameContent = wordGameService.queryWordGameContentById(wordGameContent.getGameId(),wordGameContent.getSerialNo());
				
				// 将子题serialNo与父题option对应起来
				String oldSonSerialNo = "";
				switch (i) {
				case 0:
					oldSonSerialNo = oldWordGameContent.getOption0();
					wordGameContent.setOption0(wordGameContentSon.getSerialNo());
					break;
				case 1:
					oldSonSerialNo = oldWordGameContent.getOption1();
					wordGameContent.setOption1(wordGameContentSon.getSerialNo());
					break;
				case 2:
					oldSonSerialNo = oldWordGameContent.getOption2();
					wordGameContent.setOption2(wordGameContentSon.getSerialNo());
					break;
				case 3:
					oldSonSerialNo = oldWordGameContent.getOption3();
					wordGameContent.setOption3(wordGameContentSon.getSerialNo());
					break;
				case 4:
					oldSonSerialNo = oldWordGameContent.getOption4();
					wordGameContent.setOption4(wordGameContentSon.getSerialNo());
					break;
				default:
					break;
				}
				
				if(oldSonSerialNo == null || oldSonSerialNo.equals("") || oldSonSerialNo.equals(wordGameContentSon.getSerialNo())){//从无效到新子题、从新子题到新子题
					//先删除父题option对应的子题(无效到新题,新题到新题的情况)
					wordGameService.deleteOneGameContentById(wordGameContent.getGameId(), wordGameContentSon.getSerialNo());
					// 再插入子题
					wordGameService.insertWordGameContent(wordGameContentSon);
				} else {//指定题到新子题的情况
					//先查出子题
					WordGameContent oldSonWordGameContent = wordGameService.queryWordGameContentById(wordGameContent.getGameId(), oldSonSerialNo);
					String originParentSerialNo = oldSonWordGameContent.getParentSerialNo();
					String[] parentSerialNoArray = originParentSerialNo.split("\\|");
					String resultParentSerialNo = parentSerialNoArray[0];
					for(int index=1;index < parentSerialNoArray.length;index++){
						if(!wordGameContent.getSerialNo().equals(parentSerialNoArray[index])){
							resultParentSerialNo = resultParentSerialNo + "|" + parentSerialNoArray[index];
						}
					}
					
					oldSonWordGameContent.setParentSerialNo(resultParentSerialNo);
					//更改子题parentSerialNo
					wordGameService.updateWordGameContent(oldSonWordGameContent);
					
					// 再插入子题
					wordGameService.insertWordGameContent(wordGameContentSon);
				}
				
				flag = false;
				
			}  else if(option != null && !option.equals("")){// 进入指定题
				
				//option对应的值就是子题的serialNo
				String sonSerialNo = option;
				
				//补全子题的parentSerialNo,以|隔开
				//先查出子题parentSerialNo中是否已经有父题SerialNo,如果有,说明原本就查过，用户在前端页面没有改动，所以不要重新插入
				boolean alreadyUpdate = false;
				WordGameContent oldSonWordGameContent = wordGameService.queryWordGameContentById(wordGameContent.getGameId(), sonSerialNo);
				String originParentSerialNo = oldSonWordGameContent.getParentSerialNo();
				String[] parentSerialNoArray = originParentSerialNo.split("\\|");
				for(int index=0;index<parentSerialNoArray.length;index++){
					if(parentSerialNoArray[index].equals(wordGameContent.getSerialNo())){
						alreadyUpdate = true;
					}
				}
				
				if(!alreadyUpdate){
					//如果没有,则要更改子题，追加parentSerialNo
					wordGameService.updateParentSerialNoOfSon(sonSerialNo,wordGameContent.getSerialNo(),wordGameContent.getGameId());
				}
				
				String oldSonSerialNo = "";
				WordGameContent oldWordGameContent = wordGameService.queryWordGameContentById(wordGameContent.getGameId(), wordGameContent.getSerialNo());
				
				// 将子题serialNo与父题option对应起来
				switch (i) {
				case 0:
					oldSonSerialNo = oldWordGameContent.getOption0();
					wordGameContent.setOption0(sonSerialNo);
					break;
				case 1:
					oldSonSerialNo = oldWordGameContent.getOption1();
					wordGameContent.setOption1(sonSerialNo);
					break;
				case 2:
					oldSonSerialNo = oldWordGameContent.getOption2();
					wordGameContent.setOption2(sonSerialNo);
					break;
				case 3:
					oldSonSerialNo = oldWordGameContent.getOption3();
					wordGameContent.setOption3(sonSerialNo);
					break;
				case 4:
					oldSonSerialNo = oldWordGameContent.getOption4();
					wordGameContent.setOption4(sonSerialNo);
					break;
				default:
					break;
				}
				
				boolean delteFlag = false;
				if(oldSonSerialNo != null){
					delteFlag = wordGameContent.getSerialNo().equals(oldSonSerialNo.substring(0, oldSonSerialNo.length()-1)) ? true : false;
				}
				
				//如果是从新子题到指定题，则删除父题option对应的子题
				if(delteFlag){
					wordGameService.deleteOneGameContentById(wordGameContent.getGameId(), oldSonSerialNo);
				}
				
				flag = false;
			}
			// 将自身end、option进行修改
			wordGameContent.setEnd(flag);
			/**
			 * 2、将自身update
			 */
			wordGameService.updateWordGameContent(wordGameContent);
		}
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
