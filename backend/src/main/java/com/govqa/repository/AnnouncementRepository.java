package com.govqa.repository;

import com.govqa.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByStatusOrderByIsTopDescPublishTimeDesc(Integer status);
}
