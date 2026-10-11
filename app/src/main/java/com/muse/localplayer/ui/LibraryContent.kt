package com.muse.localplayer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.muse.localplayer.data.Album
import com.muse.localplayer.data.ContentSearchResult
import com.muse.localplayer.data.FeaturedAudioPack
import com.muse.localplayer.data.FeaturedPackMetadata
import com.muse.localplayer.data.FeaturedTopicProgress
import com.muse.localplayer.data.LibraryTab
import com.muse.localplayer.data.LocalPlaylist
import com.muse.localplayer.data.Track
import com.muse.localplayer.playback.LibraryUiState
import com.muse.localplayer.playback.PlayerViewModel

// ── 统一转场（对齐书斋翻页的顺滑感）─────────────────────────────────────────
// 标签页之间用 300ms 减速淡入 + 轻微放大，避免整块内容硬切。
private const val MuseTransitionMs = 300

private val museTabEnter: EnterTransition =
    fadeIn(tween(MuseTransitionMs, easing = FastOutSlowInEasing)) +
        scaleIn(tween(MuseTransitionMs, easing = FastOutSlowInEasing), initialScale = 0.98f)

private val museTabExit: ExitTransition =
    fadeOut(tween(180, easing = FastOutSlowInEasing))

@Composable
internal fun LibraryContent(
    selectedTab: LibraryTab,
    tracks: List<Track>,
    featuredTracks: List<Track>,
    featuredTopics: List<FeaturedAudioPack>,
    activeFeaturedTopicId: String?,
    featuredTopicProgresses: Map<String, FeaturedTopicProgress>,
    featuredJourney: PlayerViewModel.FeaturedJourneyState,
    featuredMetadata: FeaturedPackMetadata,
    visibleTracks: List<Track>,
    contentSearchResults: List<ContentSearchResult>,
    isSearching: Boolean,
    favoriteIds: Set<Long>,
    libraryUiState: LibraryUiState,
    contentPadding: PaddingValues,
    audioPermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    playbackHistory: List<Track>,
    bookmarks: List<PlayerViewModel.BookmarkItem>,
    recentlyAdded: List<Track>,
    sleepTimerRemainingMs: Long,
    onSetSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onClearPlaybackHistory: () -> Unit,
    onPlayBookmark: (PlayerViewModel.BookmarkItem) -> Unit,
    onRemoveBookmark: (PlayerViewModel.BookmarkItem) -> Unit,
    onRequestPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRescan: () -> Unit,
    onPlay: (Track) -> Unit,
    onPlayContentSearchResult: (ContentSearchResult) -> Unit,
    onContinueFeaturedJourney: () -> Unit,
    onContinueFeaturedTopicJourney: (String) -> Unit,
    onRestartFeaturedJourney: () -> Unit,
    onPlayFeaturedTracks: () -> Unit,
    onSelectFeaturedTopic: (String) -> Unit,
    onAddTracksToQueue: (List<Track>) -> Unit,
    onPlayAlbum: (List<Track>, Track?) -> Unit,
    onShowSongs: () -> Unit,
    onMore: (Track) -> Unit,
    onOpenFeaturedDetail: () -> Unit,
    localPlaylists: List<LocalPlaylist>,
    onPlayPlaylist: (LocalPlaylist) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onRenamePlaylist: (String, String) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onRemoveTrackFromPlaylist: (String, Long) -> Unit,
    onReorderTracks: (String, List<Long>) -> Unit
) {
    val songsForScreen = remember(visibleTracks, isSearching) {
        if (isSearching) visibleTracks else visibleTracks.filterNot { it.isFeaturedAsset }
    }
    val albums = remember(tracks) {
        tracks.groupBy { it.albumId }.map { (_, songs) ->
            val first = songs.first()
            Album(first.albumId, first.album, first.artist, first.artworkUri, songs.size, first.year)
        }.sortedWith(compareByDescending<Album> { it.year }.thenBy { it.title.lowercase() })
    }
    val favoriteTracks = remember(tracks, favoriteIds) {
        tracks.filter { it.id in favoriteIds }
    }
    // 文件夹与艺术家视图仅针对设备本地音乐，避免混入 APK 内置专题音频。
    val deviceTracks = remember(tracks) {
        tracks.filterNot { it.isFeaturedAsset }
    }

    AnimatedContent(
        targetState = selectedTab,
        transitionSpec = { museTabEnter togetherWith museTabExit },
        label = "muse-tab"
    ) { currentTab ->
    when (currentTab) {
        LibraryTab.HOME -> HomeScreen(
            featuredTracks = featuredTracks,
            featuredTopics = featuredTopics,
            activeFeaturedTopicId = activeFeaturedTopicId,
            featuredTopicProgresses = featuredTopicProgresses,
            featuredJourney = featuredJourney,
            featuredMetadata = featuredMetadata,
            libraryUiState = libraryUiState,
            contentPadding = contentPadding,
            audioPermissionGranted = audioPermissionGranted,
            notificationPermissionGranted = notificationPermissionGranted,
            playbackHistory = playbackHistory,
            bookmarks = bookmarks,
            recentlyAdded = recentlyAdded,
            sleepTimerRemainingMs = sleepTimerRemainingMs,
            onSetSleepTimer = onSetSleepTimer,
            onCancelSleepTimer = onCancelSleepTimer,
            onClearPlaybackHistory = onClearPlaybackHistory,
            onPlayBookmark = onPlayBookmark,
            onRemoveBookmark = onRemoveBookmark,
            onRequestPermission = onRequestPermission,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRescan = onRescan,
            onPlay = onPlay,
            onContinueFeaturedJourney = onContinueFeaturedJourney,
            onContinueFeaturedTopicJourney = onContinueFeaturedTopicJourney,
            onRestartFeaturedJourney = onRestartFeaturedJourney,
            onPlayFeaturedTracks = onPlayFeaturedTracks,
            onSelectFeaturedTopic = onSelectFeaturedTopic,
            onAddFeaturedTracksToQueue = { onAddTracksToQueue(featuredTracks) },
            onSongsClick = onShowSongs,
            onMore = onMore,
            onOpenFeaturedDetail = onOpenFeaturedDetail
        )
        LibraryTab.SONGS -> SongsScreen(
            tracks = songsForScreen,
            isSearching = isSearching,
            contentSearchResults = contentSearchResults,
            libraryUiState = libraryUiState,
            contentPadding = contentPadding,
            onRequestPermission = onRequestPermission,
            onRescan = onRescan,
            onPlay = onPlay,
            onPlayContentSearchResult = onPlayContentSearchResult,
            onMore = onMore
        )
        LibraryTab.ALBUMS -> AlbumsScreen(
            albums = albums,
            tracks = tracks,
            contentPadding = contentPadding,
            onPlayAlbum = onPlayAlbum,
            onAddTracksToQueue = onAddTracksToQueue,
            onMore = onMore
        )
        LibraryTab.PLAYLISTS -> FavoritesScreen(
            favoriteTracks = favoriteTracks,
            contentPadding = contentPadding,
            onPlay = onPlay,
            onPlayAll = { onPlayAlbum(favoriteTracks, favoriteTracks.firstOrNull()) },
            onAddAllToQueue = { onAddTracksToQueue(favoriteTracks) },
            onMore = onMore
        )
        LibraryTab.LOCAL_PLAYLISTS -> LocalPlaylistsScreen(
            playlists = localPlaylists,
            allTracks = tracks,
            contentPadding = contentPadding,
            onPlayPlaylist = onPlayPlaylist,
            onCreatePlaylist = onCreatePlaylist,
            onRenamePlaylist = onRenamePlaylist,
            onDeletePlaylist = onDeletePlaylist,
            onPlayTrack = onPlay,
            onRemoveTrackFromPlaylist = onRemoveTrackFromPlaylist,
            onReorderTracks = onReorderTracks,
            onMore = onMore
        )
        LibraryTab.FOLDERS -> FolderScreen(
            tracks = deviceTracks,
            contentPadding = contentPadding,
            onPlayFolder = onPlayAlbum,
            onAddTracksToQueue = onAddTracksToQueue,
            onMore = onMore
        )
        LibraryTab.ARTISTS -> ArtistScreen(
            tracks = deviceTracks,
            contentPadding = contentPadding,
            onPlayArtist = onPlayAlbum,
            onAddTracksToQueue = onAddTracksToQueue,
            onMore = onMore
        )
    }
    }
}
