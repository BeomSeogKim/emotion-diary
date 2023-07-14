package com.programmers.emotiondiary.repository;

import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입 테스트")
    void save() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");
        Member member = Member.createMember(signupRequestDto);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertAll(
                () -> assertThat(memberRepository.findAll().size()).isEqualTo(1),
                () -> assertThat(savedMember.getId()).isNotNull()
        );
    }

    @Test
    @DisplayName("회원 단건 조회 테스트")
    void findById() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        assertThat(findMember).isSameAs(member);
    }

    @Test
    @DisplayName("회원 다건 조회 테스트")
    void findAll() {

        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");
        SignupRequestDto signupRequestDto2 = new SignupRequestDto("kbs4522", "password", "Tommy", 20, "kbs45@daum.net");
        Member member1 = Member.createMember(signupRequestDto1);
        Member member2 = Member.createMember(signupRequestDto2);
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        List<Member> memberList = memberRepository.findAll();

        // then
        assertAll(
                () -> assertThat(memberList.size()).isEqualTo(2),
                () -> assertThat(memberList).containsExactlyInAnyOrder(member1, member2)
        );
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void delete() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs45@naver.com");
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);

        // when
        memberRepository.delete(member);

        // then
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("로그인 아이디가 null일 경우 Exception 발생")
    void invalidSaveLoginIdNull() {
        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto(null, "password", "Tommy", 20, "kbs4520@naver.com");
        Member member1 = Member.createMember(signupRequestDto1);

        // when && then
        assertThatThrownBy(
                () -> memberRepository.save(member1)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("로그인 아이디가 20자 이상일 경우 Exception 발생")
    void invalidSaveLoginIdLong() {
        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("qeoeekkdjddjdueueujddjddkdlkjafksdljadsfasdf", "password", "Tommy", 20, "kbs4520@naver.com");
        Member member1 = Member.createMember(signupRequestDto1);

        // when && then
        assertThatThrownBy(
                () -> memberRepository.save(member1)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }


    @Test
    @DisplayName("로그인 아이디 중복시 Exception 발생")
    void invalidSaveLoginIdUnique() {
        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs4520@naver.com");
        SignupRequestDto signupRequestDto2 = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs4511@naver.com");
        Member member1 = Member.createMember(signupRequestDto1);
        Member member2 = Member.createMember(signupRequestDto2);
        memberRepository.save(member1);

        // when && then
        assertThatThrownBy(
                () -> memberRepository.save(member2)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("비밀번호가 null일 경우 Exception 발생")
    void invalidSavePasswordNull() {
        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("kbs4520", null, "Tommy", 20, "kbs4520@naver.com");
        Member member1 = Member.createMember(signupRequestDto1);

        // when && then
        assertThatThrownBy(
                () -> memberRepository.save(member1)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("비밀번호가 20자 이상 일 경우 Exception 발생")
    void invalidSavePasswordLong() {
        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("kbs4520", "qerpquewrmdmdmdmdheqwherqjwer", "Tommy", 20, "kbs4520@naver.com");
        Member member1 = Member.createMember(signupRequestDto1);

        // when && then
        assertThatThrownBy(
                () -> memberRepository.save(member1)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("나이가 null일 경우 Exception 발생")
    void invalidSaveAgedNull() {
        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("kbs4520", "qewkrjqwekr", "Tommy", null, "kbs4520@naver.com");
        Member member1 = Member.createMember(signupRequestDto1);

        // when && then
        assertThatThrownBy(
                () -> memberRepository.save(member1)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("이메일이 null일 경우 Exception 발생")
    void invalidSaveEmailNull() {
        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("kbs4520", "qewkrjqwekr", "Tommy", 20, null);
        Member member1 = Member.createMember(signupRequestDto1);

        // when && then
        assertThatThrownBy(
                () -> memberRepository.save(member1)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }
    @Test
    @DisplayName("이메일이 중복 일 경우 Exception 발생")
    void invalidSaveEmailUnique() {
        // given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "kbs4520@naver.com");
        SignupRequestDto signupRequestDto2 = new SignupRequestDto("kbs4521", "password", "Tommy", 20, "kbs4520@naver.com");
        Member member1 = Member.createMember(signupRequestDto1);
        Member member2 = Member.createMember(signupRequestDto2);
        memberRepository.save(member1);

        // when && then
        assertThatThrownBy(
                () -> memberRepository.save(member2)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }


}
