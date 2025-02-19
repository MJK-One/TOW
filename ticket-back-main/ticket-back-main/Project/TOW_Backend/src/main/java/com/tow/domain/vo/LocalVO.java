package com.tow.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalVO {
	private String title;
	private String address;
	private String mapx;
	private String mapy;
}
