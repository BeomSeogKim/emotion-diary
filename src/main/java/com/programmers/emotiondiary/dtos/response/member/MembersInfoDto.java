package com.programmers.emotiondiary.dtos.response.member;

import com.programmers.emotiondiary.domain.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MembersInfoDto {
    List<MemberInfoDto> memberInfoDtoList = new ArrayList<>();

    public static MembersInfoDto convertMemberToDto(List<Member> memberList) {
        MembersInfoDto membersInfoDto = new MembersInfoDto();
        memberList.forEach(
                m -> {
                    membersInfoDto.memberInfoDtoList.add(MemberInfoDto.convertMemberToDto(m));
                }
        );
        return membersInfoDto;
    }
}
