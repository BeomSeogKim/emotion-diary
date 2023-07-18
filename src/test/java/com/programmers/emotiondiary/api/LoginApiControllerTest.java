package com.programmers.emotiondiary.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.login.LoginRequestDto;
import com.programmers.emotiondiary.dtos.request.login.LogoutRequestDto;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import com.programmers.emotiondiary.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class LoginApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("로그인 검증")
    void login() throws Exception {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);

        LoginRequestDto loginRequestDto = new LoginRequestDto("kbs4520", "password");

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequestDto))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(cookie().exists("memberId"))
                .andExpect(cookie().httpOnly("memberId", true));

    }

    @Test
    @DisplayName("로그아웃 검증")
    void logout() throws Exception {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com");
        Member member = Member.createMember(signupRequestDto);
        Member savedMember = memberRepository.save(member);

        LogoutRequestDto logoutRequestDto = new LogoutRequestDto("password");

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/logout")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(logoutRequestDto))
                        .cookie(new Cookie("memberId", String.valueOf(savedMember.getId())))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(cookie().exists("memberId"))
                .andExpect(cookie().maxAge("memberId", 0))
                .andExpect(cookie().httpOnly("memberId", true));
    }
}
