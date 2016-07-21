package com.eastrobot.sweepbot.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 
 * @author baoy
 *
 */
public interface CellStyles {
	
	void setWorkBook(Workbook workbook);
	
	/**
	 * 标题样式
	 * @return
	 */
	CellStyle getHeaderStyle();
	
	/**
	 * String样式
	 * @return
	 */
	CellStyle getStringStyle();
	
	/**
	 * 日期样式
	 * @return
	 */
	CellStyle getDateStyle();
	
	/**
	 * 数字样式
	 * @return
	 */
	CellStyle getNumberStyle();

	/**
	 * 合计列样式
	 * @return
	 */
	CellStyle getFormulaStyle();
	
	/**
	 * 获取粉红色背景颜色
	 * @return
	 */
	CellStyle getPinkBgStyle();
	
	/**
	 * 获取黄色背景色
	 * @return
	 */
	CellStyle getYellowBgStyle();
	
	/**
	 * 获取绿色背景色
	 * @return
	 */
	CellStyle getGreenBgStyle();
}
