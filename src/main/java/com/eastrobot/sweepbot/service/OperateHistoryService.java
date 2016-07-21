package com.eastrobot.sweepbot.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.eastrobot.sweepbot.model.OperateHistory;

@Service
public class OperateHistoryService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("sweepManager");
	public void insertOH(HttpServletRequest request, String operateUserId,
			String opereteTypeId, String operateSummary,int isSuccess) {
		String ip = "";
		String mac = "";
		String serverIp = "";
		try {
			ip = getIpAddr(request);
//			mac = getMACAddr(ip);
			serverIp = request.getLocalAddr();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		String operateUserCity = "";
		String sql = "insert into operate_history (operate_user_id,operete_type_id,create_time,operate_user_ip,operate_user_mac,local_server,operate_user_city,operate_summary,is_success)" +
				"values(?,?,sysdate(),?,?,?,?,?,?)";
		try {
			jdbcTemplate.update(sql, operateUserId, opereteTypeId, ip, mac,
					serverIp, operateUserCity, operateSummary,isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("operateUserId = "+operateUserId +",opereteTypeId="+opereteTypeId+",operateSummary ="+operateSummary+",ip="+ip+",mac="+mac+",serverIp="+serverIp+",出错信息："+e.getMessage());
		}
	}
	
	
	public String getOperateList(Map<String, String> condition) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sbud = new StringBuilder(" ");
		sbud.append(" select a.id, b.user_name as operate_user_id,c.operate_name as operete_type_id,a.create_time,a.operate_user_ip,a.is_success ,a.local_server ,a.operate_user_mac,a.operate_user_city,a.operate_summary");
		sbud.append(" from operate_history a left join user_login b on a.operate_user_id = b.id ");
		sbud.append(" left join operate_type c on a.operete_type_id = c.id where 1=1 ");
		int start = 0;
		int limit = 0;
		int count = 0;
		List<Object> params = new ArrayList<Object>();
		if (!condition.isEmpty()) {
			if (condition.containsKey("operateUser")) {
				sbud.append(" and b.user_name like '%" + condition.get("operateUser") + "%' ");
			}
			if (condition.containsKey("createDateStart")) {
				sbud.append(" and a.create_time >= '" + condition.get("createDateStart") + "'");
			}
			if (condition.containsKey("createDateEnd")) {
				sbud.append(" and a.create_time <= '" + condition.get("createDateEnd") + "'");
			}
			sbud.append(" order by a.create_time ");
			try {
				List<OperateHistory> ret1 = null;
				ret1 = (List<OperateHistory>) jdbcTemplate.query(
						sbud.toString(), new OperateHistoryRowMapper(),
						params.toArray(new Object[params.size()]));
				if(ret1 != null){
					count = ret1.size();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (condition.containsKey("start") && condition.containsKey("limit")) {
				sbud.append(" LIMIT ?,? ");
				start = Integer.parseInt(condition.get("start"));
				limit = Integer.parseInt(condition.get("limit"));
				params.add(start);
				params.add(limit);
			}
		}
		List<OperateHistory> ret = null;
		ret = (List<OperateHistory>) jdbcTemplate.query(sbud.toString(), new OperateHistoryRowMapper(), params.toArray(new Object[params.size()]));
		StringBuffer sb = new StringBuffer("");
		sb.append("{'totalCount':'" + count + "','products':[");
		if(ret != null){
			for(int i=0;i<ret.size();i++){
				sb.append(mapper.writeValueAsString(ret.get(i)));
				if((i+1) == ret.size()){
					
				}else{
					sb.append(",");
				}
			}
		}
		sb.append("]}");
		return sb.toString();
	}
	
	private static String getIpAddr(HttpServletRequest request) {   
		String ipAddress = null;   
		//ipAddress = this.getRequest().getRemoteAddr();   
		ipAddress = request.getHeader("x-forwarded-for");   
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
			ipAddress = request.getHeader("Proxy-Client-IP");   
		}   
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
			ipAddress = request.getHeader("WL-Proxy-Client-IP");   
		}   
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
			ipAddress = request.getRemoteAddr();   
			if(ipAddress.equals("127.0.0.1")){   
				//根据网卡取本机配置的IP   
				InetAddress inet=null;   
				try {   
					inet = InetAddress.getLocalHost();   
				} catch (UnknownHostException e) {   
					e.printStackTrace();   
				}   
				ipAddress= inet.getHostAddress();   
			}   
			
		}   
		//对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割   
		if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15   
			if(ipAddress.indexOf(",")>0){   
				ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));   
			}   
		}   
		return ipAddress;    
	}   
	
//	private static String getMACAddr(String ip){ 
//        String str = ""; 
//        String macAddress = ""; 
//        try { 
//            Process p = Runtime.getRuntime().exec("nbtstat -A " + ip); 
//            InputStreamReader ir = new InputStreamReader(p.getInputStream()); 
//            LineNumberReader input = new LineNumberReader(ir); 
//            for (int i = 1; i < 100; i++) { 
//                str = input.readLine(); 
//                if (str != null) { 
//                    if (str.indexOf("MAC Address") > 1) { 
//                        macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length()); 
//                        break; 
//                    } 
//                  if (str.indexOf("MAC Address") > 1) { 
//                        macAddress = str.substring(str.indexOf("MAC 地址") + 14, str.length()); 
//                        break; 
//                    } 
//					//以上有个判断，不同系统cmd命令执行的返回结果展示方式不一样，我测试的win7是MAC 地址 
//					//所以又第二个if判断 你可先在你机器上cmd测试下nbtstat -A 命令 当然得有一个你可以ping通的
//					//网络ip地址,然后根据你得到的结果中mac地址显示方式来确定这个循环取值
//                } 
//            } 
//        } catch (IOException e) { 
//            e.printStackTrace(System.out); 
//        } 
//        return macAddress; 
//    }
	
	private static String  getServerIp(){  
		String serverIp = null;
	    try {  
	        Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();  
	        InetAddress ip = null;  
	        while (netInterfaces.hasMoreElements()) {  
	            NetworkInterface ni = (NetworkInterface) netInterfaces  
	                    .nextElement();  
	            ip = (InetAddress) ni.getInetAddresses().nextElement();  
	            serverIp = ip.getHostAddress();  
	            if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()  
	                    && ip.getHostAddress().indexOf(":") == -1) {  
	            	serverIp = ip.getHostAddress();  
	                break;  
	            } else {  
	                ip = null;  
	            }  
	        }  
	    } catch (SocketException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    }  
	     
	     return serverIp;  
	   }
	
	
	public static class OperateHistoryRowMapper implements RowMapper<OperateHistory> {
		@Override
		public OperateHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
			OperateHistory rm = new OperateHistory();
			rm.setId(rs.getString("id"));
			rm.setIsSuccess(rs.getInt("is_success"));
			rm.setCreateTime(rs.getString("create_time"));
			rm.setLocalServer(rs.getString("local_server"));
			rm.setOperateSummary(rs.getString("operate_summary"));
			rm.setOperateUserCity(rs.getString("operate_user_city"));
			rm.setOperateUserId(rs.getString("operate_user_id"));
			rm.setOperateUserIp(rs.getString("operate_user_ip"));
			rm.setOperateUserMac(rs.getString("operate_user_mac"));
			rm.setOpereteTypeId(rs.getString("operete_type_id"));
			return rm;
		}
	}
}

