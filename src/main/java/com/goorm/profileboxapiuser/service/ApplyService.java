package com.goorm.profileboxapiuser.service;

import com.goorm.profileboxcomm.entity.Notice;
import com.goorm.profileboxcomm.entity.Profile;
import com.goorm.profileboxcomm.repository.ApplyRepository;
import com.goorm.profileboxcomm.repository.NoticeRepository;
import com.goorm.profileboxcomm.repository.ProfileRepository;
import com.goorm.profileboxcomm.repository.customRepositoryImple.CustomApplyRepositoryImple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applyRepository;
    private final CustomApplyRepositoryImple customApplyRepository;
    private final NoticeRepository noticeRepository;
    private final ProfileRepository profileRepository;

//    public Page<Apply> getAppliedNoticeList(SelectApplyListRequestDto dto, Member member) {
//        int offset = dto.getOffset();
//        int limit = dto.getLimit();
//        String sortKey = dto.getSortKey();
//        Sort.Direction sortDirection = Utils.getSrotDirection(dto.getSortDirection());
//        Pageable pageable = PageRequest.of(offset, limit, Sort.by(sortDirection, sortKey));
//
////        Page<Notice> notices = new PageImpl<>(Collections.emptyList(), pageable, 0);
////        List<Apply> applyList = customApplyRepository.findAllApplyByAppliedMember(member).get();
////        if (applyList != null && !applyList.isEmpty()) {
////            notices = customApplyRepository.findAllAppliedNoticeByApplys(pageable, applyList);
////        }
////        return notices;
//        Page<Apply> applys = customApplyRepository.findAllApplyByAppliedMember(pageable, member);
//        return applys;
//    }

//    @Transactional
//    public Long addApply(CreateApplyRequestDto dto, Member member) {
//        Profile profile = customApplyRepository.findByAvailableProfileByMember(member)
//                .orElseThrow(() -> new ApiException(ExceptionEnum.AVAILABLE_PROFILE_NOT_FOUND));
//
//        dto.setProfileId(profile.getProfileId());
//
//        Notice notice = noticeRepository.findNoticeByNoticeId(dto.getNoticeId())
//                .orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
//
//        if (existsApplyByProfileAndNotice(profile, notice)){
//            throw new ApiException(ExceptionEnum.APPLY_ALREADY_EXIST);
//        }
//
//        Apply apply = Apply.createApply(profile, notice);
//        applyRepository.save(apply);
//        return apply.getApplyId();
//    }

//    @Transactional
//    public void cancelApply(Long applyId, Member member){
//        Apply apply = customApplyRepository.findApplyByAppliedMemberAndApplyId(member, applyId)
//                .orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
//        applyRepository.delete(apply);
//    }

    public boolean existsApplyByProfileAndNotice(Profile profile, Notice notice){
        return applyRepository.existsApplyByProfileAndNotice(profile, notice);
    }
}