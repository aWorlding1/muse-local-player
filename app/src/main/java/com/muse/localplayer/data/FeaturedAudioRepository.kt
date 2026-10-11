package com.muse.localplayer.data

import com.muse.localplayer.core.T

import android.content.Context
import android.content.res.AssetManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.muse.localplayer.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs

/**
 * Loads publisher-authored audio from APK assets.
 *
 * Legacy packages may keep one topic in featured_audio/pack.json. New packages can place multiple
 * independent topics under featured_audio/topics/<topic-id>/pack.json. Each topic owns its own
 * fallback metadata, cover, track overrides and program entries while retaining the same resilient
 * compressed-asset playback path used by second-packaged APKs.
 */
class FeaturedAudioRepository(private val context: Context) {

    /** 统一标准 manifest（assets/manifest.json），不存在则为 null 并回退到旧 pack.json 结构。 */
    private val manifest: ManifestConfig? by lazy { readManifest() }

    private data class ManifestConfig(
        val collection: JSONObject = JSONObject(),
        val series: Map<String, JSONObject> = emptyMap(),
        val trackOverrides: Map<String, TrackOverride> = emptyMap()
    )

    private fun readManifest(): ManifestConfig? = runCatching {
        val raw = context.assets.open(MANIFEST_FILE_NAME).bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val collection = root.optJSONObject("collection") ?: JSONObject()
        val series = root.optJSONObject("series")?.let { obj ->
            buildMap { obj.keys().forEach { put(it, obj.optJSONObject(it) ?: JSONObject()) } }
        }.orEmpty()
        // items 支持数组形式（新标准）和对象形式（旧 tracks 格式）
        val trackOverrides = buildMap {
            (root.optJSONObject("items") ?: root.optJSONObject("tracks"))?.let { obj ->
                obj.keys().forEach { path ->
                    obj.optJSONObject(path)?.let { put(path.trim(), it.toTrackOverride()) }
                }
            }
            (root.optJSONArray("items") ?: root.optJSONArray("tracks"))?.let { array ->
                for (i in 0 until array.length()) {
                    val item = array.optJSONObject(i) ?: continue
                    val path = item.optString("path").trim()
                    if (path.isNotEmpty() && !containsKey(path)) put(path, item.toTrackOverride())
                }
            }
        }
        ManifestConfig(collection, series, trackOverrides)
    }.getOrNull()

    private fun JSONObject.toTrackOverride(): TrackOverride = TrackOverride(
        title = optionalText("title"),
        artist = optionalText("artist"),
        album = optionalText("album"),
        trackNumber = optInt("trackNumber", 0).takeIf { it > 0 },
        year = optInt("year", 0).takeIf { it > 0 },
        program = toFeaturedTrackProgram()
    )

    suspend fun loadCatalog(): FeaturedAudioCatalog = withContext(Dispatchers.IO) {
        clearStaleMaterializedAssets()
        val packs = listTopicRoots().mapNotNull { rootAssetPath ->
            runCatching { loadPackFromRoot(rootAssetPath) }.getOrNull()
        }.sortedWith(
            compareBy<FeaturedAudioPack> { it.metadata.sortOrder }
                .thenBy { it.metadata.title.lowercase() }
                .thenBy(FeaturedAudioPack::id)
        )
        FeaturedAudioCatalog(topics = packs)
    }

    /** Legacy compatibility for callers that expect the first configured topic. */
    suspend fun loadPack(): FeaturedAudioPack = loadCatalog().activeFallback ?: FeaturedAudioPack()

    suspend fun loadTracks(): List<Track> = loadCatalog().topics.flatMap(FeaturedAudioPack::tracks)

    private fun loadPackFromRoot(rootAssetPath: String): FeaturedAudioPack {
        val configuration = readPackConfiguration(rootAssetPath)
        val artwork = featuredArtwork(configuration.metadata, rootAssetPath)
        val entries = listAudioAssetPaths(rootAssetPath)
            .mapNotNull { assetPath ->
                // A bad second-packaged file must never make the remaining topic disappear.
                runCatching {
                    val override = configuration.overrideFor(assetPath, rootAssetPath)
                    assetPath to assetPath.toFeaturedTrack(
                        packId = configuration.id,
                        pack = configuration.metadata,
                        override = override,
                        artworkUri = artwork.uri
                    )
                }.getOrNull()
            }
            .sortedWith(
                compareBy<Pair<String, Track>> { it.second.trackNumber }
                    .thenBy { it.second.title.lowercase() }
                    .thenBy { it.first.lowercase() }
            )
        val tracks = entries.map { it.second }
        val programsByTrackId = entries.mapNotNull { (assetPath, track) ->
            configuration.overrideFor(assetPath, rootAssetPath)?.program
                ?.takeIf(FeaturedTrackProgram::hasContent)
                ?.let { track.id to it }
        }.toMap()
        return FeaturedAudioPack(
            id = configuration.id,
            rootAssetPath = rootAssetPath,
            metadata = configuration.metadata,
            tracks = tracks,
            programsByTrackId = programsByTrackId,
            health = FeaturedTopicHealth(
                trackCount = tracks.size,
                resolvedDurationCount = tracks.count { it.durationMs > 0L },
                chapterCount = programsByTrackId.values.sumOf { it.chapters.size },
                noteCount = programsByTrackId.values.sumOf { it.notes.size },
                hasCustomCover = artwork.isCustom,
                hasPackConfiguration = configuration.hasPackConfiguration,
                hasExplicitStableId = configuration.hasExplicitStableId
            )
        )
    }

    private fun listTopicRoots(): List<String> {
        // 统一标准：优先用 manifest.json 中 series 声明的文件夹作为主题根
        manifest?.series?.keys?.takeIf { it.isNotEmpty() }?.let { declared ->
            return declared.toList()
        }
        // 零配置兜底：扫描 assets 根下所有子目录，排除装饰目录，有音频的即作为主题
        val topLevelFolders = context.assets.list("").orEmpty()
            .map { it.substringAfterLast('/') }
            .filter { it !in EXCLUDED_ASSET_FOLDERS && it.isNotBlank() }
        val autoRoots = topLevelFolders.filter { folder ->
            listAudioAssetPaths(folder).isNotEmpty()
        }
        if (autoRoots.isNotEmpty()) return autoRoots.sorted()

        // 回退：旧 featured_audio/topics/<id>/pack.json 多专题结构
        val topicDirectory = "$FEATURED_AUDIO_ROOT/$TOPICS_DIRECTORY"
        val declaredTopics = context.assets.list(topicDirectory)
            .orEmpty()
            .map { name -> "$topicDirectory/$name" }
            .filter { root ->
                context.assets.list(root).orEmpty().isNotEmpty() &&
                    (assetExists("$root/$PACK_FILE_NAME") || listAudioAssetPaths(root).isNotEmpty())
            }
        // A populated topics directory intentionally becomes the catalog source. This prevents its
        // nested audio from being silently duplicated into the legacy default topic.
        return declaredTopics.ifEmpty { listOf(FEATURED_AUDIO_ROOT) }
    }

    private fun readPackConfiguration(rootAssetPath: String): PackConfiguration {
        val fallbackId = if (rootAssetPath == FEATURED_AUDIO_ROOT) {
            DEFAULT_TOPIC_ID
        } else {
            rootAssetPath.substringAfterLast('/').normalizeTopicId().ifBlank { DEFAULT_TOPIC_ID }
        }
        // 统一标准：优先从 manifest.json 读取 series 配置
        val manifestConfig = manifest
        manifestConfig?.series?.get(rootAssetPath)?.let { seriesJson ->
            val collection = manifestConfig.collection
            return PackConfiguration(
                id = seriesJson.optionalText("id")?.normalizeTopicId()?.ifBlank { fallbackId } ?: fallbackId,
                hasPackConfiguration = true,
                hasExplicitStableId = seriesJson.optionalText("id") != null,
                metadata = FeaturedPackMetadata(
                    title = seriesJson.textOrDefault("title", collection.textOrDefault("title", DEFAULT_TITLE)),
                    eyebrow = collection.textOrDefault("subtitle", DEFAULT_EYEBROW),
                    description = seriesJson.textOrDefault("description", collection.textOrDefault("description", DEFAULT_DESCRIPTION)),
                    defaultArtist = collection.textOrDefault("defaultArtist", DEFAULT_ARTIST),
                    defaultAlbum = collection.textOrDefault("defaultAlbum", DEFAULT_ALBUM),
                    playLabel = collection.textOrDefault("playLabel", DEFAULT_PLAY_LABEL),
                    editionLabel = collection.optionalText("version"),
                    coverAssetPath = seriesJson.optionalText("cover") ?: seriesJson.optionalText("coverAsset"),
                    sortOrder = seriesJson.optInt("order", 0)
                ),
                trackOverrides = manifestConfig.trackOverrides
            )
        }
        // 回退：旧 pack.json 单文件配置
        return runCatching {
            val rawJson = context.assets.open("$rootAssetPath/$PACK_FILE_NAME").bufferedReader().use { it.readText() }
            val json = JSONObject(rawJson)
            val explicitId = json.optionalText("id")
            PackConfiguration(
                id = explicitId?.normalizeTopicId()?.ifBlank { fallbackId } ?: fallbackId,
                hasPackConfiguration = true,
                hasExplicitStableId = explicitId != null,
                metadata = FeaturedPackMetadata(
                    title = json.textOrDefault("title", readableTopicTitle(rootAssetPath)),
                    eyebrow = json.textOrDefault("eyebrow", DEFAULT_EYEBROW),
                    description = json.textOrDefault("description", DEFAULT_DESCRIPTION),
                    defaultArtist = json.textOrDefault("defaultArtist", DEFAULT_ARTIST),
                    defaultAlbum = json.textOrDefault("defaultAlbum", DEFAULT_ALBUM),
                    playLabel = json.textOrDefault("playLabel", DEFAULT_PLAY_LABEL),
                    identityLabel = json.optionalText("identity"),
                    editionLabel = json.optionalText("edition"),
                    listeningGuide = json.optionalText("listeningGuide"),
                    coverAssetPath = json.optionalText("coverAsset"),
                    sortOrder = json.optInt("sortOrder", 0)
                ),
                trackOverrides = json.trackOverrides()
            )
        }.getOrElse {
            // 零配置：没有 pack.json 也没有 manifest series，从文件夹名推导标题
            PackConfiguration(
                id = fallbackId,
                metadata = FeaturedPackMetadata(title = readableTopicTitle(rootAssetPath))
            )
        }
    }

    /** 从文件夹名推导可读标题：去前导序号，替换 _/- 为空格，首字母大写 */
    private fun readableTopicTitle(rootAssetPath: String): String {
        val folder = rootAssetPath.substringAfterLast('/')
        return folder
            .replace(Regex("^\\s*\\d+[._ -]*"), "")
            .replace('_', ' ')
            .replace('-', ' ')
            .trim()
            .split(' ')
            .joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }
            .ifBlank { DEFAULT_TITLE }
    }

    private fun JSONObject.textOrDefault(key: String, fallback: String): String =
        T(optString(key).trim().takeIf { it.isNotBlank() } ?: fallback)

    private fun JSONObject.optionalText(key: String): String? =
        optString(key).trim().takeIf { it.isNotBlank() }

    private fun String.normalizeTopicId(): String =
        trim().lowercase()
            .replace(Regex("[^a-z0-9_-]+"), "-")
            .trim('-')
            .take(MAX_TOPIC_ID_LENGTH)

    private fun JSONObject.trackOverrides(): Map<String, TrackOverride> {
        val tracksObject = optJSONObject("tracks") ?: return emptyMap()
        val overrides = linkedMapOf<String, TrackOverride>()
        val keys = tracksObject.keys()
        while (keys.hasNext()) {
            val rawAssetPath = keys.next()
            val assetPath = rawAssetPath.trim().replace('\\', '/')
            val item = tracksObject.optJSONObject(rawAssetPath) ?: continue
            if (assetPath.isBlank()) continue
            overrides[assetPath] = TrackOverride(
                title = item.optionalText("title")?.let { T(it) },
                artist = item.optionalText("artist")?.let { T(it) },
                album = item.optionalText("album")?.let { T(it) },
                trackNumber = item.optInt("trackNumber", 0).takeIf { it > 0 },
                year = item.optInt("year", 0).takeIf { it > 0 },
                program = item.toFeaturedTrackProgram()
            )
        }
        return overrides
    }

    private fun PackConfiguration.overrideFor(assetPath: String, rootAssetPath: String): TrackOverride? =
        trackOverrides[assetPath]
            ?: trackOverrides[assetPath.removePrefix("$FEATURED_AUDIO_ROOT/")]
            ?: trackOverrides[assetPath.removePrefix("$rootAssetPath/")]

    private fun JSONObject.toFeaturedTrackProgram(): FeaturedTrackProgram? {
        val chapters = optJSONArray("chapters").toFeaturedChapters()
        val notes = optJSONArray("notes").toProgramNotes()
        return FeaturedTrackProgram(chapters = chapters, notes = notes).takeIf(FeaturedTrackProgram::hasContent)
    }

    private fun JSONArray?.toFeaturedChapters(): List<FeaturedChapter> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                val item = optJSONObject(index) ?: continue
                val title = item.optionalText("title") ?: continue
                val timestampMs = item.optLong("timestampMs", -1L).takeIf { it >= 0L }
                    ?: item.optionalText("time")?.toTimestampMs()
                    ?: continue
                add(FeaturedChapter(timestampMs = timestampMs, title = title))
            }
        }.distinctBy { it.timestampMs to it.title }.sortedBy(FeaturedChapter::timestampMs)
    }

    private fun JSONArray?.toProgramNotes(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                optString(index).trim().takeIf { it.isNotBlank() }?.let(::add)
            }
        }.distinct()
    }

    private fun String.toTimestampMs(): Long? {
        val match = TIMESTAMP_REGEX.matchEntire(trim()) ?: return null
        val minutes = match.groupValues[1].toLongOrNull() ?: return null
        val seconds = match.groupValues[2].toLongOrNull() ?: return null
        val fraction = match.groupValues.getOrElse(3) { "" }.padEnd(3, '0').take(3).toLongOrNull() ?: 0L
        if (seconds !in 0..59) return null
        return minutes * 60_000L + seconds * 1_000L + fraction
    }

    private fun listAudioAssetPaths(directory: String): List<String> {
        return context.assets.list(directory).orEmpty().flatMap { name ->
            val childPath = "$directory/$name"
            val children = context.assets.list(childPath).orEmpty()
            when {
                children.isNotEmpty() -> listAudioAssetPaths(childPath)
                name.substringAfterLast('.', "").lowercase() in AUDIO_EXTENSIONS -> listOf(childPath)
                else -> emptyList()
            }
        }
    }

    private fun String.toFeaturedTrack(
        packId: String,
        pack: FeaturedPackMetadata,
        override: TrackOverride?,
        artworkUri: Uri
    ): Track {
        val fileName = substringAfterLast('/').substringBeforeLast('.')
        val fallbackTitle = fileName
            .replace(Regex("^\\s*\\d+[._ -]*"), "")
            .replace('_', ' ')
            .replace('-', ' ')
            .trim()
            .split(' ')
            .joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }
            .ifBlank { T("未命名专题音频") }
        val fallbackTrackNumber = Regex("^\\s*(\\d+)").find(fileName)
            ?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: 0
        val metadata = readMetadata(this)
        val albumTitle = override?.album ?: metadata.album ?: pack.defaultAlbum
        return Track(
            id = stableAssetId(this),
            title = override?.title ?: metadata.title ?: fallbackTitle,
            artist = override?.artist ?: metadata.artist ?: pack.defaultArtist,
            album = albumTitle,
            albumId = -abs(("$packId:$albumTitle").hashCode().toLong()) - 2L,
            durationMs = metadata.durationMs,
            uri = resolvePlayableUri(this),
            artworkUri = artworkUri,
            trackNumber = override?.trackNumber ?: metadata.trackNumber ?: fallbackTrackNumber,
            year = override?.year ?: metadata.year ?: 0,
            source = TrackSource.FEATURED_ASSET,
            featuredTopicId = packId
        )
    }

    /**
     * Standard builds keep audio uncompressed for direct Media3 asset playback. If a secondary
     * packer compresses added audio, materialize a private copy and continue with file playback.
     */
    private fun resolvePlayableUri(assetPath: String): Uri {
        if (runCatching { context.assets.openFd(assetPath).use { } }.isSuccess) {
            return Uri.parse("asset:///$assetPath")
        }
        val cacheDirectory = materializedAssetCacheDirectory()
        if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
        val fileName = assetPath.substringAfterLast('/').ifBlank { "audio" }
        val safeName = "${assetCacheKey(assetPath)}_$fileName"
        val outputFile = File(cacheDirectory, safeName)
        if (outputFile.exists() && outputFile.length() > 0L) return Uri.fromFile(outputFile)

        val stagingFile = File(cacheDirectory, "$safeName.part")
        runCatching {
            context.assets.open(assetPath, AssetManager.ACCESS_STREAMING).use { input ->
                stagingFile.outputStream().use(input::copyTo)
            }
            if (outputFile.exists()) outputFile.delete()
            check(stagingFile.renameTo(outputFile)) { T("无法准备内置音频缓存") }
        }.getOrElse {
            stagingFile.delete()
            throw it
        }
        return Uri.fromFile(outputFile)
    }

    private fun materializedAssetCacheDirectory(): File {
        val updateTimestamp = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
        }.getOrDefault(0L)
        return File(context.cacheDir, "$MATERIALIZED_ASSET_DIRECTORY-$updateTimestamp")
    }

    private fun clearStaleMaterializedAssets() {
        val activeDirectory = materializedAssetCacheDirectory()
        context.cacheDir.listFiles()
            .orEmpty()
            .filter { it.isDirectory && it.name.startsWith(MATERIALIZED_ASSET_DIRECTORY) && it.name != activeDirectory.name }
            .forEach { staleDirectory -> runCatching { staleDirectory.deleteRecursively() } }
        activeDirectory.listFiles()
            .orEmpty()
            .filter { it.isFile && it.name.endsWith(".part") }
            .forEach { partialFile -> runCatching { partialFile.delete() } }
    }

    private fun stableAssetId(assetPath: String): Long {
        val bits = UUID.nameUUIDFromBytes(assetPath.toByteArray(Charsets.UTF_8)).leastSignificantBits and Long.MAX_VALUE
        return -bits - 1L
    }

    private fun assetCacheKey(assetPath: String): String =
        UUID.nameUUIDFromBytes(assetPath.toByteArray(Charsets.UTF_8)).toString()

    private fun readMetadata(assetPath: String): AssetMetadata {
        readMetadataFromDescriptor(assetPath).getOrNull()?.let { return it }
        // Secondary packers may compress new assets; probe a disposable private copy instead.
        return readMetadataFromTemporaryFile(assetPath).getOrDefault(AssetMetadata())
    }

    private fun readMetadataFromDescriptor(assetPath: String): Result<AssetMetadata> = runCatching {
        withRetriever { retriever ->
            context.assets.openFd(assetPath).use { descriptor ->
                retriever.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                retriever.toAssetMetadata()
            }
        }
    }

    private fun readMetadataFromTemporaryFile(assetPath: String): Result<AssetMetadata> = runCatching {
        val suffix = ".${assetPath.substringAfterLast('.', "audio")}"
        val temporaryFile = File.createTempFile("muse_asset_probe_", suffix, context.cacheDir)
        try {
            context.assets.open(assetPath, AssetManager.ACCESS_STREAMING).use { input ->
                temporaryFile.outputStream().use(input::copyTo)
            }
            withRetriever { retriever ->
                retriever.setDataSource(temporaryFile.absolutePath)
                retriever.toAssetMetadata()
            }
        } finally {
            temporaryFile.delete()
        }
    }

    private inline fun <T> withRetriever(block: (MediaMetadataRetriever) -> T): T {
        val retriever = MediaMetadataRetriever()
        return try {
            block(retriever)
        } finally {
            retriever.release()
        }
    }

    private fun MediaMetadataRetriever.toAssetMetadata(): AssetMetadata = AssetMetadata(
        title = text(MediaMetadataRetriever.METADATA_KEY_TITLE),
        artist = text(MediaMetadataRetriever.METADATA_KEY_ARTIST),
        album = text(MediaMetadataRetriever.METADATA_KEY_ALBUM),
        durationMs = text(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?.takeIf { it > 0L } ?: 0L,
        trackNumber = text(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.substringBefore('/')?.toIntOrNull(),
        year = text(MediaMetadataRetriever.METADATA_KEY_YEAR)?.toIntOrNull()
    )

    private fun MediaMetadataRetriever.text(key: Int): String? =
        extractMetadata(key)?.trim()?.takeUnless { it.isBlank() || it.equals("<unknown>", true) }

    private fun featuredArtwork(pack: FeaturedPackMetadata, rootAssetPath: String): ResolvedArtwork {
        val configuredPath = resolveCoverAssetPath(pack.coverAssetPath, rootAssetPath)
        return ResolvedArtwork(
            uri = configuredPath?.let { Uri.parse("file:///android_asset/$it") }
                ?: Uri.parse("android.resource://${context.packageName}/${R.drawable.muse_featured_hero}"),
            isCustom = configuredPath != null
        )
    }

    /**
     * 统一封面约定（与 Gallery/VLC 一致）：
     * 1. manifest.series.<folder>.cover 显式指定 → 优先用这张
     * 2. 主题文件夹下存在 cover.webp / cover.png / cover.jpg / cover.jpeg → 自动作为封面
     * 3. 都没有 → 返回 null，由调用方回退到默认资源
     */
    private fun resolveCoverAssetPath(rawPath: String?, rootAssetPath: String): String? {
        // 1. 显式指定的 cover
        val normalized = rawPath
            ?.trim()
            ?.replace('\\', '/')
            ?.removePrefix("./")
            ?.takeIf { it.isNotBlank() }
        if (normalized != null) {
            val candidates = buildList {
                add(normalized)
                if (!normalized.startsWith("$FEATURED_AUDIO_ROOT/") && !normalized.startsWith("$rootAssetPath/")) {
                    add("$rootAssetPath/$normalized")
                }
            }.distinct()
            candidates.firstOrNull { candidate ->
                candidate.substringAfterLast('.', "").lowercase() in IMAGE_EXTENSIONS && assetExists(candidate)
            }?.let { return it }
        }
        // 2. 主题文件夹下自动探测 cover.<ext>
        COVER_EXTENSIONS.forEach { ext ->
            val candidate = "$rootAssetPath/cover.$ext"
            if (assetExists(candidate)) return candidate
        }
        return null
    }

    private fun assetExists(path: String): Boolean =
        runCatching { context.assets.open(path).close() }.isSuccess

    private data class ResolvedArtwork(
        val uri: Uri,
        val isCustom: Boolean
    )

    private data class PackConfiguration(
        val id: String = DEFAULT_TOPIC_ID,
        val metadata: FeaturedPackMetadata = FeaturedPackMetadata(),
        val trackOverrides: Map<String, TrackOverride> = emptyMap(),
        val hasPackConfiguration: Boolean = false,
        val hasExplicitStableId: Boolean = false
    )

    private data class TrackOverride(
        val title: String? = null,
        val artist: String? = null,
        val album: String? = null,
        val trackNumber: Int? = null,
        val year: Int? = null,
        val program: FeaturedTrackProgram? = null
    )

    private data class AssetMetadata(
        val title: String? = null,
        val artist: String? = null,
        val album: String? = null,
        val durationMs: Long = 0L,
        val trackNumber: Int? = null,
        val year: Int? = null
    )

    private companion object {
        const val MANIFEST_FILE_NAME = "manifest.json"
        const val FEATURED_AUDIO_ROOT = "featured_audio"
        const val TOPICS_DIRECTORY = "topics"
        const val PACK_FILE_NAME = "pack.json"
        const val MATERIALIZED_ASSET_DIRECTORY = "muse_featured_audio"
        const val DEFAULT_TOPIC_ID = "default"
        // 这几项是元数据缺失时的兜底文案，需要按语言查表，因此不能用 const val（编译期常量）
        val DEFAULT_TITLE = T("本期专题音频")
        const val DEFAULT_EYEBROW = "MUSE · FEATURED"
        val DEFAULT_DESCRIPTION = T("随应用附带的专题声音内容。")
        val DEFAULT_ARTIST = T("精选内容")
        val DEFAULT_ALBUM = T("专题音频包")
        val DEFAULT_PLAY_LABEL = T("从头播放")
        const val MAX_TOPIC_ID_LENGTH = 48
        val AUDIO_EXTENSIONS = setOf("mp3", "m4a", "aac", "ogg", "wav", "flac")
        // 与 Gallery/VLC 一致的装饰资源排除清单
        val EXCLUDED_ASSET_FOLDERS = setOf("brand", "brand-assets", "images", "fonts", "webkit", "shaders")
        val IMAGE_EXTENSIONS = setOf("png", "webp", "jpg", "jpeg")
        val COVER_EXTENSIONS = listOf("webp", "png", "jpg", "jpeg")
        val TIMESTAMP_REGEX = Regex("(\\d{1,3}):(\\d{2})(?:[.:](\\d{1,3}))?")
    }
}
