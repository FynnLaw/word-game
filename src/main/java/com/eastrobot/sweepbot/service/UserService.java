package com.eastrobot.sweepbot.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.eastrobot.sweepbot.model.CategoryTreeBean;
import com.eastrobot.sweepbot.model.CategoryTreeBeanCk;
import com.eastrobot.sweepbot.model.UserLogin;
import com.eastrobot.sweepbot.model.UserRole;
import com.eastrobot.sweepbot.model.ModuleStoreBean;
import com.eastrobot.sweepbot.util.Md5Util;
import com.eastrobot.sweepbot.util.ToolUtils;

@Service
public class UserService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("sweepManager");
	
	public void updatePwd(String userName,String newUserPwd){
		String md5Pwd = Md5Util.generatePassword(newUserPwd);
		jdbcTemplate.update("update user_login set user_password = ? where user_name =? ",md5Pwd,userName);
	}
	
	public List<ModuleStoreBean> getRole(){
		return jdbcTemplate.query(" select id,role_name from user_role where is_delete = 0 ",new ModuleStoreBeanRowMapper());
	}
	
	public List<UserRole> getRoleList(Map<String, String> condition,StringBuilder sb){
		List<UserRole> list = new ArrayList<UserRole>();
		List<Object> params = new ArrayList<Object>();
		StringBuilder sbud = new StringBuilder(" select * from user_role where is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("roleName")) {
				sbud.append(" and role_name like ?");
				params.add("%"+condition.get("roleName")+"%");
			}
			list = (List<UserRole>) jdbcTemplate.query(sbud.toString(), new UserRoleRowMapper(), params.toArray(new Object[params.size()]));
			sb.append(list.size());
			if (condition.containsKey("start") && condition.containsKey("limit")) {
				sbud.append(" LIMIT ?,? ");
				int start = Integer.parseInt(condition.get("start"));
				int limit = Integer.parseInt(condition.get("limit"));
				params.add(start);
				params.add(limit);
			}
		}
		list = new ArrayList<UserRole>();
		list = (List<UserRole>) jdbcTemplate.query(sbud.toString(), new UserRoleRowMapper(), params.toArray(new Object[params.size()]));
		if (list != null && list.size() > 0) {
			return list;
		}
		return null;
	}
	public List<UserLogin> getUserList(Map<String, String> condition){
		List<UserLogin> list = null;
		List<Object> params = new ArrayList<Object>();
		StringBuilder sbud = new StringBuilder("select a.id,a.user_name,a.user_password,a.is_delete,a.user_role as user_role_id,b.role_name as user_role from user_login a left join user_role b on a.user_role = b.id where 1 = 1 and a.is_delete = 0 and b.is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("userName")) {
				sbud.append(" and a.user_name like ?");
				params.add("%"+condition.get("userName")+"%");
			}
			if(condition.containsKey("userRole")){
				sbud.append(" and a.user_role = ?");
				params.add(condition.get("userRole"));
			}
			if (condition.containsKey("start") && condition.containsKey("limit")) {
				sbud.append(" LIMIT ?,? ");
				int start = Integer.parseInt(condition.get("start"));
				int limit = Integer.parseInt(condition.get("limit"));
				params.add(start);
				params.add(limit);
			}
		}
		list = (List<UserLogin>) jdbcTemplate.query(sbud.toString(), new UserLoginRowMapper(), params.toArray(new Object[params.size()]));
		if (list != null && list.size() > 0) {
			return list;
		}
		return null;
	}
	public int getUserListCount(Map<String, String> condition){
		List<UserLogin> list = null;
		List<Object> params = new ArrayList<Object>();
		StringBuilder sbud = new StringBuilder("select count(1) from user_login a left join user_role b on a.user_role = b.id where 1 = 1 and a.is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("userName")) {
				sbud.append(" and a.user_name like ?");
				params.add("%"+condition.get("userName"+"%"));
			}
			if(condition.containsKey("userRole")){
				sbud.append(" and a.user_role = ?");
				params.add(condition.get("userRole"));
			}
		}
		int count = jdbcTemplate.queryForInt(sbud.toString(), params.toArray(new Object[params.size()]));
		return count;
	}
	public List<UserLogin> getUserListLogin(Map<String, String> condition){
		List<UserLogin> list = null;
		List<Object> params = new ArrayList<Object>();
		StringBuilder sbud = new StringBuilder("select *,1 as user_role_id from user_login where 1 = 1 and is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("userName")) {
				sbud.append(" and user_name = ?");
				params.add(condition.get("userName"));
			}
			if(condition.containsKey("userPwd")){
				sbud.append(" and user_password = ?");
				params.add(Md5Util.generatePassword(condition.get("userPwd")));
			}
		}
		list = (List<UserLogin>) jdbcTemplate.query(sbud.toString(), new UserLoginRowMapper(), params.toArray(new Object[params.size()]));
		if (list != null && list.size() > 0) {
			return list;
		}
		return null;
	}
	
	public void deleteUserByRoleId(String roleId){
		if(StringUtils.isNotBlank(roleId)){
			jdbcTemplate.update("update user_login set is_delete = 1 where user_role =?",roleId);
		}
	}
	public void deleteRole(String roleId){
		if(StringUtils.isNotBlank(roleId)){
			jdbcTemplate.update("update user_role set is_delete = 1 where id =?",roleId);
		}
	}
	public void deleteUser(String userId){
		if(StringUtils.isNotBlank(userId)){
			jdbcTemplate.update("update user_login set is_delete = 1 where id =?",userId);
		}
	}
	public void addUser(String userName,String userPwd,int role){
		String uuid = UUID.randomUUID().toString().replace("-", "");
		jdbcTemplate.update("insert into user_login(id,user_name,user_password,user_role)values(?,?,?,?)",uuid,userName,userPwd,role);
	}
	public boolean isExitUser(String userName,String userId){
		int i = 0;
		if (StringUtils.isBlank(userId)) {
			i = jdbcTemplate.queryForInt("select count(1) from user_login where user_name = ? and is_delete = 0 ",userName);
		}else{
			i = jdbcTemplate.queryForInt("select count(1) from user_login where user_name = ? and id <> ? and is_delete = 0 ",userName,userId);
		}
		return i>0;
	}
	public void DeleteRoleAuth(String roleId){
		jdbcTemplate.update("delete from sweepmgr_auth where role_id =?",roleId);
	}
	public void addRoleAuth(String roleId,String menuId){
		jdbcTemplate.update("insert into sweepmgr_auth (role_id,menu_id) values(?,?)",roleId,menuId);
	}
	public void addRole(String roleName){
		jdbcTemplate.update("insert into user_role(role_name)values(?)",roleName);
	}
	public boolean isExitRole(String roleName){
		int i = jdbcTemplate.queryForInt("select count(1) from user_role where role_name = ? and is_delete = 0 ",roleName);
		return i>0;
	}
	
	public void editRole(String roleId,String roleName){
		if(ToolUtils.isNotBlank(roleId)){
			List<Object> params = new ArrayList<Object>();
			StringBuilder sb = new StringBuilder(" update user_role set id = ? ");
			params.add(roleId);
			if(ToolUtils.isNotBlank(roleName)){
				sb.append(",role_name = ?");
				params.add(roleName);
			}
			sb.append(" where id = ?");
			params.add(roleId);
			jdbcTemplate.update(sb.toString(), params.toArray(new Object[params.size()]));
		}
	}
	public void editUser(UserLogin user){
		if(ToolUtils.isNotBlank(user.getId())){
			List<Object> params = new ArrayList<Object>();
			StringBuilder sb = new StringBuilder(" update user_login set id = ? ");
			params.add(user.getId());
			if(ToolUtils.isNotBlank(user.getUserName())){
				sb.append(",user_name = ?");
				params.add(user.getUserName());
			}
			if(ToolUtils.isNotBlank(user.getUserPassword())){
				sb.append(",user_password = ?");
				params.add(user.getUserPassword());
			}
			if(ToolUtils.isNotBlank(user.getUserRole()+"")){
				sb.append(",user_role = ?");
				params.add(user.getUserRole());
			}
			sb.append(" where id = ?");
			params.add(user.getId());
			jdbcTemplate.update(sb.toString(), params.toArray(new Object[params.size()]));
		}
	}
	public List<CategoryTreeBean> getTreeList(String roleId) {
		List<CategoryTreeBean> list = new ArrayList();
//		String sql = " select * from sweepmgr_menu ORDER BY id desc ";
		StringBuilder sb = new StringBuilder();
		sb.append(" select a.* ");
		sb.append(" from sweepmgr_menu a,(select menu_id from sweepmgr_auth  where role_id = ?) b  ");
		sb.append(" where a.id = b.menu_id ORDER BY a.id desc  ");
		list = (List<CategoryTreeBean>)jdbcTemplate.query(sb.toString(), new CategoryTreeBeanRowMapper(),roleId);
		return list;
	}
	public List<CategoryTreeBeanCk> getTreeCKListAuth(String roleId) {
		List<CategoryTreeBeanCk> list = new ArrayList();
//		String sql = " select * from sweepmgr_menu ORDER BY id desc ";
		StringBuilder sb = new StringBuilder();
		sb.append(" select a.*,b.menu_id as is_check ");
		sb.append(" from sweepmgr_menu a left join (select menu_id from sweepmgr_auth where role_id = ?) b on  a.id = b.menu_id ");
		list = (List<CategoryTreeBeanCk>)jdbcTemplate.query(sb.toString(), new CategoryTreeBeanCKRowMapper(),roleId);
		return list;
	}
	public static class CategoryTreeBeanRowMapper implements RowMapper<CategoryTreeBean> {
		@Override
		public CategoryTreeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			CategoryTreeBean devBind = new CategoryTreeBean();
			devBind.setId(rs.getString("id"));
			devBind.setText(rs.getString("menu_name"));
			devBind.setParent_id(rs.getString("parent_id"));
			devBind.setHref(rs.getString("menu_url"));
			devBind.setLeaf(rs.getInt("leaf")==0?false:true );
			return devBind;
		}
	}
	public static class CategoryTreeBeanCKRowMapper implements RowMapper<CategoryTreeBeanCk> {
		@Override
		public CategoryTreeBeanCk mapRow(ResultSet rs, int rowNum) throws SQLException {
			CategoryTreeBeanCk devBind = new CategoryTreeBeanCk();
			devBind.setId(rs.getString("id"));
			devBind.setText(rs.getString("menu_name"));
			devBind.setParent_id(rs.getString("parent_id"));
//			devBind.setHref(rs.getString("menu_url"));
			devBind.setLeaf(rs.getInt("leaf")==0?false:true );
			devBind.setChecked(rs.getString("is_check") == null?false:true);
			return devBind;
		}
	}
	
	
	
	public static class UserLoginRowMapper implements RowMapper<UserLogin> {
		@Override
		public UserLogin mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserLogin rm = new UserLogin();
			rm.setId(rs.getString("id"));
			rm.setUserName(rs.getString("user_name"));
			rm.setUserPassword(rs.getString("user_password"));
			rm.setUserRole(rs.getString("user_role"));
			rm.setUserRoleId(rs.getString("user_role_id"));
			rm.setIsDelete(rs.getInt("is_delete"));
			return rm;
		}
	}
	public static class UserRoleRowMapper implements RowMapper<UserRole> {
		@Override
		public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserRole rm = new UserRole();
			rm.setId(rs.getString("id"));
			rm.setRoleName(rs.getString("role_name"));
			return rm;
		}
	}
	public static class ModuleStoreBeanRowMapper implements RowMapper<ModuleStoreBean> {
		@Override
		public ModuleStoreBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			ModuleStoreBean rm = new ModuleStoreBean();
			rm.setValue(rs.getString("id"));
			rm.setText(rs.getString("role_name"));
			return rm;
		}
	}

}
