package com.eastrobot.sweepbot.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.eastrobot.sweepbot.model.CategoryTreeBean;
import com.eastrobot.sweepbot.model.WordGame;
import com.eastrobot.sweepbot.model.WordGameContent;

@Service
public class WordGameService {
	private static String queryWordGameContentListById = "select serial_no,title,end,parent_serial_no from word_game_content where game_id = ?";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("sweepManager");
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:31:59
	 * description: 查询游戏列表
	 */
	public List<WordGame> queryWordGameList(){
		return jdbcTemplate.query("select id,name  from word_game;",new WordGameRowMapper());
	} 
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:31:38
	 * description:插入一条游戏
	 */
	public int insertWordGame(String name){
		String sql = "insert into word_game values(null,'" + name + "')";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreatorFactory p = new PreparedStatementCreatorFactory(sql);
		p.setReturnGeneratedKeys(true);
		
		jdbcTemplate.update(p.newPreparedStatementCreator(new String[]{}),keyHolder);
		int id = keyHolder.getKey().intValue();
		return id;
	}
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:31:19
	 * description:删除一个游戏
	 */
	public void deleteWordGame(String id){
		jdbcTemplate.update("delete from word_game where id = ?", id);
	}
	
	public void updateWordGame(WordGame wordGame){
		jdbcTemplate.update("update word_game set name = ? where id = ?", wordGame.getName(),wordGame.getId());
	}
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:31:03
	 * description:查询题目列表
	 */
	public List<CategoryTreeBean> queryWordGameContentListById(String id){
		return jdbcTemplate.query(queryWordGameContentListById,new String[]{id},new WordGameContentCategoryRowMapper());
	}
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:30:34
	 * description:插入一条游戏题目
	 */
	public void insertWordGameContent(WordGameContent wordGameContent){
		Object[] params = new Object[]{
				wordGameContent.getSerialNo(),
				wordGameContent.getTitle(),
				wordGameContent.isEnd(),
				wordGameContent.getParentSerialNo(),
				wordGameContent.getGameId()
		};
		//id,serial_no,title,content,option0,option1,option2,option3,option4,end,endMessage,parent_serial_no,game_id
		jdbcTemplate.update("insert into word_game_content values(null,?,?,null,null,null,null,null,null,?,null,?,?)", params);
	}
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:30:15
	 * description:查询一条游戏题目
	 */
	public WordGameContent queryWordGameContentById(String gameId,String serialNo){
		List<WordGameContent> wordGameContentList = new ArrayList<WordGameContent>();
		String sql = "select id,serial_no,title,content,option0,option1,option2,option3,option4,end,end_message,parent_serial_no,game_id from word_game_content where game_id = ? and serial_no= ?";
		Object[] params = new Object[]{gameId,serialNo};
		wordGameContentList = (List<WordGameContent>) jdbcTemplate.query(sql, params,new WordGameContentRowMapper());
		if(wordGameContentList.size() == 1)
			return wordGameContentList.get(0);
//		wordGameContent = (WordGameContent) jdbcTemplate.queryForObject(sql, params,new WordGameContentRowMapper());
		
		return null;
	}
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:29:47
	 * description:更改游戏题目
	 */
	public void updateWordGameContent(WordGameContent wordGameContent){
		String sql1 = "update word_game_content set title=?,content=?,option0=?,option1=?,option2=?,option3=?,option4=?,end=?,end_message=? where serial_no=? and game_id=?";
		String sql2 = "update word_game_content set title=?,content=?,option0=?,option1=?,option2=?,option3=?,option4=?,end=?,end_message=?,parent_serial_no=? where serial_no=? and game_id=?";
		
		Object[] params1 = new Object[]{
				wordGameContent.getTitle(),
				wordGameContent.getContent(),
				wordGameContent.getOption0(),
				wordGameContent.getOption1(),
				wordGameContent.getOption2(),
				wordGameContent.getOption3(),
				wordGameContent.getOption4(),
				wordGameContent.isEnd(),
				wordGameContent.getEndMessage(),
				wordGameContent.getSerialNo(),
				wordGameContent.getGameId()
		};
		
		Object[] params2 = new Object[]{
				wordGameContent.getTitle(),
				wordGameContent.getContent(),
				wordGameContent.getOption0(),
				wordGameContent.getOption1(),
				wordGameContent.getOption2(),
				wordGameContent.getOption3(),
				wordGameContent.getOption4(),
				wordGameContent.isEnd(),
				wordGameContent.getEndMessage(),
				wordGameContent.getParentSerialNo(),
				wordGameContent.getSerialNo(),
				wordGameContent.getGameId()
		};
		if(wordGameContent.getParentSerialNo() == null || wordGameContent.getParentSerialNo().equals("")){
			jdbcTemplate.update(sql1,params1);
		}else{
			jdbcTemplate.update(sql2,params2);
		}
	}
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:29:25
	 * description:根据游戏id清楚所有题目
	 */
	public void deleteGameContentById(String gameId){
		String sql = "delete from word_game_content where game_id = ?";
		jdbcTemplate.update(sql,gameId);
	}
	
	/**
	 * author:FynnLaw
	 * time:2016-7-28下午5:32:42
	 * description:删除一个游戏的一个题目
	 */
	public void deleteOneGameContentById(String gameId,String serialNo){
		String sql = "delete from word_game_content where game_id = ? and serial_no = ?";
		jdbcTemplate.update(sql,gameId,serialNo);
	}
	
	public void updateParentSerialNoOfSon(String sonSerialNo,String parentSerialNo,String gameId){
		String toConcat = "|" + parentSerialNo;
		String sql = "update word_game_content set parent_serial_no=concat(parent_serial_no,?) where game_id=? and serial_no=?";
		jdbcTemplate.update(sql,toConcat,gameId,sonSerialNo);
	}
	
	public static class WordGameRowMapper implements RowMapper<WordGame> {
		@Override
		public WordGame mapRow(ResultSet rs, int rowNum) throws SQLException {
			WordGame wordGame = new WordGame();
			wordGame.setId(rs.getString("id"));
			wordGame.setName(rs.getString("name"));
			return wordGame;
		}
	}
	
	public static class WordGameContentCategoryRowMapper implements RowMapper<CategoryTreeBean> {
		@Override
		public CategoryTreeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			CategoryTreeBean category = new CategoryTreeBean();
			category.setId(rs.getString("serial_no"));
			category.setParent_id(rs.getString("parent_serial_no"));
			category.setText(rs.getString("title"));
			category.setLeaf(rs.getBoolean("end"));
			
			return category;
		}
	}
	
	public static class WordGameContentRowMapper implements RowMapper<WordGameContent> {
		@Override
		public WordGameContent mapRow(ResultSet rs, int rowNum) throws SQLException {
			WordGameContent wordGameContent = new WordGameContent();
			wordGameContent.setId(rs.getString("id"));
			wordGameContent.setSerialNo(rs.getString("serial_no"));
			wordGameContent.setTitle(rs.getString("title"));
			wordGameContent.setContent(rs.getString("content"));
			wordGameContent.setOption0(rs.getString("option0"));
			wordGameContent.setOption1(rs.getString("option1"));
			wordGameContent.setOption2(rs.getString("option2"));
			wordGameContent.setOption3(rs.getString("option3"));
			wordGameContent.setOption4(rs.getString("option4"));
			wordGameContent.setEnd(rs.getBoolean("end"));
			wordGameContent.setEndMessage(rs.getString("end_message"));
			wordGameContent.setParentSerialNo(rs.getString("parent_serial_no"));
			wordGameContent.setGameId(rs.getString("game_id"));
			
			return wordGameContent;
		}
	}
	
}
