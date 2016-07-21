package com.eastrobot.sweepbot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eastrobot.sweepbot.constants.Constants;
import com.eastrobot.robotface.RobotPushServiceProxy;
import com.eastrobot.robotface.domain.PushReceiver;
import com.incesoft.ibotsdk.output.ImageTextMessage;
/**
 * 微信硬件工具类
 * @author lufey
 *
 */
public class WeixinUtils {

	static Logger logger = LoggerFactory.getLogger("sweep");

	private static String GET_OPENID_URL = "https://api.weixin.qq.com/device/get_openid?access_token=${accessToken}&device_type=${deviceType}&device_id=${deivceId}";
	
	private static String DEVICE_STATUS_URL = "https://api.weixin.qq.com/device/get_stat?access_token=${accessToken}&device_id=${deviceId}";
	
	private static String UNBIND_URL = "https://api.weixin.qq.com/device/compel_unbind?access_token=${accessToken}";
	
	private static String BIND_URL = "https://api.weixin.qq.com/device/compel_bind?access_token=${accessToken}";

	public static String DEVICE_AUTH_URL = "https://api.weixin.qq.com/device/authorize_device?access_token=${accessToken}";
	/**
	 * 获取AccessToken
	 * @return
	 */
	public static String accessToken(){
		String data = HttpUtils.get("http://127.0.0.1/weixin/"+Constants.WEIXIN_APP_ID+"?secret="+Constants.WEIXIN_SECRET);
		String accessToken = null;
		if (StringUtils.isNotBlank(data)) {
			String[] dataArr = data.split(",");
			accessToken = dataArr[0];
		}
		return accessToken;
	}
	
	/**
	 * 设备状态查询
	 * @param deviceId
	 * @return
	 */
	public static String getDeviceStatus(String deviceIdW){
		String status = null;
		try{
			String deviceStatusUrl = DEVICE_STATUS_URL.replace("${accessToken}", accessToken()).replace("${deviceId}", deviceIdW);
			String deviceStatusResp = HttpUtils.get(deviceStatusUrl);
			// 0：未授权  1：已经授权（尚未被用户绑定） 2：已经被用户绑定 3：属性未设置
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String,Object> deviceStatusMap = objectMapper.readValue(deviceStatusResp, Map.class);
			String errorcode = deviceStatusMap.get("errcode").toString();
			if("0".equals(errorcode)){
				status = deviceStatusMap.get("status").toString();
			}else{
				logger.error("Exception:设备状态获取失败:" + deviceStatusResp);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return status;
	}
	
	/**
	 * 设备bind
	 * @param deviceIdW
	 * @param userId
	 * @return
	 */
	public static boolean bind(String deviceIdW,String userId){
		boolean flag = false;
		try{
			ObjectMapper objectMapper = new ObjectMapper();
			System.out.println("--------------------------------"+BIND_URL);
			String bindUrl = BIND_URL.replace("${accessToken}", accessToken());
			String jsonParam = "{\"device_id\": \""+deviceIdW+"\", \"openid\": \""+userId+"\"}";
			String resp = HttpUtils.post(bindUrl, jsonParam);
			Map<String,Object> bindMap = objectMapper.readValue(resp, Map.class);
			Map<String,Object> baseRespMap = (Map<String,Object>)bindMap.get("base_resp");
			String errcode = baseRespMap.get("errcode").toString();
			if("0".equals(errcode)){
				flag = true;
			}else{
				logger.error("Exception:绑定设备失败:" + resp);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return flag;
	}
	
	/**
	 * 
	 * @param deviceIdW
	 * @param userId
	 */
	public static boolean unbind(String deviceIdW,String userId){
		boolean flag = false;
		try{
			String unbindUrl = UNBIND_URL.replace("${accessToken}", accessToken());
			String jsonParam = "{\"device_id\": \""+deviceIdW+"\", \"openid\": \""+userId+"\"}";
			String resp = HttpUtils.post(unbindUrl, jsonParam);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String,Object> bindMap = objectMapper.readValue(resp, Map.class);
			Map<String,Object> baseRespMap = (Map<String,Object>)bindMap.get("base_resp");
			String errcode = baseRespMap.get("errcode").toString();
			if("0".equals(errcode)){
				flag = true;
			}else{
				logger.error("Exception:解绑设备失败:" + resp);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return flag;
	}
	
	public static void unbind(String deviceIdW){
		List<String> openIds = getOpenIds(deviceIdW);
		if(openIds!=null && openIds.size() > 0){
			for(String openId : openIds){
				unbind(deviceIdW, openId);
			}
		}
	}
	
	
	/**
	 * 获取设备绑定openID
	 * @return
	 */
	public static List<String> getOpenIds(String deviceIdW){
		List<String> openIdList = null;
		try{
			String getOpenIdUrl = GET_OPENID_URL.replace("${accessToken}", accessToken()).replace("${deviceType}", Constants.DEVICE_TYPE);
			if(StringUtils.isNotBlank(deviceIdW));
			getOpenIdUrl = getOpenIdUrl.replace("${deivceId}", deviceIdW);
			String resp = HttpUtils.get(getOpenIdUrl);	
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String,Object> respMap = objectMapper.readValue(resp, Map.class);
			if(respMap.containsKey("errcode")){
				logger.error("Exception:获取设备状态失败:" + resp);
				return null;
			}
			openIdList = (List<String>)respMap.get("open_id");
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return openIdList;
	}
	
	/**
	 * 更新属性
	 */
	public static void updateProp(String token,String deviceId){
		try{
			ObjectMapper objectMapper = new ObjectMapper();
			// 更新设备信息
			String deviceAuthUrl = DEVICE_AUTH_URL.replace("${accessToken}", token);
			String params1 = "{\"device_num\":\"1\", \"device_list\":[{ \"id\":\"" + deviceId
					+ "\", \"mac\":\"1234567890AB\", \"connect_protocol\":\"4\", \"auth_key\":\"1234567890ABCDEF1234567890ABCDEF\", \"close_strategy\":\"1\","
					+ "\"conn_strategy\":\"1\", \"crypt_method\":\"1\", \"auth_ver\":\"1\", \"manu_mac_pos\":\"-1\",  \"ser_mac_pos\":\"-1\"}],\"op_type\":\"1\"}";
			String deviceidJson = HttpUtils.post(deviceAuthUrl, params1);
			Map<String, Object> deviceMap = objectMapper.readValue(deviceidJson, Map.class);
			List respList = (List) deviceMap.get("resp");
			Map<String, Object> resp = (Map<String, Object>) respList.get(0);
			Integer errcode = (Integer) resp.get("errcode");
			if (errcode != null && errcode == 0) {
				 
			}else{
				logger.error("更新属性错误"+deviceidJson);
				throw new RuntimeException("更新属性错误"+deviceidJson);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//***************************************************
	//微信被回复消息
	//***************************************************
	private static RobotPushServiceProxy pushService = new RobotPushServiceProxy();
	
	public static void sendText(List<String> userIds , String text){
		if(userIds != null && userIds.size() > 0){
			for(String userId : userIds)
				sendText(userId, text);
		}
	}

	public static boolean sendText(String userId, String text) {
		PushReceiver r = new PushReceiver();
		r.setId(userId);
		r.setPlatform("weixin");
		return pushService.pushText(r, text);
 	}

	public static boolean sendImgTxt(String userId, ImageTextMessage data) {
		if (data != null) {
			List<ImageTextMessage> dataList = new ArrayList<ImageTextMessage>();
			dataList.add(data);
			return sendImgTxt(userId, dataList);
		}
		return false;
	}

	public static boolean sendImgTxt(String userId, List<ImageTextMessage> data) {
		PushReceiver r = new PushReceiver();
		r.setId(userId);
		r.setPlatform("weixin");
		return pushService.pushImageText(r, data);
 	}
	
	public static boolean sendStatus(String userId,String deviceIdW,String status){
		PushReceiver r = new PushReceiver();
		r.setId(userId);
		r.setPlatform("weixin");
		System.out.println("更新设备状态：" + userId + " deviceIdW:" + deviceIdW);
		return pushService.pushDeviceStatus(r, deviceIdW, status);
	}
	
	public static final String STATUS_ONLINE = "1";
	public static final String STATUS_OFFLINE = "0";
	public static final Integer STATUS_ONLINE_INTEGER = 1;
	public static final Integer STATUS_OFFLINE_INTEGER = 0;

	public static void updateStatus(List<String> userIds, String deviceIdW, String status) {
		if (userIds != null && userIds.size() > 0) {
			for (String userId : userIds)
				WeixinUtils.sendStatus(userId, deviceIdW, status);
		}
	}
	
	//****************************************************
	// Main
	//****************************************************
	public static void main(String[] args) {
		String accessToken =  WeixinUtils.accessToken();
		System.out.println(accessToken);
	}
	
}
