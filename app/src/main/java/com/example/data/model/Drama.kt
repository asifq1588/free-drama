package com.example.data.model

enum class EpisodeAccessType {
    FREE,
    AD_REQUIRED,
    VIP_ONLY
}

data class Episode(
    val id: String,
    val dramaId: String,
    val episodeNumber: Int,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val durationSeconds: Int = 120,
    val accessType: EpisodeAccessType = EpisodeAccessType.FREE,
    val monetagAdUrl: String = "", // Episode-specific Monetag Direct Link or Zone Tag
    val isUnlocked: Boolean = false,
    val viewsCount: Int = 150000
)

data class Drama(
    val id: String,
    val title: String,
    val synopsis: String,
    val coverDrawableRes: Int? = null,
    val coverUrl: String = "",
    val genres: List<String> = listOf("Romance", "Drama"),
    val totalEpisodes: Int = 54,
    val rating: Float = 4.9f,
    val views: String = "12.8M",
    val isTrending: Boolean = false,
    val isExclusive: Boolean = false,
    val language: String = "English",
    val releaseYear: Int = 2026
)
