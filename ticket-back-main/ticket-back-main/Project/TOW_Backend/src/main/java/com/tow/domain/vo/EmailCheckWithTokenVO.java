package com.tow.domain.vo;

import lombok.Data;

@Data
public class EmailCheckWithTokenVO {
	private String email;
    private String code;
}
