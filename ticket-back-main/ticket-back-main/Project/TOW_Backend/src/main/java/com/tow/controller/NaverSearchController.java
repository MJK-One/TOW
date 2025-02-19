package com.tow.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tow.domain.vo.LocalVO;
import com.tow.domain.vo.NaverResultVO;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class NaverSearchController {
	// 환경 변수 선언
	@Value("${NAVER_SEARCH_CLIENT_ID}")
	private String NaverClientId;
	
	@Value("${NAVER_SEARCH_CLIENT_SECRET}")
	private String NaverClientSecret;
	
	@Value("${NAVER_CLOUD_ID}")
	private String NaverCloudId;
	
	@Value("${NAVER_CLOUD_SECRET}")
	private String NaverCloudSecret;
	
	// 장소 검색 naver search
	@GetMapping("naverPlaceSearch")
	public List<LocalVO> getGeocodeFromPlace(@RequestParam String searchPlace) {
		//uri 설정
		URI uri = UriComponentsBuilder
				.fromUriString("https://openapi.naver.com")
				.path("/v1/search/local.json")
				.queryParam("query", searchPlace)
				.queryParam("display", 10)
				.queryParam("start", 1)
                .queryParam("sort", "random")
                .encode(Charset.forName("UTF-8"))
                .build()
                .toUri();
		
		//request & response
		RestTemplate restTemplate = new RestTemplate();		
		RequestEntity<Void> req = RequestEntity
				.get(uri)
				.header("X-Naver-Client-Id", NaverClientId)
				.header("X-Naver-Client-Secret", NaverClientSecret)
				.build();
		ResponseEntity<String> response = restTemplate.exchange(req, String.class);
		
		//vo에 넣어서 json으로 내보내기
		ObjectMapper om = new ObjectMapper();
		NaverResultVO resultVO = null;
		
		try {
			resultVO = om.readValue(response.getBody(), NaverResultVO.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
        return resultVO != null ? (resultVO.getItems().size() == 0 ? null : resultVO.getItems()) : null;
	}
	
	// 주소 검색 naver map
	@GetMapping("naverAddrSearch")
	public Map<String, String> getGeocodeFromAddr(@RequestParam String searchAddr) {
		// 선언
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper om = new ObjectMapper();
		
		// url 설정
		String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + searchAddr;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-api-key-id", NaverCloudId);
        headers.set("x-ncp-apigw-api-key", NaverCloudSecret);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        // response > JSON : geocode 부분만 가져오기
        try {
        	JsonNode root = om.readTree(response.getBody());
            JsonNode addressNode = root.path("addresses").get(0);

            String x = addressNode.path("x").asText();
            String y = addressNode.path("y").asText();

            Map<String, String> coordinates = new HashMap<>();
            coordinates.put("x", x);
            coordinates.put("y", y);

            return coordinates;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
    
}
