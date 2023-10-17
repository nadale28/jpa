package com.example.demo.member.service.impl;

import com.example.demo.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }

    public List<Member> findByUsername(String username){
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username",username)
                .getResultList();
    }


    public void update(Long id, String name) {

    }


    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age+1" +
                " where m.age >= :age" )
                .setParameter("age",age)
                .executeUpdate();
    }


}
