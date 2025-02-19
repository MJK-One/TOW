package com.tow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tow.domain.EmailCheckToken;
import com.tow.domain.NaverUserDB;

@Repository
public interface EmailCheckTokenRepository extends JpaRepository<EmailCheckToken, Long> {
	Optional<EmailCheckToken> findByEmail(String email);
	
	boolean existsByEmailAndToken(String email, String code);
	
	void deleteByEmail(String email);
}