package com.tow.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tow.domain.NaverUserDB;
import com.tow.domain.vo.EmailCheckWithTokenVO;
import com.tow.domain.vo.OnlyEmailCheckVO;
import com.tow.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public NaverUserDB register(@RequestBody NaverUserDB user) {
        return userService.registerUser(user);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody NaverUserDB user, HttpSession session) {
        boolean isAuthenticated = userService.authenticateUser(user.getEmail(), user.getPassword());
        if (isAuthenticated) {
            // 세션에 사용자 정보를 저장: email만(session이 노출되므로, 나머지 정보는 노출 안되도록(email을 key로 db에 접근해서 받아오면 됨)
            session.setAttribute("user", user.getEmail());
            
            // 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("user", session.getAttribute("user"));
            //System.out.println(session.getAttribute("user"));
            return ResponseEntity.ok(response); // 세션값을 반환
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 인증 실패
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session, HttpServletResponse response) {
        session.invalidate(); // 세션 무효화

        // 캐시 방지를 위해 응답 헤더 설정
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        System.out.println("logout");
        return ResponseEntity.ok("로그아웃 성공");
    }
    
    // 비밀번호 변경 엔드포인트
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email"); // 요청 본문에서 이메일 가져오기
        String newPassword = payload.get("newPassword"); // 요청 본문에서 새 비밀번호 가져오기

        // 비밀번호 변경 요청 처리
        boolean isUpdated = userService.updatePassword(email, newPassword);
        Map<String, String> response = new HashMap<>();

        if (isUpdated) {
            response.put("message", "비밀번호가 성공적으로 변경되었습니다.");
            return ResponseEntity.ok(response); // 성공 응답
        } else {
            response.put("message", "사용자를 찾을 수 없습니다."); // 사용자 없음
            return ResponseEntity.status(404).body(response); // 사용자 없음 응답
        }
    }
    
    // 세션 체크
    @GetMapping("/checkLoginSession")
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
    	Map<String, Object> response = new HashMap<>();
        Object userSession = session.getAttribute("user"); //email

        if (userSession != null) {
            response.put("isLoggedIn", true);
            response.put("user", userSession);
        } else {
            response.put("isLoggedIn", false);
        }
        

        return ResponseEntity.ok(response);
    }
    
    // 로그인 타입(네이버, 자체 로그인) 받아오기
    @GetMapping("/checkLoginType")
    public String checkLoginType(@RequestParam String email) {
    	return userService.checkLoginType(email);
    }
    
    // 로그인 한 사용자 이름 받아오기
    @GetMapping("/getNameWhoLogin")
    public String getNameWhoLogin(@RequestParam String email) {
    	return userService.getNameWhoLogin(email);
    }
    
    /* 이메일 인증 */
    // 이메일이 db에 있는지 확인
    @PostMapping("/isEmailExist")
    public boolean isEmailExist(@RequestBody OnlyEmailCheckVO req) {
    	return userService.authenticateEmail(req.getEmail());
    }
    
    // 이메일 보내기
    @PostMapping("/sendVerificationEmail")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody OnlyEmailCheckVO request) {
        boolean result = userService.sendVerificationEmail(request.getEmail());
        if (result) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false));
        }
    }
    
    // 인증 코드 확인
    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody EmailCheckWithTokenVO request) {
        boolean isValid = userService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().body(Map.of("success", isValid));
    }
    
    
    
}

//package com.tow.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.tow.domain.NaverUserDB;
//import com.tow.domain.vo.UpdatePasswordRequestVO;
//import com.tow.service.UserService;
//
//import jakarta.servlet.http.HttpSession;
//
//@RestController
//public class UserController {
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/register")
//    public NaverUserDB register(@RequestBody NaverUserDB user) {
//       
//        return userService.registerUser(user);
//    }
//    
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody NaverUserDB user, HttpSession session) {
//        boolean isAuthenticated = userService.authenticateUser(user.getEmail(), user.getPassword());
//        if (isAuthenticated) {
//           // 세션에 사용자 정보를 저장
//           session.setAttribute("user", user);
//            return ResponseEntity.ok("");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
//        }
//    }
//    
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpSession session) {
//        session.invalidate(); // 세션 무효화
//        return ResponseEntity.ok("로그아웃 성공");
//    }
    
//    @PutMapping("/update-password")
//    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequestVO request, HttpSession session) {
//        String userEmail = (String) session.getAttribute("userEmail"); // 세션에서 사용자 이메일 가져오기
//
//        if (userEmail == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
//        }
//
//        boolean isUpdated = userService.updatePassword(userEmail, request.getCurrentPassword(), request.getNewPassword());
//        if (isUpdated) {
//            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("현재 비밀번호가 올바르지 않습니다.");
//        }
//    }
    
//}