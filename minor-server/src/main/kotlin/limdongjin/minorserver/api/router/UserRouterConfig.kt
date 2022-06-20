package limdongjin.minorserver.api.router

import kotlinx.coroutines.reactive.awaitSingle
import limdongjin.minorserver.api.UserApiHandler
import limdongjin.minorserver.api.base.ExceptionHandlers.RouterExtension.onDefaultErrors
import limdongjin.minorserver.common.badResponseFormat
import limdongjin.minorserver.security.LoginUserResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
//import org.springframework.web.servlet.function.ServerResponse
//import org.springframework.web.servlet.function.router
import javax.security.auth.login.CredentialNotFoundException


@Configuration
class UserRouterConfig {

    @Bean
    fun userApiRoutes(handler: UserApiHandler, loginUserResolver: LoginUserResolver) = coRouter {
        "/api/user".nest {
            GET("/me", handler::getMyInformation)

            onDefaultErrors()
        }
    }
}