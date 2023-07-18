package com.programmers.emotiondiary.dtos.request.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LogoutRequestDto {
    @NotNull @NotBlank
    private String password;

    public LogoutRequestDto() {
    }

    public LogoutRequestDto(String password) {
        this.password = password;
    }
}
