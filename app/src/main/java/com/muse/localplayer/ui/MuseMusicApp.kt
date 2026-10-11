package com.muse.localplayer.ui

import com.muse.localplayer.core.T
import com.muse.localplayer.ui.theme.MuseMotion
import com.muse.localplayer.ui.theme.motionMillis
import com.muse.localplayer.ui.theme.musePressScale
import com.muse.localplayer.ui.theme.rememberMuseInteraction
import com.muse.localplayer.ui.theme.rememberReducedMotion
import com.muse.localplayer.ui.theme.rememberSmoothedFloat
import com.muse.localplayer.ui.theme.rememberSwitchProgress

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.muse.localplayer.BuildConfig
import com.muse.localplayer.R
import com.muse.localplayer.data.Album
import com.muse.localplayer.data.ContentSearchResult
import com.muse.localplayer.data.FeaturedPackMetadata
import com.muse.localplayer.data.FeaturedTopicHealth
import com.muse.localplayer.data.FeaturedTopicProgress
import com.muse.localplayer.data.FeaturedAudioPack
import com.muse.localplayer.data.FeaturedChapter
import com.muse.localplayer.data.ChapterPlaybackState
import com.muse.localplayer.data.chapterPlaybackState
import com.muse.localplayer.data.FeaturedTrackProgram
import com.muse.localplayer.data.LibraryTab
import com.muse.localplayer.data.LocalPlaylist
import com.muse.localplayer.data.LyricLine
import com.muse.localplayer.data.Track
import com.muse.localplayer.data.TrackSource
import com.muse.localplayer.playback.EqualizerState
import com.muse.localplayer.playback.LibraryUiState
import com.muse.localplayer.playback.PlayerFeedbackAction
import com.muse.localplayer.playback.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuseMusicApp(
    viewModel: PlayerViewModel,
    audioPermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    onRequestAudioPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onOpenAudioEffects: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val featuredTracks by viewModel.featuredTracks.collectAsStateWithLifecycle()
    val featuredTopics by viewModel.featuredTopics.collectAsStateWithLifecycle()
    val activeFeaturedTopicId by viewModel.activeFeaturedTopicId.collectAsStateWithLifecycle()
    val featuredTopicProgresses by viewModel.featuredTopicProgresses.collectAsStateWithLifecycle()
    val featuredJourney by viewModel.featuredJourney.collectAsStateWithLifecycle()
    val featuredPackMetadata by viewModel.featuredPackMetadata.collectAsStateWithLifecycle()
    val featuredPrograms by viewModel.featuredPrograms.collectAsStateWithLifecycle()
    val libraryUiState by viewModel.libraryUiState.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val shuffleEnabled by viewModel.shuffleEnabled.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val mixingPlaybackEnabled by viewModel.mixingPlaybackEnabled.collectAsStateWithLifecycle()
    val fadeTransitionsEnabled by viewModel.fadeTransitionsEnabled.collectAsStateWithLifecycle()
    val fadeDurationMs by viewModel.fadeDurationMs.collectAsStateWithLifecycle()
    val lyricsOffsetMs by viewModel.lyricsOffsetMs.collectAsStateWithLifecycle()
    val lyricsFontScale by viewModel.lyricsFontScale.collectAsStateWithLifecycle()
    val playbackHistory by viewModel.playbackHistory.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarkItems.collectAsStateWithLifecycle()
    val recentlyAdded by viewModel.recentlyAdded.collectAsStateWithLifecycle()
    val sleepTimerRemainingMs by viewModel.sleepTimerRemainingMs.collectAsStateWithLifecycle()
    val sleepAfterCurrentTrackId by viewModel.sleepAfterCurrentTrackId.collectAsStateWithLifecycle()
    val loopSegment by viewModel.loopSegment.collectAsStateWithLifecycle()
    val playerMessage by viewModel.playerMessage.collectAsStateWithLifecycle()
    val localPlaylists by viewModel.localPlaylists.collectAsStateWithLifecycle()
    val equalizerState by viewModel.equalizerState.collectAsStateWithLifecycle()
    val tagSecurityRequest by viewModel.tagSecurityRequest.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // 标签写回被系统拦截时，用返回的 IntentSender 申请一次性授权，成功后自动重试。
    val tagPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.retryPendingTagWrite()
        } else {
            viewModel.dismissTagSecurityRequest()
        }
    }
    LaunchedEffect(tagSecurityRequest) {
        tagSecurityRequest?.let { request ->
            runCatching {
                tagPermissionLauncher.launch(IntentSenderRequest.Builder(request.intentSender).build())
            }.onFailure { viewModel.dismissTagSecurityRequest() }
        }
    }
    var selectedTab by remember { mutableStateOf(LibraryTab.HOME) }
    var searchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var appliedSearchText by remember { mutableStateOf("") }
    var contentSearchResults by remember { mutableStateOf<List<ContentSearchResult>>(emptyList()) }
    var playerOpen by remember { mutableStateOf(false) }
    var queueOpen by remember { mutableStateOf(false) }
    var aboutOpen by remember { mutableStateOf(false) }
    var featuredDetailOpen by remember { mutableStateOf(false) }
    var actionTrack by remember { mutableStateOf<Track?>(null) }
    var audioEffectsOpen by remember { mutableStateOf(false) }
    var tagEditTrack by remember { mutableStateOf<Track?>(null) }
    var addToPlaylistTrack by remember { mutableStateOf<Track?>(null) }
    var overflowMenuOpen by remember { mutableStateOf(false) }
    // 首次启动自动展示一次新手引导；看完后写入本机标记，之后可从「更多」或「关于」回看
    val onboardingContext = androidx.compose.ui.platform.LocalContext.current
    val onboardingPrefs = remember(onboardingContext) {
        onboardingContext.getSharedPreferences("muse_onboarding", android.content.Context.MODE_PRIVATE)
    }
    var showGuide by remember { mutableStateOf(!onboardingPrefs.getBoolean("guide_seen", false)) }
    // 系统返回层级：先退出搜索，再回到首页标签；已回到首页时才交给系统退出应用
    BackHandler(enabled = searchMode || selectedTab != LibraryTab.HOME) {
        if (searchMode) {
            searchMode = false
            searchText = ""
            appliedSearchText = ""
        } else {
            selectedTab = LibraryTab.HOME
        }
    }
    LaunchedEffect(searchText) {
        if (searchText.isBlank()) {
            appliedSearchText = ""
        } else {
            delay(160)
            appliedSearchText = searchText
        }
    }
    val filteredTracks = remember(tracks, appliedSearchText) {
        val query = appliedSearchText.trim()
        if (query.isBlank()) tracks else tracks.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
        }
    }
    LaunchedEffect(appliedSearchText, tracks, featuredTracks, bookmarks) {
        contentSearchResults = if (appliedSearchText.isBlank()) emptyList() else viewModel.searchContent(appliedSearchText)
    }
    LaunchedEffect(playerMessage) {
        playerMessage?.let { feedback ->
            val result = snackbarHostState.showSnackbar(
                message = feedback.text,
                actionLabel = feedback.actionLabel
            )
            viewModel.dismissPlayerMessage()
            if (result == SnackbarResult.ActionPerformed) {
                when (feedback.action) {
                    PlayerFeedbackAction.RETRY_FAILED_TRACK -> viewModel.retryLastFailedTrack()
                    PlayerFeedbackAction.RESUME_PLAYBACK -> viewModel.resumePlaybackFromInterruption()
                    null -> Unit
                }
            }
        }
    }
    // 顶部库分类：与 LibraryTab 一一对应，作为唯一的导航入口（Musicolet 模式）。
    val libraryTabs = remember {
        listOf(
            LibraryTab.HOME,
            LibraryTab.SONGS,
            LibraryTab.ALBUMS,
            LibraryTab.ARTISTS,
            LibraryTab.FOLDERS,
            LibraryTab.LOCAL_PLAYLISTS,
            LibraryTab.PLAYLISTS
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (searchMode) {
                SearchTopBar(
                    query = searchText,
                    onQueryChange = { searchText = it },
                    onClose = {
                        searchMode = false
                        searchText = ""
                        appliedSearchText = ""
                    }
                )
            } else {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("Muse", style = MaterialTheme.typography.titleLarge) },
                        actions = {
                            IconButton(onClick = {
                                selectedTab = LibraryTab.SONGS
                                searchMode = true
                            }) {
                                Icon(Icons.Default.Search, contentDescription = T("搜索音乐"))
                            }
                            // 非导航项统一收纳进溢出菜单，避免与库分类争抢导航位。
                            Box {
                                IconButton(onClick = { overflowMenuOpen = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = T("更多选项"))
                                }
                                DropdownMenu(
                                    expanded = overflowMenuOpen,
                                    onDismissRequest = { overflowMenuOpen = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(T("音效与均衡器")) },
                                        leadingIcon = { Icon(Icons.Default.MusicNote, null) },
                                        onClick = {
                                            overflowMenuOpen = false
                                            audioEffectsOpen = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(T("重新扫描设备")) },
                                        leadingIcon = { Icon(Icons.Default.Folder, null) },
                                        onClick = {
                                            overflowMenuOpen = false
                                            onRequestAudioPermission()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(T("设置")) },
                                        leadingIcon = { Icon(Icons.Default.Settings, null) },
                                        onClick = {
                                            overflowMenuOpen = false
                                            onOpenAppSettings()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(T("新手引导")) },
                                        leadingIcon = { Icon(Icons.Default.LibraryMusic, null) },
                                        onClick = {
                                            overflowMenuOpen = false
                                            showGuide = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(T("关于与许可")) },
                                        leadingIcon = { Icon(Icons.Default.Info, null) },
                                        onClick = {
                                            overflowMenuOpen = false
                                            aboutOpen = true
                                        }
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    ScrollableTabRow(
                        selectedTabIndex = libraryTabs.indexOf(selectedTab).coerceAtLeast(0),
                        edgePadding = 12.dp,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        libraryTabs.forEach { tab ->
                            Tab(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                text = { Text(tab.label) }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = currentTrack != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                currentTrack?.let { track ->
                    MiniPlayerWithProgress(
                        viewModel = viewModel,
                        track = track,
                        isPlaying = isPlaying,
                        onOpen = { playerOpen = true },
                        onOpenQueue = { queueOpen = true }
                    )
                }
            }
        }
    ) { paddingValues ->
        LibraryContent(
            selectedTab = selectedTab,
            tracks = tracks,
            featuredTracks = featuredTracks,
            featuredTopics = featuredTopics,
            activeFeaturedTopicId = activeFeaturedTopicId,
            featuredTopicProgresses = featuredTopicProgresses,
            featuredJourney = featuredJourney,
            featuredMetadata = featuredPackMetadata,
            visibleTracks = filteredTracks,
            contentSearchResults = contentSearchResults,
            isSearching = searchMode && appliedSearchText.isNotBlank(),
            favoriteIds = favoriteIds,
            libraryUiState = libraryUiState,
            contentPadding = paddingValues,
            audioPermissionGranted = audioPermissionGranted,
            notificationPermissionGranted = notificationPermissionGranted,
            playbackHistory = playbackHistory,
            bookmarks = bookmarks,
            recentlyAdded = recentlyAdded,
            sleepTimerRemainingMs = sleepTimerRemainingMs,
            onSetSleepTimer = viewModel::setSleepTimer,
            onCancelSleepTimer = viewModel::cancelSleepTimer,
            onClearPlaybackHistory = viewModel::clearPlaybackHistory,
            onPlayBookmark = viewModel::playBookmark,
            onRemoveBookmark = viewModel::removeBookmark,
            onRequestPermission = onRequestAudioPermission,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRescan = viewModel::reloadLibrary,
            onPlay = viewModel::play,
            onPlayContentSearchResult = viewModel::playSearchResult,
            onContinueFeaturedJourney = viewModel::continueFeaturedJourney,
            onContinueFeaturedTopicJourney = viewModel::continueFeaturedTopicJourney,
            onRestartFeaturedJourney = viewModel::restartFeaturedJourney,
            onPlayFeaturedTracks = viewModel::playFeaturedTracks,
            onSelectFeaturedTopic = viewModel::selectFeaturedTopic,
            onAddTracksToQueue = { tracksToAdd ->
                val addedCount = viewModel.addTracksToQueue(tracksToAdd)
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (addedCount > 0) T("已加入 %1\$s 首到播放队列").format(addedCount) else T("这些歌曲已在播放队列中")
                    )
                }
            },
            onPlayAlbum = viewModel::playAlbum,
            onShowSongs = { selectedTab = LibraryTab.SONGS },
            onMore = { actionTrack = it },
            onOpenFeaturedDetail = { featuredDetailOpen = true },
            localPlaylists = localPlaylists,
            onPlayPlaylist = viewModel::playPlaylist,
            onCreatePlaylist = viewModel::createPlaylist,
            onRenamePlaylist = viewModel::renamePlaylist,
            onDeletePlaylist = viewModel::deletePlaylist,
            onRemoveTrackFromPlaylist = viewModel::removeTrackFromPlaylist,
            onReorderTracks = viewModel::reorderPlaylistTracks
        )
    }

    actionTrack?.let { track ->
        TrackActionsSheet(
            track = track,
            isFavorite = track.id in favoriteIds,
            onDismiss = { actionTrack = null },
            onPlay = {
                viewModel.play(track)
                actionTrack = null
            },
            onAddToQueue = {
                val addedCount = viewModel.addToQueue(track)
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (addedCount > 0) T("已加入播放队列") else T("该歌曲已在播放队列中")
                    )
                }
                actionTrack = null
            },
            onPlayNext = {
                viewModel.playNext(track)
                scope.launch { snackbarHostState.showSnackbar(T("已安排为下一首播放")) }
                actionTrack = null
            },
            onToggleFavorite = {
                val wasFavorite = track.id in favoriteIds
                viewModel.toggleFavorite(track)
                scope.launch { snackbarHostState.showSnackbar(if (wasFavorite) T("已取消收藏") else T("已加入收藏")) }
                actionTrack = null
            },
            onAddToPlaylist = {
                addToPlaylistTrack = track
                actionTrack = null
            },
            onEditTags = {
                tagEditTrack = track
                actionTrack = null
            }
        )
    }

    if (audioEffectsOpen) {
        AudioEffectsSheet(
            state = equalizerState,
            onDismiss = { audioEffectsOpen = false },
            onToggleEnabled = viewModel::setEqualizerEnabled,
            onSelectPreset = viewModel::setEqualizerPreset,
            onBandLevelChange = viewModel::setEqualizerBandLevel,
            onBassStrengthChange = viewModel::setBassBoostStrength,
            onOpenSystemPanel = onOpenAudioEffects
        )
    }

    tagEditTrack?.let { track ->
        TagEditSheet(
            track = track,
            onDismiss = { tagEditTrack = null },
            onSave = { title, artist, album ->
                viewModel.updateTrackTags(track, title, artist, album)
                tagEditTrack = null
            }
        )
    }

    addToPlaylistTrack?.let { track ->
        AddToPlaylistSheet(
            track = track,
            playlists = localPlaylists,
            onDismiss = { addToPlaylistTrack = null },
            onCreatePlaylist = viewModel::createPlaylist,
            onAddToPlaylist = { playlist ->
                val added = viewModel.addTrackToPlaylist(playlist.id, track)
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (added) T("已添加到《%1\$s》").format(playlist.name) else T("《%1\$s》已包含该歌曲").format(playlist.name)
                    )
                }
            }
        )
    }

    val openTrack = currentTrack
    if (playerOpen && openTrack != null) {
        PlayerSheetWithProgress(
            viewModel = viewModel,
            track = openTrack,
            isPlaying = isPlaying,
            repeatMode = repeatMode,
            shuffleEnabled = shuffleEnabled,
            playbackSpeed = playbackSpeed,
            mixingPlaybackEnabled = mixingPlaybackEnabled,
            fadeTransitionsEnabled = fadeTransitionsEnabled,
            fadeDurationMs = fadeDurationMs,
            lyricsOffsetMs = lyricsOffsetMs,
            lyricsFontScale = lyricsFontScale,
            sleepTimerRemainingMs = sleepTimerRemainingMs,
            sleepAfterCurrentTrackEnabled = sleepAfterCurrentTrackId == openTrack.id,
            loopSegment = loopSegment,
            isFavorite = openTrack.id in favoriteIds,
            featuredJourney = featuredJourney,
            featuredMetadata = featuredPackMetadata,
            onDismiss = { playerOpen = false },
            onToggle = viewModel::togglePlayback,
            onNext = viewModel::skipNext,
            onPrevious = viewModel::skipPrevious,
            onSeek = viewModel::seekToProgress,
            onSetPlaybackStrategy = { strategy ->
                viewModel.setPlaybackStrategy(strategy.repeatMode, strategy.shuffleEnabled)
            },
            onSetPlaybackSpeed = viewModel::setPlaybackSpeed,
            onSetMixingPlayback = viewModel::setMixingPlaybackEnabled,
            onSetFadeTransitions = viewModel::setFadeTransitionsEnabled,
            onSetFadeDuration = viewModel::setFadeDurationMs,
            onNudgeLyricsOffset = viewModel::nudgeLyricsOffset,
            onResetLyricsOffset = viewModel::resetLyricsOffset,
            onSetLyricsFontScale = viewModel::setLyricsFontScale,
            onSetSleepTimer = viewModel::setSleepTimer,
            onCancelSleepTimer = viewModel::cancelSleepTimer,
            onSetSleepAfterCurrentTrack = viewModel::setSleepAfterCurrentTrack,
            onSetLoopStart = viewModel::setLoopStart,
            onSetLoopEnd = viewModel::setLoopEnd,
            onClearLoop = viewModel::clearLoopSegment,
            onAddBookmark = viewModel::addBookmark,
            onToggleFavorite = { viewModel.toggleFavorite(openTrack) },
            onOpenQueue = {
                playerOpen = false
                queueOpen = true
            },
            onOpenFeaturedDetail = {
                playerOpen = false
                featuredDetailOpen = true
            }
        )
    }

    if (queueOpen) {
        QueueSheet(
            queue = queue,
            currentTrack = currentTrack,
            onDismiss = { queueOpen = false },
            onPlay = viewModel::playQueueItem,
            onRemove = { index ->
                viewModel.removeFromQueue(index)?.let { removedTrack ->
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = T("已将《%1\$s》移出队列").format(removedTrack.title),
                            actionLabel = T("撤销"),
                            withDismissAction = true
                        )
                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                            if (viewModel.restoreLastRemovedQueueItem()) {
                                snackbarHostState.showSnackbar(T("已恢复到原队列位置"))
                            }
                        }
                    }
                }
            },
            onMove = viewModel::moveQueueItem,
            onRemovePlayed = {
                val removedCount = viewModel.removePlayedQueueItems()
                if (removedCount > 0) {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = T("已移除 %1\$s 首已播歌曲").format(removedCount),
                            actionLabel = T("撤销"),
                            withDismissAction = true
                        )
                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                            if (viewModel.restoreLastTrimmedQueueItems()) {
                                snackbarHostState.showSnackbar(T("已恢复已播歌曲"))
                            }
                        }
                    }
                }
            },
            onClear = {
                viewModel.clearQueue()
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = T("已清空播放队列"),
                        actionLabel = T("撤销"),
                        withDismissAction = true
                    )
                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                        if (viewModel.restoreLastClearedQueue()) {
                            snackbarHostState.showSnackbar(T("已恢复播放队列"))
                        }
                    }
                }
            }
        )
    }

    if (aboutOpen) {
        AboutSheet(
            onDismiss = { aboutOpen = false },
            onOpenGuide = { aboutOpen = false; showGuide = true }
        )
    }

    if (showGuide) {
        OnboardingGuide(onFinish = { showGuide = false; onboardingPrefs.edit().putBoolean("guide_seen", true).apply() })
    }

    if (featuredDetailOpen) {
        FeaturedTopicSheet(
            metadata = featuredPackMetadata,
            tracks = featuredTracks,
            topics = featuredTopics,
            activeTopicId = activeFeaturedTopicId,
            progressByTopicId = featuredTopicProgresses,
            programsByTrackId = featuredPrograms,
            journey = featuredJourney,
            currentFeaturedTrackId = currentTrack?.takeIf(Track::isFeaturedAsset)?.id,
            onDismiss = { featuredDetailOpen = false },
            onSelectTopic = viewModel::selectFeaturedTopic,
            onPlayAll = {
                viewModel.playFeaturedTracks()
                featuredDetailOpen = false
                playerOpen = true
            },
            onContinue = {
                viewModel.continueFeaturedJourney()
                featuredDetailOpen = false
                playerOpen = true
            },
            onRestart = {
                viewModel.restartFeaturedJourney()
                featuredDetailOpen = false
                playerOpen = true
            },
            onAddAllToQueue = {
                val addedCount = viewModel.addTracksToQueue(featuredTracks)
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (addedCount > 0) T("已将 %1\$s 首专题节目加入播放队列").format(addedCount) else T("专题节目已全部在播放队列中")
                    )
                }
            },
            onPlayTrackAt = { track, positionMs ->
                viewModel.playFeaturedTrackAt(track, positionMs)
                featuredDetailOpen = false
                playerOpen = true
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(query: String, onQueryChange: (String) -> Unit, onClose: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(T("搜索专题与设备音乐")) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = if (query.isNotBlank()) {
                    { IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Close, T("清除搜索")) } }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = T("退出搜索"))
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
internal fun HomeScreen(
    featuredTracks: List<Track>,
    featuredTopics: List<FeaturedAudioPack>,
    activeFeaturedTopicId: String?,
    featuredTopicProgresses: Map<String, FeaturedTopicProgress>,
    featuredJourney: PlayerViewModel.FeaturedJourneyState,
    featuredMetadata: FeaturedPackMetadata,
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
    onContinueFeaturedJourney: () -> Unit,
    onContinueFeaturedTopicJourney: (String) -> Unit,
    onRestartFeaturedJourney: () -> Unit,
    onPlayFeaturedTracks: () -> Unit,
    onSelectFeaturedTopic: (String) -> Unit,
    onAddFeaturedTracksToQueue: () -> Unit,
    onSongsClick: () -> Unit,
    onMore: (Track) -> Unit,
    onOpenFeaturedDetail: () -> Unit
) {
    val featuredDuration = featuredTracks.sumOf { it.durationMs }
    // 首屏聚焦策展与本期曲目，其余内容默认折叠，按需展开（功能不变）。
    var showSecondarySections by remember { mutableStateOf(false) }
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column {
                Text(T("本期策展"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text(featuredMetadata.title, style = MaterialTheme.typography.displaySmall)
                Spacer(Modifier.height(8.dp))
                Text(
                    featuredMetadata.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (featuredTopics.size > 1) {
            item {
                FeaturedTopicShelf(
                    topics = featuredTopics,
                    selectedTopicId = activeFeaturedTopicId,
                    progressByTopicId = featuredTopicProgresses,
                    onSelect = onSelectFeaturedTopic
                )
            }
            val resumableTopics = featuredTopics.mapNotNull { topic ->
                val resume = featuredTopicProgresses[topic.id]?.resumeState ?: return@mapNotNull null
                val track = resume.trackId?.let { trackId -> topic.tracks.firstOrNull { it.id == trackId } } ?: return@mapNotNull null
                if (topic.id == activeFeaturedTopicId) null else TopicResumeItem(topic, track, resume.positionMs)
            }
            if (resumableTopics.isNotEmpty()) {
                item { FeaturedTopicResumeShelf(resumableTopics, onContinueFeaturedTopicJourney) }
            }
        }
        item {
            FeaturedPackageHero(
                metadata = featuredMetadata,
                artworkUri = featuredTracks.firstOrNull()?.artworkUri,
                trackCount = featuredTracks.size,
                totalDurationMs = featuredDuration,
                onPlay = if (featuredTracks.isEmpty()) null else onPlayFeaturedTracks,
                onAddToQueue = if (featuredTracks.isEmpty()) null else onAddFeaturedTracksToQueue,
                onOpenTopic = onOpenFeaturedDetail
            )
        }
        if (featuredJourney.totalCount > 0) {
            item {
                FeaturedJourneyCard(
                    journey = featuredJourney,
                    onContinue = onContinueFeaturedJourney,
                    onRestart = onRestartFeaturedJourney
                )
            }
        }
        if (featuredTracks.isEmpty()) {
            item { EmptyFeaturedAudioCard(featuredMetadata) }
        } else {
            item { SectionHeader(T("本期曲目"), T("完整节目单"), onOpenFeaturedDetail) }
            items(featuredTracks.take(HOME_PREVIEW_LIMIT), key = { it.id }) { track ->
                TrackListItem(
                    track = track,
                    isFeaturedCompleted = track.id in featuredJourney.completedTrackIds,
                    onClick = { onPlay(track) },
                    onMore = { onMore(track) }
                )
            }
            if (featuredTracks.size > HOME_PREVIEW_LIMIT) {
                item {
                    OutlinedButton(onClick = onOpenFeaturedDetail, modifier = Modifier.fillMaxWidth()) {
                        Text(T("查看全部 %1\$s 首节目与章节").format(featuredTracks.size))
                    }
                }
            }
        }
        item {
            FeaturedIdentityGuide(metadata = featuredMetadata, trackCount = featuredTracks.size)
        }
        item {
            SleepTimerCard(
                remainingMs = sleepTimerRemainingMs,
                onSetTimer = onSetSleepTimer,
                onCancelTimer = onCancelSleepTimer
            )
        }
        item {
            OutlinedButton(
                onClick = { showSecondarySections = !showSecondarySections },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (showSecondarySections) T("收起更多内容") else T("展开更多内容"))
            }
        }
        if (showSecondarySections) {
            if (bookmarks.isNotEmpty()) {
                item {
                    Text(T("继续收听"), style = MaterialTheme.typography.titleLarge)
                }
                items(bookmarks.take(HOME_PREVIEW_LIMIT), key = { "bookmark_${it.bookmark.savedAtEpochMs}" }) { item ->
                    BookmarkListItem(
                        item = item,
                        onPlay = { onPlayBookmark(item) },
                        onRemove = { onRemoveBookmark(item) }
                    )
                }
            }
            if (playbackHistory.isNotEmpty()) {
                item { SectionHeader(T("最近播放"), T("清空"), onClearPlaybackHistory) }
                items(playbackHistory.take(HOME_PREVIEW_LIMIT), key = { "history_${it.id}" }) { track ->
                    TrackListItem(track = track, onClick = { onPlay(track) }, onMore = { onMore(track) })
                }
            }
            if (recentlyAdded.isNotEmpty()) {
                item { SectionHeader(T("最近加入"), T("设备资料库"), onSongsClick) }
                items(recentlyAdded, key = { "recent_${it.id}" }) { track ->
                    TrackListItem(track = track, onClick = { onPlay(track) }, onMore = { onMore(track) })
                }
            }
        }
        item {
            PermissionStatusCard(
                audioPermissionGranted = audioPermissionGranted,
                notificationPermissionGranted = notificationPermissionGranted,
                onRequestAudioPermission = onRequestPermission,
                onRequestNotificationPermission = onRequestNotificationPermission
            )
        }
        item { SectionHeader(T("设备音乐"), T("打开资料库"), onSongsClick) }
        item { LibraryStatusCard(libraryUiState = libraryUiState, onRequestPermission = onRequestPermission, onRescan = onRescan) }
    }
}

@Composable
private fun BookmarkListItem(
    item: PlayerViewModel.BookmarkItem,
    onPlay: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onPlay,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumArt(Modifier.size(48.dp), item.track.artworkUri, item.track.title, ArtEmphasis.Secondary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.track.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    T("从 %1\$s 继续 · %2\$s").format(formatTime(item.bookmark.positionMs), item.track.artist),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TextButton(onClick = onRemove) { Text(T("移除")) }
        }
    }
}

private data class TopicResumeItem(
    val topic: FeaturedAudioPack,
    val track: Track,
    val positionMs: Long
)

@Composable
private fun FeaturedTopicResumeShelf(
    items: List<TopicResumeItem>,
    onContinue: (String) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(T("跨专题继续"), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            Text(T("%1\$s 期未听完").format(items.size), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(5.dp))
        Text(T("切换专题后，会在原来的断点继续完整节目单。"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items, key = { "resume_${it.topic.id}" }) { item ->
                Card(
                    onClick = { onContinue(item.topic.id) },
                    modifier = Modifier.width(248.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.topic.metadata.title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(Modifier.height(7.dp))
                        Text(item.track.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(5.dp))
                        Text(
                            T("从 %1\$s 继续").format(formatTime(item.positionMs)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.78f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedTopicShelf(
    topics: List<FeaturedAudioPack>,
    selectedTopicId: String?,
    progressByTopicId: Map<String, FeaturedTopicProgress>,
    onSelect: (String) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(T("专题目录"), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            Text(T("%1\$s 期").format(topics.size), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            T("每一期拥有独立的曲目、节目单和收听进度。"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(topics, key = FeaturedAudioPack::id) { topic ->
                val completed = progressByTopicId[topic.id]?.completedTrackIds.orEmpty()
                    .intersect(topic.tracks.map(Track::id).toSet())
                    .size
                val selected = topic.id == selectedTopicId
                Card(
                    onClick = { onSelect(topic.id) },
                    modifier = Modifier.width(224.dp).height(148.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = topic.tracks.firstOrNull()?.artworkUri ?: R.drawable.muse_featured_hero,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().alpha(if (selected) 0.9f else 0.58f),
                            contentScale = ContentScale.Crop
                        )
                        Box(modifier = Modifier.fillMaxSize().background(Color(0x6609122D)))
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = Color.White.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    if (selected) T("当前浏览") else topic.metadata.eyebrow,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                            Column {
                                Text(topic.metadata.title, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    T("%1\$s 首 · 已完成 %2\$s/%1\$s").format(topic.tracks.size, completed),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.88f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedPackageHero(
    metadata: FeaturedPackMetadata,
    artworkUri: android.net.Uri?,
    trackCount: Int,
    totalDurationMs: Long,
    onPlay: (() -> Unit)?,
    onAddToQueue: (() -> Unit)?,
    onOpenTopic: () -> Unit
) {
    Card(
        onClick = onOpenTopic,
        modifier = Modifier.fillMaxWidth().height(258.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(artworkUri ?: R.drawable.muse_featured_hero)
                    .size(640)
                    .memoryCacheKey("featured_hero_${artworkUri ?: "default"}")
                    .diskCacheKey("featured_hero_${artworkUri ?: "default"}")
                    .crossfade(false)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.fillMaxSize().background(Color(0x33000000)).padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                    ) {
                        Text(metadata.eyebrow, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                    metadata.editionLabel?.let { edition ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = Color(0xD926335C)
                        ) {
                            Text(edition, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                Column {
                    Text(metadata.title, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (trackCount == 0) T("等待你放入第一首音频") else T("%1\$s 首 · %2\$s").format(trackCount, formatTime(totalDurationMs)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.86f)
                    )
                    if (onPlay != null) {
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilledTonalButton(onClick = onPlay) {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(metadata.playLabel)
                            }
                            OutlinedButton(onClick = onOpenTopic) {
                                Icon(Icons.Default.LibraryMusic, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(T("节目单"))
                            }
                        }
                        if (onAddToQueue != null) {
                            TextButton(onClick = onAddToQueue) { Text(T("加入完整播放队列")) }
                        }
                    } else {
                        Spacer(Modifier.height(12.dp))
                        FilledTonalButton(onClick = onOpenTopic) { Text(T("查看专题放入方式")) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedIdentityGuide(metadata: FeaturedPackMetadata, trackCount: Int) {
    val identity = metadata.identityLabel ?: T("Muse 离线专题")
    val guide = metadata.listeningGuide ?: when {
        trackCount == 0 -> T("内置音频就绪后，回到这里开始你的专题收听。")
        else -> T("建议从头播放以保留内容编排；播放页可查看章节、歌词和节目笔记，并随时保存书签。")
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.72f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(T("本期导览"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
            Spacer(Modifier.height(6.dp))
            Text(identity, style = MaterialTheme.typography.titleLarge)
            metadata.editionLabel?.let { edition ->
                Spacer(Modifier.height(3.dp))
                Text(edition, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.72f))
            }
            Spacer(Modifier.height(10.dp))
            Text(guide, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.86f))
        }
    }
}

@Composable
private fun FeaturedJourneyCard(
    journey: PlayerViewModel.FeaturedJourneyState,
    onContinue: () -> Unit,
    onRestart: () -> Unit
) {
    val progress = if (journey.totalCount > 0) {
        journey.completedCount.toFloat() / journey.totalCount.toFloat()
    } else 0f
    val resumeTrack = journey.resumeTrack
    val nextTrack = journey.nextTrack
    Card(
        modifier = Modifier.fillMaxWidth().height(244.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = R.drawable.muse_journey_deck,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x20000000), Color(0xBB07102A))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Text(
                            if (journey.isComplete) T("本期完成") else T("收听旅程"),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Text(
                            "${journey.completedCount}/${journey.totalCount}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
                Column {
                    Text(
                        when {
                            journey.isComplete -> T("这一期，已经听完")
                            resumeTrack != null -> T("从上次停下的地方回来")
                            journey.completedCount == 0 -> T("从第一首，走进这一期")
                            else -> T("下一段内容，正在等你")
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        when {
                            journey.isComplete -> T("从头再听会开启一段新的旅程，原有书签仍会保留。")
                            resumeTrack != null -> T("%1\$s · %2\$s 处继续").format(resumeTrack.title, formatTime(journey.resumePositionMs))
                            nextTrack != null -> T("下一首 · %1\$s · %2\$s").format(nextTrack.title, nextTrack.artist)
                            else -> T("从完整的专题顺序开始收听。")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.24f)
                    )
                    Spacer(Modifier.height(12.dp))
                    FilledTonalButton(
                        onClick = if (journey.isComplete) onRestart else onContinue,
                        colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.White.copy(alpha = 0.92f),
                            contentColor = Color(0xFF121A35)
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when {
                                journey.isComplete -> T("从头再听")
                                resumeTrack != null -> T("继续收听")
                                journey.completedCount == 0 -> T("开启旅程")
                                else -> T("继续下一首")
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeaturedTopicSheet(
    metadata: FeaturedPackMetadata,
    tracks: List<Track>,
    topics: List<FeaturedAudioPack>,
    activeTopicId: String?,
    progressByTopicId: Map<String, FeaturedTopicProgress>,
    programsByTrackId: Map<Long, FeaturedTrackProgram>,
    journey: PlayerViewModel.FeaturedJourneyState,
    currentFeaturedTrackId: Long?,
    onDismiss: () -> Unit,
    onSelectTopic: (String) -> Unit,
    onPlayAll: () -> Unit,
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onAddAllToQueue: () -> Unit,
    onPlayTrackAt: (Track, Long) -> Unit
) {
    val totalDurationMs = remember(tracks) { tracks.sumOf(Track::durationMs) }
    val chapterCount = remember(programsByTrackId) { programsByTrackId.values.sumOf { it.chapters.size } }
    val hasPrograms = chapterCount > 0 || programsByTrackId.values.any(FeaturedTrackProgram::hasContent)
    val resumeTrack = journey.resumeTrack
    val nextTrack = journey.nextTrack
    val topicHealth = topics.firstOrNull { it.id == activeTopicId }?.health
        ?: FeaturedTopicHealth(trackCount = tracks.size, resolvedDurationCount = tracks.count { it.durationMs > 0L })
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth().heightIn(max = 760.dp)) {
            AsyncImage(
                model = R.drawable.muse_journey_deck,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xF0081028), Color(0xFC10182E))))
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = MaterialTheme.shapes.small, color = Color.White.copy(alpha = 0.14f)) {
                            Text(
                                metadata.eyebrow,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = onDismiss) { Text(T("收起")) }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(T("专题全览"), style = MaterialTheme.typography.labelLarge, color = Color(0xFFAEC6FF))
                    Spacer(Modifier.height(4.dp))
                    Text(metadata.title, style = MaterialTheme.typography.displaySmall, color = Color.White)
                    metadata.identityLabel?.let {
                        Spacer(Modifier.height(6.dp))
                        Text(it, style = MaterialTheme.typography.titleMedium, color = Color(0xFFE0E9FF))
                    }
                    metadata.editionLabel?.let {
                        Spacer(Modifier.height(5.dp))
                        Text(it, style = MaterialTheme.typography.labelLarge, color = Color(0xFFAEC6FF))
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(metadata.description, style = MaterialTheme.typography.bodyLarge, color = Color(0xFFD6E1F6))
                }
                if (topics.size > 1) {
                    item {
                        FeaturedTopicQuickSwitch(
                            topics = topics,
                            selectedTopicId = activeTopicId,
                            progressByTopicId = progressByTopicId,
                            onSelect = onSelectTopic
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FeaturedTopicStat(T("曲目"), tracks.size.toString(), Modifier.weight(1f))
                        FeaturedTopicStat(T("时长"), if (totalDurationMs > 0L) formatTime(totalDurationMs) else T("待读取"), Modifier.weight(1f))
                        FeaturedTopicStat(T("章节"), chapterCount.toString(), Modifier.weight(1f))
                    }
                }
                item { FeaturedTopicProductionSummary(topicHealth) }
                if (tracks.isEmpty()) {
                    item { EmptyFeaturedAudioCard(metadata) }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    if (journey.isComplete) T("这一期已完整收听") else T("收听进度 · %1\$s/%2\$s").format(journey.completedCount, journey.totalCount),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    when {
                                        journey.isComplete -> T("从头再听会开启新的收听顺序，原有书签仍会保留。")
                                        resumeTrack != null -> T("将在《%1\$s》的 %2\$s 继续。").format(resumeTrack.title, formatTime(journey.resumePositionMs))
                                        nextTrack != null -> T("下一首是《%1\$s》，也可以在下方节目单中任意进入。").format(nextTrack.title)
                                        else -> T("从第一首开始，按专题顺序连续收听。")
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFD6E1F6)
                                )
                                Spacer(Modifier.height(12.dp))
                                androidx.compose.material3.LinearProgressIndicator(
                                    progress = { if (journey.totalCount > 0) journey.completedCount.toFloat() / journey.totalCount else 0f },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFC6D8FF),
                                    trackColor = Color.White.copy(alpha = 0.16f)
                                )
                                Spacer(Modifier.height(14.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    FilledTonalButton(onClick = onPlayAll) {
                                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(T("从头播放"))
                                    }
                                    OutlinedButton(onClick = if (journey.isComplete) onRestart else onContinue) {
                                        Icon(Icons.Default.Replay, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(if (journey.isComplete) T("重新开始") else T("继续"))
                                    }
                                }
                                TextButton(onClick = onAddAllToQueue, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.AutoMirrored.Filled.QueueMusic, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(T("加入完整专题队列"))
                                }
                            }
                        }
                    }
                    item {
                        Column {
                            Text(T("节目单"), style = MaterialTheme.typography.titleLarge, color = Color.White)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (hasPrograms) T(T("点击曲目开始播放；已配置的章节可直接进入对应段落。")) else T(T("这一期按曲目顺序编排，点选任一首都可开始收听。")),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB6C6E9)
                            )
                        }
                    }
                    itemsIndexed(tracks, key = { _, track -> track.id }) { index, track ->
                        FeaturedProgramListItem(
                            index = index + 1,
                            track = track,
                            program = programsByTrackId[track.id],
                            completed = track.id in journey.completedTrackIds,
                            isCurrent = currentFeaturedTrackId == track.id,
                            onPlay = { onPlayTrackAt(track, 0L) },
                            onPlayChapter = { positionMs -> onPlayTrackAt(track, positionMs) }
                        )
                    }
                    if (metadata.listeningGuide != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.extraLarge,
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF24365D))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(T("策展人提示"), style = MaterialTheme.typography.labelLarge, color = Color(0xFFC6D8FF))
                                    Spacer(Modifier.height(7.dp))
                                    Text(metadata.listeningGuide, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedTopicProductionSummary(health: FeaturedTopicHealth) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.09f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFC6D8FF), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(T("内容摘要"), style = MaterialTheme.typography.titleSmall, color = Color.White)
            }
            Spacer(Modifier.height(7.dp))
            Text(health.contentSummary, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFD6E1F6))
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                FeaturedTopicHealthToken(if (health.hasPackConfiguration) T(T("已编排章节")) else T(T("纯音频收听")))
                FeaturedTopicHealthToken(if (health.hasExplicitStableId) T("已固定专题 ID") else T("使用目录 ID"))
                FeaturedTopicHealthToken(if (health.hasCustomCover) T("已配置专题封面") else T("使用 Muse 默认封面"))
                if (health.noteCount > 0) FeaturedTopicHealthToken(T("%1\$s 条节目笔记").format(health.noteCount))
            }
        }
    }
}

@Composable
private fun FeaturedTopicHealthToken(text: String) {
    Surface(shape = MaterialTheme.shapes.small, color = Color.White.copy(alpha = 0.12f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFE0E9FF)
        )
    }
}

@Composable
private fun FeaturedTopicQuickSwitch(
    topics: List<FeaturedAudioPack>,
    selectedTopicId: String?,
    progressByTopicId: Map<String, FeaturedTopicProgress>,
    onSelect: (String) -> Unit
) {
    Column {
        Text(T("切换专题"), style = MaterialTheme.typography.labelLarge, color = Color(0xFFC6D8FF))
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(topics, key = FeaturedAudioPack::id) { topic ->
                val completed = progressByTopicId[topic.id]?.completedTrackIds.orEmpty()
                    .intersect(topic.tracks.map(Track::id).toSet())
                    .size
                AssistChip(
                    onClick = { onSelect(topic.id) },
                    label = {
                        Text(
                            "${topic.metadata.title} · $completed/${topic.tracks.size}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = if (topic.id == selectedTopicId) {
                        { Icon(Icons.Default.CheckCircle, contentDescription = T("当前专题"), modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (topic.id == selectedTopicId) Color(0xFF38568F) else Color.White.copy(alpha = 0.12f),
                        labelColor = Color.White,
                        leadingIconContentColor = Color(0xFFC6D8FF)
                    )
                )
            }
        }
    }
}

@Composable
private fun FeaturedTopicStat(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFFB6C6E9))
            Spacer(Modifier.height(3.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun FeaturedProgramListItem(
    index: Int,
    track: Track,
    program: FeaturedTrackProgram?,
    completed: Boolean,
    isCurrent: Boolean,
    onPlay: () -> Unit,
    onPlayChapter: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCurrent -> Color(0xFF2A4478)
                completed -> Color.White.copy(alpha = 0.14f)
                else -> Color.White.copy(alpha = 0.09f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).clickable(onClick = onPlay),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.15f)) {
                    Box(contentAlignment = Alignment.Center) {
                        if (completed) {
                            Icon(Icons.Default.CheckCircle, contentDescription = T("已完成"), tint = Color(0xFFC6D8FF), modifier = Modifier.size(22.dp))
                        } else {
                            Text(index.toString(), style = MaterialTheme.typography.labelLarge, color = Color.White)
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isCurrent) T("正在播放") else if (completed) T("已完成") else T("专题曲目 %1\$s").format(index.toString().padStart(2, '0')),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCurrent) Color(0xFFC6D8FF) else Color(0xFFB6C6E9)
                    )
                    Text(track.title, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        buildString {
                            append(track.artist)
                            if (track.durationMs > 0L) append(" · ${formatTime(track.durationMs)}")
                            program?.chapters?.takeIf { it.isNotEmpty() }?.let { append(T(" · %1\$s 个章节").format(it.size)) }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD6E1F6),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(Icons.Default.PlayArrow, contentDescription = T("播放 %1\$s").format(track.title), tint = Color.White)
            }
            val chapters = program?.chapters.orEmpty()
            if (chapters.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(Modifier.height(4.dp))
                chapters.take(3).forEach { chapter ->
                    TextButton(onClick = { onPlayChapter(chapter.timestampMs) }, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "${formatTime(chapter.timestampMs)}  ${chapter.title}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Start
                        )
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                    }
                }
                if (chapters.size > 3) {
                    Text(
                        T("另有 %1\$s 个章节可在播放页查看").format(chapters.size - 3),
                        modifier = Modifier.padding(start = 12.dp, top = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFB6C6E9)
                    )
                }
            }
            program?.notes?.firstOrNull()?.let { note ->
                Spacer(Modifier.height(7.dp))
                Text(
                    note,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFB6C6E9),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyFeaturedAudioCard(metadata: FeaturedPackMetadata) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(T("%1\$s 尚未加入音频").format(metadata.title), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(T(T("这一期暂时没有可播放的音频。内置专题音频会随应用一起提供。")), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SleepTimerCard(
    remainingMs: Long,
    onSetTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit
) {
    var customDialogVisible by remember { mutableStateOf(false) }
    var customMinutesText by remember { mutableStateOf("") }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(T("睡眠定时"), style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (remainingMs > 0L) T("将在 %1\$s 后平滑暂停播放").format(formatTime(remainingMs)) else T("设置后先淡出音量再暂停，适合睡前收听"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.78f)
                    )
                }
                if (remainingMs > 0L) TextButton(onClick = onCancelTimer) { Text(T("关闭")) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 30, 60, 90).forEach { minutes ->
                    AssistChip(
                        onClick = { onSetTimer(minutes) },
                        label = { Text(T("%1\$s 分钟").format(minutes)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f))
                    )
                }
            }
            OutlinedButton(
                onClick = {
                    customMinutesText = ""
                    customDialogVisible = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(T("自定义分钟数"))
            }
        }
    }
    if (customDialogVisible) {
        val minutes = customMinutesText.trim().toIntOrNull()
        AlertDialog(
            onDismissRequest = { customDialogVisible = false },
            title = { Text(T("自定义睡眠定时")) },
            text = {
                Column {
                    Text(T("输入 1 到 180 之间的分钟数，到点后先淡出音量再暂停。"), style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customMinutesText,
                        onValueChange = { input -> customMinutesText = input.filter { it.isDigit() }.take(3) },
                        singleLine = true,
                        label = { Text(T("分钟")) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        minutes?.let(onSetTimer)
                        customDialogVisible = false
                    },
                    enabled = minutes != null && minutes in 1..180
                ) { Text(T("开始")) }
            },
            dismissButton = {
                TextButton(onClick = { customDialogVisible = false }) { Text(T("取消")) }
            }
        )
    }
}

@Composable
private fun PermissionStatusCard(
    audioPermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    onRequestAudioPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(T("权限状态"), style = MaterialTheme.typography.titleMedium)
            PermissionStatusRow(
                icon = Icons.Default.LibraryMusic,
                title = T("设备音乐"),
                description = if (audioPermissionGranted) {
                    T("已允许 · 可以扫描并播放设备中的本地音频")
                } else {
                    T("未授权 · 设备音乐资料库暂不可用")
                },
                granted = audioPermissionGranted,
                actionLabel = T("授权音乐"),
                onRequest = onRequestAudioPermission
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            PermissionStatusRow(
                icon = Icons.Default.Notifications,
                title = T("播放通知"),
                description = if (notificationPermissionGranted) {
                    T("已允许 · 通知栏与锁屏会显示播放控制")
                } else {
                    T("未授权 · 后台播放正常，但系统播放通知不会显示")
                },
                granted = notificationPermissionGranted,
                actionLabel = T("授权通知"),
                onRequest = onRequestNotificationPermission
            )
        }
    }
}

@Composable
private fun PermissionStatusRow(
    icon: ImageVector,
    title: String,
    description: String,
    granted: Boolean,
    actionLabel: String,
    onRequest: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = if (granted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null)
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (granted) {
            Text(T("已允许"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        } else {
            TextButton(onClick = onRequest) { Text(actionLabel) }
        }
    }
}

@Composable
private fun LibraryStatusCard(libraryUiState: LibraryUiState, onRequestPermission: () -> Unit, onRescan: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LibraryMusic, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(libraryStatusTitle(libraryUiState), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(libraryStatusDescription(libraryUiState), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
            }
            when (libraryUiState) {
                LibraryUiState.PermissionRequired -> TextButton(onClick = onRequestPermission) { Text(T("授权")) }
                is LibraryUiState.Error, LibraryUiState.Empty, is LibraryUiState.Ready -> TextButton(onClick = onRescan) { Text(T("扫描")) }
                else -> Unit
            }
        }
    }
}

@Composable
private fun FeaturedTrackCard(track: Track, onPlay: (Track) -> Unit) {
    Card(
        onClick = { onPlay(track) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            AlbumArt(modifier = Modifier.size(88.dp), artworkUri = track.artworkUri, seed = track.title, emphasis = ArtEmphasis.Primary)
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(T("从这里开始"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f))
                Spacer(Modifier.height(3.dp))
                Text(track.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${track.artist} · ${track.album}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(14.dp))
                FilledTonalButton(onClick = { onPlay(track) }) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(T("播放"))
                }
            }
        }
    }
}

@Composable
internal fun SongsScreen(
    tracks: List<Track>,
    isSearching: Boolean,
    contentSearchResults: List<ContentSearchResult>,
    libraryUiState: LibraryUiState,
    contentPadding: PaddingValues,
    onRequestPermission: () -> Unit,
    onRescan: () -> Unit,
    onPlay: (Track) -> Unit,
    onPlayContentSearchResult: (ContentSearchResult) -> Unit,
    onMore: (Track) -> Unit
) {
    var sortAscending by remember { mutableStateOf(true) }
    var sortMode by remember { mutableStateOf(SongSortMode.TITLE) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val sortedTracks = remember(tracks, sortAscending, sortMode) {
        val comparator = when (sortMode) {
            SongSortMode.TITLE -> compareBy<Track> { it.title.lowercase() }
            SongSortMode.ARTIST -> compareBy<Track> { it.artist.lowercase() }.thenBy { it.title.lowercase() }
            SongSortMode.ALBUM -> compareBy<Track> { it.album.lowercase() }.thenBy { it.trackNumber }.thenBy { it.title.lowercase() }
            SongSortMode.DATE_ADDED -> compareBy<Track> { it.dateAddedSeconds }.thenBy { it.title.lowercase() }
        }
        if (sortAscending) tracks.sortedWith(comparator) else tracks.sortedWith(comparator.reversed())
    }
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = contentPadding.calculateTopPadding() + 12.dp, bottom = contentPadding.calculateBottomPadding() + 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(if (isSearching) T("搜索结果") else T("设备音乐"), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                if (isSearching) T("%1\$s 项本地内容命中").format(contentSearchResults.size) else T("%1\$s 首设备本地音乐").format(tracks.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            if (isSearching) {
                Text(
                    T("可搜索曲目、章节、逐行歌词、节目笔记和书签；点按即可播放或定位。"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box {
                    AssistChip(
                        onClick = { sortMenuExpanded = true },
                        label = { Text(T("排序：%1\$s").format(sortMode.label)) },
                        leadingIcon = { Icon(Icons.Default.LibraryMusic, null, modifier = Modifier.size(18.dp)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    )
                    DropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) {
                        SongSortMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.label) },
                                onClick = {
                                    sortMode = mode
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                AssistChip(
                    onClick = { sortAscending = !sortAscending },
                    label = { Text(if (sortAscending) T("正序") else T("倒序")) },
                    leadingIcon = { Icon(if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward, null, modifier = Modifier.size(18.dp)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                )
            }
            Spacer(Modifier.height(12.dp))
        }
        if (isSearching) {
            if (contentSearchResults.isEmpty()) item {
                EmptySearchContentCard()
            } else items(
                contentSearchResults,
                key = { "${it.kind}_${it.track.id}_${it.positionMs}_${it.title}" }
            ) { result ->
                ContentSearchResultItem(result = result, onClick = { onPlayContentSearchResult(result) })
            }
        } else if (tracks.isEmpty()) item {
            EmptyLibraryCard(libraryUiState, onRequestPermission, onRescan)
        } else itemsIndexed(sortedTracks, key = { _, track -> track.id }) { index, track ->
            TrackListItem(track = track, enterIndex = index, onClick = { onPlay(track) }, onMore = { onMore(track) })
        }
    }
}

@Composable
private fun ContentSearchResultItem(result: ContentSearchResult, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumArt(Modifier.size(46.dp), result.track.artworkUri, result.track.title, ArtEmphasis.Secondary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(result.kind.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Text(result.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    result.supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.PlayArrow, contentDescription = T("播放或定位"), tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun EmptySearchContentCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(T("没有匹配的本地内容"), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(T("可尝试曲名、艺人、章节标题、歌词文字、节目笔记或书签时间。"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
internal fun AlbumsScreen(
    albums: List<Album>,
    tracks: List<Track>,
    contentPadding: PaddingValues,
    onPlayAlbum: (List<Track>, Track?) -> Unit,
    onAddTracksToQueue: (List<Track>) -> Unit,
    onMore: (Track) -> Unit
) {
    var selectedAlbumId by remember { mutableStateOf<Long?>(null) }
    val selectedAlbum = remember(albums, selectedAlbumId) {
        selectedAlbumId?.let { id -> albums.firstOrNull { it.id == id } }
    }
    val selectedTracks = remember(tracks, selectedAlbumId) {
        selectedAlbumId?.let { id ->
            tracks.filter { it.albumId == id }
                .sortedWith(
                    compareBy<Track> { if (it.trackNumber > 0) it.trackNumber else Int.MAX_VALUE }
                        .thenBy { it.title.lowercase() }
                )
        }.orEmpty()
    }

    if (selectedAlbum != null) {
        AlbumDetailScreen(
            album = selectedAlbum,
            tracks = selectedTracks,
            contentPadding = contentPadding,
            onBack = { selectedAlbumId = null },
            onPlayAll = { onPlayAlbum(selectedTracks, selectedTracks.firstOrNull()) },
            onAddToQueue = { onAddTracksToQueue(selectedTracks) },
            onPlayTrack = { track -> onPlayAlbum(selectedTracks, track) },
            onMore = onMore
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = contentPadding.calculateTopPadding() + 12.dp, bottom = contentPadding.calculateBottomPadding() + 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(T("专辑"), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(T("点按专辑即可查看曲目并连续播放"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
        }
        if (albums.isEmpty()) item { EmptyAlbumsCard() }
        else items(albums, key = { it.id }) { album ->
            Card(
                onClick = { selectedAlbumId = album.id },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ListItem(
                    headlineContent = { Text(album.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    supportingContent = { Text(T("%1\$s · %2\$s 首歌曲").format(album.artist, album.trackCount), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = { AlbumArt(Modifier.size(56.dp), album.artworkUri, album.title, ArtEmphasis.Tertiary) },
                    trailingContent = { Icon(Icons.Default.PlayArrow, contentDescription = T("查看并播放 %1\$s").format(album.title)) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun AlbumDetailScreen(
    album: Album,
    tracks: List<Track>,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayTrack: (Track) -> Unit,
    onMore: (Track) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = contentPadding.calculateTopPadding() + 12.dp, bottom = contentPadding.calculateBottomPadding() + 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = T("返回专辑列表"))
                }
                Spacer(Modifier.width(4.dp))
                Text(T("专辑详情"), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlbumArt(Modifier.size(104.dp), album.artworkUri, album.title, ArtEmphasis.Tertiary)
                Spacer(Modifier.width(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(album.title, style = MaterialTheme.typography.headlineSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text(album.artist, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(T("%1\$s 首歌曲").format(tracks.size) + if (album.year > 0) " · ${album.year}" else "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(onClick = onPlayAll, enabled = tracks.isNotEmpty()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(T("播放整张专辑"))
                }
                OutlinedButton(onClick = onAddToQueue, enabled = tracks.isNotEmpty()) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(T("加入队列"))
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(T("曲目"), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
        }
        items(tracks, key = { it.id }) { track ->
            TrackListItem(
                track = track,
                onClick = { onPlayTrack(track) },
                onMore = { onMore(track) }
            )
        }
    }
}

@Composable
internal fun FolderScreen(
    tracks: List<Track>,
    contentPadding: PaddingValues,
    onPlayFolder: (List<Track>, Track?) -> Unit,
    onAddTracksToQueue: (List<Track>) -> Unit,
    onMore: (Track) -> Unit
) {
    var selectedFolder by remember { mutableStateOf<String?>(null) }
    // 按 Track.uri 推导出的文件夹名分组，每组内按标题排序。
    val folders = remember(tracks) {
        tracks.groupBy { it.folderName }
            .map { (name, songs) -> name to songs.sortedBy { it.title.lowercase() } }
            .sortedBy { it.first.lowercase() }
    }
    val selectedTracks = remember(folders, selectedFolder) {
        folders.firstOrNull { it.first == selectedFolder }?.second.orEmpty()
    }

    selectedFolder?.let { folderName ->
        FolderDetailScreen(
            folderName = folderName,
            tracks = selectedTracks,
            contentPadding = contentPadding,
            onBack = { selectedFolder = null },
            onPlayAll = { onPlayFolder(selectedTracks, selectedTracks.firstOrNull()) },
            onAddToQueue = { onAddTracksToQueue(selectedTracks) },
            onPlayTrack = { track -> onPlayFolder(selectedTracks, track) },
            onMore = onMore
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = contentPadding.calculateTopPadding() + 12.dp, bottom = contentPadding.calculateBottomPadding() + 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(T("文件夹"), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(T("点按文件夹即可查看其中歌曲并整文件夹播放"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
        }
        if (folders.isEmpty()) item { EmptyGroupCard(T("扫描音乐后，这里会按所在文件夹归类显示。")) }
        else items(folders, key = { it.first }) { (name, songs) ->
            Card(
                onClick = { selectedFolder = name },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ListItem(
                    headlineContent = { Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    supportingContent = { Text(T("%1\$s 首歌曲").format(songs.size), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = { AlbumArt(Modifier.size(56.dp), songs.firstOrNull()?.artworkUri, name, ArtEmphasis.Tertiary) },
                    trailingContent = { Icon(Icons.Default.PlayArrow, contentDescription = T("查看并播放 %1\$s").format(name)) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun FolderDetailScreen(
    folderName: String,
    tracks: List<Track>,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayTrack: (Track) -> Unit,
    onMore: (Track) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = contentPadding.calculateTopPadding() + 12.dp, bottom = contentPadding.calculateBottomPadding() + 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = T("返回文件夹列表"))
                }
                Spacer(Modifier.width(4.dp))
                Text(T("文件夹详情"), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlbumArt(Modifier.size(104.dp), tracks.firstOrNull()?.artworkUri, folderName, ArtEmphasis.Tertiary)
                Spacer(Modifier.width(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(folderName, style = MaterialTheme.typography.headlineSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text(T("%1\$s 首歌曲").format(tracks.size), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(onClick = onPlayAll, enabled = tracks.isNotEmpty()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(T("播放整个文件夹"))
                }
                OutlinedButton(onClick = onAddToQueue, enabled = tracks.isNotEmpty()) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(T("加入队列"))
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(T("曲目"), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
        }
        items(tracks, key = { it.id }) { track ->
            TrackListItem(track = track, onClick = { onPlayTrack(track) }, onMore = { onMore(track) })
        }
    }
}

@Composable
internal fun ArtistScreen(
    tracks: List<Track>,
    contentPadding: PaddingValues,
    onPlayArtist: (List<Track>, Track?) -> Unit,
    onAddTracksToQueue: (List<Track>) -> Unit,
    onMore: (Track) -> Unit
) {
    var selectedArtist by remember { mutableStateOf<String?>(null) }
    // 按 Track.artist 分组，每组内按标题排序。
    val artists = remember(tracks) {
        tracks.groupBy { it.artist }
            .map { (name, songs) -> name to songs.sortedBy { it.title.lowercase() } }
            .sortedBy { it.first.lowercase() }
    }
    val selectedTracks = remember(artists, selectedArtist) {
        artists.firstOrNull { it.first == selectedArtist }?.second.orEmpty()
    }

    selectedArtist?.let { artistName ->
        ArtistDetailScreen(
            artistName = artistName,
            tracks = selectedTracks,
            contentPadding = contentPadding,
            onBack = { selectedArtist = null },
            onPlayAll = { onPlayArtist(selectedTracks, selectedTracks.firstOrNull()) },
            onAddToQueue = { onAddTracksToQueue(selectedTracks) },
            onPlayTrack = { track -> onPlayArtist(selectedTracks, track) },
            onMore = onMore
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = contentPadding.calculateTopPadding() + 12.dp, bottom = contentPadding.calculateBottomPadding() + 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(T("艺术家"), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(T("点按艺术家即可查看并连续播放其全部歌曲"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
        }
        if (artists.isEmpty()) item { EmptyGroupCard(T("扫描音乐后，这里会按艺术家归类显示。")) }
        else items(artists, key = { it.first }) { (name, songs) ->
            Card(
                onClick = { selectedArtist = name },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ListItem(
                    headlineContent = { Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    supportingContent = { Text(T("%1\$s 首歌曲 · %2\$s 张专辑").format(songs.size, songs.map { it.album }.distinct().size), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = {
                        Surface(modifier = Modifier.size(52.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    },
                    trailingContent = { Icon(Icons.Default.PlayArrow, contentDescription = T("查看并播放 %1\$s").format(name)) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun ArtistDetailScreen(
    artistName: String,
    tracks: List<Track>,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayTrack: (Track) -> Unit,
    onMore: (Track) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = contentPadding.calculateTopPadding() + 12.dp, bottom = contentPadding.calculateBottomPadding() + 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = T("返回艺术家列表"))
                }
                Spacer(Modifier.width(4.dp))
                Text(T("艺术家详情"), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(96.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Spacer(Modifier.width(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(artistName, style = MaterialTheme.typography.headlineSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text(T("%1\$s 首歌曲").format(tracks.size), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(onClick = onPlayAll, enabled = tracks.isNotEmpty()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(T("播放全部歌曲"))
                }
                OutlinedButton(onClick = onAddToQueue, enabled = tracks.isNotEmpty()) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(T("加入队列"))
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(T("曲目"), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
        }
        items(tracks, key = { it.id }) { track ->
            TrackListItem(track = track, onClick = { onPlayTrack(track) }, onMore = { onMore(track) })
        }
    }
}

@Composable
private fun EmptyGroupCard(message: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(message, modifier = Modifier.padding(20.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun FavoritesScreen(
    favoriteTracks: List<Track>,
    contentPadding: PaddingValues,
    onPlay: (Track) -> Unit,
    onPlayAll: () -> Unit,
    onAddAllToQueue: () -> Unit,
    onMore: (Track) -> Unit
) {
    if (favoriteTracks.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(modifier = Modifier.size(92.dp), shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(T("还没有收藏"), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(T("从歌曲菜单或播放页点击爱心，即可在这里保存喜爱的音乐。"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = contentPadding.calculateTopPadding() + 12.dp, bottom = contentPadding.calculateBottomPadding() + 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Text(T("收藏"), style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(4.dp))
                Text(T("%1\$s 首你喜欢的音乐").format(favoriteTracks.size), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(onClick = onPlayAll) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(T("播放全部收藏"))
                    }
                    OutlinedButton(onClick = onAddAllToQueue) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(T("加入队列"))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            items(favoriteTracks, key = { it.id }) { track ->
                TrackListItem(track = track, onClick = { onPlay(track) }, onMore = { onMore(track) })
            }
        }
    }
}

@Composable
private fun EmptyLibraryCard(libraryUiState: LibraryUiState, onRequestPermission: () -> Unit, onRescan: () -> Unit) {
    val needsPermission = libraryUiState == LibraryUiState.PermissionRequired
    val isLoading = libraryUiState == LibraryUiState.Loading
    val title = when (libraryUiState) {
        LibraryUiState.PermissionRequired -> T("需要读取音乐的权限")
        LibraryUiState.Loading -> T("正在扫描设备音乐")
        is LibraryUiState.Error -> T("暂时无法读取媒体库")
        else -> T("这里会出现你的音乐")
    }
    val description = when (libraryUiState) {
        LibraryUiState.PermissionRequired -> T("授权后，Muse 才能读取设备中的本地音频。")
        LibraryUiState.Loading -> T("正在整理歌曲、专辑和封面信息，请稍候。")
        is LibraryUiState.Error -> libraryUiState.message
        else -> T("将音频放入设备的 Music 文件夹后，点击扫描即可添加。")
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) { Icon(if (needsPermission) Icons.Default.Folder else Icons.Default.LibraryMusic, null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
            }
            Spacer(Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!isLoading) {
                Spacer(Modifier.height(18.dp))
                FilledTonalButton(onClick = if (needsPermission) onRequestPermission else onRescan) {
                    Text(if (needsPermission) T("授权并扫描") else T("重新扫描"))
                }
            }
        }
    }
}

@Composable
private fun EmptyAlbumsCard() {
    Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(T("扫描音乐后，这里会显示带有专辑标签的作品。"), modifier = Modifier.padding(20.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionHeader(title: String, action: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        TextButton(onClick = onClick) { Text(action) }
    }
}

@Composable
private fun TrackListItem(
    track: Track,
    isFeaturedCompleted: Boolean = false,
    enterIndex: Int = 0,
    onClick: () -> Unit,
    onMore: () -> Unit
) {
    val completedFeaturedTrack = track.isFeaturedAsset && isFeaturedCompleted
    val reduceMotion = rememberReducedMotion()
    val interaction = rememberMuseInteraction()
    val enter = remember { Animatable(if (reduceMotion) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!reduceMotion) {
            delay((enterIndex.coerceAtMost(10) * MuseMotion.STAGGER_STEP_MILLIS).toLong())
            enter.animateTo(1f, animationSpec = tween(MuseMotion.ENTER_MILLIS, easing = FastOutSlowInEasing))
        }
    }
    ListItem(
        overlineContent = {
            Text(
                when {
                    completedFeaturedTrack -> T("专题音频包 · 已完成")
                    track.isFeaturedAsset -> T("专题音频包")
                    else -> T("设备音乐")
                },
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    completedFeaturedTrack -> MaterialTheme.colorScheme.tertiary
                    track.isFeaturedAsset -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        headlineContent = { Text(track.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text("${track.artist} · ${track.album}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingContent = { AlbumArt(Modifier.size(52.dp), track.artworkUri, track.title, ArtEmphasis.Secondary) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (completedFeaturedTrack) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = T("已完成专题曲目"),
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    if (track.durationMs > 0L) track.durationLabel else "—:—",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onMore) { Icon(Icons.Default.MoreVert, contentDescription = T("%1\$s 的歌曲选项").format(track.title)) }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .graphicsLayer {
                alpha = enter.value
                translationY = (1f - enter.value) * 22f
            }
            .clip(MaterialTheme.shapes.medium)
            .musePressScale(interaction, reduceMotion)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackActionsSheet(
    track: Track,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayNext: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onEditTags: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlbumArt(Modifier.size(56.dp), track.artworkUri, track.title, ArtEmphasis.Primary)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(track.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(track.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.height(16.dp))
            ActionRow(Icons.Default.PlayArrow, T("立即播放"), onPlay)
            ActionRow(Icons.Default.SkipNext, T("下一首播放"), onPlayNext)
            ActionRow(Icons.Default.Add, T("加入播放队列"), onAddToQueue)
            ActionRow(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, if (isFavorite) T("取消收藏") else T("加入收藏"), onToggleFavorite)
            // 本地歌单与标签编辑仅对设备音乐有意义，内置专题音频不提供。
            if (!track.isFeaturedAsset) {
                ActionRow(Icons.Default.PlaylistAdd, T("添加到歌单"), onAddToPlaylist)
                ActionRow(Icons.Default.Edit, T("编辑标签"), onEditTags)
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun ActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    val reduceMotion = rememberReducedMotion()
    val interaction = rememberMuseInteraction()
    ListItem(
        headlineContent = { Text(label) },
        leadingContent = { Icon(icon, null) },
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .musePressScale(interaction, reduceMotion)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
    )
}

private const val QUICK_SEEK_MS = 15_000L
private const val HOME_PREVIEW_LIMIT = 5
/** 歌词时间偏移的调整步长：0.5 秒。 */
private const val LYRIC_OFFSET_STEP_MS = 500L
/** 歌单详情行高（含条目间距），用于把拖拽位移换算成移动的条目数。 */
private val PLAYLIST_ROW_STEP = 76.dp

private enum class SongSortMode(val label: String) {
    TITLE(T("标题")),
    ARTIST(T("艺人")),
    ALBUM(T("专辑")),
    DATE_ADDED(T("最近加入"))
}

private enum class PlaybackStrategy(
    val label: String,
    val description: String,
    val repeatMode: Int,
    val shuffleEnabled: Boolean,
    val icon: ImageVector
) {
    SEQUENTIAL_LOOP(T("顺序循环"), T("按队列顺序播放，播完后从第一首继续"), Player.REPEAT_MODE_ALL, false, Icons.Default.Repeat),
    RANDOM(T("随机播放"), T("随机选择队列中的歌曲，播完当前队列后停止"), Player.REPEAT_MODE_OFF, true, Icons.Default.Shuffle),
    SINGLE_REPEAT(T("单曲循环"), T("重复播放当前歌曲"), Player.REPEAT_MODE_ONE, false, Icons.Default.RepeatOne),
    NO_REPEAT(T("不循环"), T("按队列顺序播放，播完后停止"), Player.REPEAT_MODE_OFF, false, Icons.Default.SkipNext)
}

private fun playbackStrategyFor(repeatMode: Int, shuffleEnabled: Boolean): PlaybackStrategy = when {
    shuffleEnabled -> PlaybackStrategy.RANDOM
    repeatMode == Player.REPEAT_MODE_ONE -> PlaybackStrategy.SINGLE_REPEAT
    repeatMode == Player.REPEAT_MODE_ALL -> PlaybackStrategy.SEQUENTIAL_LOOP
    else -> PlaybackStrategy.NO_REPEAT
}

private enum class ArtEmphasis { Primary, Secondary, Tertiary }

@Composable
private fun AlbumArt(modifier: Modifier, seed: String, emphasis: ArtEmphasis) {
    AlbumArt(modifier = modifier, artworkUri = null, seed = seed, emphasis = emphasis)
}

@Composable
private fun AlbumArt(modifier: Modifier, artworkUri: android.net.Uri?, seed: String, emphasis: ArtEmphasis) {
    val context = LocalContext.current
    val artworkRequest = remember(artworkUri) {
        artworkUri?.let { uri ->
            ImageRequest.Builder(context)
                .data(uri)
                .size(384)
                .memoryCacheKey("muse-artwork:${uri}")
                .diskCacheKey("muse-artwork:${uri}")
                .crossfade(false)
                .build()
        }
    }
    val palette = when (emphasis) {
        ArtEmphasis.Primary -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        ArtEmphasis.Secondary -> when (seed.hashCode().absoluteValue % 3) {
            0 -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
            1 -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
            else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        }
        ArtEmphasis.Tertiary -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
    }
    Surface(modifier = modifier, shape = MaterialTheme.shapes.medium, color = palette.first) {
        Box(contentAlignment = Alignment.Center) {
            if (artworkUri != null) {
                AsyncImage(
                    model = artworkRequest,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.muse_default_album_art),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.82f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerWithProgress(
    viewModel: PlayerViewModel,
    track: Track,
    isPlaying: Boolean,
    onOpen: () -> Unit,
    onOpenQueue: () -> Unit
) {
    val progress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    MiniPlayer(
        track = track,
        isPlaying = isPlaying,
        progress = progress,
        onOpen = onOpen,
        onOpenQueue = onOpenQueue,
        onToggle = viewModel::togglePlayback,
        onNext = viewModel::skipNext
    )
}

@Composable
private fun MiniPlayer(
    track: Track,
    isPlaying: Boolean,
    progress: Float,
    onOpen: () -> Unit,
    onOpenQueue: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit
) {
    val reduceMotion = rememberReducedMotion()
    val smoothProgress by rememberSmoothedFloat(progress, reduceMotion)
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
        Column {
            androidx.compose.material3.LinearProgressIndicator(
                progress = { smoothProgress },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                AlbumArt(Modifier.size(48.dp), track.artworkUri, track.title, ArtEmphasis.Primary)
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(track.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(track.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                MusePlayPauseButton(isPlaying = isPlaying, reduceMotion = reduceMotion, onClick = onToggle)
                IconButton(onClick = onNext) { Icon(Icons.Default.SkipNext, contentDescription = T("下一首")) }
                IconButton(onClick = onOpenQueue) { Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = T("打开播放队列")) }
            }
        }
    }
}

/**
 * 播放/暂停按钮：图标在切换时做交叉淡入 + 轻微缩放，替代原来的硬切。
 * 尊重「减少动态效果」时退化为瞬时切换。
 */
@Composable
private fun MusePlayPauseButton(
    isPlaying: Boolean,
    reduceMotion: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Crossfade(
            targetState = isPlaying,
            animationSpec = tween(durationMillis = motionMillis(reduceMotion, MuseMotion.STATE_MILLIS), easing = FastOutSlowInEasing),
            label = "muse_play_pause"
        ) { playing ->
            Icon(
                if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = T("播放或暂停")
            )
        }
    }
}

/**
 * 收藏按钮：图标在实心/空心之间过渡，颜色随选中态平滑插值，并在选中瞬间做一次轻微放大确认。
 * 尊重「减少动态效果」时退化为瞬时切换。
 */
@Composable
private fun MuseFavoriteButton(
    isFavorite: Boolean,
    reduceMotion: Boolean,
    onClick: () -> Unit
) {
    val progress by rememberSwitchProgress(isFavorite, reduceMotion)
    val tint by animateColorAsState(
        targetValue = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = motionMillis(reduceMotion, MuseMotion.STATE_MILLIS)),
        label = "muse_favorite_tint"
    )
    IconButton(onClick = onClick) {
        Icon(
            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) T("取消收藏") else T("加入收藏"),
            tint = tint,
            modifier = Modifier.scale(if (reduceMotion) 1f else 0.9f + 0.1f * progress)
        )
    }
}

@Composable
private fun FeaturedNowPlayingContext(
    metadata: FeaturedPackMetadata,
    journey: PlayerViewModel.FeaturedJourneyState,
    track: Track,
    onOpenTopic: () -> Unit
) {
    Card(
        onClick = onOpenTopic,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF20365F))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.14f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = Color(0xFFC6D8FF), modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(T("正在收听专题"), style = MaterialTheme.typography.labelMedium, color = Color(0xFFC6D8FF))
                Text(metadata.title, style = MaterialTheme.typography.titleSmall, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    T("《%1\$s》 · 已完成 %2\$s/%3\$s").format(track.title, journey.completedCount, journey.totalCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD6E1F6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TextButton(onClick = onOpenTopic) { Text(T("节目单")) }
        }
    }
}

@Composable
private fun PlayerSheetWithProgress(
    viewModel: PlayerViewModel,
    track: Track,
    isPlaying: Boolean,
    repeatMode: Int,
    shuffleEnabled: Boolean,
    playbackSpeed: Float,
    mixingPlaybackEnabled: Boolean,
    fadeTransitionsEnabled: Boolean,
    fadeDurationMs: Int,
    lyricsOffsetMs: Long,
    lyricsFontScale: Int,
    sleepTimerRemainingMs: Long,
    sleepAfterCurrentTrackEnabled: Boolean,
    loopSegment: PlayerViewModel.LoopSegment,
    isFavorite: Boolean,
    featuredJourney: PlayerViewModel.FeaturedJourneyState,
    featuredMetadata: FeaturedPackMetadata,
    onDismiss: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onSetPlaybackStrategy: (PlaybackStrategy) -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onSetMixingPlayback: (Boolean) -> Unit,
    onSetFadeTransitions: (Boolean) -> Unit,
    onSetFadeDuration: (Int) -> Unit,
    onNudgeLyricsOffset: (Long) -> Unit,
    onResetLyricsOffset: () -> Unit,
    onSetLyricsFontScale: (Int) -> Unit,
    onSetSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onSetSleepAfterCurrentTrack: (Boolean) -> Unit,
    onSetLoopStart: () -> Unit,
    onSetLoopEnd: () -> Unit,
    onClearLoop: () -> Unit,
    onAddBookmark: () -> Boolean,
    onToggleFavorite: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenFeaturedDetail: () -> Unit
) {
    val progress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    val positionMs by viewModel.positionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val lyrics by viewModel.currentLyrics.collectAsStateWithLifecycle()
    val program by viewModel.currentProgram.collectAsStateWithLifecycle()
    val audioOutputLabel by viewModel.audioOutputLabel.collectAsStateWithLifecycle()
    PlayerSheet(
        track = track,
        isPlaying = isPlaying,
        progress = progress,
        positionMs = positionMs,
        durationMs = durationMs,
        lyrics = lyrics,
        program = program,
        audioOutputLabel = audioOutputLabel,
        repeatMode = repeatMode,
        shuffleEnabled = shuffleEnabled,
        playbackSpeed = playbackSpeed,
        mixingPlaybackEnabled = mixingPlaybackEnabled,
        fadeTransitionsEnabled = fadeTransitionsEnabled,
        fadeDurationMs = fadeDurationMs,
        lyricsOffsetMs = lyricsOffsetMs,
        lyricsFontScale = lyricsFontScale,
        sleepTimerRemainingMs = sleepTimerRemainingMs,
        sleepAfterCurrentTrackEnabled = sleepAfterCurrentTrackEnabled,
        loopSegment = loopSegment,
        isFavorite = isFavorite,
        featuredJourney = featuredJourney,
        featuredMetadata = featuredMetadata,
        onDismiss = onDismiss,
        onToggle = onToggle,
        onNext = onNext,
        onPrevious = onPrevious,
        onSeek = onSeek,
        onSetPlaybackStrategy = onSetPlaybackStrategy,
        onSetPlaybackSpeed = onSetPlaybackSpeed,
        onSetMixingPlayback = onSetMixingPlayback,
        onSetFadeTransitions = onSetFadeTransitions,
        onSetFadeDuration = onSetFadeDuration,
        onNudgeLyricsOffset = onNudgeLyricsOffset,
        onResetLyricsOffset = onResetLyricsOffset,
        onSetLyricsFontScale = onSetLyricsFontScale,
        onSetSleepTimer = onSetSleepTimer,
        onCancelSleepTimer = onCancelSleepTimer,
        onSetSleepAfterCurrentTrack = onSetSleepAfterCurrentTrack,
        onSetLoopStart = onSetLoopStart,
        onSetLoopEnd = onSetLoopEnd,
        onClearLoop = onClearLoop,
        onAddBookmark = onAddBookmark,
        onToggleFavorite = onToggleFavorite,
        onOpenQueue = onOpenQueue,
        onOpenFeaturedDetail = onOpenFeaturedDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerSheet(
    track: Track,
    isPlaying: Boolean,
    progress: Float,
    positionMs: Long,
    durationMs: Long,
    lyrics: List<LyricLine>,
    program: FeaturedTrackProgram?,
    audioOutputLabel: String,
    repeatMode: Int,
    shuffleEnabled: Boolean,
    playbackSpeed: Float,
    mixingPlaybackEnabled: Boolean,
    fadeTransitionsEnabled: Boolean,
    fadeDurationMs: Int,
    lyricsOffsetMs: Long,
    lyricsFontScale: Int,
    sleepTimerRemainingMs: Long,
    sleepAfterCurrentTrackEnabled: Boolean,
    loopSegment: PlayerViewModel.LoopSegment,
    isFavorite: Boolean,
    featuredJourney: PlayerViewModel.FeaturedJourneyState,
    featuredMetadata: FeaturedPackMetadata,
    onDismiss: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onSetPlaybackStrategy: (PlaybackStrategy) -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onSetMixingPlayback: (Boolean) -> Unit,
    onSetFadeTransitions: (Boolean) -> Unit,
    onSetFadeDuration: (Int) -> Unit,
    onNudgeLyricsOffset: (Long) -> Unit,
    onResetLyricsOffset: () -> Unit,
    onSetLyricsFontScale: (Int) -> Unit,
    onSetSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onSetSleepAfterCurrentTrack: (Boolean) -> Unit,
    onSetLoopStart: () -> Unit,
    onSetLoopEnd: () -> Unit,
    onClearLoop: () -> Unit,
    onAddBookmark: () -> Boolean,
    onToggleFavorite: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenFeaturedDetail: () -> Unit
) {
    var isSeeking by remember { mutableStateOf(false) }
    var speedMenuExpanded by remember { mutableStateOf(false) }
    var strategyMenuExpanded by remember { mutableStateOf(false) }
    var programExpanded by remember(track.id) { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(progress) }
    var customSleepDialogVisible by remember { mutableStateOf(false) }
    var customSleepMinutesText by remember { mutableStateOf("") }
    val reduceMotion = rememberReducedMotion()
    // 全屏沉浸式 + 长按菜单 + 下载到 Download
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    var isImmersive by remember(track.id) { mutableStateOf(false) }
    var showLongPressMenu by remember(track.id) { mutableStateOf(false) }
    var isSavingToDownloads by remember(track.id) { mutableStateOf(false) }
    DisposableEffect(isImmersive) {
        val window = (context as? Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        if (isImmersive) {
            controller?.hide(WindowInsetsCompat.Type.systemBars())
            controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
    // 播放进度平滑跟随：避免每秒一次的跳变，拖动时直接跟手不做平滑
    val smoothSliderProgress by rememberSmoothedFloat(if (isSeeking) scrubProgress else progress, reduceMotion)
    val playbackStrategy = playbackStrategyFor(repeatMode, shuffleEnabled)
    val loopForCurrentTrack = loopSegment.takeIf { it.trackId == track.id }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            DynamicPlaybackAtmosphere(
                isPlaying = isPlaying,
                modifier = Modifier.matchParentSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xD9121A31))
                    .verticalScroll(rememberScrollState())
                    .padding(start = 28.dp, end = 28.dp, bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(T("正在播放"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(track.album, style = MaterialTheme.typography.bodySmall, color = Color(0xFFD6E1F6), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(T("输出 · %1\$s").format(audioOutputLabel), style = MaterialTheme.typography.labelSmall, color = Color(0xFFB6C6E9), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                TextButton(onClick = { speedMenuExpanded = true }) { Text("${formatSpeed(playbackSpeed)}×") }
                DropdownMenu(expanded = speedMenuExpanded, onDismissRequest = { speedMenuExpanded = false }) {
                    listOf(0.75f, 1f, 1.25f, 1.5f, 2f).forEach { speed ->
                        DropdownMenuItem(
                            text = { Text("${formatSpeed(speed)}×") },
                            onClick = {
                                onSetPlaybackSpeed(speed)
                                speedMenuExpanded = false
                            }
                        )
                    }
                }
                IconButton(onClick = onOpenQueue) { Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = T("打开播放队列")) }
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = T("关闭播放页")) }
            }
            Spacer(Modifier.height(20.dp))
            Box {
                AlbumArt(
                    modifier = if (lyrics.isEmpty() || isImmersive) {
                        Modifier.fillMaxWidth().aspectRatio(1f)
                    } else {
                        Modifier.fillMaxWidth().height(224.dp)
                    },
                    artworkUri = track.artworkUri,
                    seed = track.title,
                    emphasis = ArtEmphasis.Primary
                )
                // 双击封面切换全屏 + 长按弹菜单
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(track.id) {
                            detectTapGestures(
                                onDoubleTap = { isImmersive = !isImmersive },
                                onLongPress = { showLongPressMenu = true }
                            )
                        }
                )
                DropdownMenu(
                    expanded = showLongPressMenu,
                    onDismissRequest = { showLongPressMenu = false }
                ) {
                    if (track.isFeaturedAsset) {
                        DropdownMenuItem(
                            text = { Text(if (isSavingToDownloads) T("正在保存…") else T("保存到下载")) },
                            onClick = {
                                showLongPressMenu = false
                                if (!isSavingToDownloads) {
                                    isSavingToDownloads = true
                                    scope.launch {
                                        val result = withContext(Dispatchers.IO) {
                                            saveFeaturedAudioToDownloads(context, track)
                                        }
                                        isSavingToDownloads = false
                                        Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(if (isImmersive) T("退出全屏") else T("进入全屏")) },
                        onClick = {
                            showLongPressMenu = false
                            isImmersive = !isImmersive
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isFavorite) T("取消收藏") else T("加入收藏")) },
                        onClick = {
                            showLongPressMenu = false
                            onToggleFavorite()
                        }
                    )
                }
                if (isImmersive) {
                    Text(
                        text = T("双击退出全屏"),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                    )
                }
            }
            if (!isImmersive) {
                Spacer(Modifier.height(16.dp))
                LyricsStage(
                    lyrics = lyrics,
                    positionMs = positionMs,
                    offsetMs = lyricsOffsetMs,
                    fontScale = lyricsFontScale,
                    onNudgeOffset = onNudgeLyricsOffset,
                    onResetOffset = onResetLyricsOffset,
                    onSetFontScale = onSetLyricsFontScale
                )
            }
            Spacer(Modifier.height(if (isImmersive) 28.dp else 18.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(track.title, style = MaterialTheme.typography.headlineSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text(track.artist, style = MaterialTheme.typography.bodyLarge, color = Color(0xFFD6E1F6), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (track.isFeaturedAsset) {
                    IconButton(
                        onClick = {
                            if (!isSavingToDownloads) {
                                isSavingToDownloads = true
                                scope.launch {
                                    val result = withContext(Dispatchers.IO) {
                                        saveFeaturedAudioToDownloads(context, track)
                                    }
                                    isSavingToDownloads = false
                                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isSavingToDownloads
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = if (isSavingToDownloads) T("正在保存") else T("保存到下载"), tint = MaterialTheme.colorScheme.primary)
                    }
                }
                TextButton(onClick = { onAddBookmark() }) { Text(T("存书签")) }
                MuseFavoriteButton(isFavorite = isFavorite, reduceMotion = reduceMotion, onClick = onToggleFavorite)
            }
            if (track.isFeaturedAsset && !isImmersive) {
                Spacer(Modifier.height(14.dp))
                FeaturedNowPlayingContext(
                    metadata = featuredMetadata,
                    journey = featuredJourney,
                    track = track,
                    onOpenTopic = onOpenFeaturedDetail
                )
            }
            Spacer(Modifier.height(16.dp))
            Slider(
                value = smoothSliderProgress,
                onValueChange = {
                    isSeeking = true
                    scrubProgress = it
                },
                onValueChangeFinished = {
                    onSeek(scrubProgress)
                    isSeeking = false
                },
                enabled = durationMs > 0L,
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    if (durationMs > 0L) formatTime(if (isSeeking) (durationMs * scrubProgress).toLong() else positionMs) else "—:—",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD6E1F6)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    if (durationMs > 0L) formatTime(durationMs) else T("正在读取时长"),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD6E1F6)
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = T("上一首"), modifier = Modifier.size(34.dp))
                }
                FilledIconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Crossfade(
                        targetState = isPlaying,
                        animationSpec = tween(durationMillis = motionMillis(reduceMotion, MuseMotion.STATE_MILLIS), easing = FastOutSlowInEasing),
                        label = "muse_full_play_pause"
                    ) { playing ->
                        Icon(
                            if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = T("播放或暂停"),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                IconButton(onClick = onNext, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipNext, contentDescription = T("下一首"), modifier = Modifier.size(34.dp))
                }
            }
            if (durationMs > 0L) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AssistChip(
                        onClick = {
                            onSeek(
                                ((positionMs - QUICK_SEEK_MS).coerceAtLeast(0L).toDouble() / durationMs.toDouble())
                                    .toFloat()
                            )
                        },
                        enabled = positionMs > 0L,
                        label = { Text(T("后退 15 秒")) },
                        leadingIcon = { Icon(Icons.Default.Replay10, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Spacer(Modifier.width(10.dp))
                    AssistChip(
                        onClick = {
                            onSeek(
                                ((positionMs + QUICK_SEEK_MS).coerceAtMost(durationMs).toDouble() / durationMs.toDouble())
                                    .toFloat()
                            )
                        },
                        enabled = positionMs < durationMs,
                        label = { Text(T("前进 15 秒")) },
                        leadingIcon = { Icon(Icons.Default.Forward10, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
            if (program?.chapters?.isNotEmpty() == true && durationMs > 0L) {
                Spacer(Modifier.height(8.dp))
                ChapterTimelineMarkers(
                    chapters = program.chapters,
                    positionMs = positionMs,
                    durationMs = durationMs
                )
            }
            if (durationMs > 0L) {
                Spacer(Modifier.height(12.dp))
                Text(T("A-B 片段循环"), style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth())
            Text(
                when {
                    loopForCurrentTrack?.isActive == true -> T("正在循环 %1\$s 至 %2\$s").format(formatTime(loopForCurrentTrack.startMs ?: 0L), formatTime(loopForCurrentTrack.endMs ?: 0L))
                    loopForCurrentTrack?.startMs != null -> T("起点 A：%1\$s，移动进度后设置终点 B").format(formatTime(loopForCurrentTrack.startMs))
                    else -> T("适合反复听一句歌词、片段或访谈段落")
                },
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFD6E1F6),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = onSetLoopStart, label = { Text(T("设为起点 A")) })
                AssistChip(
                    onClick = onSetLoopEnd,
                    enabled = loopForCurrentTrack?.startMs != null,
                    label = { Text(T("设为终点 B")) }
                )
                    if (loopForCurrentTrack?.isActive == true) {
                        TextButton(onClick = onClearLoop) { Text(T("关闭")) }
                    }
                }
            }
            if (program?.chapters?.isNotEmpty() == true) {
                Spacer(Modifier.height(14.dp))
                ChapterFlowCard(
                    state = program.chapterPlaybackState(positionMs = positionMs, durationMs = durationMs),
                    chapterCount = program.chapters.size,
                    onChapterSelect = { timestampMs ->
                        if (durationMs > 0L) {
                            onSeek((timestampMs.toDouble() / durationMs.toDouble()).toFloat().coerceIn(0f, 1f))
                        }
                    }
                )
            }
            if (program?.hasContent == true) {
                Spacer(Modifier.height(16.dp))
                ProgramGuide(
                    program = program,
                    positionMs = positionMs,
                    expanded = programExpanded,
                    onToggleExpanded = { programExpanded = !programExpanded },
                    onChapterSelect = { timestampMs ->
                        if (durationMs > 0L) {
                            onSeek((timestampMs.toDouble() / durationMs.toDouble()).toFloat().coerceIn(0f, 1f))
                        }
                    }
                )
            }
            Spacer(Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                FilledTonalButton(
                    onClick = { strategyMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(playbackStrategy.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(T("播放方式：%1\$s").format(playbackStrategy.label))
                }
                DropdownMenu(
                    expanded = strategyMenuExpanded,
                    onDismissRequest = { strategyMenuExpanded = false }
                ) {
                    PlaybackStrategy.entries.forEach { strategy ->
                        DropdownMenuItem(
                            leadingIcon = { Icon(strategy.icon, contentDescription = null) },
                            text = {
                                Column {
                                    Text(strategy.label)
                                    Text(
                                        strategy.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onSetPlaybackStrategy(strategy)
                                strategyMenuExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(T("声音处理"), style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { onSetMixingPlayback(!mixingPlaybackEnabled) },
                    label = { Text(if (mixingPlaybackEnabled) T("混音播放：开启") else T("混音播放：关闭")) },
                    leadingIcon = { Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (mixingPlaybackEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                AssistChip(
                    onClick = { onSetFadeTransitions(!fadeTransitionsEnabled) },
                    label = { Text(if (fadeTransitionsEnabled) T("淡化：开启") else T("淡化：关闭")) },
                    leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (fadeTransitionsEnabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(T("淡入淡出时长"), style = MaterialTheme.typography.labelMedium, color = Color(0xFFD6E1F6))
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0, 200, 400, 800, 1200).forEach { durationMs ->
                    AssistChip(
                        onClick = { onSetFadeDuration(durationMs) },
                        label = { Text(if (durationMs == 0) T("关闭") else "${durationMs}ms") },
                        enabled = fadeTransitionsEnabled,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (fadeTransitionsEnabled && durationMs == fadeDurationMs) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (mixingPlaybackEnabled) T("混音开启：Muse 不会抢占其他应用的音频焦点，可与其他声音同时播放。") else T("独占播放：Muse 会按系统音频焦点规则协调其他应用。"),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFD6E1F6)
            )
            Text(
                when {
                    !fadeTransitionsEnabled -> T("淡化关闭：切歌与暂停立即执行。")
                    fadeDurationMs == 0 -> T("淡化已开启但时长为 0ms：切换将即时生效。")
                    else -> T("淡化开启：在应用内切歌、上一首、下一首和暂停时使用约 %1\$sms 的淡入淡出。").format(fadeDurationMs)
                },
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFD6E1F6)
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(T("睡眠定时"), style = MaterialTheme.typography.titleSmall)
                    Text(
                        when {
                            sleepTimerRemainingMs > 0L && sleepAfterCurrentTrackEnabled -> T("将在 %1\$s 后，或本曲结束时平滑暂停").format(formatTime(sleepTimerRemainingMs))
                            sleepTimerRemainingMs > 0L -> T("将在 %1\$s 后平滑暂停").format(formatTime(sleepTimerRemainingMs))
                            sleepAfterCurrentTrackEnabled -> T("已设置本曲结束后平滑暂停")
                            else -> T("未设置自动暂停")
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFD6E1F6)
                    )
                }
                if (sleepTimerRemainingMs > 0L) {
                    TextButton(onClick = onCancelSleepTimer) { Text(T("关闭")) }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 30, 60, 90).forEach { minutes ->
                    AssistChip(onClick = { onSetSleepTimer(minutes) }, label = { Text(T("%1\$s 分钟").format(minutes)) })
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    customSleepMinutesText = ""
                    customSleepDialogVisible = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(T("自定义分钟数"))
            }
            Spacer(Modifier.height(10.dp))
            FilledTonalButton(
                onClick = { onSetSleepAfterCurrentTrack(!sleepAfterCurrentTrackEnabled) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (sleepAfterCurrentTrackEnabled) T("本曲结束后暂停：已开启") else T("本曲结束后暂停"))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                playbackStrategy.description,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFD6E1F6)
            )
            }
        }
    }
    if (customSleepDialogVisible) {
        val minutes = customSleepMinutesText.trim().toIntOrNull()
        AlertDialog(
            onDismissRequest = { customSleepDialogVisible = false },
            title = { Text(T("自定义睡眠定时")) },
            text = {
                Column {
                    Text(T("输入 1 到 180 之间的分钟数，到点后先淡出音量再暂停。"), style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customSleepMinutesText,
                        onValueChange = { input -> customSleepMinutesText = input.filter { it.isDigit() }.take(3) },
                        singleLine = true,
                        label = { Text(T("分钟")) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        minutes?.let(onSetSleepTimer)
                        customSleepDialogVisible = false
                    },
                    enabled = minutes != null && minutes in 1..180
                ) { Text(T("开始")) }
            },
            dismissButton = {
                TextButton(onClick = { customSleepDialogVisible = false }) { Text(T("取消")) }
            }
        )
    }
}

@Composable
private fun ChapterTimelineMarkers(
    chapters: List<FeaturedChapter>,
    positionMs: Long,
    durationMs: Long
) {
    if (durationMs <= 0L || chapters.isEmpty()) return
    val completedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)
    val pendingColor = Color(0xFFB6C6E9).copy(alpha = 0.58f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
    ) {
        val lineY = size.height / 2f
        drawLine(
            color = pendingColor.copy(alpha = 0.4f),
            start = Offset(0f, lineY),
            end = Offset(size.width, lineY),
            strokeWidth = 1.dp.toPx()
        )
        chapters.forEach { chapter ->
            val fraction = (chapter.timestampMs.toDouble() / durationMs.toDouble()).toFloat().coerceIn(0f, 1f)
            val isReached = chapter.timestampMs <= positionMs
            drawCircle(
                color = if (isReached) completedColor else pendingColor,
                radius = if (isReached) 3.dp.toPx() else 2.dp.toPx(),
                center = Offset(size.width * fraction, lineY)
            )
        }
    }
}

@Composable
private fun ChapterFlowCard(
    state: ChapterPlaybackState,
    chapterCount: Int,
    onChapterSelect: (Long) -> Unit
) {
    val active = state.activeChapter
    val next = state.nextChapter
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (state.isTransitionImminent) Color(0x5B3B8EFF) else Color(0x3D26335C)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (state.isTransitionImminent) T("下一章即将开始") else T("章节进度"),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    T("已完成 %1\$s/%2\$s").format(state.completedCount.coerceAtMost(chapterCount), chapterCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD6E1F6)
                )
            }
            active?.let { chapter ->
                Spacer(Modifier.height(6.dp))
                Text(
                    T("当前 · %1\$s").format(chapter.title),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { state.chapterProgress },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color(0x454E5F86)
                )
            }
            if (active != null || next != null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { state.previousChapter?.let { onChapterSelect(it.timestampMs) } },
                        enabled = state.previousChapter != null,
                        label = { Text(T("上一章")) },
                        leadingIcon = { Icon(Icons.Default.SkipPrevious, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    AssistChip(
                        onClick = { active?.let { onChapterSelect(it.timestampMs) } },
                        enabled = active != null,
                        label = { Text(T("重播本章")) },
                        leadingIcon = { Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    AssistChip(
                        onClick = { next?.let { onChapterSelect(it.timestampMs) } },
                        enabled = next != null,
                        label = { Text(T("下一章")) },
                        leadingIcon = { Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            when {
                next != null && state.remainingInChapterMs != null -> Text(
                    T("接下来 · %1\$s · %2\$s · %3\$s 后进入").format(next.title, formatTime(next.timestampMs), formatTime(state.remainingInChapterMs)),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (state.isTransitionImminent) Color.White else Color(0xFFD6E1F6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                active != null -> Text(
                    T("最后一章 · 本曲结束后将完成本次章节收听"),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD6E1F6)
                )
                else -> Text(
                    T("章节尚未开始，播放后会自动显示进度与下一章前瞻。"),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD6E1F6)
                )
            }
        }
    }
}

@Composable
private fun ProgramGuide(
    program: FeaturedTrackProgram,
    positionMs: Long,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onChapterSelect: (Long) -> Unit
) {
    val activeChapterIndex = program.chapters.indexOfLast { it.timestampMs <= positionMs }
    val activeChapter = activeChapterIndex.takeIf { it >= 0 }?.let(program.chapters::getOrNull)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = Color(0x3D26335C)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(T("章节与笔记"), style = MaterialTheme.typography.titleSmall)
                    activeChapter?.let { chapter ->
                        Text(
                            T("当前 · %1\$s  %2\$s").format(formatTime(chapter.timestampMs), chapter.title),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFD6E1F6),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                TextButton(onClick = onToggleExpanded) { Text(if (expanded) T("收起") else T("展开")) }
            }
            if (expanded) {
                program.chapters.forEachIndexed { index, chapter ->
                    TextButton(
                        onClick = { onChapterSelect(chapter.timestampMs) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "${formatTime(chapter.timestampMs)}  ${chapter.title}",
                            style = if (index == activeChapterIndex) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                            color = if (index == activeChapterIndex) Color.White else Color(0xFFBDCAE4),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                program.notes.take(3).forEach { note ->
                    Text(
                        note,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD6E1F6),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LyricsStage(
    lyrics: List<LyricLine>,
    positionMs: Long,
    offsetMs: Long,
    fontScale: Int,
    onNudgeOffset: (Long) -> Unit,
    onResetOffset: () -> Unit,
    onSetFontScale: (Int) -> Unit
) {
    // 歌词时间偏移实时作用于高亮定位；正偏移让歌词提前出现，负偏移让歌词延后。
    val adjustedPosition = positionMs + offsetMs
    val activeIndex = lyrics.indexOfLast { it.timestampMs <= adjustedPosition }
    val anchorIndex = activeIndex.coerceAtLeast(0)
    val visibleLines = (anchorIndex - 1..anchorIndex + 1)
        .mapNotNull(lyrics::getOrNull)
        .distinctBy { it.timestampMs to it.text }
    val activeStyle = when (fontScale) {
        0 -> MaterialTheme.typography.bodyMedium
        1 -> MaterialTheme.typography.titleMedium
        else -> MaterialTheme.typography.titleLarge
    }
    val inactiveStyle = when (fontScale) {
        0 -> MaterialTheme.typography.bodySmall
        1 -> MaterialTheme.typography.bodyMedium
        else -> MaterialTheme.typography.titleSmall
    }
    val offsetSeconds = offsetMs / 1000.0
    val offsetLabel = if (offsetMs == 0L) "0.0s" else String.format(java.util.Locale.US, "%+.1fs", offsetSeconds)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = Color(0x4526335C)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    T("离线逐行歌词"),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD6E1F6),
                    modifier = Modifier.weight(1f)
                )
                Text(T("字号"), style = MaterialTheme.typography.labelSmall, color = Color(0xFF8FA3C8))
                listOf(0 to T("小"), 1 to T("中"), 2 to T("大")).forEach { (scale, label) ->
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (scale == fontScale) Color.White else Color(0xFF8FA3C8),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onSetFontScale(scale) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            if (lyrics.isEmpty()) {
                // 无歌词占位：避免空白，提示用户可以继续欣赏或后续补充 LRC 文件。
                Text(
                    T("暂无歌词"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    T("这首歌没有可用的逐行歌词，继续享受音乐吧。"),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBDCAE4)
                )
            } else {
                visibleLines.forEach { line ->
                    val isActive = activeIndex >= 0 && line.timestampMs == lyrics.getOrNull(activeIndex)?.timestampMs
                    Text(
                        line.text,
                        style = if (isActive) activeStyle else inactiveStyle,
                        color = if (isActive) Color.White else Color(0xFFBDCAE4),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    T("偏移 %1\$s").format(offsetLabel),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFB6C6E9)
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { onNudgeOffset(-LYRIC_OFFSET_STEP_MS) }) { Text("−0.5s", color = Color.White) }
                TextButton(onClick = { onNudgeOffset(LYRIC_OFFSET_STEP_MS) }) { Text("+0.5s", color = Color.White) }
                if (offsetMs != 0L) {
                    TextButton(onClick = onResetOffset) { Text(T("重置"), color = Color(0xFFB6C6E9)) }
                }
            }
        }
    }
}

@Composable
private fun DynamicPlaybackAtmosphere(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val atmosphere = rememberInfiniteTransition(label = "playbackAtmosphere")
    val drift by atmosphere.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "atmosphereDrift"
    )
    val glow by atmosphere.animateFloat(
        initialValue = 0.34f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5_800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "atmosphereGlow"
    )
    val motion = if (isPlaying) drift else 0.5f
    val glowAlpha = if (isPlaying) glow else 0.26f
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF071127))
    ) {
        Image(
            painter = painterResource(R.drawable.muse_playback_atmosphere),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.08f + motion * 0.05f
                    scaleY = 1.08f + motion * 0.05f
                    translationX = (motion - 0.5f) * 34f
                    translationY = (0.5f - motion) * 46f
                }
                .alpha(if (isPlaying) 0.94f else 0.72f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0x73030718),
                        0.45f to Color(0x22030718),
                        1f to Color(0xB8070B18)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x443B8EFF).copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QueueSheet(
    queue: List<Track>,
    currentTrack: Track?,
    onDismiss: () -> Unit,
    onPlay: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onMove: (Int, Int) -> Unit,
    onRemovePlayed: () -> Unit,
    onClear: () -> Unit
) {
    val listState = rememberLazyListState()
    val currentIndex = remember(queue, currentTrack?.id) {
        queue.indexOfFirst { it.id == currentTrack?.id }
    }
    val upcomingTracks = remember(queue, currentIndex) {
        queue.drop(if (currentIndex >= 0) currentIndex + 1 else 0)
    }
    val upcomingDurationMs = remember(upcomingTracks) { upcomingTracks.sumOf { it.durationMs.coerceAtLeast(0L) } }
    val unresolvedUpcomingCount = remember(upcomingTracks) { upcomingTracks.count { it.durationMs <= 0L } }
    val queueSummary = remember(queue, currentIndex, upcomingTracks, upcomingDurationMs, unresolvedUpcomingCount) {
        if (queue.isEmpty()) {
            T("队列为空")
        } else {
            buildString {
                append(T("%1\$s 首歌曲").format(queue.size))
                if (currentIndex >= 0) append(T(" · 正在播放第 %1\$s 首").format(currentIndex + 1))
                if (upcomingTracks.isNotEmpty()) {
                    append(T(" · 接下来 "))
                    append(if (upcomingDurationMs > 0L) formatTime(upcomingDurationMs) else T("正在读取时长"))
                    if (unresolvedUpcomingCount > 0) append(T("（%1\$s 首时长待解析）").format(unresolvedUpcomingCount))
                }
            }
        }
    }
    LaunchedEffect(queue, currentTrack?.id) {
        if (currentIndex >= 0) {
            // 当前项上方保留分段标题，打开队列后直接落在正在播放区域。
            listState.scrollToItem(if (currentIndex > 0) currentIndex + 1 else 0)
        }
    }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(T("播放队列"), style = MaterialTheme.typography.headlineSmall)
                    Text(
                        queueSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (currentIndex > 0) TextButton(onClick = onRemovePlayed) { Text(T("移除已播")) }
                if (queue.isNotEmpty()) TextButton(onClick = onClear) { Text(T("清空队列")) }
            }
            Spacer(Modifier.height(8.dp))
            if (queue.isEmpty()) {
                Text(T("从歌曲菜单中选择“加入播放队列”，即可在这里管理播放顺序。"), modifier = Modifier.padding(vertical = 24.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 26.dp)) {
                    if (currentIndex > 0) {
                        item(key = "queue_before_current") {
                            QueueSectionLabel(T("队列前序"))
                        }
                        items(queue.take(currentIndex).withIndex().toList(), key = { "queue_before_${it.value.id}" }) { indexedTrack ->
                            QueueTrackItem(
                                index = indexedTrack.index,
                                item = indexedTrack.value,
                                isCurrent = false,
                                lastIndex = queue.lastIndex,
                                onPlay = onPlay,
                                onMove = onMove,
                                onRemove = onRemove
                            )
                        }
                    }
                    if (currentIndex >= 0) {
                        item(key = "queue_now_playing") {
                            QueueSectionLabel(T("正在播放"))
                        }
                        item(key = "queue_current_${queue[currentIndex].id}") {
                            QueueTrackItem(
                                index = currentIndex,
                                item = queue[currentIndex],
                                isCurrent = true,
                                lastIndex = queue.lastIndex,
                                onPlay = onPlay,
                                onMove = onMove,
                                onRemove = onRemove
                            )
                        }
                    }
                    val nextStartIndex = if (currentIndex >= 0) currentIndex + 1 else 0
                    if (nextStartIndex < queue.size) {
                        item(key = "queue_up_next") {
                            QueueSectionLabel(if (currentIndex >= 0) T("接下来") else T("播放队列"))
                        }
                        items(queue.drop(nextStartIndex).withIndex().toList(), key = { "queue_next_${it.value.id}" }) { indexedTrack ->
                            QueueTrackItem(
                                index = nextStartIndex + indexedTrack.index,
                                item = indexedTrack.value,
                                isCurrent = false,
                                lastIndex = queue.lastIndex,
                                onPlay = onPlay,
                                onMove = onMove,
                                onRemove = onRemove
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueSectionLabel(label: String) {
    Text(
        text = label,
        modifier = Modifier.padding(top = 12.dp, start = 8.dp, bottom = 4.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun QueueTrackItem(
    index: Int,
    item: Track,
    isCurrent: Boolean,
    lastIndex: Int,
    onPlay: (Int) -> Unit,
    onMove: (Int, Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    val reduceMotion = rememberReducedMotion()
    val interaction = rememberMuseInteraction()
    val container by animateColorAsState(
        targetValue = if (isCurrent) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        animationSpec = tween(motionMillis(reduceMotion, MuseMotion.STATE_MILLIS)),
        label = "queue_current_container"
    )
    ListItem(
        headlineContent = { Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(item.artist, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingContent = {
            AlbumArt(
                Modifier.size(48.dp),
                item.artworkUri,
                item.title,
                if (isCurrent) ArtEmphasis.Primary else ArtEmphasis.Secondary
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onMove(index, index - 1) }, enabled = index > 0) { Icon(Icons.Default.ArrowUpward, T("上移")) }
                IconButton(onClick = { onMove(index, index + 1) }, enabled = index < lastIndex) { Icon(Icons.Default.ArrowDownward, T("下移")) }
                IconButton(onClick = { onRemove(index) }) { Icon(Icons.Default.Delete, T("移出队列")) }
            }
        },
        colors = ListItemDefaults.colors(containerColor = container),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .musePressScale(interaction, reduceMotion)
            .clickable(interactionSource = interaction, indication = null) { onPlay(index) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutSheet(onDismiss: () -> Unit, onOpenGuide: () -> Unit) {
    var showLicenses by remember { mutableStateOf(false) }
    var showChangelog by remember { mutableStateOf(false) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 12.dp)) {
            Surface(modifier = Modifier.size(64.dp), shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.primaryContainer) {
                Image(
                    painter = painterResource(R.drawable.ic_muse_brand_mark),
                    contentDescription = T("Muse 图标"),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(T("Muse 本地音乐"), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(6.dp))
            Text(
                T("随时可听的私域音乐库：专题音频随应用一起交付，本地音乐只留在你的设备上。"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            AboutPoint(Icons.Default.LibraryMusic, T("内置内容"), T("随应用附带的专题音频，按主题整理，可离线收听，并可随时导出到「下载」目录。"))
            AboutPoint(Icons.Default.Album, T("通用播放"), T("也能当作完整的本地音乐播放器：扫描设备音乐库、队列与歌单、收藏、均衡器与歌词。"))
            AboutPoint(Icons.Default.FolderOpen, T("私域优先"), T("收藏、歌单、播放记录与音效设置都只留在本机，不登录、不上传。"))
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenGuide) { Text(T("新手引导")) }
                OutlinedButton(onClick = { showLicenses = true }) { Text(T("开源许可")) }
                OutlinedButton(onClick = { showChangelog = true }) { Text(T("版本与更新")) }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
    if (showLicenses) {
        InfoDialog(
            title = T("开源许可"),
            body = T("本应用基于 Kotlin、Jetpack Compose 与 AndroidX Media3 等开源项目构建，相关组件分别采用 Apache License 2.0、MIT License 等许可。完整许可文本可在应用内「开源许可」页查阅。"),
            onDismiss = { showLicenses = false }
        )
    }
    if (showChangelog) {
        InfoDialog(
            title = T("版本与更新"),
            body = T("1.0.0（Build 100）\n首个公开发布版本。\n\n· 专题音频改为随应用附带并支持导出到「下载」\n· 新增新手引导，可在「更多」中随时回看\n· 返回层级完整支持系统侧滑与返回键\n· 全局转场动效统一为 300 毫秒减速曲线\n\n更早的内部迭代版本未对外发布，此处不再列出。"),
            onDismiss = { showChangelog = false }
        )
    }
}

// ── 新手引导 ────────────────────────────────────────────────────────────────
// 首次启动自动展示一次，之后可从「更多 → 新手引导」随时回看。
// 五页结构：私域定位 → 内置内容 → 下载内置内容 → 通用播放能力 → 随时回看。
private data class GuidePage(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val body: String,
    val downloadTips: Boolean = false
)

private fun guidePages() = listOf(
    GuidePage(
        Icons.Default.LibraryMusic,
        T("欢迎使用 Muse"),
        T("这是一个「私域音乐库」：专题音频随应用一起交付，收听、收藏与歌单全部在你的设备上完成——不登录、不上传、不依赖云端。")
    ),
    GuidePage(
        Icons.Default.Album,
        T("开箱即听的专题音频"),
        T("应用已附带专题音频，按主题整理好，打开就能听。它们存放在应用自己的空间里，不需要任何存储权限。")
    ),
    GuidePage(
        Icons.Default.Download,
        T("把内置内容下载到本地"),
        T("内置音频可以随时导出到「下载」目录，文件名保持原样。之后用任何播放器都能打开，换设备、清缓存也不怕丢。"),
        downloadTips = true
    ),
    GuidePage(
        Icons.Default.PlayArrow,
        T("它也是一台完整的音乐播放器"),
        T("除了内置内容，你还能把设备上的音乐纳入进来：队列与歌单、收藏、均衡器、歌词与睡眠定时都支持。授权后仅在本机读取。")
    ),
    GuidePage(
        Icons.Default.Settings,
        T("随时回来查看"),
        T("以后想再看这份引导，打开右上角「更多 → 新手引导」即可。")
    )
)

@Composable
private fun OnboardingGuide(onFinish: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    val pages = guidePages()
    val current = pages[page.coerceIn(0, pages.lastIndex)]
    AlertDialog(
        onDismissRequest = onFinish,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(current.icon, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text(current.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${page + 1} / ${pages.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                Text(current.body, style = MaterialTheme.typography.bodyMedium)
                if (current.downloadTips) {
                    Spacer(Modifier.height(14.dp))
                    Column(
                        Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium)
                            .padding(14.dp)
                    ) {
                        Text(T("两个入口都能下载"), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            T("① 播放页右上角的「保存到下载」\n② 长按播放画面，在弹出菜单中选择「保存到下载」"),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = { if (page < pages.lastIndex) page++ else onFinish() }) {
                Text(if (page < pages.lastIndex) T("下一步") else T("开始使用"))
            }
        },
        dismissButton = {
            if (page < pages.lastIndex) OutlinedButton(onClick = onFinish) { Text(T("跳过")) }
        }
    )
}

@Composable
private fun AboutPoint(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, body: String) {
    Row(Modifier.padding(vertical = 6.dp)) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(
                body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun InfoDialog(title: String, body: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = { FilledTonalButton(onClick = onDismiss) { Text(T("知道了")) } }
    )
}

@Composable
private fun AboutItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatSpeed(speed: Float): String = when (speed) {
    speed.toInt().toFloat() -> speed.toInt().toString()
    else -> speed.toString().trimEnd('0').trimEnd('.')
}

private fun formatTime(milliseconds: Long): String {
    val safeSeconds = (milliseconds.coerceAtLeast(0L) / 1_000L)
    return "%d:%02d".format(safeSeconds / 60L, safeSeconds % 60L)
}

private fun repeatModeLabel(repeatMode: Int): String = when (repeatMode) {
    Player.REPEAT_MODE_ONE -> T("单曲循环")
    Player.REPEAT_MODE_ALL -> T("列表循环")
    else -> T("不循环")
}

private fun libraryStatusTitle(state: LibraryUiState): String = when (state) {
    LibraryUiState.Idle -> T("等待扫描设备音乐")
    LibraryUiState.Loading -> T("正在扫描设备音乐")
    LibraryUiState.PermissionRequired -> T("设备音乐需要访问权限")
    LibraryUiState.Empty -> T("暂未发现音乐")
    is LibraryUiState.Ready -> T("资料库已就绪")
    is LibraryUiState.Error -> T("扫描遇到问题")
}

private fun libraryStatusDescription(state: LibraryUiState): String = when (state) {
    LibraryUiState.Idle -> T("专题音频包无需权限；授权后还可读取设备中的本地音频。")
    LibraryUiState.Loading -> T("正在读取歌曲、专辑和本地标签信息。")
    LibraryUiState.PermissionRequired -> T("专题音频包可直接播放；授予权限后可额外扫描设备音乐。")
    LibraryUiState.Empty -> T("请确认音频位于设备媒体库，或复制到 Music 文件夹后重试。")
    is LibraryUiState.Ready -> T("已发现 %1\$s 首歌曲，按专辑信息自动整理。").format(state.trackCount)
    is LibraryUiState.Error -> state.message
}

/**
 * 把内置音频保存到下载目录（与 Gallery / VLC 一致的约定）：
 * - API 29+：用 MediaStore.Downloads，文件位于公共 Download 目录（无需权限）
 * - API 26-28：回退到应用专属目录 getExternalFilesDir(DIRECTORY_DOWNLOADS)
 * 保留 assets 里的原文件名（不重命名）。
 * 支持两种内置 uri：asset:///<path> 与 file:///（已解压缓存）
 */
private fun saveFeaturedAudioToDownloads(context: Context, track: Track): String {
    if (!track.isFeaturedAsset) return T("无法保存：仅内置专题音频支持下载")
    val uri = track.uri
    val scheme = uri.scheme?.lowercase()
    val fileName = (uri.lastPathSegment ?: track.title.ifBlank { "audio" }).let {
        if (it.contains('.')) it else "$it.mp3"
    }
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
        fileName.substringAfterLast('.', "").lowercase()
    ) ?: "audio/mpeg"

    return runCatching {
        val input = when (scheme) {
            "asset" -> {
                val assetPath = uri.path?.removePrefix("/")?.ifBlank { null }
                    ?: return T("保存失败：无法解析内置音频路径")
                context.assets.open(assetPath)
            }
            "file" -> java.io.FileInputStream(java.io.File(uri.path ?: return T("保存失败：无法解析缓存路径")))
            else -> return T("保存失败：不支持的音频来源（%1\$s）").format(scheme)
        }
        input.use { stream ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val values = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val outputUri = resolver.insert(collection, values)
                    ?: return T("保存失败：无法创建下载条目")
                try {
                    val output = resolver.openOutputStream(outputUri)
                        ?: run {
                            resolver.delete(outputUri, null, null)
                            return T("保存失败：无法打开输出流")
                        }
                    output.use { stream.copyTo(it) }
                    // 写入完成后再对系统可见，避免其他应用读到半截文件
                    val publish = android.content.ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }
                    resolver.update(outputUri, publish, null, null)
                    T("已保存到下载 /%1\$s").format(fileName)
                } catch (failure: Throwable) {
                    resolver.delete(outputUri, null, null)
                    throw failure
                }
            } else {
                val targetDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: return T("保存失败：无法访问下载目录")
                val target = java.io.File(targetDir, fileName)
                java.io.FileOutputStream(target).use { output -> stream.copyTo(output) }
                T("已保存到应用下载目录\n%1\$s").format(target.absolutePath)
            }
        }
    }.getOrElse { T("保存失败：%1\$s").format(it.message ?: T("未知错误")) }
}

/** 歌单名称编辑器的临时状态：playlistId 为空表示新建。 */
private data class PlaylistNameEditor(val playlistId: String?, val initialName: String)

/** 本地歌单列表与详情：列表页可新建/重命名/删除，详情页可播放或移除歌曲。 */
@Composable
internal fun LocalPlaylistsScreen(
    playlists: List<LocalPlaylist>,
    allTracks: List<Track>,
    contentPadding: PaddingValues,
    onPlayPlaylist: (LocalPlaylist) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onRenamePlaylist: (String, String) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onPlayTrack: (Track) -> Unit,
    onRemoveTrackFromPlaylist: (String, Long) -> Unit,
    onReorderTracks: (String, List<Long>) -> Unit,
    onMore: (Track) -> Unit
) {
    var selectedPlaylistId by remember { mutableStateOf<String?>(null) }
    var nameEditor by remember { mutableStateOf<PlaylistNameEditor?>(null) }
    val trackIndex = remember(allTracks) { allTracks.associateBy(Track::id) }
    val selectedPlaylist = playlists.firstOrNull { it.id == selectedPlaylistId }

    if (selectedPlaylist == null) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = contentPadding.calculateTopPadding() + 12.dp,
                bottom = contentPadding.calculateBottomPadding() + 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Text(T("本地歌单"), style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    T("自己整理的设备歌曲集合"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                FilledTonalButton(onClick = { nameEditor = PlaylistNameEditor(playlistId = null, initialName = "") }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(T("新建歌单"))
                }
                Spacer(Modifier.height(8.dp))
            }
            if (playlists.isEmpty()) {
                item {
                    Text(
                        T("还没有歌单。点击上方按钮新建，再在歌曲菜单里添加到歌单。"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(playlists, key = { it.id }) { playlist ->
                    PlaylistRow(
                        playlist = playlist,
                        coverArtUri = playlist.trackIds.firstNotNullOfOrNull { trackIndex[it]?.artworkUri },
                        coverSeed = playlist.trackIds.firstNotNullOfOrNull { trackIndex[it]?.title },
                        onOpen = { selectedPlaylistId = playlist.id },
                        onPlay = { onPlayPlaylist(playlist) },
                        onRename = { nameEditor = PlaylistNameEditor(playlist.id, playlist.name) },
                        onDelete = { onDeletePlaylist(playlist.id) }
                    )
                }
            }
        }
    } else {
        val playlistTracks = selectedPlaylist.trackIds.mapNotNull(trackIndex::get)
        // 拖拽排序的本地顺序：以歌单实际曲序为初始值，拖拽结束后整体回写持久化。
        var orderIds by remember(selectedPlaylist.id, selectedPlaylist.trackIds) { mutableStateOf(selectedPlaylist.trackIds) }
        var draggingId by remember { mutableStateOf<Long?>(null) }
        var dragOffsetY by remember { mutableFloatStateOf(0f) }
        val rowStepPx = with(LocalDensity.current) { PLAYLIST_ROW_STEP.toPx() }
        val orderedTracks = orderIds.mapNotNull(trackIndex::get)
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { selectedPlaylistId = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = T("返回歌单列表"))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            selectedPlaylist.name,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            T("%1\$s 首歌曲").format(playlistTracks.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                FilledTonalButton(onClick = { onPlayPlaylist(selectedPlaylist) }, enabled = playlistTracks.isNotEmpty()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(T("播放歌单"))
                }
                Spacer(Modifier.height(12.dp))
            }
            if (playlistTracks.isEmpty()) {
                item {
                    Text(
                        T("这个歌单还没有歌曲，可在歌曲菜单中添加到歌单。"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(orderedTracks, key = { it.id }) { track ->
                    val isDragging = draggingId == track.id
                    ListItem(
                        headlineContent = { Text(track.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        supportingContent = { Text("${track.artist} · ${track.album}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingContent = { AlbumArt(Modifier.size(48.dp), track.artworkUri, track.title, ArtEmphasis.Secondary) },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // 拖拽手柄：纵向拖动时按行高换算移动条目，不引入任何第三方依赖。
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .pointerInput(track.id) {
                                            detectDragGestures(
                                                onDragStart = {
                                                    draggingId = track.id
                                                    dragOffsetY = 0f
                                                },
                                                onDragEnd = {
                                                    draggingId = null
                                                    dragOffsetY = 0f
                                                    if (orderIds != selectedPlaylist.trackIds) {
                                                        onReorderTracks(selectedPlaylist.id, orderIds)
                                                    }
                                                },
                                                onDragCancel = {
                                                    draggingId = null
                                                    dragOffsetY = 0f
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    val currentId = draggingId ?: return@detectDragGestures
                                                    dragOffsetY += dragAmount.y
                                                    val from = orderIds.indexOf(currentId)
                                                    if (from < 0) return@detectDragGestures
                                                    val threshold = rowStepPx / 2f
                                                    when {
                                                        dragOffsetY > threshold && from < orderIds.lastIndex -> {
                                                            orderIds = orderIds.toMutableList().apply { add(from + 1, removeAt(from)) }
                                                            dragOffsetY -= rowStepPx
                                                        }
                                                        dragOffsetY < -threshold && from > 0 -> {
                                                            orderIds = orderIds.toMutableList().apply { add(from - 1, removeAt(from)) }
                                                            dragOffsetY += rowStepPx
                                                        }
                                                    }
                                                }
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.DragHandle,
                                        contentDescription = T("拖动调整顺序"),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onRemoveTrackFromPlaylist(selectedPlaylist.id, track.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = T("从歌单移除"))
                                }
                                IconButton(onClick = { onMore(track) }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = T("%1\$s 的歌曲选项").format(track.title))
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer { translationY = if (isDragging) dragOffsetY else 0f }
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { onPlayTrack(track) }
                    )
                }
            }
        }
    }

    nameEditor?.let { editor ->
        PlaylistNameSheet(
            title = if (editor.playlistId == null) T("新建歌单") else T("重命名歌单"),
            initialName = editor.initialName,
            onDismiss = { nameEditor = null },
            onConfirm = { name ->
                if (editor.playlistId == null) onCreatePlaylist(name) else onRenamePlaylist(editor.playlistId, name)
                nameEditor = null
            }
        )
    }
}

@Composable
private fun PlaylistRow(
    playlist: LocalPlaylist,
    coverArtUri: android.net.Uri?,
    coverSeed: String?,
    onOpen: () -> Unit,
    onPlay: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    ListItem(
        overlineContent = { Text(T("本地歌单"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) },
        headlineContent = { Text(playlist.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(T("%1\$s 首歌曲").format(playlist.trackIds.size), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingContent = {
            // 歌单封面：取歌单内第一首歌的封面；没有可用封面时回退到图标。
            if (coverArtUri != null || coverSeed != null) {
                AlbumArt(Modifier.size(48.dp), coverArtUri, coverSeed ?: playlist.name, ArtEmphasis.Secondary)
            } else {
                Surface(modifier = Modifier.size(48.dp), shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.secondaryContainer) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlaylistPlay, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPlay) { Icon(Icons.Default.PlayArrow, contentDescription = T("播放歌单")) }
                Box {
                    IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, contentDescription = T("歌单选项")) }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text(T("重命名")) }, onClick = { menuOpen = false; onRename() })
                        DropdownMenuItem(text = { Text(T("删除")) }, onClick = { menuOpen = false; onDelete() })
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clip(MaterialTheme.shapes.medium).clickable(onClick = onOpen)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistNameSheet(
    title: String,
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(T("歌单名称")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(T("取消")) }
                FilledTonalButton(
                    onClick = { onConfirm(name) },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) { Text(T("确定")) }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

/** 均衡器面板：开关、预设选择、频段滑块与低音增强。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioEffectsSheet(
    state: EqualizerState,
    onDismiss: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onSelectPreset: (Int) -> Unit,
    onBandLevelChange: (Int, Int) -> Unit,
    onBassStrengthChange: (Int) -> Unit,
    onOpenSystemPanel: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Text(T("音效与均衡器"), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            if (!state.available) {
                Spacer(Modifier.height(8.dp))
                Text(
                    T("当前设备未提供可用的均衡器，你仍可尝试打开系统音效面板。"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(T("均衡器"), style = MaterialTheme.typography.titleMedium)
                        Text(T("对当前输出音频生效"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = state.enabled, onCheckedChange = onToggleEnabled)
                }
                if (state.enabled) {
                    if (state.presets.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(T("预设"), style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.presets.forEachIndexed { index, preset ->
                                AssistChip(
                                    onClick = { onSelectPreset(index) },
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (index == state.presetIndex) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                            }
                                            Text(preset)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(T("频段增益"), style = MaterialTheme.typography.labelLarge)
                    state.bands.forEach { band ->
                        if (band.maxLevelMb > band.minLevelMb) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(formatBandFrequency(band.centerFreqHz), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                    Text("${"%.1f".format(band.levelMb / 100f)} dB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Slider(
                                    value = band.levelMb.coerceIn(band.minLevelMb, band.maxLevelMb).toFloat(),
                                    onValueChange = { onBandLevelChange(band.index, it.toInt()) },
                                    valueRange = band.minLevelMb.toFloat()..band.maxLevelMb.toFloat()
                                )
                            }
                        }
                    }
                    if (state.bassSupported) {
                        Spacer(Modifier.height(8.dp))
                        Text(T("低音增强"), style = MaterialTheme.typography.labelLarge)
                        Slider(
                            value = state.bassStrength.toFloat().coerceIn(0f, state.bassMax.toFloat()),
                            onValueChange = { onBassStrengthChange(it.toInt()) },
                            valueRange = 0f..state.bassMax.toFloat()
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onOpenSystemPanel, modifier = Modifier.fillMaxWidth()) { Text(T("打开系统音效面板")) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** 标签编辑面板：编辑标题 / 艺术家 / 专辑并写回媒体库。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagEditSheet(
    track: Track,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember(track.id) { mutableStateOf(track.title) }
    var artist by remember(track.id) { mutableStateOf(track.artist) }
    var album by remember(track.id) { mutableStateOf(track.album) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(T("编辑标签"), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                T("将写回设备媒体库；部分文件会弹出系统确认。"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(T("标题")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = artist, onValueChange = { artist = it }, label = { Text(T("艺术家")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = album, onValueChange = { album = it }, label = { Text(T("专辑")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(T("取消")) }
                FilledTonalButton(
                    onClick = { onSave(title, artist, album) },
                    enabled = title.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) { Text(T("保存")) }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

/** 添加到歌单面板：列出现有歌单并支持即时新建。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddToPlaylistSheet(
    track: Track,
    playlists: List<LocalPlaylist>,
    onDismiss: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onAddToPlaylist: (LocalPlaylist) -> Unit
) {
    var createOpen by remember { mutableStateOf(false) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(T("添加到歌单"), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(track.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(12.dp))
            ActionRow(Icons.Default.Add, T("新建歌单"), { createOpen = true })
            if (playlists.isEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(T("还没有歌单，先新建一个吧。"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                playlists.forEach { playlist ->
                    val alreadyAdded = track.id in playlist.trackIds
                    ActionRow(
                        icon = Icons.Default.PlaylistAdd,
                        label = if (alreadyAdded) T("%1\$s（已包含）").format(playlist.name) else playlist.name,
                        onClick = { onAddToPlaylist(playlist) }
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
    if (createOpen) {
        PlaylistNameSheet(
            title = T("新建歌单"),
            initialName = "",
            onDismiss = { createOpen = false },
            onConfirm = { name ->
                onCreatePlaylist(name)
                createOpen = false
            }
        )
    }
}

private fun formatBandFrequency(freqHz: Int): String =
    if (freqHz >= 1000) "${"%.1f".format(freqHz / 1000f)} kHz" else "$freqHz Hz"
