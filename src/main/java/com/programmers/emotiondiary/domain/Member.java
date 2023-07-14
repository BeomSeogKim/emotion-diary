package com.programmers.emotiondiary.domain;

import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @Column(unique = true, length = 20, nullable = false)
    private String loginId;
    @Column(length = 20, nullable = false)
    private String password;
    private String nickName;
    @Column(nullable = false)
    private Integer age;
    @Column(unique = true, nullable = false)
    private String email;

    protected Member() {
    }

    private Member(String loginId, String password, String nickName, Integer age, String email) {
        this.loginId = loginId;
        this.password = password;
        this.nickName = nickName;
        this.age = age;
        this.email = email;
    }

    public static Member createMember(SignupRequestDto signupRequestDto) {
        return new Member(signupRequestDto.getLoginId(), signupRequestDto.getPassword(), signupRequestDto.getNickName(), signupRequestDto.getAge(), signupRequestDto.getEmail());
    }
}
