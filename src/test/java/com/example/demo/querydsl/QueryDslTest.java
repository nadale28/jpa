package com.example.demo.querydsl;


import com.example.demo.member.entity.Member;
import com.example.demo.member.entity.MemberDto;
import com.example.demo.member.entity.QMember;
import com.example.demo.team.QTeam;
import com.example.demo.team.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
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
import static com.querydsl.jpa.JPAExpressions.*;


@SpringBootTest
@Transactional
public class QueryDslTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
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
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
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
    public void search() {
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

        list.stream().forEach(m -> {
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
        list.stream().forEach(m -> {
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

    /**
     * 일반 조인
     * inner join
     */
    @Test
    public void join() {
        queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.teamName.eq("teamA"))
                .fetch();
    }

    @Test
    public void leftJoin() {
        queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(team.teamName.eq("teamA"))
                .fetch();
    }

    /**
     * 세타 조인
     * 연관관계가 없는데 join
     * 단, left outer join 같은게 안된다.
     * 쓸모가 없네
     */
    @Test
    public void theta_join() {
        queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.teamName));
    }

    @Test
    public void join_on_filtering() {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .on(team.teamName.eq("teamA"))
                .where(team.teamName.eq("teamA"))
                .fetch();
        System.out.println("fetch = " + fetch);
    }

    @Test
    public void join_on_no_relation() {

        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        List<Tuple> fetch = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.teamName))
                .fetch();
        Tuple t = fetch.get(0);
        System.out.println("t = " + t.get(0, Member.class).getTeam().getTeamName());
        System.out.println("t = " + fetch.get(7).get(1, Team.class).getTeamName());


    }

    @Test
    public void fetchJoin() {
        queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(team.teamName.eq("teamA"))
                .fetch();
    }

    @Test
    public void subQuery() {

        QMember memberSub = new QMember("memberSub");

        queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();
    }

    /**
     * 서브쿼리 한계
     * from절의 서브쿼리는 불가능하다... jpql도 마찬가지
     */
    @Test
    public void selectSubQuery() {
        QMember memberSub = new QMember("memberSub");

        queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();
    }


    @Test
    public void basicCase() {
        queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
    }

    @Test
    public void constant() {
        queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();
    }

    @Test
    public void concat() {
        queryFactory
                .select(member.username.concat("-").concat(member.age.stringValue()))
                .from(member)
                .fetch();
    }

    @Test
    public void simpleProjection() {
        List<String> usernameList = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
    }

    /**
     * tuple은 queryDsl 이 제공하는 클래스이다.
     * 나중에 queryDsl을 안쓸수도 있는건데...
     * repository안에서만 쓰는 걸 추천
     */
    @Test
    public void tupleProjection() {
        List<Tuple> tupleList = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : tupleList) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
        }

    }


    /**
     * getter,setter를 이용하여 매핑
     */
    @Test
    public void findDtoBySetter() {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
    }


    /**
     * getter,setter가 없어도 필드에 값이 들어감
     * 만약 db 컬럼명과 dto 필드명이 다르다면>
     * member.username.as("name")
     */
    @Test
    public void findDtoByField() {

        QMember memberSub = new QMember("memberSub");

        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age,
                        ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                        .from(memberSub), "maxAge")
                        )
                )
                .from(member)
                .fetch();
    }


    /**
     * Projections.constructor 은 런타임에 오류가 발생
     * new QmemberDto는 컴파일 오류라서
     * 아무래도 new QmemberDto 가 좋은가?
     * 근데 이 방식은 MemberDto가 QueryDSL에 의존적이게 된다는 단점이 있다.
     */
    @Test
    public void findDtoByConstructor() {
        queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        //또는 생성자에 @QueryProjection 설정을 하고
        // select(new QMemberDto()) 이런식으로 사용할 수 있다.
    }

    @Test
    public void dynamicQuery_BooleanBuilder() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);

    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam){
        BooleanBuilder builder = new BooleanBuilder();
        if(usernameParam != null) {
            builder.and(member.username.eq(usernameParam));
        }

        if(ageParam != null) {
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam){
        return queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameParam), ageEq(ageParam))
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameParam){
        return (usernameParam==null)?null:member.username.eq(usernameParam);
    }

    private BooleanExpression ageEq(Integer ageParam){
        return (ageParam==null)?null:member.age.eq(ageParam);
    }

    private Predicate allEq(String usernameParam, Integer ageParam){
        return usernameEq(usernameParam).and(ageEq(ageParam));
    }


    @Test
    public void bulkUpdate(){
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        em.flush();
        em.clear();

        //영속성 컨텍스트는 그대로 유지된다.
        //위의 벌크 update 결과가 적용되지 않음
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member fetch1 : fetch) {
            System.out.println("fetch1 = " + fetch1);
        }
    }

    @Test
    public void sqlFunction() {
        queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace', {0}, {1}, {2})",
                        member.username, "member", "M"))
                .from(member)
                .fetch();

    }




}
