package com.example.demo.member;

import com.example.demo.member.entity.Member;
import com.example.demo.member.service.impl.MemberDataRepository;
import com.example.demo.member.service.impl.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberContoller {


    private final MemberDataRepository memberDataRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberDataRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/member2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @PostConstruct
    public void init() {
        memberDataRepository.save(new Member("userA",20));
    }


}
