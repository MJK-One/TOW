package com.tow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tow.domain.ReservationDB;
import com.tow.repository.ReservationRepository;
import com.tow.service.EmailReservationService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservations")
public class EmailReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EmailReservationService emailReservationService;

    @PostMapping
    public ResponseEntity<ReservationDB> createReservation(@RequestBody ReservationDB reservation) {
        // 이메일 발송 상태 초기화
        reservation.setEmailSent(false); // 이메일이 아직 발송되지 않았음을 표시

        // 예약 생성
        ReservationDB savedReservation = reservationRepository.save(reservation);
        
        // 예약 알림 이메일 전송은 스케줄러에 의해 처리되므로 여기서는 호출하지 않음
        // emailReservationService.sendReservationEmail(savedReservation);

        return ResponseEntity.ok(savedReservation);
    }
}
