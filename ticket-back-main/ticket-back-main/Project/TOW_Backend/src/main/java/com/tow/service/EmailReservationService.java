package com.tow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tow.domain.ReservationDB;
import com.tow.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@EnableScheduling
public class EmailReservationService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * 1분마다 실행되는 배치 프로세스
     * 개선점: findAll() 대신 발송이 필요한 데이터만 쿼리 레벨에서 필터링하여 메모리 부하 방지
     */
    @Scheduled(fixedRate = 60000)
    @Transactional // 데이터 정합성을 위한 트랜잭션 관리
    public void sendScheduledEmails() {
        LocalDateTime now = LocalDateTime.now();
        
        // [핵심 개선] DB 인덱스를 활용하여 '미발송' & '발송 예정 시간 도달' 데이터만 추출
        List<ReservationDB> targetReservations = 
            reservationRepository.findByEmailSentFalseAndNotifyTimeBefore(now);

        if (targetReservations.isEmpty()) return;

        System.out.println("[Batch] 발송 대상 데이터 수: " + targetReservations.size());

        for (ReservationDB reservation : targetReservations) {
            // 개별 발송 로직을 분리하여 특정 발송 실패가 전체 배치에 영향을 주지 않도록 격리
            processEmail(reservation);
        }
    }

    /**
     * 단일 알림 처리 로직 (Single Responsibility)
     * 개선점: 비즈니스 로직 분리를 통해 향후 @Async(비동기) 도입이 용이한 구조 확보
     */
    private void processEmail(ReservationDB reservation) {
        try {
            String eventName = reservation.getTicketDB().getEvent_name();
            Integer ticketId = reservation.getTicketId();
            LocalDateTime ticketOpenDate = reservation.getTicketOpenDate();

            // 이메일 발송 실행
            sendEmail(reservation.getEmail(), ticketOpenDate, eventName, ticketId);

            // 발송 성공 시 상태 업데이트 (Dirty Checking을 통한 상태 전이)
            reservation.setEmailSent(true);
            reservationRepository.save(reservation); 
            
            System.out.println("[Success] 발송 완료: " + reservation.getEmail());
        } catch (Exception e) {
            // 예외 발생 시 로그만 남기고 다음 주기에 재시도되도록 (isEmailSent가 false이므로) 설계
            System.err.println("[Error] 발송 실패: " + reservation.getEmail() + " | 사유: " + e.getMessage());
        }
    }

    private void sendEmail(String to, LocalDateTime ticketOpenDate, String eventName, Integer ticketId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[TOW] " + eventName + " 오픈 티켓 예약 알림");
        
        String formattedDate = ticketOpenDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String ticketLink = "https://towave.site/detail/" + ticketId;
        
        message.setText("티켓 이름: " + eventName + "\n" +
                        "오픈 일시: " + formattedDate + "\n" +
                        "예매 링크: " + ticketLink);

        try {
            emailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Mail 전송 중 오류 발생", e); // 상위 processEmail의 catch문으로 전달
        }
    }
}
