package limdongjin.minorserver.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.withContext
import limdongjin.minorserver.api.base.BaseApiHandler
import limdongjin.minorserver.application.UserService
import limdongjin.minorserver.security.LoginUserResolver
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.awaitPrincipal


@Component
class UserApiHandler(
    private val userService: UserService,
    private val validator: AnnotatedEntityValidator,
    private val loginUserResolver: LoginUserResolver
): BaseApiHandler {
    suspend fun getMyInformation(request: ServerRequest): ServerResponse {
        return request.awaitPrincipal()?.name
            ?.let {
                println("getInfo $it")
                userService.getInformation(it)
            }
            ?.let {
                println("ok")
                ok().bodyValue(it)
            }?.awaitSingle() ?: throw IllegalArgumentException()
    }

    override fun getValidator(): AnnotatedEntityValidator {
        return validator
    }
}
