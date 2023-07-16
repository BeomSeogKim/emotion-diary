package com.programmers.emotiondiary.dtos.response.member;

import lombok.Getter;

@Getter
public class SignupResponseDto {
    private Long signupId;

    public SignupResponseDto(Long signupId) {
        this.signupId = signupId;
    }
}
