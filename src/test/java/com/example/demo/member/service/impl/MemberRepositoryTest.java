package com.example.demo.member.service.impl;

import com.example.demo.member.entity.Member;
import com.example.demo.member.entity.MemberDto;
import com.example.demo.member.service.MemberService;
import com.example.demo.member.service.UsernameOnly;
import com.example.demo.team.Team;
import com.example.demo.team.TeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberService memberService;
    
    @Autowired
    MemberDataRepository memberDataRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

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
    
    @Test
    void findUser() {
        List<Member> list = memberDataRepository.findUser("인환");
        
        List<MemberDto> list2 = memberDataRepository.findMemberDto();
        list2.stream().forEach(memberDto -> {
            System.out.println("memberDto = " + memberDto);
        });

        //0페이지에서 3개, username 내림차순
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");
        Page<Member> list3 = memberDataRepository.findByUsername("userA", pageRequest);

        List<Member> content = list3.getContent();
        long totalElement = list3.getTotalElements(); // 총 카운트
        int pageNo = list3.getNumber(); //현재 페이지
        int totalPages = list3.getTotalPages(); // 총페이지
        boolean isFirstPage = list3.isFirst(); // 첫번째 페이지냐
        boolean isLastPage = list3.isLast(); // 마지막 페이지냐
        boolean haNext = list3.hasNext(); // 다음페이지가 있냐

        System.out.println("pageNo = " + pageNo);
        System.out.println("totalElement = " + totalElement);

        content.stream().forEach(member -> {
            System.out.println("Page = " + member);
        });

        //dto 변환
        Page<MemberDto> map = list3.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        System.out.println("map = " + map);
    }


    @Test
    public void lazyTest(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberDataRepository.save(member1);
        memberDataRepository.save(member2);

        em.flush();
        em.clear();
        
        List<Member> members = memberDataRepository.findAll();
        
        for(Member member : members){
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getTeamName() = " + member.getTeam().getTeamName());
        }

    }

    @Test
    public void queryHint() {
        Member member1 = new Member("member1", 10);
        memberDataRepository.save(member1);
        em.flush();
        em.clear();

        Member findMember = memberDataRepository.findById(member1.getId()).get();
        findMember.setUsername("member2"); //변경감지

        em.flush();

        // 변겸감지를 위해... 원본을 항상 남겨둔다. 메모리를 더 써야한다는거
        // 근데 내가 진짜 이거 조회만 할건데 억울해. 변경감지 필요없는데
        // 이러면 @QueryHints(value=@QueryHint(name="org.hibernate.readOnly", value = "true"))
        // 힌트를 주는거다.
    }


    @Test
    public void projection(){
        List<UsernameOnly> list = memberDataRepository.findProjectionsByUsername("member1");
        for (UsernameOnly usernameOnly : list) {
            System.out.println("usernameOnly = " + usernameOnly);
        }
    }


}