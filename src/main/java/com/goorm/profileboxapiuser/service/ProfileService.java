package com.goorm.profileboxapiuser.service;

import com.goorm.profileboxcomm.dto.filmo.request.CreateFilmoRequestDto;
import com.goorm.profileboxcomm.dto.image.request.CreateImageRequestDto;
import com.goorm.profileboxcomm.dto.link.request.CreateLinkRequestDto;
import com.goorm.profileboxcomm.dto.profile.request.CreateProfileRequestDto;
import com.goorm.profileboxcomm.dto.profile.request.SelectProfileListRequestDto;
import com.goorm.profileboxcomm.dto.video.request.CreateVideoRequestDto;
import com.goorm.profileboxcomm.entity.*;
import com.goorm.profileboxcomm.exception.ApiException;
import com.goorm.profileboxcomm.exception.ExceptionEnum;
import com.goorm.profileboxcomm.repository.*;
import com.goorm.profileboxcomm.utils.FileHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final VideoRepository videoRepository;
    private final FilmoRepository filmoRepository;
    private final LinkRepository linkRepository;
    private final FileHandler fileHandler;

    public Page<Profile> getAllProfile(SelectProfileListRequestDto requestDto) {
        int offset = requestDto.getOffset() < 1 ? 0 : requestDto.getOffset() - 1 ;
        int limit = requestDto.getLimit() < 1 ? 10 : requestDto.getLimit();
        String sortKey = requestDto.getSortKey();
        Sort.Direction sortDirection = getSrotDirection(requestDto.getSortDirection());
        return profileRepository.findAll(PageRequest.of(offset, limit, Sort.by(sortDirection, sortKey)));
    }

//    public Page<Profile> getProfileByFilmoName(SelectProfileListByFilmoRequestDto requestDto) {
//        int offset = requestDto.getOffset() < 1 ? 0 : requestDto.getOffset() - 1 ;
//        int limit = requestDto.getLimit() < 1 ? 10 : requestDto.getLimit();
//        String sortKey = requestDto.getSortKey();
//        return profileRepository.findAll(PageRequest.of(offset, limit, Sort.by(sortKey)));
//    }

    public Profile getProfileByProfileId(Long profileId) {
        return profileRepository.findProfileByProfileId(profileId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.PROFILE_NOT_FOUND));
    }

    // 1:1
    public boolean existsProfileByMemberId(Member member) {
        return profileRepository.existsProfileByMember(member);
    }

    @Transactional
    public Long addProfile(CreateProfileRequestDto profileDto, List<MultipartFile> images, List<MultipartFile> videos) {
        Member member = memberRepository.findMemberByMemberId(profileDto.getMemberId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.MEMBER_NOT_FOUND));

        if(existsProfileByMemberId(member)){
            throw new ApiException(ExceptionEnum.PROFILE_ALREADY_EXIST);
        }

        Profile profile = Profile.createProfile(profileDto, member);
        profileRepository.save(profile);

        if (images != null & !images.get(0).getOriginalFilename().isEmpty()) {
            if (profileDto.getDefaultImageIdx() < 0 || profileDto.getDefaultImageIdx() > images.size() - 1) {
                profileDto.setDefaultImageIdx(0);
            }
            List<Image> imageList = new ArrayList<>();
            List<CreateImageRequestDto> imageDtoList = images.stream()
                    .map(o -> fileHandler.imageWrite(o))
                    .collect(toList());
            for (int idx = 0; idx < imageDtoList.size(); idx++) {
                CreateImageRequestDto dto = imageDtoList.get(idx);
                Image image = Image.createImage(dto, profile);
                imageList.add(image);
                if (idx == profileDto.getDefaultImageIdx()) {
                    profile.setDefaultImageId(image.getImageId());
                }
            }
            profileRepository.save(profile);
            imageRepository.saveAll(imageList);
        }

        if (videos != null & !videos.get(0).getOriginalFilename().isEmpty()) {
            List<Video> videoList = new ArrayList<>();
            List<CreateVideoRequestDto> videoDtoList = videos.stream()
                    .map(o -> fileHandler.videoWrite(o))
                    .collect(toList());
            for (CreateVideoRequestDto dto : videoDtoList) {
                videoList.add(Video.createVideo(dto, profile));
            }
            videoRepository.saveAll(videoList);
        }

        if (profileDto.getFilmos() != null & profileDto.getFilmos().size() > 0) {
            List<Filmo> filmoList = new ArrayList<>();
            for (CreateFilmoRequestDto dto : profileDto.getFilmos()) {
                filmoList.add(Filmo.createFilmo(dto, profile));
            }
            filmoRepository.saveAll(filmoList);
        }

        if (profileDto.getLinks() != null & profileDto.getLinks().size() > 0) {
            List<Link> linkList = new ArrayList<>();
            for (CreateLinkRequestDto dto : profileDto.getLinks()) {
                linkList.add(Link.createLink(dto, profile));
            }
            linkRepository.saveAll(linkList);
        }
        return profile.getProfileId();
    }

    @Transactional
    public Profile updateProfile(Long profileId, CreateProfileRequestDto profileDto) {
        Profile profile = profileRepository.findProfileByProfileId(profileId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.PROFILE_NOT_FOUND));
//        if (patchRequest.getName() != null) {
////        user.setName(patchRequest.getName());
////    }
        profile = profileRepository.save(profile);
        return profile;
    }

    @Transactional
    public void deleteProfile(Long profileId) {

        Profile profile = profileRepository.findProfileByProfileId(profileId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.PROFILE_NOT_FOUND));

//        imageRepository.findImagesByProfile(profile)
//                .ifPresent(images -> {
//                    images.stream().peek(image -> fileHandler.deleteFile(image.getFilePath()));
//                });

        imageRepository.findImagesByProfile(profile)
                .ifPresent(images -> {
                    images.forEach(image -> fileHandler.deleteFile(image.getFilePath()));
                });

        videoRepository.findVideosByProfile(profile)
                .ifPresent(videos -> {
                    videos.stream().peek(video -> fileHandler.deleteFile(video.getFilePath()));
                });

        profileRepository.deleteByProfileId(profileId);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findImageByImageId(imageId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.IMAGE_NOT_FOUND));

        fileHandler.deleteFile(image.getFilePath());
        imageRepository.deleteByImageId(imageId);
    }

    @Transactional
    public void deleteVideo(Long videoId) {
        Video video = videoRepository.findVideoByVideoId(videoId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.VIDEO_NOT_FOUND));

        fileHandler.deleteFile(video.getFilePath());
        videoRepository.deleteByVideoId(videoId);
    }

    @Transactional
    public void deleteFilmo(Long filmoId) {
        filmoRepository.findFilmoByFilmoId(filmoId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.FILMO_NOT_FOUND));

        filmoRepository.deleteByFilmoId(filmoId);
    }

    @Transactional
    public void deleteLink(Long linkId) {
        linkRepository.findLinkByLinkId(linkId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.LINK_NOT_FOUND));

        linkRepository.deleteByLinkId(linkId);
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
}