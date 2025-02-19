package com.tow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tow.domain.NaverUserDB;

@Repository
public interface UserRepository extends JpaRepository<NaverUserDB, String> {
}