package com.tow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.tow.domain.vo.PasswordResetTokenVO;
import com.tow.service.PasswordResetTokenService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/pwdToken")
public class PasswordResetTokenController {
	@Autowired
	private PasswordResetTokenService pwdResetService;
	
	@PostMapping("/findPassword")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetTokenVO request) {
		pwdResetService.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String token) {
    	pwdResetService.resetPassword(token);
        return ResponseEntity.ok().build();
    }
	
}
