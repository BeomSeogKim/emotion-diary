package com.programmers.emotiondiary.dtos.response.diary;

import lombok.Getter;

@Getter
public class DiaryResponseDto {
    private Long diaryId;
    public DiaryResponseDto(Long diaryId) {
        this.diaryId = diaryId;
    }
}
