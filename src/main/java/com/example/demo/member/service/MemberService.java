package com.example.demo.member.service;

import com.example.demo.member.entity.Member;

import java.util.List;

public interface MemberService {
    Long save(Member member);
    Member find(Long id);
    List<?> findByUsername(String username);
    List<?> findAll();
    Long join(Member member);
}
