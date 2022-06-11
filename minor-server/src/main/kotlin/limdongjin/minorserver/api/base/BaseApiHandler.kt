package limdongjin.minorserver.api.base

import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.reactor.awaitSingle
import limdongjin.minorserver.api.AnnotatedEntityValidator
import limdongjin.minorserver.common.myBadRequest
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


interface BaseApiHandler {
    fun getValidator(): AnnotatedEntityValidator
}

suspend inline fun <reified T : Any> BaseApiHandler.handle(
    request: ServerRequest,
    thenBlock: (T) -> ServerResponse,
): ServerResponse {
    val body = request.awaitBody<T>()

    this.getValidator().validate(body)?.let {
        if(it.hasErrors()){
            println("handle error occur")
            return myBadRequest(request, it)
        }
    }

    return thenBlock(body)
}
