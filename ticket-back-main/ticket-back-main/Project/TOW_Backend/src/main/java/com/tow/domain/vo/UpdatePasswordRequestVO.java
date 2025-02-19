package com.tow.domain.vo;

import lombok.Data;

@Data
public class UpdatePasswordRequestVO {
	private String currentPassword;
    private String newPassword;

}
