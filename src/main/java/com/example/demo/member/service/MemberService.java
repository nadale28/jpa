package com.example.demo.member.service;

import com.example.demo.member.entity.Member;

public interface MemberService {
    Long save(Member member);
    Member find(Long id);
}
