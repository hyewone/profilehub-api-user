package com.goorm.profileboxapiuser.controller;

import com.goorm.profileboxapiuser.service.ProfileService;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;

public class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfileService profileService;

//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(new ProfileController(profileService)).build();
//    }

//    @Test
//    public void testGetProfiles() throws Exception {
//        // given
//        // 리스트 데이터가 주어저야겠지
//
//        // when
//        // 호출하며 mockList를 리턴하도록
//
//        // then
//        // API 호출 시 예상 응답
//
//        // 가짜 데이터 생성
//        SelectProfileListRequestDto requestDto = new SelectProfileListRequestDto();
//        Page<Profile> fakeProfilePage = createFakeProfilePage();
//
//        // Mock 객체를 사용하여 profileService.getAllProfile()가 가짜 데이터를 반환하도록 설정
//        when(profileService.getAllProfile(requestDto)).thenReturn(fakeProfilePage);
//
//        mockMvc.perform(get("/open/profiles")
//                        .param("offset", "1")
//                        .param("limit", "10")
//                        .param("sortKey", "profileId")
//                        .param("sortDirection", "DESC"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.resultType").value("SUCCESS"))
//                .andExpect(jsonPath("$.message").value("프로필 리스트 조회"))
//                .andExpect(jsonPath("$.data.totalPages").value(fakeProfilePage.getTotalPages()))
//                .andExpect(jsonPath("$.data.totalElements").value(fakeProfilePage.getTotalElements()))
//                .andExpect(jsonPath("$.data.profiles").isArray())
//                .andExpect(jsonPath("$.data.profiles[0].field1").value("Value1")) // 가짜 데이터에 따라 수정
//                .andExpect(jsonPath("$.data.profiles[1].field1").value("Value2")) // 가짜 데이터에 따라 수정
//                .andExpect(jsonPath("$.data.profiles[2].field1").value("Value3")); // 가짜 데이터에 따라 수정
//    }
//
//    // 가짜 Profile 페이지 생성 메서드 (테스트 용도)
//    private Page<Profile> createFakeProfilePage() {
//        // 가짜 Profile 데이터 생성
//
//        List<Profile> fakeProfiles = new ArrayList<>();
////        fakeProfiles.add(new Profile("Value1"));
////        fakeProfiles.add(new Profile("Value2"));
////        fakeProfiles.add(new Profile("Value2lue3"));
//
//        // 가짜 Page 객체 생성
//        Page<Profile> fakePage = new PageImpl<>(fakeProfiles);
//
//        return fakePage;
//    }
//
//
//    @Test
//    public void testCreateProfile() throws Exception {
//        // given
//        Profile profile = new Profile();
//
//        // when
//
//        // then
//    }

//    @Test
//    public void testDeleteProfile() throws Exception {
//        Long profileIdToDelete = 1L; // 삭제할 프로필 ID
//
//        // DELETE 요청을 보냅니다.
//        mockMvc.perform(MockMvcRequestBuilders
//                        .delete("/v1/profile/{profileId}", profileIdToDelete)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk()) // HTTP 상태 코드 200 OK 확인
//
//                .andExpect(jsonPath("$.resultType").value("SUCCESS"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.resultType").value("SUCCESS")) // 응답 JSON의 "type" 필드 값 확인
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("프로필 삭제")); // 응답 JSON의 "message" 필드 값 확인
//    }
}

