package com.example.demo.member.service.impl;

import com.example.demo.member.entity.Member;
import com.example.demo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
