package dev.yourhandle.jellyfinplayer.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.yourhandle.jellyfinplayer.data.model.AuthResponse
import dev.yourhandle.jellyfinplayer.data.model.Item
import dev.yourhandle.jellyfinplayer.data.model.ItemsResponse
import dev.yourhandle.jellyfinplayer.data.model.MediaFolder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class JellyfinRepository(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "jellyfin_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private var baseUrl: String = ""
    private var token: String = ""
    private var userId: String = ""
    private val deviceId: String = UUID.randomUUID().toString()

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private var api: JellyfinApi? = null

    private fun getApi(): JellyfinApi {
        if (api == null) {
            val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            api = Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JellyfinApi::class.java)
        }
        return api!!
    }

    private fun authHeader(): String {
        return "MediaBrowser Client=\"JellyfinPlayer\", Device=\"Android\", DeviceId=\"$deviceId\", Version=\"1.0.0\", Token=\"$token\""
    }

    fun saveCredentials(serverUrl: String, accessToken: String, userId: String) {
        prefs.edit()
            .putString("server_url", serverUrl)
            .putString("access_token", accessToken)
            .putString("user_id", userId)
            .apply()
        this.baseUrl = serverUrl
        this.token = accessToken
        this.userId = userId
        this.api = null
    }

    fun loadCredentials(): Boolean {
        baseUrl = prefs.getString("server_url", "") ?: ""
        token = prefs.getString("access_token", "") ?: ""
        userId = prefs.getString("user_id", "") ?: ""
        api = null
        return baseUrl.isNotBlank() && token.isNotBlank()
    }

    fun clearCredentials() {
        prefs.edit().clear().apply()
        baseUrl = ""
        token = ""
        userId = ""
        api = null
    }

    suspend fun authenticate(serverUrl: String, username: String, password: String): Result<String> {
        return try {
            baseUrl = serverUrl
            api = null
            val tempAuth = "MediaBrowser Client=\"JellyfinPlayer\", Device=\"Android\", DeviceId=\"$deviceId\", Version=\"1.0.0\""
            val response = getApi().authenticate(tempAuth, AuthBody(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                val accessToken = body?.AccessToken ?: return Result.failure(Exception("No access token"))
                val uid = body?.User?.Id ?: return Result.failure(Exception("No user ID"))
                saveCredentials(serverUrl, accessToken, uid)
                Result.success(uid)
            } else {
                Result.failure(Exception("Auth failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMusicLibraries(): Result<List<MediaFolder>> {
        return try {
            val response = getApi().getMediaFolders(authHeader())
            if (response.isSuccessful) {
                Result.success(response.body()?.Items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get libraries: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlbums(parentId: String? = null): Result<List<Item>> {
        return try {
            val response = getApi().getItems(
                authorization = authHeader(),
                userId = userId,
                includeItemTypes = "MusicAlbum",
                recursive = true,
                parentId = parentId
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.Items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get albums: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTracks(albumId: String): Result<List<Item>> {
        return try {
            val response = getApi().getItems(
                authorization = authHeader(),
                userId = userId,
                includeItemTypes = "Audio",
                recursive = true,
                parentId = albumId
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.Items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get tracks: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getStreamUrl(itemId: String): String {
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return "${url}Audio/${itemId}/universal?audioCodec=aac&container=mp4&maxStreamingBitrate=320000&api_key=$token"
    }

    fun getImageUrl(itemId: String, imageType: String = "Primary"): String {
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return "${url}Items/${itemId}/Images/$imageType?api_key=$token"
    }

    fun getUserId(): String = userId
    fun getBaseUrl(): String = baseUrl
    fun getToken(): String = token
}
