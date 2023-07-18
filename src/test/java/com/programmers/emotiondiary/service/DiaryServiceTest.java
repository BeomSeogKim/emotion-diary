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
        Diary findDiary = diaryService.findDiary(diaryId, savedMember.getId());

        // then
        assertAll(
                () -> assertThat(findDiary.getMember()).isEqualTo(savedMember),
                () -> assertThat(findDiary.getEmotion()).isEqualTo(Emotion.HAPPY)
        );
    }

    @Test
    @DisplayName("다이어리 단건 조회 시 private일 경우 다른 회원 조회 불가")
    void findMember_private() {
        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member member2 = Member.createMember(new SignupRequestDto("kbs4521", "password", "tommy", 20, "tommy1@gmail.com"));
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto);

        // when && then
        assertThatThrownBy(
                () -> diaryService.findDiary(diaryId, savedMember2.getId())
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다이어리 단건 조회 시 public일 경우 다른 회원 조회 가능")
    void findMember_public() {
        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member member2 = Member.createMember(new SignupRequestDto("kbs4521", "password", "tommy", 20, "tommy1@gmail.com"));
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto);
        Diary diary = diaryRepository.findById(diaryId).get();
        diary.publish();

        // when
        Diary findDiary = diaryService.findDiary(diaryId, savedMember2.getId());

        // then
        assertThat(findDiary.isSecret()).isFalse();
        assertThat(findDiary.getEmotion()).isEqualTo(Emotion.HAPPY);
        assertThat(findDiary.getContent()).isEqualTo("오늘 나는 해피해요");

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
                () -> diaryService.findDiary(diaryId + 1, savedMember.getId())
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
        diaryService.delete(savedMember.getId(), diaryId);

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
                () -> diaryService.delete(savedMember.getId(), diaryId + 1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("작성자만 게시글 삭제 가능")
    void delete_noAuthority() {

        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member member2 = Member.createMember(new SignupRequestDto("kbs4522", "password", "tommy", 20, "tommy2@gmail.com"));
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto);
        assertThat(diaryRepository.findAll().size()).isEqualTo(1);

        // when && then
        assertThatThrownBy(
                () -> diaryService.delete(savedMember2.getId(), diaryId + 1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다이어리 공개 기능 검증")
    void publish() {

        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember1 = memberRepository.save(member1);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto);

        Diary beforePublish = diaryRepository.findById(diaryId).get();
        assertThat(beforePublish.isSecret()).isTrue();

        // when
        diaryService.publishDiary(diaryId, savedMember1.getId());

        // then
        Diary afterPublishDiary = diaryRepository.findById(diaryId).get();
        assertThat(afterPublishDiary.isSecret()).isFalse();
    }

    @Test
    @DisplayName("다이어리 공개 기능 작성자가 아닌 경우 예외")
    void publish_noAuthority() {

        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "tommy", 20, "tommy@gmail.com"));
        Member savedMember1 = memberRepository.save(member1);

        Member member2 = Member.createMember(new SignupRequestDto("kbs4521", "password", "tommy", 20, "tommy1@gmail.com"));
        Member savedMember2 = memberRepository.save(member2);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("오늘 나는 해피해요", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto);

        Diary findDiary = diaryRepository.findById(diaryId).get();
        assertThat(findDiary.isSecret()).isTrue();

        // when && then
        assertThatThrownBy(
                () -> diaryService.publishDiary(diaryId, savedMember2.getId())
        ).isInstanceOf(IllegalArgumentException.class);

        Diary afterDiary = diaryRepository.findById(diaryId).get();
        assertThat(afterDiary.isSecret()).isTrue();

    }

}
