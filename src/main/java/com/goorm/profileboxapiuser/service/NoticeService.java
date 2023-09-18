package com.goorm.profileboxapiuser.service;

import com.goorm.profileboxcomm.dto.notice.request.CreateNoticeRequestDto;
import com.goorm.profileboxcomm.dto.notice.request.SelectNoticeListRequsetDto;
import com.goorm.profileboxcomm.entity.Member;
import com.goorm.profileboxcomm.entity.Notice;
import com.goorm.profileboxcomm.exception.ApiException;
import com.goorm.profileboxcomm.exception.ExceptionEnum;
import com.goorm.profileboxcomm.repository.NoticeRepository;
import com.goorm.profileboxcomm.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Page<Notice> getAllNoitce(SelectNoticeListRequsetDto dto){
        int offset = dto.getOffset() ;
        int limit = dto.getLimit();
        String sortKey = dto.getSortKey();
        Sort.Direction sortDirection = Utils.getSrotDirection(dto.getSortDirection());
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(sortDirection, sortKey));
        return noticeRepository.findAll(pageable);
    }

    public Notice getNoticeByNoticeId(Long noticeId){
        return noticeRepository.findNoticeByNoticeId(noticeId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
    }

    @Transactional
    public Long addNotice(CreateNoticeRequestDto dto, Authentication authentication) throws ParseException {
        Member member = (Member) authentication.getPrincipal();
        Notice notice = Notice.createNotice(dto, member);
        noticeRepository.save(notice);
        return notice.getNoticeId();
    }

    @Transactional
    public Long updateNotice(Long noticeId, CreateNoticeRequestDto dto, Member member) {
        Notice notice = noticeRepository.findNoticeByNoticeIdAndMember(noticeId, member)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
        Notice.updateNotice(notice, dto);
        noticeRepository.save(notice);
        return notice.getNoticeId();
    }

    @Transactional
    public void deleteNotice(Long noticeId, Member member){
        Notice notice = noticeRepository.findNoticeByNoticeIdAndMember(noticeId, member)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
        noticeRepository.deleteByNoticeId(noticeId);
    }
}
