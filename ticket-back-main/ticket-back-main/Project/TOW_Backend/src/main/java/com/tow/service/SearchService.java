package com.tow.service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tow.domain.SearchTicketDB;
import com.tow.repository.SearchTicketRepository;

@Service
public class SearchService {
	@Autowired
	private SearchTicketRepository sticketRep;
	
	public Date convertStringToSqlDate(String dateString) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		java.util.Date parseDate = dateFormat.parse(dateString);
		return new java.sql.Date(parseDate.getTime());
	}
	
	public Page<SearchTicketDB> findPageByFilters(List<String> regionList, List<String> genreList, List<String> siteList,
			Date startDate, Date endDate, String searchKeyword,
			Pageable pageable) {
		return sticketRep.findPageByFilters(regionList, genreList, siteList, startDate, endDate, searchKeyword, pageable);
	}
	
	// 조회순
	public Page<SearchTicketDB> findPageByFiltersOrderByViews(List<String> regionList, List<String> genreList, List<String> siteList,
			Date startDate, Date endDate, String searchKeyword,
			Pageable pageable) {
		return sticketRep.findPageByFiltersOrderByViews(regionList, genreList, siteList, startDate, endDate, searchKeyword, pageable);
	}
	
	// 좋아요 순
	public Page<SearchTicketDB> findPageByFiltersOrderByLikes(List<String> regionList, List<String> genreList, List<String> siteList,
			Date startDate, Date endDate, String searchKeyword,
			Pageable pageable) {
		return sticketRep.findPageByFiltersOrderByLikes(regionList, genreList, siteList, startDate, endDate, searchKeyword, pageable);
	}
	
	// 가나다순
	public Page<SearchTicketDB> findPageByFiltersOrderByTitle(List<String> regionList, List<String> genreList, List<String> siteList,
			Date startDate, Date endDate, String searchKeyword,
			Pageable pageable) {
		return sticketRep.findPageByFiltersOrderByTitle(regionList, genreList, siteList, startDate, endDate, searchKeyword, pageable);
	}
	
	public List<SearchTicketDB> searchTop10BySerarchKey(String searchKeyword) {
		Pageable topTen = PageRequest.of(0, 10); //top10
		return sticketRep.findTop10BySearchKey(searchKeyword, topTen);
	}
}
