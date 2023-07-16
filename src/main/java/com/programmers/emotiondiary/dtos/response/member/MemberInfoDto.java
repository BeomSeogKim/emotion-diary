package com.programmers.emotiondiary.dtos.response.member;

import com.programmers.emotiondiary.domain.Member;
import lombok.Getter;

@Getter
public class MemberInfoDto {
    private Long id;
    private String nickName;
    private Integer age;
    private String email;

    private MemberInfoDto(Long id, String nickName, Integer age, String email) {
        this.id = id;
        this.nickName = nickName;
        this.age = age;
        this.email = email;
    }

    public static MemberInfoDto convertMemberToDto(Member member) {
        return new MemberInfoDto(member.getId(), member.getNickName(), member.getAge(), member.getEmail());
    }
}
