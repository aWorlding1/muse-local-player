package com.muse.localplayer.data

import com.muse.localplayer.core.T

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 标签写回结果：成功 / 需要用户授权（附带 IntentSender）/ 失败。 */
sealed interface TagWriteResult {
    data object Success : TagWriteResult
    data class NeedsPermission(val intentSender: IntentSender) : TagWriteResult
    data class Failed(val message: String) : TagWriteResult
}

/**
 * 通过 MediaStore 把标题 / 艺术家 / 专辑写回设备本地音频。
 * Android 10+ 修改非本应用拥有的文件会抛出 RecoverableSecurityException，
 * 此时返回其 userAction 的 IntentSender，交由界面申请确认后再重试。
 */
class MusicTagger(private val context: Context) {

    suspend fun writeTags(
        trackId: Long,
        title: String,
        artist: String,
        album: String
    ): TagWriteResult = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val uri = ContentUris.withAppendedId(collection, trackId)
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.TITLE, title.trim())
            put(MediaStore.Audio.Media.ARTIST, artist.trim())
            put(MediaStore.Audio.Media.ALBUM, album.trim())
        }
        try {
            val updatedRows = context.contentResolver.update(uri, values, null, null)
            if (updatedRows > 0) {
                TagWriteResult.Success
            } else {
                TagWriteResult.Failed(T("未能写入标签，该文件可能已不在媒体库中。"))
            }
        } catch (security: SecurityException) {
            val intentSender = if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                security is RecoverableSecurityException
            ) {
                security.userAction.actionIntent.intentSender
            } else {
                null
            }
            if (intentSender != null) {
                TagWriteResult.NeedsPermission(intentSender)
            } else {
                TagWriteResult.Failed(T("没有修改该文件的权限。"))
            }
        } catch (error: Exception) {
            TagWriteResult.Failed(T("更新标签失败：%1\$s").format(error.message ?: T("未知错误")))
        }
    }
}
