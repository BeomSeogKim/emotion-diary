package com.programmers.emotiondiary.api;

import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.member.ResignRequestDto;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import com.programmers.emotiondiary.dtos.response.member.MembersInfoDto;
import com.programmers.emotiondiary.dtos.response.member.SignupResponseDto;
import com.programmers.emotiondiary.dtos.response.member.MemberInfoDto;
import com.programmers.emotiondiary.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public SignupResponseDto signUp(@RequestBody @Valid SignupRequestDto signupRequestDto) throws NoSuchMethodException {
        Long memberId = memberService.signUp(signupRequestDto);
        return new SignupResponseDto(memberId);
    }

    @GetMapping("/{memberId}")
    public MemberInfoDto findMember(@PathVariable Long memberId) {
        Member member = memberService.findMember(memberId);
        return MemberInfoDto.convertMemberToDto(member);
    }

    @GetMapping
    public MembersInfoDto findMembers() {
        List<Member> memberList = memberService.findAll();
        return MembersInfoDto.convertMemberToDto(memberList);
    }

    @DeleteMapping("/{memberId}")
    public String deleteMember(@PathVariable Long memberId,
                               @RequestBody @Valid ResignRequestDto resignRequestDto
    ) {
        memberService.deleteMember(memberId, resignRequestDto);
        return "회원 탈퇴가 완료되었습니다.";
    }
}
