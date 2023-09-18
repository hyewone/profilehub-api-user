package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.NoticeService;
import com.goorm.profileboxcomm.dto.notice.request.CreateNoticeRequestDto;
import com.goorm.profileboxcomm.dto.notice.request.SelectNoticeListRequsetDto;
import com.goorm.profileboxcomm.dto.notice.response.SelectNoticeListResponseDto;
import com.goorm.profileboxcomm.dto.notice.response.SelectNoticeResponseDto;
import com.goorm.profileboxcomm.entity.Member;
import com.goorm.profileboxcomm.entity.Notice;
import com.goorm.profileboxcomm.response.ApiResult;
import com.goorm.profileboxcomm.response.ApiResultType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Notice")
@RestController
@RequestMapping("/v1/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @Operation(summary = "작품공고 리스트 조회")
    @GetMapping("/notices")
    public ApiResult<List<SelectNoticeListResponseDto>> getNotices(@ModelAttribute SelectNoticeListRequsetDto dto ) {
        Page<Notice> notices = noticeService.getAllNoitce(dto);
        List<SelectNoticeResponseDto> dtoList = notices.stream()
                .map(o -> new SelectNoticeResponseDto(o))
                .collect(Collectors.toList());
        SelectNoticeListResponseDto result = new SelectNoticeListResponseDto(notices.getTotalPages(), notices.getTotalElements(), dtoList);
        return ApiResult.getResult(ApiResultType.SUCCESS, "작품공고 리스트 조회", result);
    }

    @Operation(summary = "작품공고 조회")
    @GetMapping("/{noticeId}")
    public ApiResult<SelectNoticeResponseDto> getNotice(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeService.getNoticeByNoticeId(noticeId);
        SelectNoticeResponseDto result = new SelectNoticeResponseDto(notice);
        return ApiResult.getResult(ApiResultType.SUCCESS, "작품공고 조회", result);
    }


    @Operation(summary = "작품공고 등록")
    @PostMapping
    public ApiResult<Long> addNotice(@Valid @RequestBody CreateNoticeRequestDto dto, Authentication authentication) throws ParseException {
        Long noticeId = noticeService.addNotice(dto, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "작품공고 등록", noticeId, HttpStatus.CREATED);
    }

    @Operation(summary = "작품공고 수정")
    @PatchMapping("/{noticeId}")
    public ApiResult<Long> updateNotice(@PathVariable Long noticeId,
                                        @Valid @RequestPart(value = "data") CreateNoticeRequestDto dto,
                                        Authentication authentication) {
        Long savedNoticeId = noticeService.updateNotice(noticeId, dto, (Member) authentication.getPrincipal());
        return ApiResult.getResult(ApiResultType.SUCCESS, "작품공고 수정", savedNoticeId);
    }

    @Operation(summary = "작품공고 삭제")
    @DeleteMapping("/{noticeId}")
    public ApiResult deleteNotice (@PathVariable("noticeId") Long noticeId, Authentication authentication){
        noticeService.deleteNotice(noticeId, (Member) authentication.getPrincipal());
        return ApiResult.getResult(ApiResultType.SUCCESS, "작품공고 삭제", null);
    }
}

