package com.tow.domain;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tickets")
public class MainPostTicketDB {
    @Id
    private Integer id;
    private String event_name;
    private Timestamp ticket_open_date;
    private Timestamp pre_sale_date;
    private Date event_start_date;
    private Date event_end_date;
    private String image_url;
    private String genre;
    
    @OneToMany(mappedBy = "ticketDB", fetch = FetchType.EAGER)
    private List<EventSiteDB> eventSites;
}