package com.tow.controller;

import java.sql.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.tow.domain.SearchTicketDB;
import com.tow.domain.vo.SearchReqVO;
import com.tow.service.SearchService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class SearchController {
	@Autowired
	private SearchService searchService;
	
	// 검색 필터 적용해서 Page 검색
	@PostMapping("getSearchData")
	public ResponseEntity<?> searchTickets(@RequestBody SearchReqVO searchReq) {
		// 필터 데이터 : 리스트
		List<String> genreList = searchReq.getGenreFilter();
		List<String> regionList = searchReq.getRegionFilter();
		List<String> siteList = searchReq.getSiteFilter();
		if(genreList.size() == 0) { genreList = null; }
		if(regionList.size() == 0) { regionList = null; }
		if(siteList.size() == 0) { siteList = null; }
		
		// 필터 데이터 : 문자열, 숫자
		String period = searchReq.getPeriod();
		String searchKeyword = searchReq.getSearchKeyword();
		String orderByKey = searchReq.getOrderByKey();
		int pageNum = searchReq.getPageNum();
		
		if(searchKeyword.length() == 0) {
			searchKeyword = null;
		}
		
		// 기간
		Date startDate = null;
		Date endDate = null;
		if(!"전체".equals(period)) {
			String[] periodStr = period.split(" ~ ");
			if(periodStr.length < 2) { // 단일 날짜
				try {
					startDate = searchService.convertStringToSqlDate(periodStr[0]);
					endDate = searchService.convertStringToSqlDate(periodStr[0]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					startDate = searchService.convertStringToSqlDate(periodStr[0]);
					endDate = searchService.convertStringToSqlDate(periodStr[1]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// Pageable
		Pageable pageable = PageRequest.of(pageNum, 20);
		
		// 페이지 결과 받아오기
		Page<SearchTicketDB> searchResults = null;
		if ("popular".equals(orderByKey)) {
		    // 관심순
		    searchResults = searchService.findPageByFiltersOrderByLikes(regionList, genreList, siteList, startDate, endDate, searchKeyword, pageable);
		} else if ("view".equals(orderByKey)) {
		    // 조회순
		    searchResults = searchService.findPageByFiltersOrderByViews(regionList, genreList, siteList, startDate, endDate, searchKeyword, pageable);
		} else {
		    // 가나다순
		    searchResults = searchService.findPageByFiltersOrderByTitle(regionList, genreList, siteList, startDate, endDate, searchKeyword, pageable);
		}
		
		// 결과 반환
		Map<String, Object> response = new HashMap<>();
		response.put("content", searchResults.getContent());
		response.put("totalElements", searchResults.getTotalElements());
		response.put("totalPages", searchResults.getTotalPages());
		
		return ResponseEntity.ok(response);
	}
	
	
	// 자동 완성(검색어에 따라 상위 10개)
	@GetMapping("autoComplete")
	public List<SearchTicketDB> searchAutoComplete(@RequestParam String searchKey) {
		return searchService.searchTop10BySerarchKey(searchKey);
	}
	
	
	
}
