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
import com.goorm.profileboxcomm.repository.customRepositoryImple.CustomProfileRepositoryImple;
import com.goorm.profileboxcomm.utils.FileHandler;
import com.goorm.profileboxcomm.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final CustomProfileRepositoryImple customProfileRepository;
    private final ImageRepository imageRepository;
    private final VideoRepository videoRepository;
    private final FilmoRepository filmoRepository;
    private final LinkRepository linkRepository;
    private final FileHandler fileHandler;

    public Page<Profile> getAllProfile(SelectProfileListRequestDto dto) {
        int offset = dto.getOffset() ;
        int limit = dto.getLimit();
        String sortKey = dto.getSortKey();
        Sort.Direction sortDirection = Utils.getSrotDirection(dto.getSortDirection());
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(sortDirection, sortKey));
        return customProfileRepository.findProfiles(pageable, dto);
//        return profileRepository.findAll(pageable);
    }

    public Profile getProfileByProfileId(Long profileId) {
        return profileRepository.findProfileByProfileId(profileId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.PROFILE_NOT_FOUND));
    }

    // 1:1
    public boolean existsProfileByMemberId(Member member) {
        return profileRepository.existsProfileByMember(member);
    }

    @Transactional
    public Long addProfile(CreateProfileRequestDto profileDto, List<MultipartFile> images, List<MultipartFile> videos, Member member) {
        if(existsProfileByMemberId(member)){
            throw new ApiException(ExceptionEnum.PROFILE_ALREADY_EXIST);
        }

        // 프로필 저장
        Profile profile = Profile.createProfile(profileDto, member);
        profileRepository.save(profile);

        // 이미지 저장
        if (images != null & !images.get(0).getOriginalFilename().isEmpty()) {
            List<Image> imageList = new ArrayList<>();
            List<CreateImageRequestDto> imageDtoList = images.stream()
                    .map(o -> fileHandler.imageWrite(o))
                    .collect(toList());
            for (int idx = 0; idx < imageDtoList.size(); idx++) {
                CreateImageRequestDto dto = imageDtoList.get(idx);
                Image image = Image.createImage(dto, profile);
                imageList.add(image);
            }
            imageRepository.saveAll(imageList);
            profile.setDefaultImageId(imageList.get(0).getImageId());
            profileRepository.save(profile);
        }

        // 비디오 저장
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

        // 필모 저장
        if (profileDto.getFilmos() != null & profileDto.getFilmos().size() > 0) {
            List<Filmo> filmoList = new ArrayList<>();
            for (CreateFilmoRequestDto dto : profileDto.getFilmos()) {
                filmoList.add(Filmo.createFilmo(dto, profile));
            }
            filmoRepository.saveAll(filmoList);
        }

        // 링크 저장
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
    public Long updateProfile(Long profileId, CreateProfileRequestDto dto, Member member) {
        Profile profile = profileRepository.findProfileByProfileIdAndMember(profileId, member)
                .orElseThrow(() -> new ApiException(ExceptionEnum.PROFILE_NOT_FOUND));
        profile.setTitle(dto.getTitle());
        profile.setContent(dto.getContent());
        profile.setYnType(dto.getYnType());
        profileRepository.save(profile);
        return profile.getProfileId();
    }

    @Transactional
    public void deleteProfile(Long profileId, Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        Profile profile = profileRepository.findProfileByProfileId(profileId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.PROFILE_NOT_FOUND));
        Long memberId = profile.getMember().getMemberId();
        boolean isDelete = Utils.isAdmin(authentication) || member.getMemberId().equals(memberId);

        if (isDelete) {
            imageRepository.findImagesByProfile(profile)
                    .ifPresent(images -> {
                        images.forEach(image -> fileHandler.deleteFile(image.getFilePath()));
                    });

            videoRepository.findVideosByProfile(profile)
                    .ifPresent(videos -> {
                        videos.forEach(video -> fileHandler.deleteFile(video.getFilePath()));
                    });

            profileRepository.deleteByProfileId(profileId);
        }
    }

    @Transactional
    public void deleteImage(Long imageId, Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        Image image = imageRepository.findImageByImageId(imageId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.IMAGE_NOT_FOUND));
        Long memberId = image.getProfile().getMember().getMemberId();

        boolean isDelete = Utils.isAdmin(authentication) || member.getMemberId().equals(memberId);

        if (isDelete) {
            imageRepository.deleteByImageId(imageId);
            profileRepository.findProfileByDefaultImageId(imageId)
                    .ifPresent(profile -> {
                        imageRepository.findImagesByProfile(profile)
                                .ifPresentOrElse(images -> profile.setDefaultImageId(images.get(0).getImageId()),
                                                     () -> profile.setDefaultImageId(null));
                        profileRepository.save(profile);
                    });
            fileHandler.deleteFile(image.getFilePath());
        }
    }

    @Transactional
    public void deleteVideo(Long videoId, Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        Video video = videoRepository.findVideoByVideoId(videoId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.VIDEO_NOT_FOUND));
        Long memberId = video.getProfile().getMember().getMemberId();

        boolean isDelete = Utils.isAdmin(authentication) || member.getMemberId().equals(memberId);

        if (isDelete) {
            fileHandler.deleteFile(video.getFilePath());
            videoRepository.deleteByVideoId(videoId);
        }
    }

    @Transactional
    public void deleteFilmo(Long filmoId, Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        Filmo filmo = filmoRepository.findFilmoByFilmoId(filmoId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.FILMO_NOT_FOUND));
        Long memberId = filmo.getProfile().getMember().getMemberId();

        boolean isDelete = Utils.isAdmin(authentication) || member.getMemberId().equals(memberId);

        if (isDelete) {
            filmoRepository.deleteByFilmoId(filmoId);
        }
    }

    @Transactional
    public void deleteLink(Long linkId, Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        Link link = linkRepository.findLinkByLinkId(linkId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.LINK_NOT_FOUND));
        Long memberId = link.getProfile().getMember().getMemberId();

        boolean isDelete = Utils.isAdmin(authentication) || member.getMemberId().equals(memberId);

        if (isDelete) {
            linkRepository.deleteByLinkId(linkId);
        }
    }
}