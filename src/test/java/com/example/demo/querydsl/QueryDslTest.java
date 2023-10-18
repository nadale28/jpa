package com.example.demo.querydsl;


import com.example.demo.member.entity.Member;
import com.example.demo.member.entity.QMember;
import com.example.demo.team.QTeam;
import com.example.demo.team.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.member.entity.QMember.*;
import static com.example.demo.team.QTeam.*;


@SpringBootTest
@Transactional
public class QueryDslTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 50, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJQPL() {
        //member1을 찾기
        String sql = "" +
                "select m " +
                "from Member m " +
                "where m.username = :username";

        Member findMember = em.createQuery(sql, Member.class)
                .setParameter("username","member1")
                .getSingleResult();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl(){
        //JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //QMember m = new QMember("m");
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 제공 검색옵션
     * eq =
     * ne !=
     * isNotNull
     * in
     * notIn
     * between
     * goe >=
     * gt >
     * loe <=
     * lt <
     * like
     * contains '%test%'
     * startsWith 'test%'
     */
    @Test
    public void search(){
        Member findMember = queryFactory
                .select(member)
                .where(
                        member.username.eq("member1")
                        .and(member.age.eq(10))
                )
                .fetchOne();
    }

    @Test
    public void resultFetch() {

        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();

        queryFactory.selectFrom(member).fetchFirst();

        // fetchResult : 페이징 쿼리포함
        // 근데 토탈 카운트를 가져오는 쿼리는 따로 요청하여 쓰는게 좋다. 토탈 카운트 쿼리는 성능상 간단해야 하므로
    }

    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> list = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        list.stream().forEach(m->{
            System.out.println("m.getUsername() = " + m.getUsername());
        });
    }

    @Test
    public void paging1() {
        List<Member> list = queryFactory
                .select(member)
                .from(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
        list.stream().forEach(m->{
            System.out.println("m.getUsername() = " + m.getUsername());
        });
    }

    @Test
    public void paging2() {
        List<Member> list = queryFactory
                .select(member)
                .from(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(member.count())
                .from(member);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

        Page<Member> page = PageableExecutionUtils.getPage(list, pageRequest, count::fetchOne);

        List<Member> content = page.getContent();

        System.out.println("총 카운트 = " + page.getTotalElements());
        System.out.println("현재 페이지 = " + page.getNumber());
        System.out.println("총페이지 " + page.getTotalPages());
        System.out.println("page.isFirst() = " + page.isFirst());
        System.out.println("page.isLast() = " + page.isLast());
        System.out.println("page.hasNext() = " + page.hasNext());
    }

    @Test
    public void aggregation() {
        List<Tuple> tuples = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();
        Tuple tuple = tuples.get(0);
        tuple.get(member.count());
        tuple.get(member.age.sum());

        System.out.println("tuple.toString() = " + tuple.toString());
    }

    @Test
    public void group() {
        List<Tuple> result = queryFactory
                .select(team.teamName, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.teamName)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        System.out.println("teamA = " + teamA.get(member.age.avg()));
        System.out.println("teamB = " + teamB.get(member.age.avg()));
    }
}
