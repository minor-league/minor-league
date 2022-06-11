package limdongjin.minorserver.api

import limdongjin.minorserver.api.base.BaseApiHandler
import limdongjin.minorserver.api.base.handle
import limdongjin.minorserver.application.SearchRequestDto
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class SearchApiHandler(
    private val validator: AnnotatedEntityValidator
): BaseApiHandler {
    suspend fun searchDefault(request: ServerRequest): ServerResponse
        = handle<SearchRequestDto>(request) { body ->
            WebClient.create()
                .get()
                .uri("http://localhost:8800/")
                .retrieve()
                .awaitBody<Any>()
                .let { body2 ->
                    ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(body2)
                }
        }

    override fun getValidator(): AnnotatedEntityValidator {
        return validator
    }
}