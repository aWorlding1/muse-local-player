package com.muse.localplayer.data

import com.muse.localplayer.core.T

import android.net.Uri

enum class TrackSource {
    FEATURED_ASSET,
    DEVICE
}

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val durationMs: Long,
    val uri: Uri,
    val artworkUri: Uri? = null,
    val dateAddedSeconds: Long = 0L,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val source: TrackSource = TrackSource.DEVICE,
    /** Stable topic ID for APK-packaged featured audio; null for device music. */
    val featuredTopicId: String? = null
) {
    val durationLabel: String
        get() {
            val totalSeconds = durationMs / 1_000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }

    val isFeaturedAsset: Boolean
        get() = source == TrackSource.FEATURED_ASSET

    /**
     * 从曲目 URI 推导文件所在文件夹名：取 uri.path 的父目录名。
     * 对 file:// 路径可直接得到真实目录；对 content:// 媒体库 URI 取其路径末段的父目录。
     */
    val folderName: String
        get() {
            val path = uri.path ?: return T("未知文件夹")
            val parent = path.substringBeforeLast('/', "")
            val name = parent.substringAfterLast('/')
            return name.ifBlank { T("根目录") }
        }
}

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUri: Uri?,
    val trackCount: Int,
    val year: Int = 0
)

enum class LibraryTab(val label: String) {
    HOME(T("首页")),
    SONGS(T("歌曲")),
    ALBUMS(T("专辑")),
    PLAYLISTS(T("收藏")),
    LOCAL_PLAYLISTS(T("歌单")),
    FOLDERS(T("文件夹")),
    ARTISTS(T("艺术家"))
}
