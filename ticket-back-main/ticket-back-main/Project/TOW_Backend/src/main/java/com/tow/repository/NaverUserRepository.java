package com.tow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tow.domain.NaverUserDB;

public interface NaverUserRepository extends JpaRepository<NaverUserDB, String> {
	//
	Optional<NaverUserDB> findByEmail(String email);
	
	// 네이버 로그인인지 확인
	@Query("SELECT CASE WHEN u.naver_token IS NULL THEN false ELSE true END FROM NaverUserDB u WHERE u.email = :email")
	boolean IsNaverLoginFindByEmail(@Param("email") String email);
	
	// 유저 이름 받아오기
	@Query("SELECT u.name FROM NaverUserDB u WHERE u.email = :email")
	String findNameByEmail(@Param("email") String email);
}
