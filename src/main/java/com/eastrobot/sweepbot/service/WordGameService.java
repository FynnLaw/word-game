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

import com.eastrobot.sweepbot.model.WordGame;

@Service
public class WordGameService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("sweepManager");
	
	public List<WordGame> queryWordGameList(){
		return jdbcTemplate.query("select id,name  from word_game;",new ModuleStoreBeanRowMapper());
	} 
	
	public void insertWordGame(String name){
		jdbcTemplate.update("insert into word_game values(null,?)", name);
	}
	
	public void deleteWordGame(String id){
		jdbcTemplate.update("delete from word_game where id = ?", id);
	}
	
	public static class ModuleStoreBeanRowMapper implements RowMapper<WordGame> {
		@Override
		public WordGame mapRow(ResultSet rs, int rowNum) throws SQLException {
			WordGame wordGame = new WordGame();
			wordGame.setId(rs.getString("id"));
			wordGame.setName(rs.getString("name"));
			return wordGame;
		}
	}
}
