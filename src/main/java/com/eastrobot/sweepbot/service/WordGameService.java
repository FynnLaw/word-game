package com.eastrobot.sweepbot.service;

import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	public List<WordGame> queryWordGameList(){
		return jdbcTemplate.query("select id,name  from word_game;",new WordGameRowMapper());
	} 
	
	public int insertWordGame(String name){
		
		String sql = "insert into word_game values(null,'" + name + "')";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreatorFactory p = new PreparedStatementCreatorFactory(sql);
		p.setReturnGeneratedKeys(true);
		
		jdbcTemplate.update(p.newPreparedStatementCreator(new String[]{}),keyHolder);
		int id = keyHolder.getKey().intValue();
		return id;
	}
	
	public void deleteWordGame(String id){
		jdbcTemplate.update("delete from word_game where id = ?", id);
	}
	
	public List<CategoryTreeBean> queryWordGameContentListById(String id){
		return jdbcTemplate.query(queryWordGameContentListById,new String[]{id},new WordGameContentCategoryRowMapper());
	}
	
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
	
	public WordGameContent queryWordGameContentById(String gameId,String questionId){
		WordGameContent wordGameContent = new WordGameContent();
		String sql = "select id,serial_no,title,content,option0,option1,option2,option3,option4,end,end_message,parent_serial_no,game_id from word_game_content where game_id = ? and serial_no= ?";
		Object[] params = new Object[]{gameId,questionId};
		wordGameContent = (WordGameContent) jdbcTemplate.queryForObject(sql, params,new WordGameContentRowMapper());
		
		return wordGameContent;
	}
	
	public void updateWordGameContent(WordGameContent wordGameContent){
		String sql = "update word_game_content set title=?,content=?,end=?,end_message=? where serial_no=? and game_id=?";
		Object[] params = new Object[]{
				wordGameContent.getTitle(),
				wordGameContent.getContent(),
				wordGameContent.isEnd(),
				wordGameContent.getEndMessage(),
				wordGameContent.getSerialNo(),
				wordGameContent.getGameId()
		};
		jdbcTemplate.update(sql,params);
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
