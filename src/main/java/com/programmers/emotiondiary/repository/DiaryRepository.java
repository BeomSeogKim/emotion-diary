package com.programmers.emotiondiary.repository;

import com.programmers.emotiondiary.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    @Query(value = "select d from Diary d where d.secret = false")
    List<Diary> findAllDiariesPublic();
}
