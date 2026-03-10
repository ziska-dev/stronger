package org.ziskadev.stronger.domain.model

/**
 * Sealed class for generic ErrorHandling for API calls
 */
sealed class ApiResult<out T> {

    // Successful API call contains data
    data class Success<T>(val data: T) : ApiResult<T>()

    // Error Handling - e.g. no internet, server error, parsing error
    data class Error(
        val message: String,
        val cause: Throwable? = null,
    ) : ApiResult<Nothing>()

    // Loading state - e.g. for UI spinner
    object Loading : ApiResult<Nothing>()
}