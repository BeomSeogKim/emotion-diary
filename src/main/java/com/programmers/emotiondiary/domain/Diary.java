package com.programmers.emotiondiary.domain;

import com.programmers.emotiondiary.dtos.request.diary.DiaryRequestDto;
import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
public class Diary {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "diary_id")
    private Long id;
    @Column(length = 500)
    private String content;
    @Enumerated
    private Emotion emotion;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    private boolean secret;

    protected Diary() {
    }

    private Diary(String content, Emotion emotion, Member member) {
        this.content = content;
        this.emotion = emotion;
        this.member = member;
        this.secret = true;
    }

    public static Diary createDiary(Member member, DiaryRequestDto diaryRequestDto) {
        return new Diary(diaryRequestDto.getContent(), diaryRequestDto.getEmotion(), member);
    }
}
