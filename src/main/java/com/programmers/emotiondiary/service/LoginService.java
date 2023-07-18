package com.programmers.emotiondiary.service;

import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.login.LoginRequestDto;
import com.programmers.emotiondiary.dtos.request.login.LogoutRequestDto;
import com.programmers.emotiondiary.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;


    public void login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        Member loginMember = memberRepository.findByLoginId(loginRequestDto.getLoginId())
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 회원 아이디가 존재하지 않습니다.")
                );
        if (loginMember.getPassword().equals(loginRequestDto.getPassword())) {
            addValidCookie(httpServletResponse, loginMember);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private static void addValidCookie(HttpServletResponse httpServletResponse, Member loginMember) {
        Cookie cookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
    }

    public void logout(Long memberId, LogoutRequestDto logoutRequestDto, HttpServletResponse httpServletResponse) {
        Member logoutMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 회원 아이디가 존재하지 않습니다.")
        );
        if (logoutMember.getPassword().equals(logoutRequestDto.getPassword())) {
            addInvalidCookie(httpServletResponse, logoutMember);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private static void addInvalidCookie(HttpServletResponse httpServletResponse, Member logoutMember) {
        Cookie cookie = new Cookie("memberId", String.valueOf(logoutMember.getId()));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
    }
}
