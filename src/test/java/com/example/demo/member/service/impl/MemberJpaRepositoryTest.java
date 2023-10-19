package com.example.demo.member.service.impl;

import com.example.demo.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public  void basicTest(){
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).contains(member);

        List<Member> result2 = memberJpaRepository.findByUsername("member1");
        assertThat(result2).contains(member);

    }




}
