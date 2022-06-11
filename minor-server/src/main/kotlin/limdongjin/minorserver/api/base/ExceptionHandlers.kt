package limdongjin.minorserver.api.base

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import kotlinx.coroutines.reactor.mono
import limdongjin.minorserver.common.badResponseFormat
import limdongjin.minorserver.common.errorResponseFormat
import limdongjin.minorserver.common.myBadRequest
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


import javax.persistence.EntityNotFoundException
import javax.security.auth.login.CredentialNotFoundException

class ExceptionHandlers {
    companion object RouterExtension {
        fun CoRouterFunctionDsl.onDefaultErrors() {
            val defaultMessage = "Invalid Request"

            onError<HttpMessageNotReadableException> { throwable, serverRequest ->
                val message = when (val cause = throwable.cause) {
                    is MissingKotlinParameterException -> "${cause.parameter.name.orEmpty()} CAN'T be null"
                    is InvalidFormatException -> "${cause.path.last().fieldName.orEmpty()} is INVALID FORMATTED"
                    else -> cause?.message.orEmpty()
                }
                myBadRequest(request = serverRequest, message = message)
            }
            onError<IllegalArgumentException> { throwable, serverRequest ->
                val message = throwable.message ?: defaultMessage
                myBadRequest(request = serverRequest, message = message)
            }
            onError<IllegalStateException> { throwable, serverRequest ->
                val message = throwable.message ?: defaultMessage
                myBadRequest(request = serverRequest, message = message)
            }
            onError<EntityNotFoundException> { _, serverRequest ->
                val message = "Entity Not Found"
                myBadRequest(request = serverRequest, message = message)
            }
            onError<CredentialNotFoundException> { _, serverRequest ->
                val message = "Credential Not Found"
                val status = HttpStatus.UNAUTHORIZED
                ServerResponse.status(status).bodyValueAndAwait(
                    badResponseFormat(
                        message = message,
                        path = serverRequest.path(),
                        httpStatus = status
                    )
                )
            }

//            onError<Exception> { _, serverRequest ->
//                val message = "Exception"
//                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    errorResponseFormat(
//                        message = message,
//                        path = serverRequest.path(),
//                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
//                    )
//                )
//            }

        }
    }
}

