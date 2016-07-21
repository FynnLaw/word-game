package com.eastrobot.sweepbot.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import com.incesoft.tools.excel.xlsx.Sheet;
import com.incesoft.tools.excel.xlsx.SimpleXLSXWorkbook;

public class XLSXWelcomeResourceWriter  {

	private SimpleXLSXWorkbook workbook;
	private Sheet sheet;
	private OutputStream output;
	
	
	public XLSXWelcomeResourceWriter(OutputStream output) {
		this.workbook = new SimpleXLSXWorkbook(new File(getClass().getResource("/empty3.xlsx").getFile()));
		this.sheet = workbook.getSheet(0, true);
		this.output = output;
	}
	/*
	 * newContent 0:成功，1失败
	 */
    public static void writeInTemplate( String flag, int r, int c,String path) throws IOException  
    {     
		FileInputStream fis = new FileInputStream(path);
		XSSFWorkbook wb = new XSSFWorkbook( fis );
		XSSFSheet sheet = wb.getSheetAt(0);  
		XSSFRow  row  = sheet.getRow(r);   
        if(null == row ){  
            //如果不做空判断，你必须让你的模板文件画好边框，beginRow和beginCell必须在边框最大值以内  
            //否则会出现空指针异常  
            row = sheet.createRow(r);  
        }  
        XSSFCell   cell   = row.getCell(c);  
        if(null == cell){  
            cell = row.createCell(c);  
        }  
        //设置存入内容为字符串  
        cell.setCellType(XSSFCell.CELL_TYPE_STRING);  
        //向单元格中放入值  
        if("0".equals(flag)){
        	cell.setCellValue("成功");
        }else{
	    	XSSFCellStyle ztStyle = (XSSFCellStyle) wb.createCellStyle();
	    	Font ztFont = wb.createFont();
	    	ztFont.setColor(Font.COLOR_RED);            // 将字体设置为“红色”   
	    	ztStyle.setFont(ztFont);                    // 将字体应用到样式上面   
        	cell.setCellValue("失败");
        	cell.setCellStyle(ztStyle);
        }
        OutputStream output = new FileOutputStream(path);
        wb.write(output);
        output.close();
        output.flush();
    }  

	public void write(List<String> list,String path) {
//		title,welcome,semantic,type,platform
		for(String row:list){
			this.sheet.modify(Integer.parseInt(row), 4, "123", null);
		}
		try {
			this.workbook.commit(this.output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws IOException{
		
//		String path = System.getProperty("user.dir")+"\\src\\main\\resources\\456.xlsx";
//		List<String> list = new ArrayList<String>();
//		list.add("0");
//		list.add("1");
//		list.add("2");
//		OutputStream output = new FileOutputStream(path);
//		XLSXWelcomeResourceWriter x = new XLSXWelcomeResourceWriter(output);
//		x.write(list,path);
		writeInTemplate("成功", 1, 1, "H:\\jetty-distribution-7.4.2.v20110526\\webapps\\robot-dev\\uploadFile\\1436245991739.xlsx");
	}

}
