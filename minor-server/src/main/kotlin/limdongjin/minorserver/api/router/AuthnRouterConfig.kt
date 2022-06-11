package limdongjin.minorserver.api

import limdongjin.minorserver.api.base.ExceptionHandlers.RouterExtension.onDefaultErrors
//import limdongjin.minorserver.common.badResponseFormat
//import limdongjin.minorserver.security.LoginUserResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
//import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.router
//import org.springframework.web.reactive.function.server.router
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
//
//import org.springframework.web.servlet.function.*
import javax.security.auth.login.CredentialNotFoundException
//@EnableWebMvc

@Configuration
class AuthnRouterConfig: WebFluxConfigurer {
    @Bean
    fun authnRoute(handler: AuthnApiHandler) = coRouter {
        "/api/user".and(accept(MediaType.APPLICATION_JSON)).nest {
            POST("/register", handler::register)
            POST("/login", handler::login)

            onDefaultErrors()
        }
    }
}