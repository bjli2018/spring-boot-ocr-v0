package com.enjoy.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class OcrService {
	private final static String LANG_OPTION = "-l";
	private final static String EOL = System.getProperty("line.separator");
	
	/**
	 * Tesseract-OCR的安装路径
	 */
	private static String tessPath = "C:\\Users\\Administrator\\Tesseract-OCR";
	public void setTessPath(String tessPath) {
		OcrService.tessPath = tessPath;
	}
	

	/**
	 * @param imageFile   传入的图像文件
	 * @param imageFormat 传入的图像格式
	 * @return 识别后的字符串
	 */
	public String recognizeText(File inputFile,File outputFile,String language) throws Exception {
		StringBuffer strB = new StringBuffer();
		List<String> cmd = new ArrayList<String>();

		String outputFileName = outputFile.getName();
		
		cmd.add(tessPath + "//tesseract");
		cmd.add("");
		cmd.add(outputFile.getParent() + "\\" + outputFileName.substring(0,outputFileName.indexOf(".")));
		cmd.add(LANG_OPTION);
		cmd.add(language);
		// cmd.add("eng");

		ProcessBuilder pb = new ProcessBuilder();
		/**
		 * Sets this process builder's working directory.
		 */
		pb.directory(inputFile.getParentFile());
		cmd.set(1, inputFile.getName());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		long startTime = System.currentTimeMillis();
		String format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);  
		System.out.println("开始时间：" + sdf.format(new Date(startTime)));
		Process process = pb.start();
		// tesseract.exe 1.jpg 1 -l chi_sim
		// 不习惯使用ProcessBuilder的，也可以使用Runtime，效果一致
		// Runtime.getRuntime().exec("tesseract.exe 1.jpg 1 -l chi_sim");
		/**
		 * the exit value of the process. By convention, 0 indicates normal termination.
		 */
//	      System.out.println(cmd.toString());
		int w = process.waitFor();
		if (w == 0)// 0代表正常退出
		{
			BufferedReader in = new BufferedReader(
					new InputStreamReader(new FileInputStream(outputFile), "UTF-8"));
			String str;

			while ((str = in.readLine()) != null) {
				strB.append(str).append(EOL);
			}
			in.close();

			long endTime = System.currentTimeMillis();
			System.out.println("结束时间：" + sdf.format(new Date(endTime)));
			System.out.println("耗时：" + (endTime - startTime)/1000 + "秒");
		} else {
			String msg;
			switch (w) {
			case 1:
				msg = "Errors accessing files. There may be spaces in your image's filename.";
				break;
			case 29:
				msg = "Cannot recognize the image or its selected region.";
				break;
			case 31:
				msg = "Unsupported image format.";
				break;
			default:
				msg = "Errors occurred.";
			}
			throw new RuntimeException(msg);
		}
		// new File(outputFile.getAbsolutePath() + ".txt").delete();
		return strB.toString().replaceAll("\\s*", "");
	}
}
