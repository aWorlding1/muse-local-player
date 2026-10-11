package com.muse.localplayer.data

import com.muse.localplayer.core.T

enum class ContentSearchKind(val label: String) {
    TRACK(T("曲目")),
    CHAPTER(T("章节")),
    LYRIC(T("歌词")),
    NOTE(T("笔记")),
    BOOKMARK(T("书签"))
}

/** A local-only result that can start a track directly or seek to a publisher/user-defined point. */
data class ContentSearchResult(
    val kind: ContentSearchKind,
    val track: Track,
    val title: String,
    val supportingText: String,
    val positionMs: Long = 0L,
    val bookmark: PlaybackBookmark? = null
)
