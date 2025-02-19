package com.tow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import com.tow.domain.MainPostTicketDB;
import com.tow.service.MainPostServcie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class MainPostController {
    @Autowired
    private MainPostServcie mainpostService;
    
    //Top10
    @GetMapping("/top10")
    public List<MainPostTicketDB> getTop10() {
        return mainpostService.getTop10();
    }
    
    //월별
    @GetMapping("/month")
	public List<MainPostTicketDB> getMonth() {
		return mainpostService. getTicketsForCurrentMonth(6);
	}
    
    // 모두 가져오기 (무한스크롤)
    @GetMapping("/alllist")
    public Page<MainPostTicketDB> getAllList(@RequestParam int page, @RequestParam int size) {
        return mainpostService.getAll(page, size);
    }
    
    // Top10
    @GetMapping("/top10/{genre}")
    public List<MainPostTicketDB> getTop10ByGenre(@PathVariable String genre) {
        return mainpostService.getTop10ByGenre(genre);
    }
    
    //genre별 가져오기
    @GetMapping("/genre/{genre}")
    public List<MainPostTicketDB> getByGenre(@PathVariable String genre) {
        return mainpostService.getByGenre(genre);
    }
}