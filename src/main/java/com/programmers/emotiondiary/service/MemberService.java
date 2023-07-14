package com.programmers.emotiondiary.service;

import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import com.programmers.emotiondiary.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long signUp(SignupRequestDto signupRequestDto) {
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);
        return member.getId();
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new NoSuchElementException("Member who has memberId : " + memberId + " cannot find"));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
