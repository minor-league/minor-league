package limdongjin.minorserver.api

import limdongjin.minorserver.api.base.BaseApiHandler
import limdongjin.minorserver.api.base.handle
import limdongjin.minorserver.application.SearchRequestDto
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class RecApiHandler(
    private val validator: AnnotatedEntityValidator
): BaseApiHandler {
    suspend fun rec(request: ServerRequest): ServerResponse {
        return handle<SearchRequestDto>(request) { body ->
            WebClient.create()
                .get()
                .uri("http://localhost:5000/recommend?${request.queryParams()}")
                .retrieve()
                .awaitBody<Any>()
                .let { body2 ->
                    ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(body2)
                }
        }
    }

    override fun getValidator(): AnnotatedEntityValidator {
        return validator
    }
}