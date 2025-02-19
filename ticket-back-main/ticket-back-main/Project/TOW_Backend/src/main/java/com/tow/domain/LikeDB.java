package com.tow.domain;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ticket_like")
public class LikeDB {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "ticket_id")
	private Integer ticketId;
	
	@Column(name = "u_id")
	private String userId;
	
	@Column(name = "time", insertable = false, updatable = false)
	private Timestamp likeTime;
	
	@ManyToOne
	@JoinColumn(name = "u_id", referencedColumnName = "email", insertable = false, updatable = false)
	private NaverUserDB naverUser;
	
	@ManyToOne
	@JoinColumn(name = "ticket_id", referencedColumnName = "id", insertable = false, updatable = false)
	private TicketDB ticketDB;
	
}
