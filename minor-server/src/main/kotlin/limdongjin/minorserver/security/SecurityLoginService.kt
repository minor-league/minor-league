package limdongjin.minorserver.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.stereotype.Service

@Service
class SecurityLoginService(
    val jwtTokenProvider: JwtTokenProvider,
    val authenticationManager: ReactiveAuthenticationManager
){

}