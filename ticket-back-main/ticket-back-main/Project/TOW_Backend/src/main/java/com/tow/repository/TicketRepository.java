package com.tow.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tow.domain.TicketDB;

@Repository
public interface TicketRepository extends JpaRepository<TicketDB, Integer> {
	@Query("SELECT t FROM TicketDB t WHERE t.id = :ticketId")
	Optional<TicketDB> findTicketAndSites(@Param("ticketId") Integer ticketId);
	
	@Query("SELECT t.ticket_open_date FROM TicketDB t WHERE t.id = :ticketId")
	Timestamp findTicketOpenDateById(@Param("ticketId") Integer ticketId);
	
	@Query("SELECT t FROM TicketDB t WHERE " +
			"t.address LIKE :keyword%")
	List<TicketDB> findByAddrLimitTop(
			@Param("keyword") String keyword,
            Pageable pageable);
	
	/* 상세 페이지 top5 */
	// 티켓 목록에 있는 티켓 반환
	@Query("SELECT t FROM TicketDB t WHERE t.id IN :ticketIdList")
	List<TicketDB> findByTicketIdList(@Param("ticketIdList") List<Integer> ticketIdList);
	
	// 티켓 아이디 받고 장르 반환
	@Query("SELECT t.genre FROM TicketDB t WHERE t.id = :ticketId")
	String findGenreByTicketId(@Param("ticketId") Integer ticketId);
}
