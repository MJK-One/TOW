package com.tow.domain.vo;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
public class NaverUserVO {
    private String resultCode;
    private String message;
    private Response response;
    
    @Data
    @Entity
    public static class Response {
        @Id
        private String email;
        private String name;
        private String id;
        private String age;
    }
}
