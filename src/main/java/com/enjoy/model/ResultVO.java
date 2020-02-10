package com.enjoy.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResultVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer code;
	private String msg;
	private String fileId;
}
