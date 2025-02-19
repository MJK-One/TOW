package com.tow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.tow.domain.TicketDB;
import com.tow.service.RegionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RegionController {
	@Autowired
	private RegionService regionService;
	
	@GetMapping("region")
	public List<TicketDB> getInfo(@RequestParam String keyword) {
		return regionService.getTop6(keyword);
	}
	
}
