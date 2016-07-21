package com.eastrobot.sweepbot.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ReportBulder {
	public static String outFile_Erro = "Load Template Erro，文件加载失败！！";

	FileOutputStream fileOutputStream = null;
	HSSFWorkbook workbook = null;
	HSSFSheet sheet = null;
	HSSFPatriarch patriarch = null;

	/**
	 * @用途：加载一个已经存在的模板，将生成的内容保存到 workbook中
	 * @参数：String templateFile：指索要加载的模板的路径，如："C:/Tamplates/texting-1.xls"
	 * @用法：templateFile： String templateFile_Name1 =
	 *                   "C:/Tamplates/texting-1.xls"
	 * @author dean
	 */
	public void loadTemplate(String templateURL) {
		boolean a = templateURL.trim().indexOf(".xls") == -1;
		boolean b = templateURL.trim().indexOf(".XLS") == -1;
		if (templateURL == null || templateURL.trim().equals("")) {
			// 文件不能为空提示
			System.out.println("文件不能为空提示");
		} else if (a && b)// && c&&d)
		{
			System.out.println("文件格式不正确！");

		} else {
			try {
				FileInputStream templateFile_Input = new FileInputStream(templateURL);
				POIFSFileSystem fs = new POIFSFileSystem(templateFile_Input);
				workbook = new HSSFWorkbook(fs);
				sheet = workbook.getSheetAt(0);
				System.out
						.println("========" + templateURL + "文件加载已完成========");
			} catch (Exception e) {
				System.err.println(outFile_Erro);
			}
		}

	}

	/**
	 * 写入非图片格式信息
	 * 
	 * @描述：这是一个实体类，提供了相应的接口，用于操作Excel，在任意坐标处写入数据。
	 * @参数：String newContent：你要输入的内容 int beginRow ：行坐标，Excel从 0 算起 int beginCol
	 *            ：列坐标，Excel从 0 算起
	 * @author dean
	 */
	public void writeInTemplate(String newContent, int beginRow, int beginCell) {
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
		// 向单元格中放入值
		cell.setCellValue(newContent);
	}

	/**
	 * 写入图片格式信息
	 * 
	 * @描述：这是一个实体类，提供了相应的接口，用于操作Excel，在任意坐标处写入数据。
	 * @参数： String imageFileURL：他接受一个外界传入的图片路径，图片以 *.jpeg 形式存在。
	 * 
	 * @用法： ReportBuilder twi = new ReportBuilder(); String imageFileURL =
	 *      "D:/workspace/Tamplates/1.jpeg"; twi.writeInTemplate(imageFileURL ,
	 *      0,0, 0, 0, (short)6, 5, (short)8, 8);
	 * 
	 * @param dx1
	 *            ：第一个cell开始的X坐标
	 * @param dy1
	 *            ：第一个cell开始的Y坐标
	 * @param dx2
	 *            ：第二个cell开始的X坐标
	 * @param dy2
	 *            ：第二个cell开始的Y坐标
	 * @param col1
	 *            ：图片的左上角放在第几个列cell (the column(o based); of the first cell)
	 * @param row1
	 *            ：图片的左上角放在第几个行cell (the row(o based); of the first cell)
	 * @param col2
	 *            ：图片的右下角放在第几个列cell (the column(o based); of the second cell)
	 * @param row2
	 *            ：图片的右下角放在第几个行cell (the row(o based); of the second cell)
	 * 
	 * @author dean
	 */
	public void writeInTemplate(String imageFileURL, int dx1, int dy1, int dx2,
			int dy2, short col1, int row1, short col2, int row2) {
		BufferedImage bufferImage = null;

		// 写入图片格式信息
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			// 先把读入的图片放到第一个 ByteArrayOutputStream 中，用于产生ByteArray
			File fileImage = new File(imageFileURL);

			bufferImage = ImageIO.read(fileImage);
			ImageIO.write(bufferImage, "JPG", byteArrayOutputStream);
			System.out.println("ImageIO 写入完成");

			// 准备插入图片
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			HSSFClientAnchor anchor = new HSSFClientAnchor(dx1, dy1, dx2, dy2,
					col1, row1, col2, row2);

			// 插入图片
			byte[] pictureData = byteArrayOutputStream.toByteArray();
			int pictureFormat = HSSFWorkbook.PICTURE_TYPE_JPEG;
			int pictureIndex = workbook.addPicture(pictureData, pictureFormat);
			patriarch.createPicture(anchor, pictureIndex);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IO Erro：" + e.getMessage());
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}
	}

	/**
	 * 写入图片格式信息
	 * 
	 * @描述：这是一个实体类，提供了相应的接口，用于操作Excel，在任意坐标处写入数据。
	 * @参数： ImageInputStream imageInputStream：他接受一个外界传入的图片流，图片以流形式存在。
	 * 
	 * @用法：
	 * 
	 * 
	 * 
	 * 
	 * @param dx1
	 *            ：第一个cell开始的X坐标
	 * @param dy1
	 *            ：第一个cell开始的Y坐标
	 * @param dx2
	 *            ：第二个cell开始的X坐标
	 * @param dy2
	 *            ：第二个cell开始的Y坐标
	 * @param col1
	 *            ：图片的左上角放在第几个列cell (the column(o based); of the first cell)
	 * @param row1
	 *            ：图片的左上角放在第几个行cell (the row(o based); of the first cell)
	 * @param col2
	 *            ：图片的右下角放在第几个列cell (the column(o based); of the second cell)
	 * @param row2
	 *            ：图片的右下角放在第几个行cell (the row(o based); of the second cell)
	 * 
	 * @author dean
	 */
	public void writeInTemplate(ImageInputStream imageInputStream, int dx1,
			int dy1, int dx2, int dy2, short col1, int row1, short col2,
			int row2) {
		BufferedImage bufferImage = null;
		// 写入图片格式信息
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			// 先把读入的图片放到一个 ByteArrayOutputStream 中，用于产生ByteArray
			bufferImage = ImageIO.read(imageInputStream);
			ImageIO.write(bufferImage, "JPG", byteArrayOutputStream);
			System.out.println("ImageIO 写入完成");

			// 准备插入图片
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			HSSFClientAnchor anchor = new HSSFClientAnchor(dx1, dy1, dx2, dy2,
					col1, row1, col2, row2);

			// 插入图片
			byte[] pictureData = byteArrayOutputStream.toByteArray();
			int pictureFormat = HSSFWorkbook.PICTURE_TYPE_JPEG;
			int pictureIndex = workbook.addPicture(pictureData, pictureFormat);
			patriarch.createPicture(anchor, pictureIndex);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IO Erro：" + e.getMessage());
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存模板
	 * 
	 * @描述：这个方法用于保存workbook(工作薄)中的内容，并写入到一个Excel文件中
	 * @参数：String templateFile：取得已经保存的类模板 路径名称
	 * @用法：templateFile：String templateFile_Name1 = "C:/Tamplates/texting-1.xls"
	 *                         TemplateAdapter ta = new TemplateAdapter();
	 *                         ta.SaveTemplate(templateFile_Name1);
	 * @param templateFile
	 */
	public void SaveTemplate(String templateFile) {
		try {

			// 建立输出流
			fileOutputStream = new FileOutputStream(templateFile);
			workbook.write(fileOutputStream);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IO Erro" + e.getMessage());
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}
	}

}
