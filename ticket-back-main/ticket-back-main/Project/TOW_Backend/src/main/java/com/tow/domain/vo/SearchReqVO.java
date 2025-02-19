package com.tow.domain.vo;

import java.util.List;

import lombok.Data;

@Data
public class SearchReqVO {
	// 필터
	private List<String> genreFilter;
	private List<String> regionFilter;
	private List<String> siteFilter;
	private String period;
	private String searchKeyword;
	private String orderByKey;
	
	// 페이징
	private int pageNum;
}
