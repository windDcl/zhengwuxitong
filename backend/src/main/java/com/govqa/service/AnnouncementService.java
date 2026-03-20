package com.govqa.service;

import com.govqa.common.BizException;
import com.govqa.dto.AnnouncementUpsertRequest;
import com.govqa.entity.Announcement;
import com.govqa.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public List<Announcement> listAll() {
        return announcementRepository.findAll();
    }

    public List<Announcement> listPublic() {
        return announcementRepository.findByStatusOrderByIsTopDescPublishTimeDesc(1);
    }

    public Announcement create(AnnouncementUpsertRequest req) {
        Announcement a = new Announcement();
        apply(req, a);
        return announcementRepository.save(a);
    }

    public Announcement update(Long id, AnnouncementUpsertRequest req) {
        Announcement a = announcementRepository.findById(id).orElseThrow(() -> new BizException("公告不存在"));
        apply(req, a);
        return announcementRepository.save(a);
    }

    public void delete(Long id) {
        announcementRepository.deleteById(id);
    }

    private void apply(AnnouncementUpsertRequest req, Announcement a) {
        a.setTitle(req.getTitle());
        a.setContent(req.getContent());
        a.setPublishTime(req.getPublishTime());
        a.setIsTop(req.getIsTop());
        a.setStatus(req.getStatus());
    }
}
