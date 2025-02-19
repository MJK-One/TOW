package com.tow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tow.domain.MainPostTicketDB;

import java.util.List;

@Repository
public interface MainPostRepositroy extends JpaRepository<MainPostTicketDB, Integer> {
	// 인기도 TOP 10
    @Query(value = "SELECT * FROM tickets ORDER BY id DESC LIMIT 10", nativeQuery = true)
    List<MainPostTicketDB> findTop10();

    // 월별 id순으로 limit
    @Query(value = "SELECT * FROM tickets WHERE MONTH(ticket_open_date) = :month AND YEAR(ticket_open_date) = :year ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<MainPostTicketDB> findTicketsForCurrentMonth(@Param("month") int month, @Param("year") int year, @Param("limit") int limit);

    // 무한스크롤
    Page<MainPostTicketDB> findAll(Pageable pageable);
    
  
    @Query("SELECT m FROM MainPostTicketDB m WHERE m.genre = :genre ORDER BY id DESC LIMIT 10")
    List<MainPostTicketDB> findTop10ByGenreLimit(@Param("genre") String genre);
    
    List<MainPostTicketDB> findByGenre(String genre);
}