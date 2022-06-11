package limdongjin.minorserver.api

import limdongjin.minorserver.api.base.BaseApiHandler
import limdongjin.minorserver.api.base.handle
import limdongjin.minorserver.application.AuthenticateUserRequest
import limdongjin.minorserver.application.RegisterUserRequest
import limdongjin.minorserver.application.UserAuthenticationService
import limdongjin.minorserver.application.UserResponse
import limdongjin.minorserver.domain.user.User
import limdongjin.minorserver.security.LoginUser
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import reactor.core.publisher.Mono

//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.servlet.function.ServerRequest
//import org.springframework.web.servlet.function.ServerResponse
//import org.springframework.web.servlet.function.ServerResponse.ok


@Component
class AuthnApiHandler(
    private val userAuthenticationService: UserAuthenticationService,
    private val validator: AnnotatedEntityValidator
) : BaseApiHandler {

    suspend fun register(request: ServerRequest): ServerResponse =
        handle<RegisterUserRequest>(request) { body ->
            userAuthenticationService.generateTokenByRegister(body).let { token ->
                ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(token)
            }
        }

    suspend fun login(request: ServerRequest): ServerResponse =
        handle<AuthenticateUserRequest>(request) { body ->
            userAuthenticationService.generateTokenByLogin(body).let { token ->
                ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(token)
            }
        }

    override fun getValidator(): AnnotatedEntityValidator {
        return validator
    }
}

