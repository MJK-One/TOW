package com.tow.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tow.domain.LikeDB;
import com.tow.domain.ReservationDB;
import com.tow.domain.TicketDB;
import com.tow.domain.vo.TicketLikeVO;
import com.tow.repository.LikeRepository;
import com.tow.repository.ReservationRepository;
import com.tow.repository.TicketRepository;
import com.tow.repository.TicketViewsRepository;

@Service
public class DetailService {
	@Autowired
	private TicketRepository ticketRep;
	@Autowired
	private TicketViewsRepository ticketViewRep;
	@Autowired
	private LikeRepository likeRep;
	@Autowired
    private ReservationRepository reservationRepository;
	
	//티켓 상세 정보 조회
	public Optional<TicketDB> getInfo(int id) {
		return ticketRep.findTicketAndSites(id);
	}
	
	//조회수 증가
	@Transactional
	public void incrementViewCnt(Integer ticketId) {
		ticketViewRep.incrementViewsByTicketId(ticketId);
	}
	
	/* 좋아요 버튼 */
	// 좋아요 클릭
	public void saveTicketLike(Integer ticketId, String uId) {
		LikeDB ticketLike = new LikeDB();
		ticketLike.setTicketId(ticketId);
		ticketLike.setUserId(uId);
		likeRep.save(ticketLike);
	}
	
	// 좋아요 해제
	@Transactional
	public void deleteTicketLike(Integer ticketId, String uId) {
		likeRep.deleteByTicketIdAndUserId(ticketId, uId);
	}
	
	// 좋아요 상태 확인
	public boolean likeStateCk(Integer ticketId, String uId) {
		long state = likeRep.countByTidAndUid(ticketId, uId);
		if(state > 0) { //존재
			return true;
		} else {
			return false;
		}
	}
	
	// 좋아요 수 가져오기
	public long getCntLike(Integer ticketId) {
		return likeRep.countByTid(ticketId);
	}
	
	// 좋아요 수 비율 구하기
	public TicketLikeVO getPerForAge(Integer ticketId) {
		// 총 좋아요 수
		long totalLikeCnt = likeRep.countByTid(ticketId);
		System.out.println(totalLikeCnt);
		if(totalLikeCnt == 0) { // 좋아요가 없다면
			return null;
		}
		
		// 연령별 좋아요 수: 10~60대, 형식: '10-19'
		String[] ageArr = {"10-19", "20-29", "30-39", "40-49", "50-59", "60-69"};
		double[] agePerArr = new double[6];
		
		for(int i = 0 ; i < ageArr.length; i++){
			long totalCnt = likeRep.countByTidAndAge(ticketId, ageArr[i]);
			agePerArr[i] = Math.round(((double) totalCnt / totalLikeCnt) * 1000) / 10.0; // 소수 첫째 자리까지만 나오도록
			System.out.println(i + " : " + totalCnt);
			System.out.println(agePerArr[i]);
        }
		
		TicketLikeVO tkLike = new TicketLikeVO();
		tkLike.setTkId(ticketId);
		tkLike.setPerFor10(agePerArr[0]);
		tkLike.setPerFor20(agePerArr[1]);
		tkLike.setPerFor30(agePerArr[2]);
		tkLike.setPerFor40(agePerArr[3]);
		tkLike.setPerFor50(agePerArr[4]);
		tkLike.setPerFor60(agePerArr[5]);
		
		return tkLike;
	}
	
	
	/* 알림 버튼 */
    // Timestamp에서 LocalDateTime으로 변환하는 메서드
    public LocalDateTime convertTicketTime(Timestamp ticket_open_date) {
        if (ticket_open_date != null) {
            return ticket_open_date.toInstant()
                                  .atZone(ZoneId.of("Asia/Seoul"))
                                  .toLocalDateTime();
        } else {
        	return null;
        }
    }
    
    // 알림 버튼 클릭
 	public void saveTicketBell(Integer ticketId, String uId, Integer bellTime) {
 		// 티켓의 오픈 시간 받아오기
 		LocalDateTime tk_open = convertTicketTime(ticketRep.findTicketOpenDateById(ticketId));
 		
 		// 티켓 오픈 시간이 있다면 저장
 		if(tk_open != null) {
 			ReservationDB ticketReservation = new ReservationDB();
 	 		ticketReservation.setTicketId(ticketId);
 	 		ticketReservation.setEmail(uId);
 	 		ticketReservation.setNotificationHours(bellTime);
 	 		ticketReservation.setTicketOpenDate(tk_open);
 	 		reservationRepository.save(ticketReservation);
 		}
 	}
 	
 	// 알림 해제
 	@Transactional
 	public void deleteTicketBell(Integer ticketId, String uId) {
 		reservationRepository.deleteByTicketIdAndEmail(ticketId, uId);
 	}
 	
 	// 알림 상태 확인
 	public boolean bellStateCk(Integer ticketId, String uId) {
 		long state = reservationRepository.countByTidAndUid(ticketId, uId);
 		if(state > 0) { //존재
 			return true;
 		} else {
 			return false;
 		}
 	}
 	
 	// 좋아요 수 가져오기
 	public long getCntBell(Integer ticketId) {
 		return reservationRepository.countByTid(ticketId);
 	}
 	
 	/* 상세 페이지 top5 */
 	// 좋아요 누른 유저 목록 받아오기
 	public List<String> getUserIdListWhoLikedTicket(Integer ticketId) {
 		return likeRep.findUserIdByTicketId(ticketId);
 	}
 	
 	// 가장 많이 좋아요한 티켓 5 목록 가져오기
 	public List<Integer> getTop5LikedTicketsByUsersWhoLikedTicket(Integer ticketId) {
 		// 좋아요 누른 유저 목록 가져오기
 		List<String> userIdList = getUserIdListWhoLikedTicket(ticketId);
 		
 		if(userIdList.isEmpty()) {
 			return List.of();
 		}
 		
 		// 티켓의 장르 받아오기
 		String genre = ticketRep.findGenreByTicketId(ticketId);
 		if(genre.isEmpty()) {
 			genre = null;
 		}
 		
 		// 유저 목록이 좋아요한 다른 티켓 top 5
 		List<Object[]> result = likeRep.findTop5LikedTicketsByUserIds(userIdList, ticketId, genre);
 		
 		// 결과에서 티켓 아이디만 추출
 		return result.stream()
 				.map(row -> (Integer) row[0])
 				.collect(Collectors.toList());
 	}
 	
 	// 티켓 목록으로 ticketDB 목록 가져오기
 	public List<TicketDB> getTop5LikedTicketsByTicketIdList(Integer ticketId) {
 		List<Integer> ticketIdList = getTop5LikedTicketsByUsersWhoLikedTicket(ticketId);
 		
 		return ticketRep.findByTicketIdList(ticketIdList);
 	}
 	
	
}
