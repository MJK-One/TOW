package com.tow.domain;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tickets")
public class SearchTicketDB {
    @Id
    private Integer id;
    private Timestamp ticket_open_date;
    private Timestamp pre_sale_date;
    private String image_url;
    // 필터링
    private String genre; //장르 필터링
    private String region; // 지역 필터링
    private String event_name; // 검색어 필터링
    private Date event_start_date; // 관람 기간 필터링
    private Date event_end_date;
    
    @OneToMany(mappedBy = "ticketDB", fetch = FetchType.EAGER)
    private List<EventSiteDB> eventSites;
    
    //join ticket_views
  	@OneToOne(mappedBy = "ticketDB")
  	private TicketViewsDB ticketViews;
  	
  	@OneToMany(mappedBy = "ticketDB", fetch = FetchType.EAGER)
  	@JsonIgnore
    private List<LikeDB> likes;
    
}