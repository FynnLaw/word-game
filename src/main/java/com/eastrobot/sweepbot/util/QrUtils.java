package com.eastrobot.sweepbot.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import javax.imageio.ImageIO;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.eastrobot.sweepbot.model.DeviceMac;
import com.swetake.util.Qrcode;

public class QrUtils {

	public static String DEVICE_NEW = 	"https://api.weixin.qq.com/device/getqrcode?access_token=${accessToken}";
	
	/**
	 * 生成二维码
	 * @param token
	 * @throws Exception
	 */
	public static DeviceMac generalQr(String token){
		try{
			String strUrl = DEVICE_NEW.replace("${accessToken}", token);
			String deviceResp = HttpUtils.get(strUrl);
			System.out.println(deviceResp);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> map = objectMapper.readValue(deviceResp, Map.class);
			Map<String, Object> baseRespMap = (Map<String, Object>) map.get("base_resp");
			Integer errcode = (Integer) baseRespMap.get("errcode");
			if (errcode != null && errcode == 0) {
				String deviceIdW = (String) map.get("deviceid");
				String qrticket = (String) map.get("qrticket");
				DeviceMac deviceMac = new DeviceMac(deviceIdW, qrticket);
				return deviceMac;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static Logger logger = org.slf4j.LoggerFactory.getLogger("sweep");
	
	
	public static BufferedImage createQrBufferImage(String qrticket, int width, int height) {
		try {
			Qrcode testQrcode = new Qrcode();
			testQrcode.setQrcodeErrorCorrect('Q');
			testQrcode.setQrcodeEncodeMode('B');
			testQrcode.setQrcodeVersion(5);
			String testString = qrticket;
			byte[] d = testString.getBytes("UTF-8");
			System.out.println(d.length);
			// 限制最大字节数为120
			if (d.length > 0 && d.length < 120) {
				boolean[][] s = testQrcode.calQrcode(d);
				BufferedImage bi = new BufferedImage(s.length, s[0].length, BufferedImage.TYPE_BYTE_BINARY);
				Graphics2D g = bi.createGraphics();
				g.setBackground(Color.WHITE);
				g.clearRect(0, 0, s.length, s[0].length);
				g.setColor(Color.BLACK);
				int mulriple = 1;
				for (int i = 0; i < s.length; i++) {
					for (int j = 0; j < s.length; j++) {
						if (s[j][i]) {
							g.fillRect(j * mulriple, i * mulriple, mulriple, mulriple);
						}
					}
				}
				g.dispose();
				bi.flush();
				bi = resize(bi, width, height);
				return bi;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * @param qrticket
	 *            二维码ticket
	 * @param filePath
	 *            二维码保存地址
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 */
	public static void createQrImage(String qrticket, String filePath, int width, int height) {
		try {
			File f = new File(filePath);
			if (!f.exists()) {
				f.createNewFile();
			}
			BufferedImage bi = createQrBufferImage(qrticket, width, height);
			// 创建图片
			ImageIO.write(bi, "png", f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图像缩放
	 * 
	 * @param source
	 * @param targetW
	 * @param targetH
	 * @return
	 */
	private static BufferedImage resize(BufferedImage source, int targetW, int targetH) {
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		target = new BufferedImage(targetW, targetH, type);
		Graphics2D g = target.createGraphics();
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}
}
