package com.example.demo.team;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Team {
    @Id @GeneratedValue
    @Column(name="team_id")
    private Long id;

    private String teamName;

    public Team(String teamName) {
        this.teamName = teamName;
    }

    public Team() {

    }
}
