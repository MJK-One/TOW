package com.tow.service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tow.domain.NaverUserDB;
import com.tow.domain.PasswordResetToken;
import com.tow.repository.NaverUserRepository;
import com.tow.repository.PasswordResetTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PasswordResetTokenService {
	@Autowired
    private NaverUserRepository userRep;
	@Autowired
	private PasswordResetTokenRepository pwdResetRep;
	@Autowired
    private JavaMailSender emailSender;
    
    //
    private String frontendUrl = "https://towave.site";
    
    // 임시 비밀번호 발급 메일 보내기
    public void sendPasswordResetEmail(String email) {
        // 사용자 존재 확인
    	Optional<NaverUserDB> userOptional = userRep.findByEmail(email);
    	if (!userOptional.isPresent()) {
    	    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    	}
    	
    	// sns 로그인 이용자인지 확인
    	NaverUserDB user = userOptional.get();
    	if(userRep.IsNaverLoginFindByEmail(user.getEmail())) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SNS login user does not have a password");
    	}
            
        // 기존 토큰이 있다면 제거
    	pwdResetRep.deleteByEmail(email);
        pwdResetRep.flush();
        
        // 새 토큰 생성 및 저장
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        pwdResetRep.save(resetToken);
        
        // 이메일 발송
        // 메시지 구성
        String resetUrl = frontendUrl + "/resetPassword?token=" + token;
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(email);
            helper.setSubject("Ticket Open Wave 비밀번호 찾기 알림");
            
            String emailContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin: 0; padding: 20px; font-family: Arial, sans-serif;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                        <h2 style="color: #333333; margin-bottom: 20px;">TOW 비밀번호 찾기를 신청하셨습니다.</h2>
                        <p style="color: #666666; line-height: 1.6; margin-bottom: 30px;">
                            아래 버튼을 누르시면 임시 비밀번호가 메일로 발송됩니다.<br>
                            만약 본인이 아니라면 메일을 무시해 주세요.
                        </p>
                        <div style="text-align: center; margin-bottom: 30px;">
                            <a href="%s" 
                               style="display: inline-block; 
                                      padding: 12px 30px; 
                                      background-color: #007bff; 
                                      color: white; 
                                      text-decoration: none; 
                                      border-radius: 5px;
                                      font-weight: bold;
                                      transition: background-color 0.3s;">
                                임시 비밀번호 발급
                            </a>
                        </div>
                        <p style="color: #999999; font-size: 12px; margin-top: 30px; text-align: center;">
                            본 메일은 발신 전용입니다.
                        </p>
                    </div>
                </body>
                </html>
                """, resetUrl);
            
            helper.setText(emailContent, true); // true는 HTML 사용을 의미
            
            System.out.println("Sending email to: " + email);
            emailSender.send(message);
            System.out.println("Email sent successfully!");
            
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
            throw new RuntimeException("Unexpected error while sending email", e);
        }
            
    }
    
    //
    public void resetPassword(String token) {
        // 토큰으로 사용자 이메일 찾기
    	Optional<PasswordResetToken> resetTokenOptional = pwdResetRep.findByToken(token);
    	if (!resetTokenOptional.isPresent()) {
    	    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "token not found");
    	}
    	PasswordResetToken resetToken = resetTokenOptional.get();
            
        String email = resetToken.getEmail();
        
        // 사용자 찾기
        Optional<NaverUserDB> userOptional = userRep.findByEmail(email);
    	if (!userOptional.isPresent()) {
    	    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    	}
    	NaverUserDB user = userOptional.get();
            
        // 임시 비밀번호 생성 및 저장
        String temporaryPassword = generateTemporaryPassword();
        user.setPassword(temporaryPassword);
        userRep.save(user);
        
        // 임시 비밀번호 이메일 발송
        // 메시지 구성
        String emailContent = String.format("""
            %s님,
            임시 비밀번호가 발급되었습니다. 
            임시 비밀번호로 로그인 후, 마이페이지에서 비밀번호를 변경해주세요.
            
            임시 비밀번호: %s
            """, email, temporaryPassword);
        // 메시지 보내기
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Ticket Open Wave 임시 비밀번호 발급 안내");
        message.setText(emailContent);

        try {
            System.out.println("Sending email to: " + email); // 로그 추가
            emailSender.send(message);
            System.out.println("Email sent successfully!"); // 로그 추가
        } catch (MailException e) {
            e.printStackTrace(); // 예외 출력
            System.err.println("Error sending email: " + e.getMessage()); // 에러 메시지 출력
        } catch (Exception e) {
            e.printStackTrace(); // 모든 예외 출력
            System.err.println("Unexpected error: " + e.getMessage());
        }
        
        // 토큰 지우기 실패함: mysql에서 처리해야할 것 같음
            
    }
    
    
    
    // 숫자 + 영문자 조합의 8자리 임시 비밀번호 생성
    private String generateTemporaryPassword() {
        // 숫자 + 영문자 조합의 8자리 임시 비밀번호 생성
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
}

