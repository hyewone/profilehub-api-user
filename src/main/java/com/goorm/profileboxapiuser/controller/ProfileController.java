package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.ProfileService;
import com.goorm.profileboxcomm.dto.profile.request.CreateProfileRequestDto;
import com.goorm.profileboxcomm.dto.profile.request.SelectProfileListRequestDto;
import com.goorm.profileboxcomm.dto.profile.response.SelectProfileListResponseDto;
import com.goorm.profileboxcomm.dto.profile.response.SelectProfileResponseDto;
import com.goorm.profileboxcomm.entity.Member;
import com.goorm.profileboxcomm.entity.Profile;
import com.goorm.profileboxcomm.response.ApiResult;
import com.goorm.profileboxcomm.response.ApiResultType;
import com.goorm.profileboxcomm.validator.ProfileImage;
import com.goorm.profileboxcomm.validator.ProfileVideo;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Tag(name = "Profile")
@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
@Validated
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = "프로필 리스트 조회")
    @GetMapping("/open/profiles")
    public ApiResult<List<SelectProfileResponseDto>> getProfiles(@ModelAttribute SelectProfileListRequestDto dto) {
        Page<Profile> profiles = profileService.getAllProfile(dto);
        List<SelectProfileResponseDto> dtoList = profiles.stream()
                .map(o -> new SelectProfileResponseDto(o))
                .collect(toList());
        SelectProfileListResponseDto result = new SelectProfileListResponseDto(profiles.getTotalPages(), profiles.getTotalElements(), dtoList);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 리스트 조회", result);
    }

    @Operation(summary = "프로필 조회")
    @GetMapping("/open/profile/{profileId}")
    public ApiResult<SelectProfileResponseDto> getProfile(@PathVariable Long profileId){
        Profile profile = profileService.getProfileByProfileId(profileId);
        SelectProfileResponseDto result = new SelectProfileResponseDto(profile);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 조회", result);
    }

    @Operation(summary = "프로필 작성")
    @PreAuthorize("hasAnyAuthority('ACTOR')")
    @PostMapping
    public ApiResult<Long> addProfile(@Valid @RequestPart(value = "data") CreateProfileRequestDto profileDto,
                                      @ProfileImage @RequestPart(value = "images") List<@Valid MultipartFile> imageFiles,
                                      @ProfileVideo @RequestPart(value = "videos") List<MultipartFile> videoFiles,
                                      Authentication authentication
    ) {
        Member member = (Member) authentication.getPrincipal();
        profileDto.setMemberId(member.getMemberId());
        Long profileId = profileService.addProfile(profileDto, imageFiles, videoFiles, member);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 등록", profileId, HttpStatus.CREATED);
    }

    @Operation(summary = "프로필 수정")
    @PreAuthorize("hasAnyAuthority('ACTOR')")
    @PatchMapping("/{profileId}")
    public ApiResult<SelectProfileResponseDto> updateProfile(@PathVariable Long profileId,
                                                             @Valid @RequestPart(value = "data") CreateProfileRequestDto profileDto,
                                                            Authentication authentication
    ){
        Member member = (Member) authentication.getPrincipal();
        Long savedProfileId = profileService.updateProfile(profileId, profileDto, member);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 수정", savedProfileId);
    }

    @Operation(summary = "프로필 삭제")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/{profileId}")
    public ApiResult deleteProfile(@PathVariable Long profileId, Authentication authentication){
        profileService.deleteProfile(profileId, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 삭제", null);
    }

    @Operation(summary = "이미지 삭제")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/image/{imageId}")
    public ApiResult deleteImage(@PathVariable Long imageId, Authentication authentication){
        profileService.deleteImage(imageId, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "이미지 삭제", null);
    }

    @Operation(summary = "비디오 삭제")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/video/{videoId}")
    public ApiResult deleteVideo(@PathVariable Long videoId, Authentication authentication){
        profileService.deleteVideo(videoId, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "비디오 삭제", null);
    }

    @Operation(summary = "필모 삭제")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/filmo/{filmoId}")
    public ApiResult deleteFilmo(@PathVariable Long filmoId, Authentication authentication){
        profileService.deleteFilmo(filmoId, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "필모그래피 삭제", null);
    }

    @Operation(summary = "링크 삭제")
    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/link/{linkId}")
    public ApiResult deleteLink(@PathVariable Long linkId, Authentication authentication){
        profileService.deleteLink(linkId, authentication);
        return ApiResult.getResult(ApiResultType.SUCCESS, "링크 삭제", null);
    }

//    @GetMapping("/open/profile/filmoName/{filmoName}")
//@       Operation(summary = "Filmo로 프로필 리스트 조회")
//    public ApiResult<List<SelectProfileResponseDto>> findProfilesByFilmoName(@PathVariable String filmoName, @ModelAttribute SelectProfileListByFilmoRequestDto requestDto) {
//        requestDto.setFilmoName(filmoName);
//        Page<Profile> profiles = profileService.getProfileByFilmoName(requestDto);
//        List<SelectProfileResponseDto> dtoList = profiles.stream()
//                .map(o -> new SelectProfileResponseDto(o))
//                .collect(toList());
//        SelectProfileListResponseDto result = new SelectProfileListResponseDto(profiles.getTotalPages(), profiles.getTotalElements(), dtoList);
//        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 리스트 조회(필모 이름)", result);
//    }

//    @PreAuthorize("hasAnyAuthority('ADMIN','PRODUCER','ACTOR')")

//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SelectProfileResponseDto.class))),
//            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = SelectProfileResponseDto.class)))
//    })
}
