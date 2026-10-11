package com.muse.localplayer.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** 本地歌单：仅保存名称与设备歌曲 ID，不复制音频文件。 */
data class LocalPlaylist(
    val id: String,
    val name: String,
    val trackIds: List<Long>,
    val createdAtMs: Long
)

/**
 * 本地歌单仓库：使用 SharedPreferences + JSON 持久化，避免引入 Room 等新依赖。
 * 所有写操作都在内存中重建整个列表后整体落盘，返回最新列表供上层刷新状态。
 */
class PlaylistRepository(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadPlaylists(): List<LocalPlaylist> {
        val raw = preferences.getString(KEY_PLAYLISTS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val id = item.optString("id").takeIf { it.isNotBlank() } ?: continue
                    val name = item.optString("name").takeIf { it.isNotBlank() } ?: continue
                    val trackIdsArray = item.optJSONArray("trackIds") ?: JSONArray()
                    val trackIds = buildList {
                        for (trackIndex in 0 until trackIdsArray.length()) {
                            val trackId = trackIdsArray.optLong(trackIndex, -1L)
                            if (trackId > 0L) add(trackId)
                        }
                    }
                    add(
                        LocalPlaylist(
                            id = id,
                            name = name,
                            trackIds = trackIds.distinct(),
                            createdAtMs = item.optLong("createdAtMs", 0L)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun createPlaylist(name: String): List<LocalPlaylist> {
        val safeName = name.trim().ifBlank { return loadPlaylists() }
        val playlists = loadPlaylists().toMutableList()
        playlists += LocalPlaylist(
            id = UUID.randomUUID().toString(),
            name = safeName,
            trackIds = emptyList(),
            createdAtMs = System.currentTimeMillis()
        )
        return persist(playlists)
    }

    fun renamePlaylist(playlistId: String, name: String): List<LocalPlaylist> {
        val safeName = name.trim().ifBlank { return loadPlaylists() }
        val playlists = loadPlaylists().map { playlist ->
            if (playlist.id == playlistId) playlist.copy(name = safeName) else playlist
        }
        return persist(playlists)
    }

    fun deletePlaylist(playlistId: String): List<LocalPlaylist> {
        return persist(loadPlaylists().filterNot { it.id == playlistId })
    }

    fun addTrack(playlistId: String, trackId: Long): List<LocalPlaylist> {
        val playlists = loadPlaylists().map { playlist ->
            if (playlist.id == playlistId && trackId !in playlist.trackIds) {
                playlist.copy(trackIds = playlist.trackIds + trackId)
            } else {
                playlist
            }
        }
        return persist(playlists)
    }

    fun removeTrack(playlistId: String, trackId: Long): List<LocalPlaylist> {
        val playlists = loadPlaylists().map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(trackIds = playlist.trackIds.filterNot { it == trackId })
            } else {
                playlist
            }
        }
        return persist(playlists)
    }

    /**
     * 拖拽排序：以传入顺序为准重建歌单内歌曲顺序。
     * 仅保留原本就属于该歌单的 ID，遗漏的歌曲追加到末尾，避免拖拽状态异常时丢失歌曲。
     */
    fun reorderTracks(playlistId: String, orderedTrackIds: List<Long>): List<LocalPlaylist> {
        val playlists = loadPlaylists().map { playlist ->
            if (playlist.id != playlistId) return@map playlist
            val existing = playlist.trackIds.toSet()
            val reordered = orderedTrackIds.filter { it in existing }.distinct()
            val missing = playlist.trackIds.filterNot { it in reordered }
            playlist.copy(trackIds = reordered + missing)
        }
        return persist(playlists)
    }

    private fun persist(playlists: List<LocalPlaylist>): List<LocalPlaylist> {
        val array = JSONArray()
        playlists.forEach { playlist ->
            val item = JSONObject()
            item.put("id", playlist.id)
            item.put("name", playlist.name)
            item.put("createdAtMs", playlist.createdAtMs)
            val trackIdsArray = JSONArray()
            playlist.trackIds.forEach(trackIdsArray::put)
            item.put("trackIds", trackIdsArray)
            array.put(item)
        }
        preferences.edit().putString(KEY_PLAYLISTS, array.toString()).apply()
        return playlists
    }

    private companion object {
        const val PREFERENCES_NAME = "muse_local_playlists"
        const val KEY_PLAYLISTS = "playlists_json"
    }
}
