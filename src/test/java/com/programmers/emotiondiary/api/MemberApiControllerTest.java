package com.programmers.emotiondiary.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.emotiondiary.dtos.request.member.ResignRequestDto;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import com.programmers.emotiondiary.repository.MemberRepository;
import com.programmers.emotiondiary.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class MemberApiControllerTest {

    @Autowired
    MemberApiController memberApiController;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 검증")
    void signUp() throws Exception {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");

        // when && then
        this.mockMvc.perform(
                        post("/api/members")
                                .content(objectMapper.writeValueAsString(signupRequestDto))
                                .contentType("application/json"))
                .andExpect(status().isCreated());

        assertThat(memberRepository.findByLoginId(signupRequestDto.getLoginId())).isPresent();
    }

    @Test
    @DisplayName("이미 존재하는 로그인 아이디일 경우 bad request")
    void signUp_existLoginId() throws Exception {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        memberService.signUp(signupRequestDto);

        // when && then
        this.mockMvc.perform(
                post("/api/members")
                        .content(objectMapper.writeValueAsString(signupRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 단건 조회 검증")
    void findMember() throws Exception {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        Long saveId = memberService.signUp(signupRequestDto);

        // when && then
        this.mockMvc.perform(
                        get("/api/members/{memberId}", saveId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 단건 조회시 유효하지 않은 memberId일 경우 bad request")
    void findMember_invalidMemberId() throws Exception {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        Long saveId = memberService.signUp(signupRequestDto);

        // when && then
        this.mockMvc.perform(
                        get("/api/members/{memberId}", saveId + 100))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 다중 조회 검증")
    void findMembers() throws Exception {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        memberService.signUp(signupRequestDto);

        // when && then
        this.mockMvc.perform(
                        get("/api/members"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("회원 탈퇴 검증")
    void deleteMember() throws Exception{

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        ResignRequestDto resignRequestDto = new ResignRequestDto("kbs4520", "password");
        Long saveId = memberService.signUp(signupRequestDto);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

        // when
        this.mockMvc.perform(
                delete("/api/members/{memberId}", saveId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(resignRequestDto))
        ).andExpect(status().isOk());

        // then
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("유효하지 않은 memberId일 경우 bad request")
    void deleteMember_invalidMemberId() throws Exception{

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        ResignRequestDto resignRequestDto = new ResignRequestDto("kbs4520", "password");
        Long saveId = memberService.signUp(signupRequestDto);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

        // when && then
        this.mockMvc.perform(
                delete("/api/members/{memberId}", saveId + 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(resignRequestDto))
        ).andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("잘못된 로그인 id일 경우 bad request")
    void deleteMember_invalidLoginId() throws Exception{

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        ResignRequestDto resignRequestDto = new ResignRequestDto("tommyK", "password");
        Long saveId = memberService.signUp(signupRequestDto);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

        // when && then
        this.mockMvc.perform(
                delete("/api/members/{memberId}", saveId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(resignRequestDto))
        ).andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("잘못된 password일 경우 bad request")
    void deleteMember_invalidPassword() throws Exception{

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        ResignRequestDto resignRequestDto = new ResignRequestDto("kbs4520", "new_password");
        Long saveId = memberService.signUp(signupRequestDto);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

        // when && then
        this.mockMvc.perform(
                delete("/api/members/{memberId}", saveId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(resignRequestDto))
        ).andExpect(status().isBadRequest());

    }



}
