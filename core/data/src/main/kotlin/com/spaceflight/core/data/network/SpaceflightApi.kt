package com.spaceflight.core.data.network

import com.spaceflight.core.data.network.dto.ArticleDto
import com.spaceflight.core.data.network.dto.PaginatedArticlesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceflightApi {

    @GET("articles/")
    suspend fun getArticles(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("search") search: String? = null,
        @Query("ordering") ordering: String = ORDERING_NEWEST_FIRST,
    ): PaginatedArticlesDto

    @GET("articles/{id}/")
    suspend fun getArticle(@Path("id") id: Int): ArticleDto

    companion object {
        const val ORDERING_NEWEST_FIRST = "-published_at"
    }
}
