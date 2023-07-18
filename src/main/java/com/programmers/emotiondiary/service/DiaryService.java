package com.programmers.emotiondiary.service;

import com.programmers.emotiondiary.domain.Diary;
import com.programmers.emotiondiary.domain.Member;
import com.programmers.emotiondiary.dtos.request.diary.DiaryRequestDto;
import com.programmers.emotiondiary.repository.DiaryRepository;
import com.programmers.emotiondiary.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;

    public Long write(Long memberId, DiaryRequestDto diaryRequestDto) {
        Member member = findMember(memberId);
        Diary diary = Diary.createDiary(member, diaryRequestDto);
        return diaryRepository.save(diary).getId();
    }

    private Member findMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("잘못된 memberId")
        );
        return member;
    }

    public Diary findDiary(Long diaryId, Long memberId) {
        Diary diary = getDiaryFromDb(diaryId);
        return getDiaryIfAccessible(memberId, diary);
    }

    private static Diary getDiaryIfAccessible(Long memberId, Diary diary) {
        if (diary.isSecret() == false) {
            return diary;
        } else {
            if (diary.getMember().getId() == memberId) {
                return diary;
            } else {
                throw new IllegalArgumentException("다이어리에 대한 접근 권한이 없습니다.");
            }
        }
    }

    private Diary getDiaryFromDb(Long diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(
                () -> new IllegalArgumentException("잘못된 diaryId")
        );
    }

    // TODO 로그인 서비스 개발 후 조회 로직 수정
    public List<Diary> findDiaryList() {
        return diaryRepository.findAll();
    }

    public void delete(Long memberId, Long diaryId) {
        Diary diary = getDiaryFromDb(diaryId);
        deleteDiary(memberId, diary);
    }

    private void deleteDiary(Long memberId, Diary diary) {
        if (diary.getMember().getId().equals(memberId)) {
            diaryRepository.delete(diary);
        } else {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
    }

    public void publishDiary(Long diaryId, Long memberId) {
        Diary diary = getDiaryFromDb(diaryId);
        if (diary.getMember().getId() == memberId) {
            diary.publish();
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
