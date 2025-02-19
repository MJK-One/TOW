package com.tow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "event_sites")
public class EventSiteDB {
	@Id
	private Integer id;
	private Integer event_id;
	private String sales_site;
	private String detail_link;
	
	//join
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id" ,insertable=false, updatable=false)
	@JsonIgnore
	private TicketDB ticketDB;
}