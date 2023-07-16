package com.programmers.emotiondiary.dtos.request.member;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

/*
loginId : 20자 이하
password : 6자 이상 20자 이하
nickname : No Constraint
age : 20살 이상
email : 이메일 형식
 */
@Getter
public class SignupRequestDto {
    @Length(max = 20) @NotNull @NotBlank
    private String loginId;
    @Length(min = 6, max = 20)
    private String password;
    @NotNull @NotBlank
    private String nickName;
    @Min(20)
    private Integer age;
    @Email
    private String email;

    public SignupRequestDto(String loginId, String password, String nickName, Integer age, String email) {
        this.loginId = loginId;
        this.password = password;
        this.nickName = nickName;
        this.age = age;
        this.email = email;
    }
}
