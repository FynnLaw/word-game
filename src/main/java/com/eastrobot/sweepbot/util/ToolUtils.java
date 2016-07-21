package com.eastrobot.sweepbot.util;

import org.apache.commons.lang.StringUtils;

public class ToolUtils {
	public static boolean isNotBlank(String val){
		return StringUtils.isNotBlank(val) && !"null".equals(val);
	}
}
