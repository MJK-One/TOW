package com.tow.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tow.domain.SearchTicketDB;

@Repository
public interface SearchTicketRepository extends JpaRepository<SearchTicketDB, Integer> {
	// 페이지
	@Query("SELECT s FROM SearchTicketDB s LEFT JOIN s.eventSites es " +
			"WHERE (:regionList IS NULL OR s.region IN :regionList) " +
			"AND (:genreList IS NULL OR s.genre IN :genreList) " +
			"AND (:siteList IS NULL OR es.sales_site IN :siteList) " +
			"AND (:startDate IS NULL OR :endDate IS NULL OR (s.event_start_date <= :endDate AND s.event_end_date >= :startDate)) "+
			"AND (:searchKeyword IS NULL OR s.event_name LIKE %:searchKeyword%)")
	Page<SearchTicketDB> findPageByFilters(
			@Param("regionList") List<String> regionList,
            @Param("genreList") List<String> genreList,
            @Param("siteList") List<String> siteList,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable);
	
	// 페이지: 조회수 정렬
	@Query("SELECT s FROM SearchTicketDB s JOIN s.ticketViews v LEFT JOIN s.eventSites es " +
			"WHERE (:regionList IS NULL OR s.region IN :regionList) " +
			"AND (:genreList IS NULL OR s.genre IN :genreList) " +
			"AND (:siteList IS NULL OR es.sales_site IN :siteList) " +
			"AND (:startDate IS NULL OR :endDate IS NULL OR (s.event_start_date <= :endDate AND s.event_end_date >= :startDate)) "+
			"AND (:searchKeyword IS NULL OR s.event_name LIKE %:searchKeyword%) "+
			"ORDER BY v.view_cnt DESC")
	Page<SearchTicketDB> findPageByFiltersOrderByViews(
			@Param("regionList") List<String> regionList,
            @Param("genreList") List<String> genreList,
            @Param("siteList") List<String> siteList,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable);
	
	// 페이지: 좋아요 정렬
	@Query("SELECT s FROM SearchTicketDB s LEFT JOIN s.likes l LEFT JOIN s.eventSites es " +
	       "WHERE (:regionList IS NULL OR s.region IN :regionList) " +
	       "AND (:genreList IS NULL OR s.genre IN :genreList) " +
	       "AND (:siteList IS NULL OR es.sales_site IN :siteList) " +
	       "AND (:startDate IS NULL OR :endDate IS NULL OR (s.event_start_date <= :endDate AND s.event_end_date >= :startDate)) " +
	       "AND (:searchKeyword IS NULL OR s.event_name LIKE %:searchKeyword%) " +
	       "GROUP BY s.id " +
	       "ORDER BY COUNT(l) DESC")
	Page<SearchTicketDB> findPageByFiltersOrderByLikes(
	       @Param("regionList") List<String> regionList,
	       @Param("genreList") List<String> genreList,
	       @Param("siteList") List<String> siteList,
	       @Param("startDate") Date startDate,
	       @Param("endDate") Date endDate,
	       @Param("searchKeyword") String searchKeyword,
	       Pageable pageable);
	
	// 페이지: title
	@Query("SELECT s FROM SearchTicketDB s LEFT JOIN s.eventSites es " +
			"WHERE (:regionList IS NULL OR s.region IN :regionList) " +
			"AND (:genreList IS NULL OR s.genre IN :genreList) " +
			"AND (:siteList IS NULL OR es.sales_site IN :siteList) " +
			"AND (:startDate IS NULL OR :endDate IS NULL OR (s.event_start_date <= :endDate AND s.event_end_date >= :startDate)) "+
			"AND (:searchKeyword IS NULL OR s.event_name LIKE %:searchKeyword%) "+
			"ORDER BY s.event_name ASC")
	Page<SearchTicketDB> findPageByFiltersOrderByTitle(
			@Param("regionList") List<String> regionList,
            @Param("genreList") List<String> genreList,
            @Param("siteList") List<String> siteList,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable);
	
	//
	@Query("SELECT s FROM SearchTicketDB s JOIN s.ticketViews v "
			+ "WHERE (:searchKeyword IS NULL OR s.event_name LIKE %:searchKeyword%) "
			+ "ORDER BY v.view_cnt DESC")
	List<SearchTicketDB> findTop10BySearchKey(@Param("searchKeyword") String searchKeyword, Pageable pageable);
}
