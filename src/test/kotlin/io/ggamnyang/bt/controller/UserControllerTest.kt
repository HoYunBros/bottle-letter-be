package io.ggamnyang.bt.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ggamnyang.bt.auth.WithAuthUser
import io.ggamnyang.bt.domain.entity.User
import io.ggamnyang.bt.dto.common.LoginDto
import io.ggamnyang.bt.exception.UserNotFoundException
import io.ggamnyang.bt.service.UserService
import io.ggamnyang.bt.utils.ApiDocumentUtils.Companion.documentRequest
import io.ggamnyang.bt.utils.ApiDocumentUtils.Companion.documentResponse
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.filter.GenericFilterBean

@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@WebMvcTest(
    controllers = [UserController::class],
    excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [GenericFilterBean::class])]
)
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var userService: UserService

    private lateinit var user: User

    @BeforeEach
    fun beforeEach() {
        user = User("user", "password")
    }

    @Test
    @DisplayName("POST /api/v1/users/login 테스트 - 성공")
    fun `login - success`() {
        // When
        val loginDto = LoginDto(user.username, user.password)
        val loginDtoJson = jacksonObjectMapper().writeValueAsString(loginDto)

        whenever(userService.login(loginDto)).thenReturn("jwt")

        // Then
        val result = mockMvc
            .perform(
                post("/api/v1/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginDtoJson)
                    .with(csrf())
            )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "login",
                    documentRequest,
                    documentResponse,
                    requestFields(
                        fieldWithPath("username").description("user's Name"),
                        fieldWithPath("password").description("user's password")
                    ),
                    responseFields(
                        fieldWithPath("jwt").description("jwt token")
                    )
                )
            )
    }

    @Test
    @DisplayName("POST /api/v1/users/login 테스트 - 실패")
    fun `login - fail 403 Unauthorized`() {
        val loginDto = LoginDto("fail", "fail")
        val loginDtoJson = jacksonObjectMapper().writeValueAsString(loginDto)

        whenever(userService.login(loginDto)).thenThrow(UserNotFoundException::class.java)

        mockMvc.post("/api/v1/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = loginDtoJson
        }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @DisplayName("POST /api/v1/users 테스트 - 성공")
    fun `signUp - success`() {
        val loginDto = LoginDto("signup", "signup")
        val loginDtoJson = jacksonObjectMapper().writeValueAsString(loginDto)

        whenever(userService.findByUsername(loginDto.username)).thenReturn(null)
        whenever(userService.save(loginDto)).thenReturn(User(loginDto.username, loginDto.password))

        // Then
        val result = mockMvc
            .perform(
                post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginDtoJson)
                    .with(csrf())
            )

        result.andExpect(status().isCreated)
            .andDo(
                document(
                    "signUp",
                    documentRequest,
                    documentResponse,
                    requestFields(
                        fieldWithPath("username").description("User's Name"),
                        fieldWithPath("password").description("User's password")
                    ),
                    responseFields(
                        fieldWithPath("username").description("User's name")
                    )
                )
            )
    }

    @Test
    @DisplayName("POST /api/v1/users 테스트 - 실패 중복 username")
    fun `signUp - fail 중복 username`() {
        val loginDto = LoginDto("test", "test")
        val loginDtoJson = jacksonObjectMapper().writeValueAsString(loginDto)

        whenever(userService.findByUsername(loginDto.username)).thenReturn(user)

        mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = loginDtoJson
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    @WithAuthUser("test", "user")
    @DisplayName("GET /api/v1/users/me 테스트 - 성공")
    fun `me - success`() {
        // Then
        val result = mockMvc
            .perform(
                get("/api/v1/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
            )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-me",
                    documentRequest,
                    documentResponse,
                    responseFields(
                        fieldWithPath("username").description("User's name")
                    )
                )
            )
    }

//    @Test
//    @DisplayName("GET /api/v1/users/me 테스트 - 403 Forbidden")
//    fun `me - fail no auth user == 403 Forbidden`() {
//        mockMvc.get("/api/v1/users/me") {
//            contentType = MediaType.APPLICATION_JSON
//        }
//            .andExpect {
//                status { isForbidden() }
//            }
//    }
}
