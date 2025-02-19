package com.tow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tow.domain.ReservationDB;
import com.tow.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@EnableScheduling // 스케줄링 활성화
public class EmailReservationService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ReservationRepository reservationRepository;

    // 주기적으로 예약된 이메일 발송 체크
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void sendScheduledEmails() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Checking for reservations at: " + now); // 로그 추가
        List<ReservationDB> reservations = reservationRepository.findAll();

        for (ReservationDB reservation : reservations) {
            // 이미 이메일이 발송된 예약은 건너뜀
            if (reservation.isEmailSent()) {
                continue;
            }
            sendReservationEmail(reservation, now);
        }
    }

    // 예약 알림 이메일 전송
    public void sendReservationEmail(ReservationDB reservation, LocalDateTime now) {
        // 티켓 오픈 날짜
        LocalDateTime ticketOpenDate = reservation.getTicketOpenDate();
        // 알림 시간을 고려한 발송 시간
        LocalDateTime notifyTime = ticketOpenDate.minusHours(reservation.getNotificationHours());

        // 현재 시간이 notifyTime과 같거나 이후일 경우 이메일 발송
        if (now.isAfter(notifyTime) && !reservation.isEmailSent()) { // 이메일이 발송되지 않은 경우
            // 티켓 이름 가져오기
            String eventName = reservation.getTicketDB().getEvent_name(); // TicketDB에서 event_name 가져오기
            Integer ticketId = reservation.getTicketId(); // ticket_id 가져오기
            
            // 이메일 발송
            sendEmail(reservation.getEmail(), ticketOpenDate, eventName, ticketId);
            reservation.setEmailSent(true); // 이메일 발송 상태 업데이트
            reservationRepository.save(reservation); // 변경된 상태 저장
            System.out.println("이메일 발송: " + reservation.getEmail());
        } else {
            System.out.println("조건 불충족: notifyTime=" + notifyTime + ", 현재시간=" + now + ", ticketOpenDate=" + ticketOpenDate);
        }
    }
    private void sendEmail(String to, LocalDateTime ticketOpenDate, String eventName, Integer ticketId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Ticket Open Wave " + eventName + " 오픈 티켓 예약 알림");
        
        // 날짜 형식 지정
        String formattedDate = ticketOpenDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String ticketLink = "https://towave.site/detail/" + ticketId; // 링크 생성
        message.setText("티켓 이름: " + eventName + "\n오픈 티켓 날짜: " + formattedDate + "\n토우 티켓 링크: " + ticketLink);

        try {
            System.out.println("Sending email to: " + to); // 로그 추가
            emailSender.send(message);
            System.out.println("Email sent successfully!"); // 로그 추가
        } catch (MailException e) {
            e.printStackTrace(); // 예외 출력
            System.err.println("Error sending email: " + e.getMessage()); // 에러 메시지 출력
        } catch (Exception e) {
            e.printStackTrace(); // 모든 예외 출력
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

}