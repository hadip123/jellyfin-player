package dev.yourhandle.jellyfinplayer.data.model

data class AuthResponse(
    val User: User?,
    val AccessToken: String?
)

data class User(
    val Id: String?,
    val Name: String?
)
