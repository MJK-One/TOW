package com.tow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tow.domain.MainPostTicketDB;
import com.tow.repository.MainPostRepositroy;

import java.time.LocalDate;
import java.util.List;

@Service
public class MainPostServcie {
    @Autowired
    private MainPostRepositroy ticketReq;
    
    //인기TOP10
    public List<MainPostTicketDB> getTop10() {
        return ticketReq.findTop10();
    }
    
    //Month 월별 10개
    public List<MainPostTicketDB> getTicketsForCurrentMonth(int limit) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        return ticketReq.findTicketsForCurrentMonth(currentMonth, currentYear, limit);
    }
    
    // 모두 가져오기 (무한스크롤)
    public Page<MainPostTicketDB> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ticketReq.findAll(pageable);
    }
    
    public List<MainPostTicketDB> getByGenre(String genre) {
        return ticketReq.findByGenre(genre);
    }
    
    
    public List<MainPostTicketDB> getTop10ByGenre(String genre) {
        return ticketReq.findTop10ByGenreLimit(genre);
    }
}
