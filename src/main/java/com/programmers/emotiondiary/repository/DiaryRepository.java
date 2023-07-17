package com.programmers.emotiondiary.repository;

import com.programmers.emotiondiary.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

}
