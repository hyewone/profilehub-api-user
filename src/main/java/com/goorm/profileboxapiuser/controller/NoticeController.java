package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.NoticeService;
import com.goorm.profileboxcomm.dto.notice.NoticeDTO;
import com.goorm.profileboxcomm.dto.notice.request.CreateNoticeRequestDTO;
import com.goorm.profileboxcomm.entity.Member;
import com.goorm.profileboxcomm.entity.Notice;
import com.goorm.profileboxcomm.response.ApiResult;
import com.goorm.profileboxcomm.response.ApiResultType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "공지 리스트 조회")
    @GetMapping("/notices")
    public ApiResult<List<NoticeDTO>> getNoticeList() {
        List<Notice> list = noticeService.getAllNoitce();
        List<NoticeDTO> noticeDTOList = list.stream()
                .map(o -> Notice.toDTO(o))
                .collect(Collectors.toList());
        return ApiResult.getResult(ApiResultType.SUCCESS, "공지 리스트 조회", noticeDTOList);
    }

    @Operation(summary = "공지 조회")
    @GetMapping("/{noticeId}")
    public ApiResult<NoticeDTO> openNotice(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeService.getSpecificNotice(noticeId);
        NoticeDTO noticeDTO = Notice.toDTO(notice);
        return ApiResult.getResult(ApiResultType.SUCCESS, "공지 조회", noticeDTO);
    }


    @Operation(summary = "공지 등록")
    @PostMapping
    public ApiResult<Notice> createNotice(@Valid @RequestBody CreateNoticeRequestDTO dto, Authentication authentication) throws ParseException {
        Notice notice = noticeService.registerNotice(dto, (Member) authentication.getPrincipal());
        NoticeDTO noticeDTO = Notice.toDTO(notice);
        return ApiResult.getResult(ApiResultType.SUCCESS, "공지 등록", noticeDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "공지 수정")
    @PatchMapping("/{noticeId}")
    public ApiResult<NoticeDTO> updateNotice(@RequestBody NoticeDTO dto, @PathVariable("noticeId") Long noticeId) throws ParseException {
        NoticeDTO noticeDTO = Notice.toDTO(noticeService.updateNotice(dto, noticeId));
        return ApiResult.getResult(ApiResultType.SUCCESS, "공지 수정", noticeDTO);
    }

    @Operation(summary = "공지 삭제")
    @DeleteMapping("/{noticeId}")
    public ApiResult deleteNotice (@PathVariable("noticeId") Long noticeId){
        noticeService.deleteNotice(noticeId);
        return ApiResult.getResult(ApiResultType.SUCCESS, "공지 삭제", null);
    }
}

