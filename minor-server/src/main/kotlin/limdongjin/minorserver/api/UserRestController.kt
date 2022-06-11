package limdongjin.minorserver.api

import limdongjin.minorserver.application.AuthenticateUserRequest
import limdongjin.minorserver.application.EditPasswordRequest
import limdongjin.minorserver.application.RegisterUserRequest
import limdongjin.minorserver.application.UserAuthenticationService
import limdongjin.minorserver.application.UserService
import limdongjin.minorserver.application.ResetPasswordRequest
import limdongjin.minorserver.application.UserResponse
//import apply.application.mail.MailService

import limdongjin.minorserver.domain.user.User
import limdongjin.minorserver.security.LoginUser

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class UserRestController(
    private val userService: UserService,
    private val userAuthenticationService: UserAuthenticationService
    //    private val mailService: MailService
){

//    @PostMapping("/register")
//    fun generateToken(@RequestBody @Valid request: RegisterUserRequest): ResponseEntity<ApiResponse<String>> {
//        val token = userAuthenticationService.generateTokenByRegister(request)
//
//        return ResponseEntity.ok().body(ApiResponse.success(token, message = "register success"))
////    }
//
//    @PostMapping("/login")
//    fun generateToken(@RequestBody @Valid request: AuthenticateUserRequest): ResponseEntity<ApiResponse<String>> {
//        val token = userAuthenticationService.generateTokenByLogin(request)
//        return ResponseEntity.ok().body(ApiResponse.success(token))
//    }
//
//    @PostMapping("/reset-password")
//    suspend fun resetPassword(@RequestBody @Valid request: ResetPasswordRequest): ResponseEntity<Unit> {
//        userService.resetPassword(request)
//        return ResponseEntity.noContent().build()
//    }

    @PostMapping("/edit-password")
    fun editPassword(
        @RequestBody @Valid request: EditPasswordRequest,
        @LoginUser user: User
    ): ResponseEntity<Unit> {
        userService.editPassword(user.id, request)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/authentication-code")
    fun generateAuthenticationCode(
        @RequestParam email: String
    ): ResponseEntity<Unit> {
        // [TODO] 이메일 인증 기능1
        TODO()
//        val authenticationCode = userAuthenticationService
//            .generateAuthenticationCode(email)
//        mailService.sendAuthenticationCodeMail(email, authenticationCode)
//        return ResponseEntity.noContent().build()
    }

    @PostMapping("/authenticate-email")
    fun authenticateEmail(
        @RequestParam email: String,
        @RequestParam authenticationCode: String
    ): ResponseEntity<Unit> {
        // [TODO] 이메일 인증 기능2
        userAuthenticationService.authenticateEmail(email, authenticationCode)
        return ResponseEntity.noContent().build()
    }

    @GetMapping
    fun findAllByKeyword(
        @RequestParam keyword: String,
        @LoginUser(administrator = true) user: User
    ): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val users = userService.findAllByKeyword(keyword)
        return ResponseEntity.ok(ApiResponse.success(users))
    }

//
//    @GetMapping("/me")
//    fun getMyInformation(
//        @LoginUser user: User
//    ): ResponseEntity<ApiResponse<UserResponse>> {
//        val userInformation = userService.getInformation(user.id)
//
//        return ResponseEntity.ok(ApiResponse.success(userInformation))
//    }

//    @PatchMapping("/information")
//    fun editInformation(
//        @RequestBody @Valid request: EditInformationRequest,
//        @LoginUser user: User
//    ): ResponseEntity<Unit> {
//        userService.editInformation(user.id, request)
//        return ResponseEntity.noContent().build()
//    }
}