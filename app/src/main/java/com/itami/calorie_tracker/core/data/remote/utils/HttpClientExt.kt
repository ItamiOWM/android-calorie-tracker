package com.itami.calorie_tracker.core.data.remote.utils

import com.itami.calorie_tracker.core.data.remote.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import kotlinx.serialization.SerializationException
import java.io.IOException


suspend inline fun <reified T, reified E> HttpClient.safeRequest(
    block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T, E> =
    try {
        val response = request { block() }
        ApiResponse.Success(response.body())
    } catch (e: ClientRequestException) {
        e.printStackTrace()
        ApiResponse.Error.HttpClientError(e.response.status.value, e.errorBody())
    } catch (e: ServerResponseException) {
        e.printStackTrace()
        ApiResponse.Error.HttpServerError(e.response.status.value, e.errorBody())
    } catch (e: IOException) {
        e.printStackTrace()
        ApiResponse.Error.NetworkError
    } catch (e: SerializationException) {
        e.printStackTrace()
        ApiResponse.Error.SerializationError
    } catch (e: Exception) {
        e.printStackTrace()
        ApiResponse.Error.SerializationError
    }

suspend inline fun <reified E> ResponseException.errorBody(): E? =
    try {
        response.body()
    } catch (e: Exception) {
        null
    }