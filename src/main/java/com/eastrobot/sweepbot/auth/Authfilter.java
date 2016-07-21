package com.eastrobot.sweepbot.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
  
public class Authfilter extends HttpServlet implements Filter {  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static List<String> whiteList;
    public void doFilter(ServletRequest req, ServletResponse resp,  
            FilterChain chain) throws IOException, ServletException {  
  
        HttpServletRequest request = (HttpServletRequest) req;  
        HttpServletResponse response = (HttpServletResponse) resp;  
        HttpSession session = request.getSession(true);  
        String url = request.getRequestURI();  
        String header = request.getHeader("X-Requested-With");
        String isLogin = (String)session.getAttribute("isLogin");
        String locationajx = "../admin/login.jsp"; 
        if (whiteList == null) {
        	String encoding="GBK";
        	whiteList = new ArrayList<String>();
        	InputStream fileis = Authfilter.class.getResourceAsStream("/whiteList.txt");
        	try {
        		InputStreamReader read = new InputStreamReader(fileis,encoding);// 考虑到编码格式
        		BufferedReader bufferedReader = new BufferedReader(read);
        		String lineTxt = null;
        		while ((lineTxt = bufferedReader.readLine()) != null) {
        			whiteList.add(lineTxt);
        		}
        	} catch (Exception e) {
        		e.printStackTrace();
        	}finally{
        		fileis.close();
        	}
        }
        
		for(String s:whiteList){
			if(request.getRequestURI().contains(s)){
				chain.doFilter(request, response);
				return;
			}
		}
		if ("XMLHTTPRequest".equalsIgnoreCase(header) && isLogin == null) {//ajax 请求且没有登陆
        	response.addHeader("error_code", "0");
			response.addHeader("redirect_url", locationajx);
			response.sendError(403);
			return;
		}
        if (isLogin == null ) {  
//            request.getRequestDispatcher(location).forward(request, response);  
//            response.setHeader("Cache-Control", "no-store");  
//            response.setDateHeader("Expires", 0);  
//            response.setHeader("Pragma", "no-cache");  
            response.sendRedirect(request.getContextPath()+"/admin/login.jsp");
        } else {  
            chain.doFilter(request, response);  
        }  
    }  
  
    public void init(FilterConfig arg0) throws ServletException {  
    }  
	/**
	 * 功能：公共方法用于响应前台请求
	 * @param resp
	 * @param data
	 */
	private void printData(HttpServletResponse resp, String data) {
		try {
//			System.out.println(data);
			resp.setContentType("text/html;charset=utf-8");
			resp.setCharacterEncoding("UTF-8");
			PrintWriter out = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), "UTF-8"));
			out.println(data);
			out.close();
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}  