package com.tow.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tow.domain.TicketDB;
import com.tow.repository.TicketRepository;

@Service
public class RegionService {
	@Autowired
	private TicketRepository ticketReq;
	
	public List<TicketDB> getTop6(String keyword) {
		Pageable pageable = PageRequest.of(0, 6);
		return ticketReq.findByAddrLimitTop(keyword, pageable);
	}
}
