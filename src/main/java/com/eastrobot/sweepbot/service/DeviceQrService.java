package com.eastrobot.sweepbot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.CellType;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eastrobot.sweepbot.domain.SweepBot;
import com.eastrobot.sweepbot.model.DeviceBind;
import com.eastrobot.sweepbot.model.DeviceMac;
import com.eastrobot.sweepbot.model.DeviceVersion;
import com.eastrobot.sweepbot.model.SweepBotBean;
import com.eastrobot.sweepbot.util.ExcelReadUtils;
import com.eastrobot.sweepbot.util.Page;
import com.eastrobot.sweepbot.util.QrUtils;
import com.eastrobot.sweepbot.util.ReportBulder;
import com.eastrobot.sweepbot.util.WeixinUtils;
import com.eastrobot.sweepbot.util.XLSXWelcomeResourceWriter;

@Service
public class DeviceQrService {


	static Logger logger = LoggerFactory.getLogger("sweepmanager");
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
//	@Autowired
//	private DeviceMacService deviceMacService;
//	
	
	
	/**
	 * 批量生成二维码
	 * @param num 二维码生成个数
	 */
	public String generatQr(int num,StringBuilder sb){
		String msg = null;
		int j = 0;
		try{
			String accessToken = WeixinUtils.accessToken();
			for(int i = 0 ; i < num ; i++){
				DeviceMac deviceMac = QrUtils.generalQr(accessToken);
				if(deviceMac != null){
					//logService.logOperation("admin", "生成二维码", deviceMac.toString(), "generateQr", 1);
					logger.info("生成二维码:"+deviceMac.toString());
					WeixinUtils.updateProp(accessToken, deviceMac.getDeviceIdW());
					this.save(deviceMac);
					j++;
				}else{
					//logService.logOperation("admin", "生成二维码", "生成二维码失败", "generateQr", 1);
					logger.error("生成二维码失败");
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			msg = e.getMessage();
			logger.error("生成二维码失败"+msg);
			if(StringUtils.isBlank(msg) || "null".equals(msg)){
				msg = "生成二维码失败";
			}
		}
		logger.info("此次共生成二维码"+j+"个");
		sb.append(j);
		return msg;
	}
	
	public void save(DeviceMac deviceMac) {
		String sql = "insert into device_mac(device_id_w,qrticket) values(?,?)";
		jdbcTemplate.update(sql, deviceMac.getDeviceIdW(), deviceMac.getQrticket());
	}
	/*
	 * 根据DeviceId检查是否存在
	 */
	public boolean checkIsExitDevice(SweepBotBean sweepBotBean) {
		String sql = "select count(1) from device_mac where device_id = ? and qrticket <> ? and is_delete <> 1 ";
		int i = jdbcTemplate.queryForInt(sql, sweepBotBean.getDeviceId(),sweepBotBean.getQrTicket());
		return i > 0;
	}
	/*
	 * 根据DeviceId检查是否存在
	 */
	public boolean checkIsExitDeviceByDeviceId(String deviceId) {
		String sql = "select count(1) from device_mac where device_id = ? and is_delete <> 1 ";
		int i = jdbcTemplate.queryForInt(sql, deviceId);
		return i > 0;
	}
	
	@Transactional
	public String updateDevice(String deviceInfo){
		String msg = null;
		JSONArray array = JSONArray.fromObject(deviceInfo); 
		for(int i = 0; i < array.size(); i++){
			JSONObject jsonObject = array.getJSONObject(i);
			SweepBotBean sweepBotBean = (SweepBotBean) JSONObject.toBean(jsonObject, SweepBotBean.class);
			boolean isExit = true;
			if (sweepBotBean != null && StringUtils.isNotBlank(sweepBotBean.getQrTicket())) {
				isExit = this.checkIsExitDevice(sweepBotBean);
			}else{
				msg = "参数有误";
			}
			if (msg == null) {
				if (!isExit) {
					msg = this.updateDevice(sweepBotBean);
				} else {
					msg = sweepBotBean.getDeviceId() + "已存在";
				}
			}
			if(msg != null){
				throw new RuntimeException(msg);
			}
		} 
		return msg;
	}
	
	
	
	/*
	 * 更新数据库
	 */
	public String updateDevice(SweepBotBean sweepBotBean) {
		String msg = null;
		String sql = "update device_mac set device_id = ?,sweep_type =? where qrticket = ?";
		if(sweepBotBean != null && StringUtils.isNotBlank(sweepBotBean.getQrTicket())){
			jdbcTemplate.update(sql, sweepBotBean.getDeviceId(),sweepBotBean.getModel(),sweepBotBean.getQrTicket());
		}else{
			msg = "参数有误";
		}
		return msg;
	}
	/*
	 * 删除设备
	 */
	public String deleteDevice(String deviceIds,String deviceWIds) {
		String msg = null;
		String sql = "update device_mac set device_id = null,sweep_length = null,sweep_type = null,sweep_version = null,auto_update = 1,force_version = null,is_delete = 0 where device_id in ("+deviceIds+")";
		if(StringUtils.isNotBlank(deviceIds)){
			jdbcTemplate.update(sql);
		}else{
			msg = "参数有误";
		}
		if(msg == null){
			sql = "update device_bind set status = 0 where device_id_w in('"+deviceWIds+"')";
			jdbcTemplate.update(sql);
		}
		return msg;
	}
	
	/**
	 * 导入Excel
	 * @param filePath
	 */
	public Map<String, String> importAndCheckDevcieQr(String filePath) {
		Map<String, String> map = new HashMap<String, String>();
		List<DeviceMac> updateDeviceMacList = null;
		List<String> l = new ArrayList<String>();
		try {
			List<DeviceMac> deviceMacList = new ArrayList<DeviceMac>();
			ArrayList<ArrayList<Object>> allRowList = ExcelReadUtils.readAllRows(filePath);
			int totalSize = allRowList.size() - 1;
			if (allRowList != null && totalSize > 0) {
				map.put("totalSize", totalSize + "");
				map.put("successSize", "0");
				for (int i = 1; i < allRowList.size(); i++) {
					ArrayList<Object> rowList = allRowList.get(i);
					String rowNum = "" + i;
					String deviceId = (String) rowList.get(1);
					String qrticket = (String) rowList.get(2);
					String sweepType = (String) rowList.get(3);
					if (StringUtils.isNotBlank(qrticket) && StringUtils.isNotBlank(deviceId) && StringUtils.isNotBlank(sweepType)) {
						DeviceMac deviceMac = new DeviceMac(null, qrticket, deviceId, rowNum, sweepType);
						deviceMacList.add(deviceMac);
					} else {
						l.add(rowNum+"_"+"该行存在空值");
					}
				}
				if (deviceMacList.size() > 0) {
					updateDeviceMacList = validate(deviceMacList, l);
				}
				try {
					if (l != null && l.size() > 0) {
						for (String s : l) {
							String[] ss = s.split("_");
//								XLSXWelcomeResourceWriter.writeInTemplate("1", Integer.parseInt(s), 4, filePath);
							this.writeExcelResult(filePath,ss[1],Integer.parseInt(ss[0]), 4);
						}
					}else{
						if (updateDeviceMacList != null && updateDeviceMacList.size() > 0) {
							for (DeviceMac dm : updateDeviceMacList) {
//									XLSXWelcomeResourceWriter.writeInTemplate("0", Integer.parseInt(dm.getRowNum()), 4, filePath);
								this.writeExcelResult(filePath,"成功",Integer.parseInt(dm.getRowNum()), 4);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(l.size() == 0){
					updateMacList(updateDeviceMacList);
					map.put("successSize", "" + totalSize);
				}else{
					map.put("successSize", "0");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/*
	 *向EXCEL写入内容
	 */
	/**
	 * 写入非图片格式信息
	 * 
	 * @描述：这是一个实体类，提供了相应的接口，用于操作Excel，在任意坐标处写入数据。
	 * @参数：String newContent：你要输入的内容 int beginRow ：行坐标，Excel从 0 算起 int beginCol
	 *            ：列坐标，Excel从 0 算起
	 * @author dean
	 * @throws IOException 
	 */
	public void writeExcelResult(String templateURL,String newContent,int beginRow,int beginCell) throws IOException {
		FileOutputStream fileOutputStream = null;
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		try {
			boolean a = templateURL.trim().indexOf(".xls") == -1;
			boolean b = templateURL.trim().indexOf(".XLS") == -1;
			if (templateURL == null || templateURL.trim().equals("")) {
				// 文件不能为空提示
				System.out.println("文件不能为空提示");
			} else if (a && b)// && c&&d)
			{
				System.out.println("文件格式不正确！");

			} else {//加载文件
				FileInputStream templateFile_Input = null;
				try {
					templateFile_Input = new FileInputStream(templateURL);
					POIFSFileSystem fs = new POIFSFileSystem(templateFile_Input);
					workbook = new HSSFWorkbook(fs);
					sheet = workbook.getSheetAt(0);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(templateFile_Input != null){
						templateFile_Input.close();
					}
				}
			}
			HSSFRow row = sheet.getRow(beginRow);
			if (null == row) {
				// 如果不做空判断，你必须让你的模板文件画好边框，beginRow和beginCell必须在边框最大值以内
				// 否则会出现空指针异常
				row = sheet.createRow(beginRow);
			}
			HSSFCell cell = row.getCell(beginCell);
			if (null == cell) {
				cell = row.createCell(beginCell);
			}
			// 设置存入内容为字符串
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			HSSFCellStyle style = workbook.createCellStyle(); // 样式对象    
			HSSFFont font=workbook.createFont();
			font.setColor(HSSFColor.RED.index);//HSSFColor.VIOLET.index //字体颜色
			style.setFont(font);
			cell.setCellStyle(style);
			// 向单元格中放入值
			cell.setCellValue(newContent);
			fileOutputStream = new FileOutputStream(templateURL);
	        workbook.write(fileOutputStream);
	        fileOutputStream.close();
	        fileOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fileOutputStream != null){
				fileOutputStream.close();
			}
		}
	}
	
	/**
	 * 验证Excel种数据是否正确
	 * @param deviceMacList
	 * @return
	 */
	private List<DeviceMac> validate(List<DeviceMac> deviceMacList,List<String> l){
		List<DeviceMac> updateDeviceMacList = new ArrayList<DeviceMac>();
		if(deviceMacList.isEmpty()){
			return null;
		}
		Map<String,Integer> map = new HashMap<String, Integer>();
		int i = 1;
		for(DeviceMac devMac : deviceMacList){
			System.out.println("" + i++);
			List<DeviceMac> deviceMacDbList = this.findByQrticket(devMac.getQrticket());
			if(deviceMacDbList != null && deviceMacDbList.size() == 1){
				DeviceMac deviceMac = deviceMacDbList.get(0);
				if(map.containsKey(devMac.getDeviceId())){
					logger.info("Excel中，设备ID："  + devMac.getQrticket()+ "重复了");
					l.add(devMac.getRowNum()+"_"+"EXCEl设备ID存在重复");
					return null;
				}else{
					map.put(devMac.getDeviceId(), 1);
				}
				
				//根据数据库查询设备ID是否存在，若存在 出错信息：设备ID已绑定 continu；
				if(checkIsExitDeviceByDeviceId(devMac.getDeviceId())){
					l.add(devMac.getRowNum()+"_"+"设备ID已绑定 ");
					continue;
				}
				if(StringUtils.isNotBlank(deviceMac.getDeviceId())){
					if(deviceMac.getDeviceId().equals(devMac.getDeviceId())){
						//l.add(devMac.getRowNum());
						continue;
					}else{
						logger.info("二维码"+ devMac.getQrticket() +"的数据库种设备ID("+deviceMac.getDeviceId()+")更新为Excel中的设备ID("+devMac.getDeviceId()+")");
//						updateDeviceMacList.add(devMac);
						l.add(devMac.getRowNum()+"_"+"设备二维码已被使用");
					}
				}else{
					updateDeviceMacList.add(devMac);
				}
			}else{
				logger.info("数据库中二维码" + devMac.getQrticket() + "不存在或者重复");
				map.put(devMac.getDeviceId(), 1);
				l.add(devMac.getRowNum()+"_"+"二维码不存在");
				//return null;
			}
				
		}
		return updateDeviceMacList;
	}
	
	/*
	 *向EXCEL写入内容 
	 */
	public String writeExcelData(File file,Page<SweepBot> page) {
		String msg = null;
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet("设备数据", 0);
			int outputRow = 0;
			sheet.addCell(new Label(0, outputRow, "微信设备ID"));
			sheet.addCell(new Label(1, outputRow, "设备ID"));
			sheet.addCell(new Label(2, outputRow, "设备二维码ticket"));
			sheet.addCell(new Label(3, outputRow, "扫地机型号"));
			
			for(int i =0;i<4;i++){
				sheet.setColumnView(i, 30); // i表示第i列，60表示宽度
			}
			List<SweepBot> list = page.getResult();
			for(SweepBot sb:list){
				outputRow++;
				sheet.addCell(new Label(0, outputRow, sb.getWxDeviceId()));
				sheet.addCell(new Label(1, outputRow, sb.getDeviceId()));
				sheet.addCell(new Label(2, outputRow, sb.getQrTicket()));
				sheet.addCell(new Label(3, outputRow, sb.getModel()));
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		return msg;
	}
	
	public List<DeviceBind> getDeviceBindBydiw(String deviceIdW,int start,int pageSize) {
		String sql = "select * from device_bind t where t.device_id_w = ?  and status <> 0 order by id LIMIT ?,?";
		List<DeviceBind> ret = (List<DeviceBind>)jdbcTemplate.query(sql, new DeviceBindRowMapper(), deviceIdW,start,pageSize);
		if (ret != null && ret.size() > 0) {
			return ret;
		}
		return null;
	}
	
	public List<DeviceBind> getDeviceBindCountBydiw(String deviceIdW) {
		String sql = "select * from device_bind t where t.device_id_w = ?  and status <> 0 order by id ";
		List<DeviceBind> ret = (List<DeviceBind>)jdbcTemplate.query(sql, new DeviceBindRowMapper(), deviceIdW);
		if (ret != null && ret.size() > 0) {
			return ret;
		}
		return null;
	}
	/*
	 * 根据二维码批量更新设备ID
	 */
	private void updateMacList(List<DeviceMac> deviceMacList){
		if(deviceMacList == null || deviceMacList.isEmpty()){
			return;
		}
		int i = 1;
		for(DeviceMac devMac : deviceMacList){
			logger.info((i++) + " " +  devMac.toString());
			this.updateDeviceMacByQrticket(devMac.getDeviceId(), devMac.getSweepType() , devMac.getQrticket());
		}
	}
	public List<DeviceMac> findByQrticket(String qrticket){
		return jdbcTemplate.query("select * from device_mac where qrticket = ?",new DeviceMacRowMapper(), qrticket);
	}
	
	
	public void updateDeviceMacByQrticket(String deviceId, String sweepType,String qrticket){
		jdbcTemplate.update("update device_mac set device_id = ?,sweep_type = ? ,edittime = ? where qrticket = ?", deviceId.toUpperCase(), sweepType.toUpperCase() , new Date(), qrticket);
	}
	
	public List<DeviceVersion> getVersion(){
		return jdbcTemplate.query(" Select DISTINCT(VERSION) as name ,(@rowNum:=@rowNum+1) as value From rom_manager,(Select (@rowNum :=0) ) b where is_delete =0 ",new DeviceVersionRowMapper());
	}
	
	public static class DeviceMacRowMapper implements RowMapper<DeviceMac> {
		@Override
		public DeviceMac mapRow(ResultSet rs, int rowNum) throws SQLException {
			DeviceMac deviceMac = new DeviceMac();
			deviceMac.setDeviceIdW(rs.getString("device_id_w"));
			deviceMac.setQrticket(rs.getString("qrticket"));
			deviceMac.setDeviceId(rs.getString("device_id")==null?null:rs.getString("device_id").toUpperCase());
			deviceMac.setEdittime(rs.getDate("edittime"));
			deviceMac.setSweepType(rs.getString("sweep_type"));
			return deviceMac;
		}
	}
	
	public static class DeviceBindRowMapper implements RowMapper<DeviceBind> {
		@Override
		public DeviceBind mapRow(ResultSet rs, int rowNum) throws SQLException {
			DeviceBind devBind = new DeviceBind();
			devBind.setId(rs.getString("id"));
			devBind.setUserId(rs.getString("user_id"));
			devBind.setDeviceIdW(rs.getString("device_id_w"));
			devBind.setCharacterType(rs.getString("character_type"));
			devBind.setNickName(rs.getString("nick_name"));
			devBind.setCreateTime(rs.getString("create_time"));
			devBind.setStatus(rs.getInt("status"));
			return devBind;
		}
	}
	public static class DeviceVersionRowMapper implements RowMapper<DeviceVersion> {
		@Override
		public DeviceVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
			DeviceVersion deviceMac = new DeviceVersion();
			deviceMac.setName(rs.getString("name"));
			deviceMac.setValue(rs.getString("value"));
			return deviceMac;
		}
	}
	
}
