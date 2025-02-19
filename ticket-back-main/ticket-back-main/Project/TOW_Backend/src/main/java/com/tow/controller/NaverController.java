package com.tow.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;	
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tow.domain.vo.NaverUserVO;
import com.tow.service.NaverService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/naver")
@RequiredArgsConstructor
public class NaverController {

    private final NaverService naverService;

    /**
     * 네이버 로그인 URL을 생성하고 반환하는 엔드포인트
     *
     * @return 네이버 로그인 페이지 URL을 포함한 ResponseEntity
     */
    @GetMapping("/login")
    public ResponseEntity<String> login() {
        String authorizationUrl = naverService.getAuthorizationUrl();
        return ResponseEntity.ok(authorizationUrl);
    }
    //밈줌바보
    /**
     * 네이버 로그인 후 콜백을 처리하는 엔드포인트
     * 인증 코드를 받아 사용자 정보를 조회합니다.
     *
     * @param code 네이버에서 발급받은 인증 코드
     * @param state CSRF 공격 방지를 위한 상태 토큰
     * @return 사용자 정보를 포함한 ResponseEntity
     */
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam String code, @RequestParam String state, HttpSession session) {
        NaverUserVO.Response userInfo = naverService.processCallback(code, state);

        if (userInfo != null) {
            String frontendUrl = "https://towave.site";
            session.setAttribute("user", userInfo.getEmail()); // 세션에 사용자 정보를 저장: email만(session이 노출되므로, 나머지 정보는 노출 안되도록(email을 key로 db에 접근해서 받아오면 됨)
            return ResponseEntity.status(HttpStatus.FOUND)
                                 .location(URI.create(frontendUrl))
                                 .build();
        } else {
            String errorUrl = "https://towave.site/login";
            return ResponseEntity.status(HttpStatus.FOUND)
                                 .location(URI.create(errorUrl))
                                 .build();
        }
    }
}