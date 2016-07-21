package com.eastrobot.sweepbot.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RobotContextListener implements ServletContextListener {

	public static final String KEY_CONTEXT_BASE_REALPATH = "dev.contextBaseRealPath";

	public static final String getContextBaseRealPath() {
		return System.getProperty(KEY_CONTEXT_BASE_REALPATH);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		InputStream is = this.getClass().getResourceAsStream("/app.properties");
		if (is != null) {
			Properties props = new Properties();
			try {
				props.load(is);
				for (Entry<Object, Object> entry : props.entrySet()) {
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					if (key.startsWith("dev.") || key.startsWith("robot.") || key.startsWith("cloud."))
						System.setProperty(key, value);
				}
				String realPath = arg0.getServletContext().getRealPath("/");
				System.setProperty(KEY_CONTEXT_BASE_REALPATH, realPath);
			} catch (IOException ignored) {}
		} else {
			throw new IllegalStateException("app.properties file is missing!");
		}
	}

}
