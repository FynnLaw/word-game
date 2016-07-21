package com.eastrobot.sweepbot.service;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.stereotype.Service;

import com.eastrobot.sweepbot.model.RomManager;
import com.eastrobot.sweepbot.domain.SweepBot;
import com.eastrobot.sweepbot.service.SweepBotService;
import com.eastrobot.sweepbot.service.SweepManagerService;
import com.eastrobot.sweepbot.service.SweepServerService;
import com.eastrobot.sweepbot.util.Page;

@Service
public class RomManagerService {

	static Logger logger = LoggerFactory.getLogger("sweepManager");

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private SweepBotService sweepBotService;

	@Autowired
	private SweepServerService sweepServerService;

	private static String[] service = null;

	private static Map<String, SweepManagerService> map = new HashMap<String, SweepManagerService>();

	public String unbindDevice(String userIds,StringBuilder sb,String deviceIds) {//
		String msg = null;
		String flag = "";
		if (service == null || service.length == 0) {
			try {
				service = sweepServerService.getHttpNodeList();
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
				logger.error("sweepServerService.getHttpNodeList()调用错误信息：" + msg);
			}
		}
		if (service == null || service.length == 0) {
			msg = "获取服务器列表失败";
			logger.error("sweepServerService.getHttpNodeList()调用错误信息：" + msg);
		}
		if (msg == null) {
			SweepManagerService sms = null;
			try {
				for (String tmp : service) {
					if (!map.containsKey(tmp)) {
						sms = createService(tmp);
						map.put(tmp, sms);
					}
				}
				List<String> serviceList = Arrays.asList(service);
				String exeHost = null;
				if (!map.isEmpty()) {
					for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
						String key = it.next();
						if (!serviceList.contains(key))
							map.remove(key);
						else
							exeHost = key;
					}
				}
				if (!map.isEmpty() && exeHost != null) {
					Set<String> set = new HashSet<String>();
					for (Map.Entry<String, SweepManagerService> entry : map.entrySet()) {
						sms = entry.getValue();
						String returnMsg = "";
						String returnIds = "";
						try {
							if (StringUtils.isNotBlank(deviceIds)) {
								returnMsg = sms.unbind(userIds, exeHost,deviceIds);
							}else{
								returnMsg = sms.unbind(userIds, exeHost);
							}
							System.out.println("sms.unbind()返回："+returnMsg);
							logger.info("sms.unbind()返回："+returnMsg);
							if(returnMsg.startsWith("Error:")){
								msg = returnMsg.substring(6,returnMsg.length());
								break;
							}else if(returnMsg.startsWith("Info:")){
								returnIds = returnMsg.substring(5,returnMsg.length());
								if(StringUtils.isNotBlank(returnIds)){
									//msg = returnIds + "用户解绑失败！";
									set.addAll(Arrays.asList(returnIds.split(",")));
								}
							} //1 ，2 ，3      1， 2，
							String temp ="";
							for(String t:set){
								temp = temp+t+",";
							}
							if(StringUtils.isNotBlank(temp)){//
								temp = temp.substring(0, temp.length()-1);
								msg = temp +  "用户解绑失败！";
							}
							//sb.append("0");
						} catch (Exception e) {
							e.printStackTrace();
							msg = e.getMessage();
							logger.error("SweepManagerService.unbind()调用错误信息：服务器：" + entry.getKey() + "出错信息：" + msg);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
				logger.error("createService()调用错误信息：" + msg);
			}
		}
		return msg;
	}


	/*
	 * 刷新设备在线状态
	 */
	public String updateDeviceStatus() {//
		int i = 0;
		String msg = null;
		try {
			i = sweepBotService.reload();
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			logger.error("sweepBotService.reload()调用错误信息：" + msg);
		}
		if (i == 1) {
			msg = "数据加载失败！";
			logger.error("sweepBotService.reload():数据加载失败");
		}
		if (msg == null) {// reload 正常情况在进行 下一步
			if (service == null || service.length == 0) {
				try {
					service = sweepServerService.getHttpNodeList();
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
					logger.error("sweepServerService.getHttpNodeList()调用错误信息：" + msg);
				}
			}
			if (service == null || service.length == 0) {
				msg = "获取服务器列表失败";
				logger.error("sweepServerService.getHttpNodeList()调用错误信息：" + msg);
			}
			if (msg == null) {
				SweepManagerService sms = null;
				try {
					for (String tmp : service) {
						if (!map.containsKey(tmp)) {
							sms = createService(tmp);
							map.put(tmp, sms);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
					logger.error("createService()调用错误信息：" + msg);
				}
				List<String> serviceList = Arrays.asList(service);
				if (!map.isEmpty()) {
					for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
						String key = it.next();
						if (!serviceList.contains(key))
							map.remove(key);
					}
				}
				if (msg == null) {
					if (!map.isEmpty()) {
						for (Map.Entry<String, SweepManagerService> entry : map.entrySet()) {
							sms = entry.getValue();
							try {
								sms.updateDeviceStatus();
							} catch (Exception e) {
								e.printStackTrace();
								msg = e.getMessage();
								logger.error("SweepManagerService.updateDeviceStatus()调用错误信息：服务器：" + entry.getKey() + "出错信息：" + msg);
							}
						}
					}
				}
			}
		}
		return msg;
	}

	private static SweepManagerService createService(String addr) {
		HttpInvokerProxyFactoryBean factory = new HttpInvokerProxyFactoryBean();
		factory.setServiceInterface(SweepManagerService.class);
		factory.setServiceUrl("http://" + addr + "/remoting/sweepmanager");
		factory.afterPropertiesSet();
		return (SweepManagerService) factory.getObject();
	}

	/*
	 * 推送升级
	 */
	public String PushData(List<String> deviceIds) {
		String msg = null;
		int i = 0;
		try {
			SweepManagerService sms = null;
			if (service == null || service.length == 0) {
				try {
					service = sweepServerService.getHttpNodeList();
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
					logger.error("sweepServerService.getServiceNodeList()调用错误信息：" + msg);
				}
			}
			if (service == null || service.length == 0) {
				msg = "获取服务器列表失败";
				logger.error("sweepServerService.getHttpNodeList()调用错误信息：" + msg);
			}
			if (msg == null) {
				for (String tmp : service) {
					if (!map.containsKey(tmp)) {
						sms = createService(tmp);
						map.put(tmp, sms);
					}
				}
				List<String> serviceList = Arrays.asList(service);
				for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
					String key = it.next();
					if (!serviceList.contains(key))
						map.remove(key);
				}
				if (!map.isEmpty()) {
					for (Map.Entry<String, SweepManagerService> entry : map.entrySet()) {
						sms = entry.getValue();
						try {
							i = sms.updateDeviceVersion(deviceIds);
						} catch (Exception e) {
							e.printStackTrace();
							msg = e.getMessage();
							logger.error("SweepManagerService.updateDeviceStatus()调用错误信息：服务器：" + entry.getKey() + "出错信息：" + msg);
						}
						if (msg != null && i != 1) {
							msg = entry.getKey() + "服务器updateDeviceVersion失败！";
							logger.error(msg);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			logger.error("sms.updateDeviceStatus()调用错误信息：" + msg);
		}
		return msg;
	}

	public void writeExcelData(File file, List<String> msgs) throws IOException {
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet("推送升级结果", 0);
		int outputRow = 0;
		try {
			sheet.addCell(new Label(0, outputRow, "推送升级结果"));
			for (String tmp : msgs) {
				outputRow++;
				sheet.addCell(new Label(0, outputRow, tmp));
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getGeneralFileName(String title, String ext) {
		return String.format("%s-%s.%s", title, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), ext);
	}
	public Page<SweepBot> getDeviceList(Map<String, String> condition,boolean isSplit) {
		Page<SweepBot> pageParams = new Page<SweepBot>();
		SweepBot botParams = new SweepBot();
		if (!condition.isEmpty()) {
			if (condition.containsKey("wxDeviceId")) {
				botParams.setWxDeviceId(condition.get("wxDeviceId"));
			}
			if (condition.containsKey("qrTicket")) {
				botParams.setQrTicket(condition.get("qrTicket"));
			}
			if (condition.containsKey("deviceId")) {
				botParams.setDeviceId(condition.get("deviceId"));
			}
			if (condition.containsKey("model")) {
				botParams.setModel(condition.get("model"));
			}
			if (condition.containsKey("version")) {
				botParams.setVersion(condition.get("version"));
			}
			if (condition.containsKey("status")) {
				int status = 0;
				try {
					status = Integer.parseInt(condition.get("status"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				botParams.setStatus(status);
			}
			if (condition.containsKey("qrticketisUse")) {
				int status = 2;
				try {
					status = Integer.parseInt(condition.get("qrticketisUse"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				botParams.setDeviceIdFlag(status);
			}
			if (condition.containsKey("createDateStart")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date;
				try {
					date = sdf.parse(condition.get("createDateStart"));
					botParams.setCreateTime1(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (condition.containsKey("createDateEnd")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date;
				try {
					date = sdf.parse(condition.get("createDateEnd"));
					botParams.setCreateTime(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (isSplit) {
				if (condition.containsKey("start")) {
					int i = 0;
					try {
						i = Integer.parseInt(condition.get("start"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					pageParams.setPageNo(i / Integer.parseInt(condition.get("limit")) + 1);
				}
				if (condition.containsKey("limit")) {
					int i = 0;
					try {
						i = Integer.parseInt(condition.get("limit"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					pageParams.setPageSize(i);
				}
			}else{
				pageParams.setPageSize(214748364);
			}
		}else{
			pageParams.setPageSize(Integer.MAX_VALUE);
		}
		Page<SweepBot> page = sweepBotService.list(pageParams, botParams);
//		JSONObject jsonObject = new JSONObject();
//		jsonObject = JSONObject.fromObject(page);
//		return jsonObject.toString();
		// return "";
		return page;
	}

	public String getDeviceListTest() {
		Page<SweepBot> pageParams = new Page<SweepBot>();
		SweepBot botParams = new SweepBot();
		List<SweepBot> result = new ArrayList<SweepBot>();
		for (int i = 0; i < 20; i++) {
			botParams = new SweepBot();
			botParams.setDeviceId("aaa" + i);
			botParams.setModel("bbb" + i);
			botParams.setQrTicket("ccc" + i);
			botParams.setServerNode("ddd" + i);
			botParams.setStatus(1);
			botParams.setVersion("fff" + i);
			botParams.setWxDeviceId("ggg" + i);
			result.add(botParams);
		}
		// Page<SweepBot> page = sweepBotService.list(pageParams, botParams);
		Page<SweepBot> page = new Page<SweepBot>();
		page.setPageNo(0);// dangqianye// limit/pagesize+1

		page.setPageSize(5);
		page.setResult(result);
		page.setTotalCount(50);
		JSONObject jsonObject = JSONObject.fromObject(page);
		return jsonObject.toString();
	}

	public String getRomManager(String id) {
		String sql = "select * from rom_manager where id = ? is_delete = 0";
		List<String> ret = jdbcTemplate.queryForList(sql, new String[] { id }, String.class);
		if (ret != null && ret.size() > 0) {
			return ret.get(0);
		}
		return null;
	}

	public boolean isRepeat(RomManager romManager) {
		boolean flag = false;
		String sql = "";
		int i = 0;
		if (StringUtils.isNotBlank(romManager.getVersion())) {
			sql = " select count(1) from rom_manager where is_delete = 0 and version =  " + romManager.getVersion();
			if (StringUtils.isNotBlank(romManager.getId())) {
				sql = sql + " and id <> '" + romManager.getId() + "'";
			}
			i = jdbcTemplate.queryForInt(sql);
		}
		return i > 0;
	}

	public void insertRomManager(RomManager romManager) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		String sql = " insert into rom_manager(id,version,type,sys_rom_name,original_rom_name,comment,create_time,modify_time,is_delete,content) VALUES(?,?,?,?,?,?,SYSDATE(),SYSDATE(),0,?) ";
		jdbcTemplate.update(sql, uuid, romManager.getVersion(), romManager.getType(), romManager.getSysRomName(), romManager.getOriginalRomName(),
				romManager.getComment(), romManager.getContent());
	}

	public void updatePushUpdate(String dids, String version) {
		String sql = "";
		sql = "update device_mac set auto_update = '0',force_version ='" + version + "'  where device_id in(" + dids + ")";
		jdbcTemplate.update(sql);
	}

	public void updateAutoUp(String dids, String val) {
		String sql = "";
		if ("0".equals(val)) {
			sql = "update device_mac set auto_update = '" + val + "' where device_id in(" + dids + ")";
		} else {
			sql = "update device_mac set auto_update = '" + val + "' , force_version = null where device_id in(" + dids + ")";
		}
		jdbcTemplate.update(sql);
	}

	public void deleteById(String id) {
		jdbcTemplate.update("update rom_manager set is_delete = 1 where id = ? ", id);
	}

	public int updateRMById(RomManager romManager) {
		if (romManager.getContent() != null) {
			return jdbcTemplate.update(
					"update rom_manager set version = ?,type=?,comment=?,modify_time = SYSDATE(),content=?,original_rom_name=?  where id =?",
					romManager.getVersion(), romManager.getType(), romManager.getComment(), romManager.getContent(), romManager.getOriginalRomName(),
					romManager.getId());
		} else {
			return jdbcTemplate.update("update rom_manager set version = ?,type=?,comment=?,modify_time = SYSDATE() where id =?", romManager.getVersion(),
					romManager.getType(), romManager.getComment(), romManager.getId());
		}
	}

	public List<RomManager> findRMList(Map<String, String> condition) {
		StringBuilder sbud = new StringBuilder("select id,version,type,original_rom_name,comment,create_time,modify_time from rom_manager where is_delete = 0 ");
		boolean islimit = false;
		int start = 0;
		int limit = 0;
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		if (!condition.isEmpty()) {
			if (condition.containsKey("romVersion")) {
				sbud.append(" and version like '%" + condition.get("romVersion") + "%' ");
			}
			if (condition.containsKey("createDateStart")) {
				sbud.append(" and create_time >= '" + condition.get("createDateStart") + "'");
			}
			if (condition.containsKey("createDateEnd")) {
				sbud.append(" and create_time <= '" + condition.get("createDateEnd") + "'");
			}
			if (condition.containsKey("editDateStart")) {
				sbud.append(" and modify_time >= '" + condition.get("editDateStart") + "'");
			}
			if (condition.containsKey("editDateEnd")) {
				sbud.append(" and modify_time <= '" + condition.get("editDateEnd") + "'");
			}
			sbud.append(" order by create_time ");
			try {
				List<RomManager> ret1 = null;
				ret1 = (List<RomManager>) jdbcTemplate.query(sbud.toString(), new RomManagerRowMapper(), params.toArray(new Object[params.size()]));
				if (ret1 != null) {
					count = ret1.size();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (condition.containsKey("start") && condition.containsKey("limit")) {
				sbud.append(" LIMIT ?,? ");
				islimit = true;
				start = Integer.parseInt(condition.get("start"));
				limit = Integer.parseInt(condition.get("limit"));
				params.add(start);
				params.add(limit);
			}
		}
		List<RomManager> ret = null;
		ret = (List<RomManager>) jdbcTemplate.query(sbud.toString(), new RomManagerRowMapper(), params.toArray(new Object[params.size()]));
		if (ret != null && ret.size() > 0) {
			return ret;
		}
		return null;
	}

	public List<RomManager> countRMList(Map<String, String> condition) {
		StringBuilder sbud = new StringBuilder("select id,version,type,original_rom_name,comment,create_time,modify_time from rom_manager where is_delete = 0 ");
		List<String> params = new ArrayList<String>();
		if (!condition.isEmpty()) {
			if (condition.containsKey("romVersion")) {
				sbud.append("and version like '%" + condition.get("romVersion") + "%' ");
			}
			if (condition.containsKey("createDateStart")) {
				sbud.append(" and create_time >= '" + condition.get("createDateStart") + "'");
			}
			if (condition.containsKey("createDateEnd")) {
				sbud.append(" and create_time <= '" + condition.get("createDateEnd") + "'");
			}
			if (condition.containsKey("editDateStart")) {
				sbud.append(" and modify_time >= '" + condition.get("editDateStart") + "'");
			}
			if (condition.containsKey("editDateEnd")) {
				sbud.append(" and modify_time <= '" + condition.get("editDateEnd") + "'");
			}
		}
		List<RomManager> ret = (List<RomManager>) jdbcTemplate.query(sbud.toString(), new RomManagerRowMapper(), params.toArray(new Object[params.size()]));
		if (ret != null && ret.size() > 0) {
			return ret;
		}
		return null;
	}
	
	public String getUserIdByDeviceW(String deviceIdw){
		List<String> list = null;
		String userIds = "";
		list = jdbcTemplate.queryForList("select user_id from device_bind where device_id_w in ('"+deviceIdw+"') and status <> 0", String.class);
		if(list != null){
			for(String str:list){
				userIds = userIds + str +",";
			}
			if(StringUtils.isNotBlank(userIds)){
				userIds = userIds.substring(0, userIds.length()-1);
			}
		}
		return userIds;
	}

	public static class RomManagerRowMapper implements RowMapper<RomManager> {
		@Override
		public RomManager mapRow(ResultSet rs, int rowNum) throws SQLException {
			RomManager rm = new RomManager();
			rm.setId(rs.getString("id"));
			rm.setComment(rs.getString("comment"));
			// rm.setContent(rs.getBytes("content"));
			rm.setCreateTime(rs.getString("create_time"));
			rm.setModifyTime(rs.getString("modify_time"));
			// rm.setIs_delete(rs.getString("is_delete"));
			rm.setOriginalRomName(rs.getString("original_rom_name"));
			// rm.setSysRomName(rs.getString("sys_rom_name"));
			rm.setType(rs.getString("type"));
			rm.setVersion(rs.getString("version"));
			return rm;
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
