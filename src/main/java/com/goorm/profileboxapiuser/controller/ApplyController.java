package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.ApplyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Apply")
@RequestMapping("/v1/apply")
@RestController
@RequiredArgsConstructor
@Validated
public class ApplyController {

    private final ApplyService applyService;

//    @Operation(summary = "내가 지원한 작품공고 리스트 조회")
//    @PreAuthorize("hasAnyAuthority('ACTOR')")
//    @GetMapping("/iApplied")
//    public ApiResult<Page<Apply>> getAppliedNoticeList(@ModelAttribute SelectApplyListRequestDto dto, Authentication authentication) {
//        Page<Apply> notices = applyService.getAppliedNoticeList(dto, (Member) authentication.getPrincipal());
//        return ApiResult.getResult(ApiResultType.SUCCESS, "지원한 작품공고 리스트 조회", notices);
//    }

//    @Operation(summary = "작품공고를 지원한 프로필 리스트 조회")
//    @PreAuthorize("hasAnyAuthority('PRODUCER')")
//    @GetMapping("/appliedToMe")
//    public ApiResult<Page<Profile>> getProfileListApplyedNotice(@Valid @ModelAttribute CreateLikeRequestDto dto, Authentication authentication) {
//        // 1. dto.getNoticeId 와 현재 로그인된 사용자 정보로 공지 조회
//        // 2. 일치하는 작품공고가 없는 경우 에러 리턴
//        // 3. 있으면 해당 notice에 지원한 applyed Profile 을 리턴
//        Long likeId = applyService.getProfileListApplyedNotice(dto, (Member) authentication.getPrincipal());
//        return ApiResult.getResult(ApiResultType.SUCCESS, "작품공고를 지원한 프로필 리스트 조회", likeId, HttpStatus.CREATED);
//    }
//
//    @Operation(summary = "작품공고 지원")
//    @PreAuthorize("hasAnyAuthority('ACTOR')")
//    @PostMapping
//    public ApiResult<Long> addApply(@Valid @ModelAttribute CreateApplyRequestDto dto, Authentication authentication) {
//        Long applyId = applyService.addApply(dto, (Member) authentication.getPrincipal());
//        return ApiResult.getResult(ApiResultType.SUCCESS, "작품공고 지원", applyId, HttpStatus.CREATED);
//    }

//    @Operation(summary = "작품공고 지원 취소")
//    @PreAuthorize("hasAnyAuthority('ACTOR')")
//    @DeleteMapping("/{applyId}")
//    public ApiResult canceApply(@PathVariable Long applyId, Authentication authentication) {
//        applyService.cancelApply(applyId, (Member) authentication.getPrincipal());
//        return ApiResult.getResult(ApiResultType.SUCCESS, "작품공고 지원 취소", null);
//    }
}