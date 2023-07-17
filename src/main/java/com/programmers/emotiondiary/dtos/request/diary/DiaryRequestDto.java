package com.programmers.emotiondiary.dtos.request.diary;

import com.programmers.emotiondiary.domain.Emotion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DiaryRequestDto {
    @NotNull @NotBlank
    private String content;
    @NotNull @NotBlank
    private Emotion emotion;

    public DiaryRequestDto(String content, Emotion emotion) {
        this.content = content;
        this.emotion = emotion;
    }
}
