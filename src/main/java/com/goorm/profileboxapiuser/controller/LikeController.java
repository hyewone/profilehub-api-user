package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.LikeService;
import com.goorm.profileboxcomm.dto.like.request.CreateLikeRequestDto;
import com.goorm.profileboxcomm.dto.like.request.SelectLikeListRequestDto;
import com.goorm.profileboxcomm.dto.like.response.SelectLikeListResponseDto;
import com.goorm.profileboxcomm.dto.like.response.SelectLikeResponseDto;
import com.goorm.profileboxcomm.dto.notice.response.SelectNoticeResponseDto;
import com.goorm.profileboxcomm.dto.profile.response.SelectProfileResponseDto;
import com.goorm.profileboxcomm.entity.Like;
import com.goorm.profileboxcomm.entity.Member;
import com.goorm.profileboxcomm.entity.Notice;
import com.goorm.profileboxcomm.entity.Profile;
import com.goorm.profileboxcomm.enumeration.LikeType;
import com.goorm.profileboxcomm.response.ApiResult;
import com.goorm.profileboxcomm.response.ApiResultType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Tag(name = "Like")
@RequestMapping("/v1/like")
@RestController
@RequiredArgsConstructor
@Validated
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 등록")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR','PRODUCER')")
    @PostMapping
    public ApiResult<Long> addLike(@Valid @ModelAttribute CreateLikeRequestDto dto, Authentication authentication) {
        Long likeId = likeService.addLike(dto, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "좋아요 등록", likeId, HttpStatus.CREATED);
    }

    @Operation(summary = "좋아요 취소")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR','PRODUCER')")
    @DeleteMapping("/{likeId}")
    public ApiResult cancelLike(@PathVariable Long likeId, Authentication authentication) {
        likeService.cancelLike(likeId, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "좋아요 취소", null);
    }

    @Operation(summary = "좋아요 리스트 조회")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR','PRODUCER')")
    @GetMapping("/likes")
    public ApiResult<List<SelectLikeResponseDto>> getLikeList(@Valid @ModelAttribute SelectLikeListRequestDto dto) {
        Page<Like> likes = likeService.getLikes(dto);
        List<SelectLikeResponseDto> dtoList = likes.stream()
                .map(o -> new SelectLikeResponseDto(o))
                .collect(toList());
        SelectLikeListResponseDto result = new SelectLikeListResponseDto(likes.getTotalPages(), likes.getTotalElements(), dtoList);
        return ApiResult.getResult(ApiResultType.SUCCESS, "좋아요 리스트 조회", result);
    }

    @Operation(summary = "내가 좋아요한 프로필 리스트 조회")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR','PRODUCER')")
    @GetMapping("/mylikeProfiles")
    public ApiResult<List<SelectLikeResponseDto>> getMyLikeProfiles(Authentication authentication) {
        List<Profile> profiles = likeService.getMyLikeProfiles((Member) authentication.getPrincipal());
        List<SelectProfileResponseDto> dtoList = profiles.stream()
                .map(o -> SelectProfileResponseDto.getDtoForList(o))
                .collect(toList());
       return ApiResult.getResult(ApiResultType.SUCCESS, "내가 좋아요한 프로필 리스트 ", dtoList);
    }

    @Operation(summary = "내가 좋아요한 작품 공고 리스트 조회")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR','PRODUCER')")
    @GetMapping("/mylikeNotices")
    public ApiResult<List<SelectLikeResponseDto>> getMyLikeNotices(Authentication authentication) {
        List<Notice> notices = likeService.getMyLikeNotices((Member) authentication.getPrincipal());
        List<SelectNoticeResponseDto> dtoList = notices.stream()
                .map(o -> SelectNoticeResponseDto.getDtoForList(o))
                .collect(toList());
        return ApiResult.getResult(ApiResultType.SUCCESS, "내가 좋아요한 작품 공고 리스트 조회", dtoList);
    }

    @Operation(summary = "현재 사용자의 특정 게시물 좋아요 여부 조회")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR','PRODUCER')")
    @GetMapping("/{likeType}/{targetId}")
    public ApiResult getLikeId(@PathVariable LikeType likeType,
                               @PathVariable Long targetId,
                               Authentication authentication) {
        Long likeId = likeService.getLikeIdByLikeTypeAndTargetIdAndMember(likeType, targetId, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "좋아요 여부 조회", likeId);
    }
}