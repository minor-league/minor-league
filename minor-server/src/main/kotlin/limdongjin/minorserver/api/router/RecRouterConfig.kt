package limdongjin.minorserver.api.router

import limdongjin.minorserver.api.RecApiHandler
import limdongjin.minorserver.api.SearchApiHandler
import limdongjin.minorserver.api.base.ExceptionHandlers.RouterExtension.onDefaultErrors
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RecRouterConfig {
    @Bean
    fun recommendApiRoutes(handler: RecApiHandler) = coRouter {
        "/api/rec".nest {
            POST("/game", handler::rec)

            onDefaultErrors()
        }
    }
}