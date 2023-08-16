package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.ProfileService;
import com.goorm.profileboxcomm.dto.profile.request.CreateProfileRequestDto;
import com.goorm.profileboxcomm.dto.profile.request.SelectProfileListRequestDto;
import com.goorm.profileboxcomm.dto.profile.response.SelectProfileListResponseDto;
import com.goorm.profileboxcomm.dto.profile.response.SelectProfileResponseDto;
import com.goorm.profileboxcomm.entity.Profile;
import com.goorm.profileboxcomm.exception.ApiException;
import com.goorm.profileboxcomm.exception.ExceptionEnum;
import com.goorm.profileboxcomm.response.ApiResult;
import com.goorm.profileboxcomm.response.ApiResultType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ProfileApiController {
    private final ProfileService profileService;

    @GetMapping("/open/profiles")
    public ApiResult<List<SelectProfileResponseDto>> getProfiles(@ModelAttribute SelectProfileListRequestDto requestDto) {
        Page<Profile> profiles = profileService.getAllProfile(requestDto);
        List<SelectProfileResponseDto> dtoList = profiles.stream()
                .map(o -> new SelectProfileResponseDto(o))
                .collect(toList());
        SelectProfileListResponseDto result = new SelectProfileListResponseDto(profiles.getTotalPages(), profiles.getTotalElements(), dtoList);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 리스트 조회", result);
    }

//    @GetMapping("/open/profile/filmoName/{filmoName}")
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
    @GetMapping("/open/profile/{profileId}")
    public ApiResult<SelectProfileResponseDto> getProfile(@PathVariable Long profileId){
        Profile profile = profileService.getProfileByProfileId(profileId);
        SelectProfileResponseDto result = new SelectProfileResponseDto(profile);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 조회", result);
    }


    @PreAuthorize("hasAnyAuthority('ACTOR')")
    @PostMapping("/profile")
    public ApiResult<Long> addProfile(@Valid @RequestPart(value = "data") CreateProfileRequestDto profileDto,
                                     @Valid @Size(min = 1, max = 5, message = "이미지는 최소1장/최대5장 첨부 가능합니다.")
                                @RequestPart(value = "images") List<@Valid MultipartFile> imageFiles,
                                     @Size(max = 2, message = "동영상은 최대2개 첨부 가능합니다.")
                                @RequestPart(value = "videos") List<MultipartFile> videoFiles) {
        if (imageFiles.isEmpty()) {
                throw new ApiException(ExceptionEnum.INVALID_REQUEST);
        }
        Long profileId = profileService.addProfile(profileDto, imageFiles, videoFiles);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 등록", profileId, HttpStatus.CREATED);
    }


    @PreAuthorize("hasAnyAuthority('ACTOR')")
    @PatchMapping("/profile/{profileId}")
    public ApiResult<SelectProfileResponseDto> updateProfile(@PathVariable Long profileId, @Valid @RequestPart(value = "data") CreateProfileRequestDto profileDto){
        Profile profile = profileService.updateProfile(profileId, profileDto);
        SelectProfileResponseDto result = new SelectProfileResponseDto(profile);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 수정", result);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/profile/{profileId}")
    public ApiResult deleteProfile(@PathVariable Long profileId){
        profileService.deleteProfile(profileId);
        return ApiResult.getResult(ApiResultType.SUCCESS, "프로필 삭제", null);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/profile/image/{imageId}")
    public ApiResult deleteImage(@PathVariable Long imageId){
        profileService.deleteImage(imageId);
        return ApiResult.getResult(ApiResultType.SUCCESS, "이미지 삭제", null);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/profile/video/{videoId}")
    public ApiResult deleteVideo(@PathVariable Long videoId){
        profileService.deleteVideo(videoId);
        return ApiResult.getResult(ApiResultType.SUCCESS, "비디오 삭제", null);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/profile/filmo/{filmoId}")
    public ApiResult deleteFilmo(@PathVariable Long filmoId){
        profileService.deleteFilmo(filmoId);
        return ApiResult.getResult(ApiResultType.SUCCESS, "필모그래피 삭제", null);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','ACTOR')")
    @DeleteMapping("/profile/link/{linkId}")
    public ApiResult deleteLink(@PathVariable Long linkId){
        profileService.deleteLink(linkId);
        return ApiResult.getResult(ApiResultType.SUCCESS, "링크 삭제", null);
    }
}
