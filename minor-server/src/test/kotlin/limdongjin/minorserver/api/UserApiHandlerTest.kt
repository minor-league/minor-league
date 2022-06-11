package limdongjin.minorserver.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.slot
import limdongjin.minorserver.config.*
import limdongjin.minorserver.fixtures.createUser

import limdongjin.minorserver.security.LoginUserResolver
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
//import org.springframework.restdocs.RestDocumentationContextProvider
//import org.springframework.restdocs.RestDocumentationExtension
//
//import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.servlet.function.ServerRequest
import javax.security.auth.login.CredentialNotFoundException
import com.ninjasquad.springmockk.MockkBean
import limdongjin.minorserver.application.UserResponse
import limdongjin.minorserver.application.UserService
import limdongjin.minorserver.fixtures.VALID_TOKEN1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@Import(RestDocsConfiguration::class)
@ExtendWith(RestDocumentationExtension::class)
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//@SpringBootTest(
//    webEnvironment = SpringBootTest.WebEnvironment.MOCK
//)
@SpringJUnitConfig(AppConfig::class, AuthenticationConfig::class,
    ObjectMapperConfig::class) //, SecurityConfig::class)
@WebAppConfiguration
//@ContextConfiguration(classes = [UserApiRouter::class, UserApiHandler::class])
class UserApiHandlerTest{
    @MockkBean lateinit var loginUserResolver: LoginUserResolver
    private lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @MockkBean lateinit var userService: UserService

//    @Autowired lateinit var client: TestRestTemplate

    @BeforeEach
    internal fun setUp(
        webApplicationContext: WebApplicationContext,
        restDocumentationContextProvider: RestDocumentationContextProvider
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(
                    restDocumentationContextProvider
                )
            )
            .build()
        loginUserResolver.also { resolver ->
            val argSlot = slot<ServerRequest>()
            every { resolver.getUser(capture(argSlot)) } answers {
                val hasToken = argSlot.captured.headers()
                                                .header(HttpHeaders.AUTHORIZATION)
                                                .contains("Bearer")
                when {
                    !hasToken -> throw CredentialNotFoundException()
                    else -> createUser()
                }
            }

        }
    }

    @Test
    fun `movckMvc null이 아닌지 테스트`() {
        mockMvc.takeIf { it != null } ?: fail()
    }

    @Test
    fun `validToken 을 가진 사용자는 자신의 정보를 조회할 수 있다`(){
            every { userService.getInformation(any()) } returns UserResponse(createUser())
            every { userService.getByEmail(any()) } returns createUser()

            mockMvc.get("/api/user/me") {
                header(HttpHeaders.AUTHORIZATION, "Bearer $VALID_TOKEN1")
//                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { json(objectMapper.writeValueAsString(createUser().information)) }
            }
    }

    @Test
    fun `login 기능 작동하는지 테스트`(){
        mockMvc.post("/api/user/login") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }
}