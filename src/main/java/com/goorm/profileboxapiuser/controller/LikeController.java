package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.LikeService;
import com.goorm.profileboxcomm.dto.like.request.CreateLikeRequestDto;
import com.goorm.profileboxcomm.dto.like.request.SelectLikeListRequestDto;
import com.goorm.profileboxcomm.dto.like.response.SelectLikeListResponseDto;
import com.goorm.profileboxcomm.dto.like.response.SelectLikeResponseDto;
import com.goorm.profileboxcomm.entity.Like;
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
    public ApiResult<List<SelectLikeResponseDto>> getLikedProfiles(@Valid @ModelAttribute SelectLikeListRequestDto dto) {
        Page<Like> likes = likeService.getLikedProfiles(dto);
        List<SelectLikeResponseDto> dtoList = likes.stream()
                .map(o -> new SelectLikeResponseDto(o))
                .collect(toList());
        SelectLikeListResponseDto result = new SelectLikeListResponseDto(likes.getTotalPages(), likes.getTotalElements(), dtoList);
        return ApiResult.getResult(ApiResultType.SUCCESS, "좋아요 리스트 조회", result);
    }
}