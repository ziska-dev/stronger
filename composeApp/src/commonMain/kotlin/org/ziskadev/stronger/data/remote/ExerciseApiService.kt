package org.ziskadev.stronger.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.ziskadev.stronger.data.remote.dto.ExerciseDto
import org.ziskadev.stronger.data.remote.dto.ExerciseListResponseDto
import org.ziskadev.stronger.domain.model.ApiResult

/**
 * ExerciseApiService: Ktor client for HTTP calls to the workout.cool backend
 * @param httpClient Ktor HttpClient (injected via Koin)
 * @param baseUrl    Base-URL of the backend (e.g. "http://10.0.2.2:3000")
 */
class ExerciseApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    companion object {
        private const val TAG = "ExerciseApiService"
        private const val PAGE_SIZE = 100
    }

    /**
     * Loads ALL exercises from the backend with automatic pagination
     * Endpoint /api/exercises/all delivers max. 100 exercises per page
     * --> Function calls all pages and returns a list of exercises
     *
     * Initial: 1000+ exercises ~10-11 API calls for the first app start
     * After that: Offline first --> SQLDelight cache
     *
     * @return ApiResult.Success with all exercises or ApiResult.Error for errors
     */
    suspend fun fetchAllExercises(): ApiResult<List<ExerciseDto>> {
        return try {
            val allExercises = mutableListOf<ExerciseDto>()
            var currentPage = 1
            var hasNextPage = true

            // Solange weitere Seiten vorhanden, laden wir weiter
            while (hasNextPage) {
                Napier.d("Lade Übungen Seite $currentPage...", tag = TAG)

                val response: ExerciseListResponseDto = httpClient
                    .get("$baseUrl/api/exercises/all") {
                        parameter("page", currentPage)
                        parameter("limit", PAGE_SIZE)
                    }
                    .body()

                allExercises.addAll(response.data)
                hasNextPage = response.pagination.hasNextPage
                currentPage++

                Napier.d(
                    "Seite ${response.pagination.page}/${response.pagination.totalPages} geladen " +
                            "(${allExercises.size}/${response.pagination.totalCount} Übungen)",
                    tag = TAG,
                )
            }

            Napier.i("Alle ${allExercises.size} Übungen erfolgreich geladen.", tag = TAG)
            ApiResult.Success(allExercises)

        } catch (e: Exception) {
            Napier.e("Fehler beim Laden der Übungen: ${e.message}", throwable = e, tag = TAG)
            ApiResult.Error(
                message = "Übungen konnten nicht geladen werden: ${e.message}",
                cause = e,
            )
        }
    }
}