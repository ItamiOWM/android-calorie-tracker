package com.itami.calorie_tracker.core.data.remote.response

sealed class ApiResponse<out T, out E> {
    /**
     * Represents successful network responses (2xx).
     */
    data class Success<T>(val body: T) : ApiResponse<T, Nothing>()

    sealed class Error<E> : ApiResponse<Nothing, E>() {
        /**
         * Represents client (40x) errors.
         */
        data class HttpClientError<E>(val code: Int, val errorBody: E?) : Error<E>()

        /**
         * Represents server (50x) errors.
         */
        data class HttpServerError<E>(val code: Int, val errorBody: E?) : Error<E>()

        /**
         * Represent IOExceptions and connectivity issues.
         */
        data object NetworkError : Error<Nothing>()

        /**
         * Represent SerializationExceptions.
         */
        data object SerializationError : Error<Nothing>()
    }
}
