package com.tow.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tow.domain.LikeDB;
import com.tow.domain.ReservationDB;
import com.tow.repository.LikeRepository;
import com.tow.repository.ReservationRepository;

@Service
public class MyPageService {
	@Autowired
	private LikeRepository likeRep;
	@Autowired
    private ReservationRepository reservationRepository;
	
	/* 좋아요 */
	//유저가 좋아요 누른 티켓 리스트 가져오기
	public List<LikeDB> getLikeList(String userId) {
		return likeRep.findByUserIdOrderByLikeTimeDesc(userId);
	}
	
	// 유저가 좋아요 누른 티켓 리스트 가져오기
	public Map<String, Object> getLikeListPage(String userId, int pageNum) {
		// 한 페이지에 20개씩
		Pageable pageable = PageRequest.of(pageNum, 20);
		
		// 페이지 결과 받아오기
		Page<LikeDB> likePage = likeRep.findByUserIdOrderByLikeTimeDesc(userId, pageable);
		
		// 결과 반환: list, 총 개수, 총 페이지 수
		Map<String, Object> res = new HashMap<>();
		res.put("list", likePage.getContent());
		res.put("totalElements", likePage.getTotalElements());
		res.put("totalPages", likePage.getTotalPages());
		
		return res;
	}
	
	// 유저가 좋아요 누른 티켓 수
	public long getLikeCnt(String userId) {
		return likeRep.countByUserId(userId);
	}
	
	/* 알림 */
	//유저가 알림 누른 티켓 리스트 가져오기
	public List<ReservationDB> getBellList(String userId) {
		return reservationRepository.findByEmailOrderByReservationTimeDesc(userId);
	}
	
	//유저가 알림 누른 티켓 리스트 가져오기: page
	public Map<String, Object> getBellListPage(String userId, int pageNum) {
		// 한 페이지에 20개씩
		Pageable pageable = PageRequest.of(pageNum, 20);
		
		// 페이지 결과 받아오기
		Page<ReservationDB> reservationPage = reservationRepository.findByEmailOrderByReservationTimeDesc(userId, pageable);
		
		// 결과 반환: list, 총 개수, 총 페이지 수
		Map<String, Object> res = new HashMap<>();
		res.put("list", reservationPage.getContent());
		res.put("totalElements", reservationPage.getTotalElements());
		res.put("totalPages", reservationPage.getTotalPages());
		
		return res;
	}
	
	// 유저가 알림 누른 티켓 수
	public long getBellCnt(String userId) {
		return reservationRepository.countByUserId(userId);
	}
	
	// 유저가 티켓에 설정한 알림 시간
	public int getBellTime(int ticketId, String userId) {
		return reservationRepository.findNotificationHours(ticketId, userId);
	}
	
	// 업데이트
	public void updateBellTime(int ticketId, String userId,int bellTime) {
		Optional<ReservationDB> reservationOP = reservationRepository.findByTidAndUid(ticketId, userId);
		ReservationDB reservationDB = reservationOP.get();
		reservationDB.setNotificationHours(bellTime);
		reservationRepository.save(reservationDB);
	}
	
	
}
