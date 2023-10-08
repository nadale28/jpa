package com.example.demo.member.service.impl;

import com.example.demo.member.entity.Member;
import com.example.demo.member.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberService memberService;
    
    @Test
    void save() {
        //given
        Member member = new Member();
        member.setUsername("김인환");

        //when
        Long id = memberService.save(member);
        Member findMember = memberService.find(id);

        //then
        org.assertj.core.api.Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        org.assertj.core.api.Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

    }

    @Test
    void find() {
        Member member = memberService.find(1l);
        System.out.println("member.toString() = " + member.toString());
    }
}