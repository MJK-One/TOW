package com.tow.domain;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tickets")
public class TicketDB {
	@Id
	private Integer id;
	private String event_name;
	private Date registration_date;
	
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy.MM.dd HH:mm", timezone="Asia/Seoul")
	private Timestamp ticket_open_date;
	
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy.MM.dd HH:mm", timezone="Asia/Seoul")
	private Timestamp pre_sale_date;
	private String image_url;
	private String basic_info;
	private String event_description;
	private String agency_info;
	private String genre;
	
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy.MM.dd", timezone="Asia/Seoul")
	private Date event_start_date;
	
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy.MM.dd", timezone="Asia/Seoul")
	private Date event_end_date;
	private String venue;
	private String address;
	
	//join enventSites
	@OneToMany(mappedBy = "ticketDB")
	private List<EventSiteDB> eventSites;
	
	//join ticket_views
	@OneToOne(mappedBy = "ticketDB")
	private TicketViewsDB ticketViews;
}