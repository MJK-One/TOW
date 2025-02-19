package com.tow.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "naver_user")
public class NaverUserDB {
    
    @Id
    private String email;
    private String name;
    private String naver_token;
    private String age;
    private String password;
}