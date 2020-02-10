package com.enjoy.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class RequestVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String inputFilePath;
	private String outputFilePath;
	private String language;
	private String fileId;
	
}
