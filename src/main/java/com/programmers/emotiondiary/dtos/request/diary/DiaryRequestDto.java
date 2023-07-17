package com.programmers.emotiondiary.dtos.request.diary;

import com.programmers.emotiondiary.domain.Emotion;
import lombok.Getter;

@Getter
public class DiaryRequestDto {
    private String content;
    private Emotion emotion;

    public DiaryRequestDto(String content, Emotion emotion) {
        this.content = content;
        this.emotion = emotion;
    }
}
