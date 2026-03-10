package org.ziskadev.stronger.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Exercise DTOs for backend JSOn structure
 * Mapping from API name to kotlin name via @SerialName
 */

@Serializable
data class ExerciseListResponseDto(
    // Exercise list from this page
    val data: List<ExerciseDto>,
    // Info for pagination to load all pages
    val pagination: PaginationDto,
)

@Serializable
data class PaginationDto(
    val page: Int,
    val limit: Int,
    val totalCount: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
)

@Serializable
data class ExerciseDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val nameEn: String? = null,
    val descriptionEn: String? = null,
    @SerialName("fullVideoUrl")
    val videoUrl: String? = null,
    @SerialName("fullVideoImageUrl")
    val thumbnailUrl: String? = null,
    // key-value attributes: muscles, quipment, type
    val attributes: List<ExerciseAttributeDto> = emptyList(),
)

@Serializable
data class ExerciseAttributeDto(
    // Attribute name - e.g. "PRIMARY_MUSCLE", "EQUIPMENT", "TYPE"
    val attributeName: AttributeNameDto,
    // Attribute value - e.g. "QUADRICEPS", "DUMBBELL", "STRENGTH"
    val attributeValue: AttributeValueDto,
)

@Serializable
data class AttributeNameDto(
    // e.g. "PRIMARY_MUSCLE", "SECONDARY_MUSCLE", "EQUIPMENT", "TYPE"
    val name: String,
)

@Serializable
data class AttributeValueDto(
    // e.g. "QUADRICEPS", "DUMBBELL", "STRENGTH"
    val value: String,
)