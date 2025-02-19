package com.tow.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.tow.domain.LikeDB;
import com.tow.domain.ReservationDB;
import com.tow.service.MyPageService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("mypage")
public class MyPageController {
	@Autowired
	private MyPageService myPageService;
	
	/* 좋아요 */
	// 좋아요 리스트 가져오기
	@GetMapping("likeList")
	public List<LikeDB> getLikeList(@RequestParam String userId) {
		return myPageService.getLikeList(userId);
	}
	
	// 좋아요 리스트 가져오기: page
	@GetMapping("likeListPage")
	public Map<String, Object> getLikeListPage(@RequestParam String userId, @RequestParam int pagenum) {
		return myPageService.getLikeListPage(userId, pagenum - 1); // 페이지 1부터 시작
	}
	
	// 좋아요 수 가져오기
	@GetMapping("likeCnt")
	public long getLikeCnt(@RequestParam String userId) {
		return myPageService.getLikeCnt(userId);
	}
	
	/* 알림 */
	// 알림 리스트 가져오기
	@GetMapping("bellList")
	public List<ReservationDB> getbellList(@RequestParam String userId) {
		return myPageService.getBellList(userId);
	}
	
	// 알림 리스트 가져오기: page
	@GetMapping("bellListPage")
	public Map<String, Object> getbellListPage(@RequestParam String userId, @RequestParam int pagenum) {
		return myPageService.getBellListPage(userId, pagenum - 1); // 페이지 1부터 시작
	}
	
	// 알림 수 가져오기
	@GetMapping("bellCnt")
	public long getbellCnt(@RequestParam String userId) {
		return myPageService.getBellCnt(userId);
	}
	
	// 알림 시간 가져오기
	@GetMapping("bellTime")
	public int getbellTime(@RequestParam String userId, @RequestParam int ticketId) {
		return myPageService.getBellTime(ticketId, userId);
	}
	
	// 알림 시간 업데이트
	@GetMapping("bellTimeUpdate")
	public void updateBellTime(@RequestParam String userId, @RequestParam int ticketId, @RequestParam int bellTime) {
		myPageService.updateBellTime(ticketId, userId, bellTime);
	}
	
	
	
	
	
}
