package com.programmers.emotiondiary.api;

import com.programmers.emotiondiary.domain.Diary;
import com.programmers.emotiondiary.dtos.request.diary.DiaryRequestDto;
import com.programmers.emotiondiary.dtos.response.diary.DiaryInfoDto;
import com.programmers.emotiondiary.dtos.response.diary.DiaryResponseDto;
import com.programmers.emotiondiary.dtos.response.diary.DiarysInfoDto;
import com.programmers.emotiondiary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries")
public class DiaryApiController {

    private final DiaryService diaryService;

    @PostMapping
    public DiaryResponseDto write(
            @RequestBody DiaryRequestDto diaryRequestDto,
            @CookieValue(name = "memberId") Long memberId
    ) {
        Long diaryId = diaryService.write(memberId, diaryRequestDto);
        return new DiaryResponseDto(diaryId);
    }

    @GetMapping("/{diaryId}")
    public DiaryInfoDto findDiary(@PathVariable Long diaryId,
                                  @CookieValue(name = "memberId") Long memberId) {
        Diary diary = diaryService.findDiary(diaryId, memberId);
        return new DiaryInfoDto(diary);
    }

    @GetMapping
    public DiarysInfoDto findDiaries() {
        List<Diary> diaryList = diaryService.findDiaryList();
        return new DiarysInfoDto(diaryList);
    }

    @DeleteMapping("/{diaryId}")
    public String deleteDiary(
            @PathVariable Long diaryId,
            @CookieValue(name = "memberId") Long memberId
    ) {
        diaryService.delete(memberId, diaryId);
        return "삭제가 완료되었습니다.";
    }

    @PostMapping("/{diaryId}/publish")
    public String publish(
            @PathVariable Long diaryId,
            @CookieValue(name = "memberId") Long memberId
    ) {
        diaryService.publishDiary(diaryId, memberId);
        return "공개처리 되었습니다.";
    }
}
