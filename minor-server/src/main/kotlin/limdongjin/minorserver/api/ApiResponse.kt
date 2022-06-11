package limdongjin.minorserver.api

data class ApiResponse<T>(
    val message: String? = "",
    val body: T? = null
) {
    companion object {
        fun error(message: String?): ApiResponse<Unit> = ApiResponse(message = message)

        fun <T> success(body: T?, message: String? = "success"): ApiResponse<T>
            = ApiResponse(body = body, message = message)
    }
}