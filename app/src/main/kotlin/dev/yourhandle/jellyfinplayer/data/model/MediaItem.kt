package dev.yourhandle.jellyfinplayer.data.model

data class MediaFoldersResponse(
    val Items: List<MediaFolder>?
)

data class MediaFolder(
    val Id: String?,
    val Name: String?
)

data class ItemsResponse(
    val Items: List<Item>?,
    val TotalRecordCount: Int?
)

data class Item(
    val Id: String?,
    val Name: String?,
    val AlbumArtist: String?,
    val Album: String?,
    val Artist: String?,
    val Artists: List<String>?,
    val AlbumId: String?,
    val ImageTags: Map<String, String>?,
    val RunTimeTicks: Long?,
    val ProductionYear: Int?
)
