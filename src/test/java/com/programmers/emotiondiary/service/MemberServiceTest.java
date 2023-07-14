package com.programmers.emotiondiary.service;

import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import com.programmers.emotiondiary.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void signUp() {

        // given

        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");

        // when
        Long signUpId = memberService.signUp(signupRequestDto);

        // then
        Member findMember = memberRepository.findById(signUpId).get();
        assertAll(
                () -> assertThat(findMember.getLoginId()).isEqualTo(signupRequestDto.getLoginId()),
                () -> assertThat(findMember.getPassword()).isEqualTo(signupRequestDto.getPassword()),
                () -> assertThat(findMember.getNickName()).isEqualTo(signupRequestDto.getNickName()),
                () -> assertThat(findMember.getAge()).isEqualTo(signupRequestDto.getAge()),
                () -> assertThat(findMember.getEmail()).isEqualTo(signupRequestDto.getEmail())
        );

    }

    @Test
    void findMember() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");
        Long signUpId = memberService.signUp(signupRequestDto);

        // when
        Member findMember = memberService.findMember(signUpId);

        // then
        assertAll(
                () -> assertThat(findMember.getId()).isEqualTo(signUpId),
                () -> assertThat(findMember.getNickName()).isEqualTo(signupRequestDto.getNickName()),
                () -> assertThat(findMember.getAge()).isEqualTo(signupRequestDto.getAge())
        );

    }

    @Test
    void findMember_Exception() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");
        Long saveId = memberService.signUp(signupRequestDto);

        // when && then
        assertThatThrownBy(
                () -> memberService.findMember(saveId + 1)
        )
        .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void findAll() {

        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");
        SignupRequestDto signupRequestDto2 = new SignupRequestDto("kbs4521", "password", "Tommy", 20, "tommy1@naver.com");
        memberService.signUp(signupRequestDto1);
        memberService.signUp(signupRequestDto2);

        // when
        List<Member> memberList = memberService.findAll();

        // then
        assertThat(memberList.size()).isEqualTo(2);
    }

    @Test
    void deleteMember() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");
        Long saveId = memberService.signUp(signupRequestDto);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

        // when
        memberService.deleteMember(saveId);

        // then
        assertThat(memberService.findAll().size()).isEqualTo(0);
    }
}
