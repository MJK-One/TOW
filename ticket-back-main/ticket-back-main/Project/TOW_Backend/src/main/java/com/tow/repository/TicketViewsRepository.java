package com.tow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tow.domain.TicketViewsDB;

@Repository
public interface TicketViewsRepository extends JpaRepository<TicketViewsDB, Integer> {
	//조회수 +1
	@Modifying
	@Query("UPDATE TicketViewsDB v SET v.view_cnt = v.view_cnt + 1 WHERE v.ticket_id = :ticketId")
	void incrementViewsByTicketId(@Param("ticketId") Integer ticketId);
}
