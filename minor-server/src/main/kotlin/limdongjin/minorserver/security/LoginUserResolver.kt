package limdongjin.minorserver.security

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import limdongjin.minorserver.application.UserService
import limdongjin.minorserver.domain.user.User
import net.bytebuddy.build.Plugin.Factory.UsingReflection.ArgumentResolver
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.ReactiveAuthenticationManager
//import org.springframework.http.server.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.reactive.BindingContext
//import org.springframework.web.method.support.HandlerMethodArgumentResolver
//import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
//import org.springframework.web.servlet.function.ServerRequest
import javax.security.auth.login.CredentialNotFoundException

private const val BEARER = "Bearer"

@Component
open class LoginUserResolver(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginUser::class.java)
    }

    fun getUser(request: ServerHttpRequest): User {
        return extractBearerToken(request)
            .takeIf {  token ->
                println("isValidToken")
                jwtTokenProvider.isValidToken(token)
            }?.let { token ->
                println("getSubject")
                jwtTokenProvider.getSubject(token)
            }?.let { email ->
                println("getEmail")
                userService.getByEmail(email)
            }?.block().takeIf { it != null }
        ?: throw CredentialNotFoundException()

//        if (!jwtTokenProvider.isValidToken(token)) {
//            throw CredentialNotFoundException()
//        }
//        val userEmail = jwtTokenProvider.getSubject(token)
//
//        val a = userService.getByEmail(userEmail)
//
//        return userService.getByEmail(userEmail).awaitSingle()
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        validateIfAdministrator(parameter)
        val token = extractBearerToken(exchange.request)
        if (!jwtTokenProvider.isValidToken(token)) {
            throw CredentialNotFoundException()
        }
        val userEmail = jwtTokenProvider.getSubject(token)

        return userService.getByEmail(userEmail).cast(Any::class.java)
//        return Mono.just(userService.getByEmail(userEmail))
//        return userService.getByEmail(userEmail)
    }
//
//    override fun resolveArgument(
//        parameter: MethodParameter,
//        mavContainer: ModelAndViewContainer?,
//        webRequest: NativeWebRequest,
//        binderFactory: WebDataBinderFactory?
//    ): User {
//        validateIfAdministrator(parameter)
//        val token = extractBearerToken(webRequest)
//        if (!jwtTokenProvider.isValidToken(token)) {
//            throw CredentialNotFoundException()
//        }
//        val userEmail = jwtTokenProvider.getSubject(token)
//        return userService.getByEmail(userEmail)
//    }

    private fun validateIfAdministrator(parameter: MethodParameter) {
        val annotation = parameter.getParameterAnnotation(LoginUser::class.java)
        if (annotation?.administrator == true) {
            throw CredentialNotFoundException()
        }
    }
//
//    private fun extractBearerToken(request: NativeWebRequest): String {
//        val authorization = request.getHeader(AUTHORIZATION) ?: throw CredentialNotFoundException()
//        val (tokenType, token) = splitToTokenFormat(authorization)
//        if (tokenType != BEARER) {
//            throw CredentialNotFoundException()
//        }
//        return token
//    }

    private fun extractBearerToken(request: ServerHttpRequest): String {
//        val authorization = request.headers().header(AUTHORIZATION).takeIf { it.isNotEmpty() }?.get(0) ?: throw CredentialNotFoundException()
        val authorization = request.headers[AUTHORIZATION]?.takeIf { it.isNotEmpty() }?.get(0) ?: throw CredentialNotFoundException()
        val (tokenType, token) = splitToTokenFormat(authorization)
        if (tokenType != BEARER) throw CredentialNotFoundException()

        return token
    }

    private fun splitToTokenFormat(authorization: String): Pair<String, String> {
        return try {
            val tokenFormat = authorization.split(" ")
            tokenFormat[0] to tokenFormat[1]
        } catch (e: IndexOutOfBoundsException) {
            throw CredentialNotFoundException()
        }
    }
}