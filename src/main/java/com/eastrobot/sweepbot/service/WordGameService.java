package com.eastrobot.sweepbot.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.eastrobot.sweepbot.model.CategoryTreeBean;
import com.eastrobot.sweepbot.model.WordGame;
import com.eastrobot.sweepbot.model.WordGameContent;

@Service
public class WordGameService {
	private static String queryWordGameContentListById = "select id,serial_no,content,option0,option1,option2,option3,option4,end,parent_serial_no,game_id from word_game_content where game_id = ?";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("sweepManager");
	
	public List<WordGame> queryWordGameList(){
		return jdbcTemplate.query("select id,name  from word_game;",new WordGameRowMapper());
	} 
	
	public void insertWordGame(String name){
		jdbcTemplate.update("insert into word_game values(null,?)", name);
	}
	
	public void deleteWordGame(String id){
		jdbcTemplate.update("delete from word_game where id = ?", id);
	}
	
	public List<CategoryTreeBean> queryWordGameContentListById(String id){
		return jdbcTemplate.query(queryWordGameContentListById,new String[]{id},new WordGameContentRowMapper());
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
	
	public static class WordGameContentRowMapper implements RowMapper<CategoryTreeBean> {
		@Override
		public CategoryTreeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			CategoryTreeBean category = new CategoryTreeBean();
			category.setId(rs.getString("serial_no"));
			category.setParent_id(rs.getString("parent_serial_no"));
			category.setText(rs.getString("serial_no"));
			category.setLeaf(rs.getBoolean("end"));
			
//			wordGameContent.setSerialNo(rs.getString("serial_no"));
//			wordGameContent.setContent(rs.getString("content"));
//			wordGameContent.setOption0(rs.getString("option0"));
//			wordGameContent.setOption1(rs.getString("option1"));
//			wordGameContent.setOption2(rs.getString("option2"));
//			wordGameContent.setOption3(rs.getString("option3"));
//			wordGameContent.setOption4(rs.getString("option4"));
//			wordGameContent.setEnd(rs.getBoolean("end"));
//			wordGameContent.setParentSerialNo(rs.getString("parent_serial_no"));
//			wordGameContent.setGameId(rs.getString("game_id"));
			
			return category;
		}
	}
}
