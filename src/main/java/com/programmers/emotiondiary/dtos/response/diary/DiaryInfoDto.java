package com.programmers.emotiondiary.dtos.response.diary;

import com.programmers.emotiondiary.domain.Diary;
import com.programmers.emotiondiary.domain.Emotion;
import lombok.Getter;

@Getter
public class DiaryInfoDto {

    private Long id;
    private String content;
    private Emotion emotion;
    private boolean secret;

    public DiaryInfoDto(Diary diary) {
        this.id = diary.getId();
        this.content = diary.getContent();
        this.emotion = diary.getEmotion();
        this.secret = diary.isSecret();
    }
}
