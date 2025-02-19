package com.tow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tow.domain.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	@Query("SELECT p FROM PasswordResetToken p WHERE p.token = :token")
	Optional<PasswordResetToken> findByToken(@Param("token") String token);
	
    Optional<PasswordResetToken> findByEmail(String email);
    void deleteByEmail(String email);
}
