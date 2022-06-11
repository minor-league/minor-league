package limdongjin.minorserver.api.router

import limdongjin.minorserver.api.SearchApiHandler
import limdongjin.minorserver.api.UserApiHandler
import limdongjin.minorserver.api.base.ExceptionHandlers.RouterExtension.onDefaultErrors
import limdongjin.minorserver.security.LoginUserResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter


@Configuration
class SearchRouterConfig {
    @Bean
    fun searchApiRoutes(handler: SearchApiHandler) = coRouter {
        "/api/search".nest {
            POST("/game", handler::searchDefault)

//            filter { serverRequest, function ->
//                try {
//                    loginUserResolver.getUser(serverRequest.awaitBody())
//                }catch (ex: CredentialNotFoundException){
//                    val status = HttpStatus.UNAUTHORIZED
//                    return@filter ServerResponse.status(status).bodyValueAndAwait(
//                        badResponseFormat(
//                            message = "Unauthorized",
//                            path = serverRequest.path(),
//                            httpStatus = status
//                        ))
//                }

//                function.invoke(serverRequest)
//            }

            onDefaultErrors()
        }
    }
}