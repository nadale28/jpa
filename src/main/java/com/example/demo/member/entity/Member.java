package com.example.demo.member.entity;

import com.example.demo.address.entity.Address;
import com.example.demo.order.entity.Order;
import com.example.demo.team.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member extends JpaBaseEntity{

    @Id @GeneratedValue
    private Long id;

    @NotNull @NotEmpty
    private String username;

    private int age;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        this.team = team;
    }

    public Member() {
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
