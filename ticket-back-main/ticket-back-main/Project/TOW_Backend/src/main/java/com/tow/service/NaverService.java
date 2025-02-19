package com.tow.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import com.tow.domain.NaverUserDB;
import com.tow.domain.vo.NaverTokenVo;
import com.tow.domain.vo.NaverUserVO;
import com.tow.repository.NaverUserRepository;

@Service
public class NaverService {
	
	@Autowired
    private NaverUserRepository naverUserRepository;

    //네이버 개발자 센터에서 발급받은 클라이언트 ID
    @Value("${naver.client.id}")
    private String clientId;
    
    //네이버 개발자 센터에서 발급받은 클라이언트 시크릿
    @Value("${naver.client.secret}")
    private String clientSecret;

    //네이버 로그인 후 리다이렉트될 URL
    @Value("${naver.redirect.uri}")
    private String redirectUri;

    // 네이버 API 엔드포인트 상수
    private static final String NAVER_AUTH_URL = "https://nid.naver.com/oauth2.0/authorize";
    private static final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String NAVER_PROFILE_URL = "https://openapi.naver.com/v1/nid/me";

    /**
     * 네이버 OAuth2.0 로그인을 위한 인증 URL을 생성합니다.
     *
     * @return 생성된 네이버 로그인 URL
     */
    public String getAuthorizationUrl() {
        // CSRF 공격 방지를 위한 상태 토큰 생성
        String state = UUID.randomUUID().toString();

        // 네이버 로그인 URL 생성
        // response_type=code: 인증 코드 방식을 사용함을 명시
        // client_id: 애플리케이션의 등록된 클라이언트 ID
        // redirect_uri: 인증 후 리다이렉트될 URL
        // state: CSRF 방지 토큰
        return NAVER_AUTH_URL + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&state=" + state
                + "&scope=account.basic,account.phone,account.birthday";
    }

    /**
     * 네이버 OAuth2.0 콜백을 처리합니다.
     *
     * @param code 네이버에서 받은 인증 코드
     * @param state CSRF 방지용 상태 토큰
     * @return 조회된 네이버 사용자 정보
     */
    public NaverUserVO.Response processCallback(String code, String state) {
    	try {
            NaverTokenVo tokenVo = getAccessToken(code, state);
            NaverUserVO.Response userInfo = getUserInfo(tokenVo.getAccess_token());
            
            // DB에서 사용자 정보 조회
            Optional<NaverUserDB> existingUser = naverUserRepository.findByEmail(userInfo.getEmail());
            System.out.println("조회된 사용자: " + existingUser);
            if (existingUser.isPresent()) {

            	System.out.println("저장저장111");
                return userInfo; // 사용자 정보를 반환
            } else {
                // 사용자 존재하지 않음: 로그인 데이터 저장
            	System.out.println("저장저장222");
                saveUserInfo(userInfo);
                return userInfo; // 사용자 정보를 반환
            }
        } catch (Exception e) {
            // 로그에 오류 메시지 출력
            e.printStackTrace(); // 콘솔에 스택 트레이스 출력
            throw new RuntimeException("Error processing callback", e); // 구체적인 에러 메시지와 함께 예외 던지기
        }
    }

    /**
     * 인증 코드를 이용해 액세스 토큰을 얻습니다.
     *
     * @param code 네이버에서 받은 인증 코드
     * @param state CSRF 방지용 상태 토큰
     * @return 네이버 액세스 토큰 정보
     */
    private NaverTokenVo getAccessToken(String code, String state) {
        // RestTemplate 생성 - HTTP 요청을 보내기 위한 스프링 유틸리티 클래스
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");  // 인증 코드 방식 사용
        params.add("client_id", clientId);  // 애플리케이션의 클라이언트 ID
        params.add("client_secret", clientSecret);  // 애플리케이션의 클라이언트 시크릿
        params.add("code", code);  // 네이버에서 받은 인증 코드
        params.add("state", state);  // CSRF 방지 토큰

        // HTTP 요청 엔티티 생성 (헤더 + 바디)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        // POST 요청 보내고 응답 받기
        try {
            ResponseEntity<NaverTokenVo> response = restTemplate.postForEntity(NAVER_TOKEN_URL, request, NaverTokenVo.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println("Error getting access token: " + e.getMessage());
            throw e; // 예외 던지기
        }
       
    }

    /**
     * 액세스 토큰을 이용해 네이버 사용자 정보를 조회합니다.
     *
     * @param accessToken 네이버 액세스 토큰
     * @return 조회된 네이버 사용자 정보
     */
    private NaverUserVO.Response getUserInfo(String accessToken) {
        // RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);  // 인증 헤더에 액세스 토큰 추가

        // HTTP 요청 엔티티 생성 (헤더만 포함, 바디는 비어있음)
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        // GET 요청 보내고 응답 받기
        ResponseEntity<NaverUserVO> response = restTemplate.exchange(
        		NAVER_PROFILE_URL,
                HttpMethod.GET,
                entity,
                NaverUserVO.class
        );
        System.out.println("API 응답: " + response.getBody());

        // 응답 바디에서 사용자 정보 추출 후 반환
        return response.getBody().getResponse();
    }
    
    public void saveUserInfo(NaverUserVO.Response userInfo) {
        NaverUserDB naverUser = new NaverUserDB();
        naverUser.setNaver_token(userInfo.getId());
        naverUser.setName(userInfo.getName());
        naverUser.setEmail(userInfo.getEmail());
        naverUser.setAge(userInfo.getAge());

        naverUserRepository.save(naverUser); // 데이터베이스에 저장
    }
}