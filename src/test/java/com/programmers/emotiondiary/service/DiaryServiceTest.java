package com.programmers.emotiondiary.service;

import com.programmers.emotiondiary.domain.Diary;
import com.programmers.emotiondiary.domain.Emotion;
import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.diary.DiaryRequestDto;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import com.programmers.emotiondiary.repository.DiaryRepository;
import com.programmers.emotiondiary.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class DiaryServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    DiaryRepository diaryRepository;
    @Autowired
    DiaryService diaryService;

    @Test
    @DisplayName("다이어리 저장 검증")
    void save() {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember = memberRepository.save(member);
        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);

        // when
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto);

        // then
        Diary findDiary = diaryRepository.findById(diaryId).get();
        assertAll(
                () -> assertThat(findDiary.getMember()).isEqualTo(savedMember),
                () -> assertThat(findDiary.getEmotion()).isEqualTo(Emotion.HAPPY)
        );
    }

    @Test
    @DisplayName("잘못된 회원 아이디인 경우 예외 발생")
    void save_invalidMemberId() {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember = memberRepository.save(member);
        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);

        // when && then
        assertThatThrownBy(
                () -> diaryService.write(savedMember.getId() + 1, diaryRequestDto)
        ).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("다이어리 단건 검증")
    void findMember() {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember = memberRepository.save(member);
        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto);

        // when
        Diary findDiary = diaryService.findDiary(diaryId);

        // then
        assertAll(
                () -> assertThat(findDiary.getMember()).isEqualTo(savedMember),
                () -> assertThat(findDiary.getEmotion()).isEqualTo(Emotion.HAPPY)
        );
    }

    @Test
    @DisplayName("잘못된 다이어리 아이디 일 경우 예외 발생")
    void findMember_invalidDiaryId() {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember = memberRepository.save(member);
        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto);

        // when && then
        assertThatThrownBy(
                () -> diaryService.findDiary(diaryId + 1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다중 다이어리 리스트 조회 검증")
    void findDiaryList() {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember = memberRepository.save(member);
        DiaryRequestDto diaryRequestDto1 = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        DiaryRequestDto diaryRequestDto2 = new DiaryRequestDto("오늘 나는 우울해요", Emotion.SAD);
        diaryService.write(savedMember.getId(), diaryRequestDto1);
        diaryService.write(savedMember.getId(), diaryRequestDto2);

        // when
        List<Diary> diaryList = diaryService.findDiaryList();

        // then
        assertThat(diaryList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("다이어리 삭제 검증")
    void delete() {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember = memberRepository.save(member);
        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto);
        assertThat(diaryRepository.findAll().size()).isEqualTo(1);

        // when
        diaryService.delete(diaryId);

        // then
        assertThat(diaryRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("잘못된 다이어리 id로 삭제 시 예외 발생")
    void delete_invalidDiaryId() {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember = memberRepository.save(member);
        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto);
        assertThat(diaryRepository.findAll().size()).isEqualTo(1);

        // when && then
        assertThatThrownBy(
                () -> diaryService.delete(diaryId + 1)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
