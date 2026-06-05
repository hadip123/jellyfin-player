package dev.yourhandle.jellyfinplayer.data

import dev.yourhandle.jellyfinplayer.data.model.AuthResponse
import dev.yourhandle.jellyfinplayer.data.model.ItemsResponse
import dev.yourhandle.jellyfinplayer.data.model.MediaFoldersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JellyfinApi {

    @POST("/Users/AuthenticateByName")
    suspend fun authenticate(
        @Header("X-Emby-Authorization") authorization: String,
        @retrofit2.http.Body body: AuthBody
    ): Response<AuthResponse>

    @GET("/Library/MediaFolders")
    suspend fun getMediaFolders(
        @Header("X-Emby-Authorization") authorization: String
    ): Response<MediaFoldersResponse>

    @GET("/Users/{userId}/Items")
    suspend fun getItems(
        @Header("X-Emby-Authorization") authorization: String,
        @Path("userId") userId: String,
        @Query("IncludeItemTypes") includeItemTypes: String,
        @Query("Recursive") recursive: Boolean = true,
        @Query("ParentId") parentId: String? = null
    ): Response<ItemsResponse>
}

data class AuthBody(
    val Username: String,
    val Pw: String
)
