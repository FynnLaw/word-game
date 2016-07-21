package com.eastrobot.sweepbot.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpUtils {

	final static Log log = LogFactory.getLog(HttpUtils.class);

	public static HttpClient getHttpClient() {
		return getHttpClient("utf-8", 5000, 10000);
	}

	public static HttpClient getHttpClient(int connTimeout, int soTimeout) {
		return getHttpClient("utf-8", connTimeout, soTimeout);
	}

	public static HttpClient getHttpClient(String encoding) {
		return getHttpClient(encoding, 5000, 10000);
	}

	public static HttpClient getHttpClient(String encoding, int connTimeout, int soTimeout) {
		HttpClient client = new HttpClient();
		client.getParams().setContentCharset(encoding);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(connTimeout);
		client.getParams().setSoTimeout(soTimeout);
		return client;
	}

	public static String get(String url) {
		return get(url, new String[] {});
	}

	public static String get(String url, String... headers) {
		GetMethod method = new GetMethod(url);
		try {
			HttpClient client = getHttpClient();
			if (headers != null && headers.length % 2 == 0) {
				for (int i = 0; i < headers.length; i = i + 2) {
					method.setRequestHeader(headers[i], headers[i + 1]);
				}
			}
			int status = client.executeMethod(method);
			if (status == 200) {
				return method.getResponseBodyAsString();
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			method.releaseConnection();
		}
		return null;
	}

	public static GetMethod getAndReturnMethod(String url) {
		GetMethod method = new GetMethod(url);
		try {
			int status = getHttpClient().executeMethod(method);
			if (status == 200) {
				return method;
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {}
		return null;
	}

	public static String post(String url, Object... params) {
		if (params.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Invalid number of parameters; each name must have a corresponding value!");
		}
		PostMethod method = new PostMethod(url);
		for (int i = 0; i < params.length; i += 2) {
			if (params[i] == null || params[i + 1] == null)
				continue;
			method.addParameter(params[i].toString(), params[i + 1].toString());
		}
		try {
			int status = getHttpClient().executeMethod(method);
			if (status == 200) {
				return method.getResponseBodyAsString();
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			method.releaseConnection();
		}
		return null;
	}

	public static String post(String url, Map<String, String> params) {
		return post(url, "UTF-8", params);
	}

	public static String post(String url, String encoding, Map<String, String> params) {
		if (params == null) {
			throw new IllegalArgumentException(
					"Invalid number of parameters; each name must have a corresponding value!");
		}
		PostMethod method = new PostMethod(url);
		for (String key : params.keySet()) {
			String p = params.get(key);
			if (key == null || p == null)
				continue;
			method.addParameter(key, p);
		}
		try {
			int status = getHttpClient(encoding).executeMethod(method);
			if (status == 200) {
				return method.getResponseBodyAsString();
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			method.releaseConnection();
		}
		return null;
	}

	public static Proxy getHttpProxy() {
		Proxy proxy = null;
		String host = System.getProperty("robot.http.proxyHost");
		String port = System.getProperty("robot.http.proxyPort", "80");
		if (host != null)
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, Integer.parseInt(port)));
		return proxy;
	}

	public static URLConnection getURLConnection(String url) {
		URLConnection conn = null;
		Proxy proxy = getHttpProxy();
		try {
			conn = (proxy == null) ? new URL(url).openConnection() : new URL(url).openConnection(proxy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 发送HttpPost请求
	 * 
	 * @param strURL
	 *            服务地址
	 * @param params
	 *            json字符串,例如: "{ \"id\":\"12345\" }" ;其中属性名必须带双引号<br/>
	 * @return 成功:返回json字符串<br/>
	 */
	public static String post(String strURL, String params) {
		String respStr = "";
		try {
			URL url = new URL(strURL);// 创建连接
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setUseCaches(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("POST"); // 设置请求方式
			httpConn.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
			httpConn.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			httpConn.connect();
			OutputStreamWriter out = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8"); // utf-8编码
			out.append(params);
			out.flush();
			out.close();
			// 获取相应码
			int respCode = httpConn.getResponseCode();
			if (respCode == 200) {
				respStr =  convertStream2Json(httpConn.getInputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}

	public static String convertStream2Json(InputStream inputStream) {
		String jsonStr = "";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		// 将输入流转移到内存输出流中
		try {
			while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, len);
			}
			// 将内存流转换为字符串
			jsonStr = new String(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}
	


	public static void main(String[] args) {
		String post = post(
				"https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=afLjO8WPBlG6mrvhmHdAc6aSXXSP0J5rR4c_ljqWhXA4Mk8lbmOl3hos8_8cJruagZEC3onuIINykYTBzxpRgxyLtu_rgveViBX76Zrs99w",
				"type", "news");
		System.out.println(post);
	}

}
