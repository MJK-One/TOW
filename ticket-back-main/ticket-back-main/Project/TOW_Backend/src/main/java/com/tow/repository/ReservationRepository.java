package com.tow.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tow.domain.LikeDB;
import com.tow.domain.ReservationDB;

public interface ReservationRepository extends JpaRepository<ReservationDB, Integer> {
	//티켓의 총 알림 수 구하기
	@Query("SELECT COUNT(r) FROM ReservationDB r WHERE r.ticketId = :tId")
	long countByTid(@Param("tId") Integer tId);
	
	// 특정 ticketId와 email 조건에 맞는 엔티티 삭제
	void deleteByTicketIdAndEmail(Integer ticketId, String email);
	
	// 사용자의 알림 상태 확인하기(상세 페이지)
	@Query("SELECT COUNT(r) FROM ReservationDB r WHERE r.ticketId = :tId AND r.email = :uId")
	long countByTidAndUid(@Param("tId") Integer tId, @Param("uId") String uId);
	
	// 사용자가 알림 누른 모든 티켓 가져오기: list
	@Query("SELECT r FROM ReservationDB r WHERE r.email = :email ORDER BY r.reservationTime DESC")
	List<ReservationDB> findByEmailOrderByReservationTimeDesc(@Param("email") String email);
	
	// 사용자가 알림 누른 모든 티켓 가져오기: page
	@Query("SELECT r FROM ReservationDB r WHERE r.email = :email ORDER BY r.reservationTime DESC")
	Page<ReservationDB> findByEmailOrderByReservationTimeDesc(@Param("email") String email, Pageable pageable);
	
	// 사용자가 알림 누른 티켓 수 가져오기
	@Query("SELECT COUNT(r) FROM ReservationDB r WHERE r.email = :userId")
	long countByUserId(@Param("userId") String userId);
	
	// 티켓 알림 시간 가져오기
	@Query("SELECT r.notificationHours FROM ReservationDB r WHERE r.ticketId = :tId AND r.email = :uId")
	int findNotificationHours(@Param("tId") Integer tId, @Param("uId") String uId);
	
	// 티켓 업데이트를 위한 쿼리
	@Query("SELECT r FROM ReservationDB r WHERE r.ticketId = :tId AND r.email = :uId")
	Optional<ReservationDB> findByTidAndUid(@Param("tId") Integer tId, @Param("uId") String uId);
}
