package com.programmers.emotiondiary.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.emotiondiary.domain.Diary;
import com.programmers.emotiondiary.domain.Emotion;
import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.diary.DiaryRequestDto;
import com.programmers.emotiondiary.dtos.request.member.SignupRequestDto;
import com.programmers.emotiondiary.repository.DiaryRepository;
import com.programmers.emotiondiary.repository.MemberRepository;
import com.programmers.emotiondiary.service.DiaryService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class DiaryApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    DiaryService diaryService;

    @Test
    @DisplayName("게시글 작성 검증")
    void write() throws Exception {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember = memberRepository.save(member);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);

        // when
        this.mockMvc.perform(
                        post("/api/diaries")
                                .content(objectMapper.writeValueAsString(diaryRequestDto))
                                .contentType("application/json")
                                .cookie(new Cookie("memberId", String.valueOf(savedMember.getId())))
                )
                .andExpect(status().isOk());

        // then

        List<Diary> diaryList = diaryRepository.findAll();
        Diary savedDiary = diaryList.get(0);
        assertAll(
                () -> assertThat(diaryList.size()).isEqualTo(1),
                () -> assertThat(savedDiary.getMember()).isEqualTo(savedMember),
                () -> assertThat(savedDiary.getContent()).isEqualTo("This is my First diary"),
                () -> assertThat(savedDiary.getEmotion()).isEqualTo(Emotion.HAPPY)
        );
    }

    @Test
    @DisplayName("게시글 작성시 유효하지 않은 회원 아이디일 경우 bad request")
    void write_invalidMemberId() throws Exception {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember = memberRepository.save(member);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);

        // when && then
        this.mockMvc.perform(
                        post("/api/diaries")
                                .content(objectMapper.writeValueAsString(diaryRequestDto))
                                .contentType("application/json")
                                .cookie(new Cookie("memberId", String.valueOf(savedMember.getId() + 1)))
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("게시글 단건 조회시 작성자의 경우 private 조회 가능")
    void findDiary() throws Exception {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember = memberRepository.save(member);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto);

        // when && then
        this.mockMvc.perform(
                        get("/api/diaries/{diaryId}", diaryId)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember.getId())))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 단건 조회시 public인 경우 다른 회원 조회 가능")
    void findDiary_public() throws Exception {

        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember1 = memberRepository.save(member1);
        Member member2 = Member.createMember(new SignupRequestDto("kbs4521", "password", "Tom", 20, "tom1.naver.com"));
        Member savedMember2 = memberRepository.save(member2);


        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto);

        Diary diary = diaryRepository.findById(diaryId).get();
        diary.publish();

        // when && then
        this.mockMvc.perform(
                        get("/api/diaries/{diaryId}", diaryId)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember2.getId())))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 단건 조회시 private인 경우 다른 회원 조회 불가")
    void findDiary_private() throws Exception {

        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember1 = memberRepository.save(member1);
        Member member2 = Member.createMember(new SignupRequestDto("kbs4521", "password", "Tom", 20, "tom1.naver.com"));
        Member savedMember2 = memberRepository.save(member2);


        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto);

        // when && then
        this.mockMvc.perform(
                        get("/api/diaries/{diaryId}", diaryId)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember2.getId())))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 단건 조회시 잘못된 diaryId일 경우 bad request")
    void findDiary_invalidId() throws Exception {
        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember = memberRepository.save(member);

        DiaryRequestDto diaryRequestDto = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto);

        // when && then
        this.mockMvc.perform(
                        get("/api/diaries/{diaryId}", diaryId + 1)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember.getId())))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 다건 조회 검증")
    void findDiaries() throws Exception {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember = memberRepository.save(member);

        DiaryRequestDto diaryRequestDto1 = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        DiaryRequestDto diaryRequestDto2 = new DiaryRequestDto("This is my Second diary", Emotion.NORMAL);

        diaryService.write(savedMember.getId(), diaryRequestDto1);
        diaryService.write(savedMember.getId(), diaryRequestDto2);
        // when
        this.mockMvc.perform(
                        get("/api/diaries")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 삭제 검증")
    void deleteDiary() throws Exception {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember = memberRepository.save(member);

        DiaryRequestDto diaryRequestDto1 = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto1);

        assertThat(diaryRepository.findAll().size()).isEqualTo(1);

        // when
        this.mockMvc.perform(
                        delete("/api/diaries/{diaryId}", diaryId)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember.getId())))
                )
                .andExpect(status().isOk());

        // then
        assertThat(diaryRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시글 삭제 시 잘못된 아이디로 삭제할 경우 bad request")
    void deleteDiary_invalidId() throws Exception {

        // given
        Member member = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember = memberRepository.save(member);

        DiaryRequestDto diaryRequestDto1 = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember.getId(), diaryRequestDto1);

        assertThat(diaryRepository.findAll().size()).isEqualTo(1);

        // when
        this.mockMvc.perform(
                        delete("/api/diaries/{diaryId}", diaryId + 1)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember.getId())))
                )
                .andExpect(status().isBadRequest());

        // then
        assertThat(diaryRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 삭제시 작성자만 삭제 할 수 있다.")
    void deleteDiary_noAuthority() throws Exception {

        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member member2 = Member.createMember(new SignupRequestDto("kbs4521", "password", "Tom", 20, "tom2.naver.com"));
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        DiaryRequestDto diaryRequestDto1 = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto1);

        assertThat(diaryRepository.findAll().size()).isEqualTo(1);

        // when
        this.mockMvc.perform(
                        delete("/api/diaries/{diaryId}", diaryId)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember2.getId())))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        // then
        assertThat(diaryRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 공개 검증")
    void publish() throws Exception {
        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member savedMember1 = memberRepository.save(member1);

        DiaryRequestDto diaryRequestDto1 = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto1);

        Diary beforePublishDiary = diaryRepository.findById(diaryId).get();
        assertThat(beforePublishDiary.isSecret()).isTrue();

        // when
        this.mockMvc.perform(
                        post("/api/diaries/{diaryId}/publish", diaryId)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember1.getId())))
                )
                .andExpect(status().isOk());

        // then
        Diary afterPublishDiary = diaryRepository.findById(diaryId).get();
        assertThat(afterPublishDiary.isSecret()).isFalse();
    }

    @Test
    @DisplayName("권한이 없는 사용자가 공개 요청시 bad request")
    void publish_noAuthority() throws Exception {
        // given
        Member member1 = Member.createMember(new SignupRequestDto("kbs4520", "password", "Tom", 20, "tom.naver.com"));
        Member member2 = Member.createMember(new SignupRequestDto("kbs4521", "password", "Tom", 20, "tom2.naver.com"));
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        DiaryRequestDto diaryRequestDto1 = new DiaryRequestDto("This is my First diary", Emotion.HAPPY);
        Long diaryId = diaryService.write(savedMember1.getId(), diaryRequestDto1);

        Diary beforePublishDiary = diaryRepository.findById(diaryId).get();
        assertThat(beforePublishDiary.isSecret()).isTrue();

        // when && then
        this.mockMvc.perform(
                        post("/api/diaries/{diaryId}/publish", diaryId)
                                .cookie(new Cookie("memberId", String.valueOf(savedMember2.getId())))
                )
                .andExpect(status().isBadRequest());

        Diary afterPublishDiary = diaryRepository.findById(diaryId).get();
        assertThat(afterPublishDiary.isSecret()).isTrue();
    }


}
