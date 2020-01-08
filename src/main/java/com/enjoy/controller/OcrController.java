package com.enjoy.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.enjoy.common.utils.ToolUtils;
import com.enjoy.model.RequestVO;
import com.enjoy.model.ResultVO;
import com.enjoy.service.OcrService;
import com.enjoy.service.PDF2IMG;

@RestController
public class OcrController {
	@Autowired
	private OcrService ocrService;
	
	/**
	 * 1.将PDF文件转换成.jpg文件
	 * 2.识别.jpg中的文字
	 * @throws Exception 
	 */
	@PostMapping("/ocr/pdfToText")
	public ResultVO pdfToText(@RequestBody RequestVO requestVO) throws Exception {
		ResultVO resultVO = new ResultVO();
		String inputFilePath = requestVO.getInputFilePath();
		String outputFilePath = requestVO.getOutputFilePath();
		String language = requestVO.getLanguage();
		String tessPath = requestVO.getTessPath();
		if(requestVO==null || 
				ToolUtils.isEmpty(inputFilePath) || 
				ToolUtils.isEmpty(outputFilePath) || 
				ToolUtils.isEmpty(language) || 
				ToolUtils.isEmpty(requestVO.getTessPath())) {
			resultVO.setCode(400);
			resultVO.setMsg("请求参数不正确,输入路径,输出路径,语言或者Tesseract路径不正确");
			return resultVO;
		}
		ocrService.setTessPath(tessPath);
		if(inputFilePath.endsWith("pdf")) {
			PDF2IMG.pdf2Img(inputFilePath);
			inputFilePath = inputFilePath.substring(0,inputFilePath.indexOf(".")) + ".jpg";
		}
		ocrService.recognizeText(new File(inputFilePath),new File(outputFilePath),language);
		resultVO.setCode(200);
		resultVO.setMsg("处理成功");;
		return resultVO;	
	}
}
