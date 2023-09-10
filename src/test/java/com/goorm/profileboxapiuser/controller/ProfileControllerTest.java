package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.ProfileService;
import com.goorm.profileboxcomm.dto.profile.request.SelectProfileListRequestDto;
import com.goorm.profileboxcomm.entity.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfileService profileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProfileController(profileService)).build();
    }

    @Test
    public void testGetProfiles() throws Exception {
        // 가짜 데이터 생성
        SelectProfileListRequestDto requestDto = new SelectProfileListRequestDto();
        Page<Profile> fakeProfilePage = createFakeProfilePage();

        // Mock 객체를 사용하여 profileService.getAllProfile()가 가짜 데이터를 반환하도록 설정
        when(profileService.getAllProfile(requestDto)).thenReturn(fakeProfilePage);

        mockMvc.perform(get("/open/profiles")
                        .param("offset", "1")
                        .param("limit", "10")
                        .param("sortKey", "field1") // 유효한 필드 이름으로 설정
                        .param("sortDirection", "DESC")) // 유효한 정렬 방향으로 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.resultType").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("프로필 리스트 조회"))
                .andExpect(jsonPath("$.data.totalPages").value(fakeProfilePage.getTotalPages()))
                .andExpect(jsonPath("$.data.totalElements").value(fakeProfilePage.getTotalElements()))
                .andExpect(jsonPath("$.data.profiles").isArray())
                .andExpect(jsonPath("$.data.profiles[0].field1").value("Value1")) // 가짜 데이터에 따라 수정
                .andExpect(jsonPath("$.data.profiles[1].field1").value("Value2")) // 가짜 데이터에 따라 수정
                .andExpect(jsonPath("$.data.profiles[2].field1").value("Value3")); // 가짜 데이터에 따라 수정
    }

    // 가짜 Profile 페이지 생성 메서드 (테스트 용도)
    private Page<Profile> createFakeProfilePage() {
        // 가짜 Profile 데이터 생성
        List<Profile> fakeProfiles = new ArrayList<>();
//        fakeProfiles.add(new Profile("Value1")); // Profile 엔티티 생성자에 맞게 수정
//        fakeProfiles.add(new Profile("Value2")); // Profile 엔티티 생성자에 맞게 수정
//        fakeProfiles.add(new Profile("Value2lue3")); // Profile 엔티티 생성자에 맞게 수정

        // 가짜 Page 객체 생성
        Page<Profile> fakePage = new PageImpl<>(fakeProfiles);

        return fakePage;
    }
}

