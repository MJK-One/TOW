package com.tow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ticket_views")
public class TicketViewsDB {
	@Id
	private Integer id;
	private Integer ticket_id;
	private int view_cnt;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", referencedColumnName = "id", insertable=false, updatable=false)
	@JsonIgnore
	private TicketDB ticketDB;
}
