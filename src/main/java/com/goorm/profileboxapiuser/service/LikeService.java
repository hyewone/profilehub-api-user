package com.goorm.profileboxapiuser.service;

import com.goorm.profileboxcomm.dto.like.request.CreateLikeRequestDto;
import com.goorm.profileboxcomm.dto.like.request.SelectLikeListRequestDto;
import com.goorm.profileboxcomm.entity.Like;
import com.goorm.profileboxcomm.entity.Member;
import com.goorm.profileboxcomm.enumeration.LikeType;
import com.goorm.profileboxcomm.enumeration.MemberType;
import com.goorm.profileboxcomm.exception.ApiException;
import com.goorm.profileboxcomm.exception.ExceptionEnum;
import com.goorm.profileboxcomm.repository.LikeRepository;
import com.goorm.profileboxcomm.repository.NoticeRepository;
import com.goorm.profileboxcomm.repository.ProfileRepository;
import com.goorm.profileboxcomm.repository.customRepositoryImple.CustomLikeRepositoryImple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final CustomLikeRepositoryImple customLikeRepository;
    private final ProfileRepository profileRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public Long addLike(CreateLikeRequestDto dto, Authentication authentication) {
//         현재 로그인 한 사람이 DTO ACTOR면 PROFILE type 을 좋아요할 수 없고,
//         PRODUCER면 NOTICE를 좋아요할 수 없다.
//        MemberType memberType = getAuthority(authentication);
//        if (memberType.equals(MemberType.ACTOR) && dto.getLikeType().equals(LikeType.PROFILE)){
//            throw new ApiException(ExceptionEnum.IMAGE_NOT_FOUND);
//        }else if(memberType.equals(MemberType.PRODUCER) && dto.getLikeType().equals(LikeType.NOTICE)){
//            throw new ApiException(ExceptionEnum.IMAGE_NOT_FOUND);
//        }

        Member member = (Member) authentication.getPrincipal();
        Like like = Like.createLike(dto, member);

        if (dto.getLikeType().equals(LikeType.PROFILE)){
            profileRepository.findProfileByProfileId(dto.getTargetId())
                    .orElseThrow(() -> new ApiException(ExceptionEnum.PROFILE_NOT_FOUND));
        }
        else if (dto.getLikeType().equals(LikeType.NOTICE)){
            profileRepository.findProfileByProfileId(dto.getTargetId())
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
        }

        likeRepository.save(like);

        return like.getLikeId();
    }

    @Transactional
    public void cancelLike(Long likeId, Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        Like like = likeRepository.findLikeByLikeId(likeId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.LIKE_NOT_FOUND));
        Long memberId = like.getMember().getMemberId();

        if (isAdmin(authentication) || member.getMemberId().equals(memberId)) {
            likeRepository.deleteByLikeId(likeId);
        }
    }

    public Page<Like> getLikedProfiles(SelectLikeListRequestDto dto) {
        int offset = dto.getOffset() ;
        int limit = dto.getLimit();
        String sortKey = dto.getSortKey();
        Sort.Direction sortDirection = getSrotDirection(dto.getSortDirection());
        return customLikeRepository.findAllLikeByCondition(PageRequest.of(offset, limit, Sort.by(sortDirection, sortKey)), dto.getLikeType().toString());
    }

    public Sort.Direction getSrotDirection(String strDirection){
        Sort.Direction sortDirection = null;
        switch (strDirection) {
            case "ASC":
                sortDirection = Sort.Direction.ASC;
                break;
            case "DESC":
                sortDirection = Sort.Direction.DESC;
                break;
            default:
                sortDirection = Sort.Direction.DESC;
                break;
        }
        return sortDirection;
    }

    public boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

    public MemberType getAuthority(Authentication authentication){
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return MemberType.valueOf(authorities.stream().findFirst().toString());
    }
}