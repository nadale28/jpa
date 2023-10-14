package com.example.demo.member.service.impl;

import com.example.demo.member.entity.Member;
import com.example.demo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Long save(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public Member find(Long id) {
        return memberRepository.find(id);
    }

    @Override
    public List<?> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    //회원 전체 조회
    @Override
    public List<?> findAll() {
        return memberRepository.findAll();
    }

    //회원가입
    @Override
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByUsername(member.getUsername());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }




}
