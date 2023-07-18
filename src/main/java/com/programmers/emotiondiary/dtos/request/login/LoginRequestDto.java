package com.programmers.emotiondiary.dtos.request.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @NotNull @NotBlank
    private String loginId;
    @NotNull @NotBlank
    private String password;

    public LoginRequestDto() {
    }

    public LoginRequestDto(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
