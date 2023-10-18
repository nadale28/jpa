package com.example.demo.team;

import com.example.demo.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "team")
    public List<Member> members = new ArrayList<>();

}
