package com.programmers.emotiondiary.dtos.request.member;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ResignRequestDto {
    @NotNull
    private String loginId;
    @NotNull
    private String password;

    public ResignRequestDto(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
