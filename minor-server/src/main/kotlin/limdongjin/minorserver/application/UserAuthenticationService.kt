package limdongjin.minorserver.application


import limdongjin.minorserver.domain.authenticationcode.AuthenticationCode
import limdongjin.minorserver.domain.authenticationcode.AuthenticationCodeRepository
import limdongjin.minorserver.domain.authenticationcode.getLastByEmail
import limdongjin.minorserver.domain.user.UnidentifiedUserException
import limdongjin.minorserver.domain.user.UserRepository
import limdongjin.minorserver.domain.user.existsByEmail
import limdongjin.minorserver.domain.user.findByEmail
import limdongjin.minorserver.security.JwtTokenProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.security.auth.login.CredentialException
import javax.security.auth.login.CredentialNotFoundException

@Transactional
@Service
class UserAuthenticationService(
    private val userRepository: UserRepository,
    private val authenticationCodeRepository: AuthenticationCodeRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun generateTokenByRegister(request: RegisterUserRequest): String {
        require(request.password == request.confirmPassword) { "비밀번호가 일치하지 않습니다." }
        check(!userRepository.existsByEmail(request.email)) { "이미 가입된 이메일입니다." }

        // [TODO] authenticationCodeRepository.getLastByEmail(request.email).validate(request.authenticationCode)
        val user = userRepository.save(request.toEntity())
        return jwtTokenProvider.createToken(user.email)
    }

    fun generateTokenByLogin(request: AuthenticateUserRequest): String {
        val user = userRepository.findByEmail(request.email)
            ?: throw CredentialNotFoundException("사용자 정보가 일치하지 않습니다.")
        user.authenticate(request.password)
        return jwtTokenProvider.createToken(user.email)
    }

    fun generateAuthenticationCode(email: String): String {
        check(!userRepository.existsByEmail(email)) { "이미 등록된 이메일입니다." }
        val authenticationCode = authenticationCodeRepository.save(AuthenticationCode(email))
        return authenticationCode.code
    }

    fun authenticateEmail(email: String, code: String) {
        val authenticationCode = authenticationCodeRepository.getLastByEmail(email)
        authenticationCode.authenticate(code)
    }
}
