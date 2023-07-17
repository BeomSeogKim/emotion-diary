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
            @RequestHeader("memberId") Long memberId
    ) {
        Long diaryId = diaryService.write(memberId, diaryRequestDto);
        return new DiaryResponseDto(diaryId);
    }

    @GetMapping("/{diaryId}")
    public DiaryInfoDto findDiary(@PathVariable Long diaryId) {
        Diary diary = diaryService.findDiary(diaryId);
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
            @RequestHeader("memberId") Long memberId
    ) {
        diaryService.delete(memberId, diaryId);
        return "삭제가 완료되었습니다.";
    }
}
