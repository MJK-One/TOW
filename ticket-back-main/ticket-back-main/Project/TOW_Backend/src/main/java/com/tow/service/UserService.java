package com.tow.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.tow.domain.EmailCheckToken;
import com.tow.domain.NaverUserDB;
import com.tow.repository.EmailCheckTokenRepository;
import com.tow.repository.NaverUserRepository;
import com.tow.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	@Autowired
    private NaverUserRepository userRepository;
	@Autowired
	private EmailCheckTokenRepository emailCkRep;
	@Autowired
    private JavaMailSender emailSender;

    public NaverUserDB registerUser(NaverUserDB user) {
        return userRepository.save(user);
    }
    
    public boolean authenticateUser(String email, String password) {
        Optional<NaverUserDB> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            NaverUserDB user = userOptional.get();
            return user.getPassword().equals(password); // 비밀번호 비교
        }
        return false; // 사용자 없음
    }
    
    
    // 네이버 로그인이면 "naver", 자체 로그인이면 "normal" 반환
    public String checkLoginType(String email) {
    	boolean IsNaverLogin = userRepository.IsNaverLoginFindByEmail(email); //naver면 true 반환
    	if(IsNaverLogin) {
    		return "naver";
    	} else {
    		return "normal";
    	}
    }
    
    // 로그인 한 사용자 이름 받아오기
    public String getNameWhoLogin(String email) {
    	return userRepository.findNameByEmail(email);
    }
    
    // 비밀번호 변경
    public boolean updatePassword(String email, String newPassword) {
        Optional<NaverUserDB> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            NaverUserDB user = userOptional.get();
            user.setPassword(newPassword); // 비밀번호 업데이트
            userRepository.save(user); // 변경 사항 저장
            return true;
        }
        return false; // 사용자 없음
    }
    
    /* 이메일 인증 */
    // 이메일이 존재하는지 확인
    public boolean authenticateEmail(String email) {
        Optional<NaverUserDB> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return true;
        }
        return false; // 사용자 없음
    }
    
    // 인증 번호 생성하기: 6자리 랜덤 숫자
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    // 이메일 인증 코드 보내기
    @Transactional
    public boolean sendVerificationEmail(String email) {
    	try {
    		// 이미 인증 번호가 존재한다면 지우기
    		emailCkRep.deleteByEmail(email);
			emailCkRep.flush();
    		
    		// 인증 번호 생성
    		String verificationCode = generateVerificationCode();
    		
    		// 인증 번호 저장
    		EmailCheckToken emailToken = new EmailCheckToken();
    		emailToken.setEmail(email);
    		emailToken.setToken(verificationCode);
    		emailCkRep.save(emailToken);
    		
    		// 이메일 발송
    		// 메시지 구성
            String emailContent = String.format("""
                %s님,
                TOW의 이메일 인증 코드입니다.
                
                인증 코드: %s
                """, email, verificationCode);
            // 메시지 보내기
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Ticket Open Wave 이메일 인증");
            message.setText(emailContent);

            try {
                System.out.println("Sending email to: " + email); // 로그 추가
                emailSender.send(message);
                System.out.println("Email sent successfully!"); // 로그 추가
            } catch (MailException e) {
                e.printStackTrace(); // 예외 출력
                System.err.println("Error sending email: " + e.getMessage()); // 에러 메시지 출력
                return false;
            } catch (Exception e) {
                e.printStackTrace(); // 모든 예외 출력
                System.err.println("Unexpected error: " + e.getMessage());
                return false;
            }
            
            return true;
    		
    	} catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 이메일 인증 코드 확인하기
    public boolean verifyCode(String email, String code) {
    	return emailCkRep.existsByEmailAndToken(email, code);
    }
    
    
//    // 사용자 인증 및 비밀번호 변경 로직
//    public boolean updatePassword(String email, String currentPassword, String newPassword) {
//        // 사용자 인증 로직
//        NaverUserDB user = userRepository.findByEmail(email);
//        if (user != null && passwordEncoder.matches(currentPassword, user.getPassword())) {
//            // 비밀번호 변경
//            user.setPassword(passwordEncoder.encode(newPassword));
//            userRepository.save(user);
//            return true;
//        }
//        return false;
//    }
}
