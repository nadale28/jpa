package com.example.demo.member.entity;

import lombok.Data;

@Data
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;
    private int age;
    private int maxAge;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public MemberDto() {
    }
}
