package com.programmers.emotiondiary.service;

import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.member.ResignRequestDto;
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

    @Transactional
    public Long signUp(SignupRequestDto signupRequestDto) {
        validateLoginId(signupRequestDto);
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateLoginId(SignupRequestDto signupRequestDto) {
        if (memberRepository.findByLoginId(signupRequestDto.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원 아이디 입니다.");
        }
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new NoSuchElementException("Member who has memberId : " + memberId + " cannot find"));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public void deleteMember(Long memberId, ResignRequestDto resignRequestDto) {
        Member member = validateMemberId(memberId);
        deleteMember(memberId, resignRequestDto, member);

    }

    private Member validateMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("잘못된 회원 아이디 입니다.")
        );
        return member;
    }

    private void deleteMember(Long memberId, ResignRequestDto resignRequestDto, Member member) {
        if (member.getLoginId().equals(resignRequestDto.getLoginId())
                && member.getPassword().equals(resignRequestDto.getPassword())) {
            memberRepository.deleteById(memberId);
        } else {
            throw new IllegalArgumentException("가입시 등록한 아이디와 비밀번호가 동일하지 않습니다.");
        }
    }
}
