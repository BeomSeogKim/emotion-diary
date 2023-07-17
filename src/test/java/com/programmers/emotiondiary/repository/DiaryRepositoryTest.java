package com.programmers.emotiondiary.repository;

import com.programmers.emotiondiary.domain.Diary;
import com.programmers.emotiondiary.domain.Emotion;
import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.diary.DiaryRequestDto;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class DiaryRepositoryTest {

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("다이어리 저장 검증")
    void save() {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "tommy@gmail.com");
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is Diary", Emotion.HAPPY);
        Diary diary = Diary.createDiary(member, diaryRequestDto);

        // when
        Diary saveDiary = diaryRepository.save(diary);

        // then
        Diary findDiary = diaryRepository.findById(saveDiary.getId()).get();
        assertAll(
                () -> assertThat(findDiary.getMember()).isEqualTo(member),
                () -> assertThat(findDiary.getEmotion()).isEqualTo(Emotion.HAPPY),
                () -> assertThat(findDiary.isSecret()).isTrue()     // 다이어리의 기본 공개 여부는 false
        );
    }

    @Test
    @DisplayName("다이어리 단건 조회 검증")
    void find() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "tommy@gmail.com");
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is Diary", Emotion.HAPPY);
        Diary diary = Diary.createDiary(member, diaryRequestDto);
        Diary savedDiary = diaryRepository.save(diary);

        // when
        Diary findDiary = diaryRepository.findById(savedDiary.getId()).get();

        // then
        assertAll(
                () -> assertThat(findDiary).isEqualTo(savedDiary),
                () -> assertThat(findDiary.getMember()).isEqualTo(member),
                () -> assertThat(findDiary.getContent()).isEqualTo("This is Diary")
        );
    }

    @Test
    @DisplayName("다이어리 다건 조회 검증")
    void findAll() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "tommy@gmail.com");
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);

        DiaryRequestDto diaryRequestDto1 = new DiaryRequestDto("This is Diary Version 1", Emotion.HAPPY);
        DiaryRequestDto diaryRequestDto2 = new DiaryRequestDto("This is Diary Version 2", Emotion.BORED);
        Diary diary1 = Diary.createDiary(member, diaryRequestDto1);
        Diary diary2 = Diary.createDiary(member, diaryRequestDto2);

        diaryRepository.save(diary1);
        diaryRepository.save(diary2);

        // when
        List<Diary> diaryList = diaryRepository.findAll();

        // then
        assertThat(diaryList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("다이어리 삭제 검증")
    void delete() {

        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("kbs4520", "password", "Tommy", 20, "tommy@gmail.com");
        Member member = Member.createMember(signupRequestDto);
        memberRepository.save(member);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is Diary", Emotion.HAPPY);
        Diary diary = Diary.createDiary(member, diaryRequestDto);
        diaryRepository.save(diary);

        assertThat(diaryRepository.findAll().size()).isEqualTo(1);

        // when
        diaryRepository.delete(diary);

        // then
        assertThat(diaryRepository.findAll().size()).isEqualTo(0);
    }

}
