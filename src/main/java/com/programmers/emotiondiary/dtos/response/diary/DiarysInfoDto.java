package com.programmers.emotiondiary.dtos.response.diary;

import com.programmers.emotiondiary.domain.Diary;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DiarysInfoDto {
    List<DiaryInfoDto> diaries = new ArrayList<>();

    public DiarysInfoDto(List<Diary> diaryList) {
        diaryList.forEach(
                d -> {
                    diaries.add(new DiaryInfoDto(d));
                }
        );
    }
}
