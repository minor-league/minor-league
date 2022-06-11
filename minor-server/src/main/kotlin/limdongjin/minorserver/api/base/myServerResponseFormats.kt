package limdongjin.minorserver.common

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.Errors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import reactor.core.publisher.Mono

import java.time.LocalDateTime

suspend fun myBadRequest(request: ServerRequest, errors: Errors): ServerResponse {
    return ServerResponse.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(errorResponseFormat(
                httpStatus = HttpStatus.BAD_REQUEST,
                message = errors.fieldError?.defaultMessage ?: "invalid fields",
                path = request.path(),
                errors = errors
        ))
}

suspend fun myBadRequest(request: ServerRequest, message: String): ServerResponse {
    return ServerResponse.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(badResponseFormat(
                httpStatus = HttpStatus.BAD_REQUEST,
                message = message,
                path = request.path()
        ))
}

fun badResponseFormat(
    message: String,
    path: String = "/",
    httpStatus: HttpStatus
): Any {
    println("badResFormat")

    return object {
        val timestamp = LocalDateTime.now().toString()
        val status = httpStatus.value()
        val error = httpStatus.name
        val message = message
        val path = path
    }
}

fun errorResponseFormat(
    message: String,
    path: String = "/",
    httpStatus: HttpStatus,
    errors: Errors
): Any {
    println("errorResFormat")
    println(errors)
    return object {
        val timestamp = LocalDateTime.now().toString()
        val status = httpStatus.value()
        val error = httpStatus.name
        val message = message
        val path = path
        val details = errors.allErrors
    }
}