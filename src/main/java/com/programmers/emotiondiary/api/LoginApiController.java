package com.programmers.emotiondiary.api;

import com.programmers.emotiondiary.dtos.request.login.LoginRequestDto;
import com.programmers.emotiondiary.dtos.request.login.LogoutRequestDto;
import com.programmers.emotiondiary.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginApiController {

    private final LoginService loginService;

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequestDto loginRequestDto,
                        HttpServletResponse httpServletResponse) {
        loginService.login(loginRequestDto, httpServletResponse);
        return "로그인 완료";
    }

    @PostMapping("/logout")
    public String logout(@RequestBody @Valid LogoutRequestDto logoutRequestDto,
                         HttpServletResponse httpServletResponse,
                         @CookieValue(name = "memberId") Long memberId
    ) {
        loginService.logout(memberId, logoutRequestDto, httpServletResponse);
        return "로그아웃 완료";
    }

}
