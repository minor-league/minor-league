package limdongjin.minorserver.security

import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtTokenAuthenticationFilter(
    val jwtTokenProvider: JwtTokenProvider
): WebFilter {
    val HEADER_PREFIX = "Bearer "

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token: String? = resolveToken(exchange.request)
        println("filter")
        if(StringUtils.hasText(token) && this.jwtTokenProvider.isValidToken(token!!)) {
            val authentication: Authentication = this.jwtTokenProvider.getAuthentication(token)
            println("filter deb")
            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        }
        println("filter deb2")
        return chain.filter(exchange)
    }

    fun resolveToken(request: ServerHttpRequest): String? {
        val bearerToken: String? = request.headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken!!.startsWith(HEADER_PREFIX)) {
            println("resolve success")
            return bearerToken.substring(7);
        }
        println("resolve fail")
        return null;
    }
}