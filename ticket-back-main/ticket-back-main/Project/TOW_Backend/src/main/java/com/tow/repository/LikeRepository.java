package com.tow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tow.domain.LikeDB;

@Repository
public interface LikeRepository extends JpaRepository<LikeDB, Integer> {
	//티켓의 총 좋아요 수 구하기
	@Query("SELECT COUNT(l) FROM LikeDB l WHERE l.ticketId = :tId")
	long countByTid(@Param("tId") Integer tId);
	
	//티켓의 연령별 좋아요 수 구하기
	@Query("SELECT COUNT(l) FROM LikeDB l JOIN l.naverUser n "
			+ "WHERE l.ticketId = :tId AND n.age = :age")
	long countByTidAndAge(@Param("tId") Integer tId, @Param("age") String age);
	
	// 특정 ticketId와 userId 조건에 맞는 엔티티 삭제
	void deleteByTicketIdAndUserId(Integer ticketId, String userId);
	
	// 사용자의 좋아요 상태 확인하기(상세 페이지)
	@Query("SELECT COUNT(l) FROM LikeDB l WHERE l.ticketId = :tId AND l.userId = :uId")
	long countByTidAndUid(@Param("tId") Integer tId, @Param("uId") String uId);
	
	// 사용자가 좋아요 누른 모든 티켓 가져오기: list
	List<LikeDB> findByUserIdOrderByLikeTimeDesc(@Param("userId") String userId);
	
	// 사용자가 좋아요 누른 모든 티켓 가져오기: page
	Page<LikeDB> findByUserIdOrderByLikeTimeDesc(@Param("userId") String userId, Pageable pageable);
	
	// 사용자가 좋아요 누른 티켓 수 가져오기
	@Query("SELECT COUNT(l) FROM LikeDB l WHERE l.userId = :userId")
	long countByUserId(@Param("userId") String userId);
	
	/* 상세 페이지 top5 */
	// 좋아요를 누른 모든 유저 ID 조회
	@Query("SELECT l.userId FROM LikeDB l WHERE l.ticketId = :ticketId")
	List<String> findUserIdByTicketId(@Param("ticketId") Integer ticketId);
	
	// 가장 많이 좋아요한 티켓 5 목록 가져오기: 장르가 같은 항목으로
	@Query("SELECT l.ticketId, COUNT(l.userId) as likeCount " +
	           "FROM LikeDB l INNER JOIN l.ticketDB tl " +
	           "WHERE l.userId IN :userIdList AND l.ticketId != :excludedTicketId " +
	           "AND (:excludedTicketGenre IS NULL OR tl.genre = :excludedTicketGenre) " +
	           "GROUP BY l.ticketId " +
	           "ORDER BY likeCount DESC " +
	           "LIMIT 5")
	List<Object[]> findTop5LikedTicketsByUserIds(@Param("userIdList") List<String> userIdList,
            @Param("excludedTicketId") Integer excludedTicketId,
            @Param("excludedTicketGenre") String excludedTicketGenre);
	
	
}
