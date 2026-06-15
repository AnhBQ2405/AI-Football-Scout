package com.scout.AI_Football_Scout.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "players") 
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;         

    private String name;     
    private String club;     
    @Column(columnDefinition = "TEXT") 
    private String position;
    private String aiReport;
    public String getAiReport() {
        return aiReport;
    }

    public void setAiReport(String aiReport) {
        this.aiReport = aiReport;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }
}