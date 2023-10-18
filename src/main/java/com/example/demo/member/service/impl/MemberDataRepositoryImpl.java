package com.example.demo.member.service.impl;

import com.example.demo.member.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * jpa에서 이렇게 사용 가능하도록 지원하는거라 반드시 이름을 맞춰줘야한다.
 * 원래 repository 이름 + Impl
 * 근데 그냥 Repository를 쪼개서 사용하는게 더 좋은 것 같다.
 */
@RequiredArgsConstructor
public class MemberDataRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }




}
