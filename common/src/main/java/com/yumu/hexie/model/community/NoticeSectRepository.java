package com.yumu.hexie.model.community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeSectRepository extends JpaRepository<NoticeSect, Long> {

    public List<NoticeSect> findByNoticeId(Long noticeId);
}
