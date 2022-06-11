package limdongjin.minorserver.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

const val TWELVE_HOURS_IN_MILLISECONDS: Long = 1000 * 60 * 60 * 12

@Component
class JwtTokenProvider(
    private val signingKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256),
    private val expirationInMilliseconds: Long = TWELVE_HOURS_IN_MILLISECONDS
){
    val AUTHORITIES_KEY = "permissions"
    fun getAuthentication(token: String): Authentication {
        val claims = getClaimsJws(token).body
        val authoritiesClaim = claims[AUTHORITIES_KEY]
        val authorities = when(authoritiesClaim == null) {
            true -> AuthorityUtils.NO_AUTHORITIES
            false -> AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString())
        }
        println(authorities)
        val principal = User(claims.subject, "", authorities)
        println(principal)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }
    fun validateToken(token: String): Boolean {
        return try {
            getClaimsJws(token)
            true
        } catch (ex: JwtException){
            false
        } catch (ex: IllegalArgumentException){
            false
        }
    }
    fun createToken(email: String): String {
        val claims: Claims = Jwts.claims().setSubject(email)
        val now = Date()
        val expiration = Date(now.time + expirationInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(signingKey)
            .compact()
    }

    fun getSubject(token: String): String {
        return getClaimsJws(token)
            .body
            .subject
    }

    fun isValidToken(token: String): Boolean {
        return try {
            getClaimsJws(token)
            println("is Valid Token")
            true
        } catch (e: JwtException) {
            println("is Not Valid Token")
            false
        } catch (e: IllegalArgumentException) {
            println("is Not Valid Token")
            false
        }
    }

    private fun getClaimsJws(token: String) = Jwts.parserBuilder()
        .setSigningKey(signingKey.encoded)
        .build()
        .parseClaimsJws(token)
}