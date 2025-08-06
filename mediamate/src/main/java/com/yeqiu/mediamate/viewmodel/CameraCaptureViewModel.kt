package com.yeqiu.mediamate.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yeqiu.mediamate.model.Constants
import com.yeqiu.mediamate.model.MediaActionType
import com.yeqiu.mediamate.model.MediaItem
import com.yeqiu.mediamate.utils.MediaFileUtil
import kotlinx.coroutines.launch
import java.io.File

/**
 * @project：media-mate
 * @author：小卷子
 * @date 2025/8/5
 * @describe：
 * @fix：
 */
internal class CameraCaptureViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    private lateinit var outputFile: File
    private lateinit var outputUri: Uri

    val launchCaptureEvent: MutableLiveData<Pair<Uri, Boolean>> = MutableLiveData()
    val result = MutableLiveData<MediaItem>()
    val loading = MutableLiveData<Boolean>()
    val cancel = MutableLiveData(false)

    fun capture(mediaType: MediaActionType) {
        val isVideo = when (mediaType) {
            MediaActionType.TAKE_IMAGE -> false
            MediaActionType.TAKE_VIDEO -> true
            else -> throw IllegalArgumentException("传入的mediaType不合法,仅支持 TAKE_IMAGE,TAKE_VIDEO")
        }

        val fileName = "${System.currentTimeMillis()}${if (isVideo) ".mp4" else ".jpg"}"
        val dir = File(context.cacheDir, "capture_temp").apply { mkdirs() }
        outputFile = File(dir, fileName)

        outputUri = FileProvider.getUriForFile(
            context, Constants.fileProvider,
            outputFile
        )

        launchCaptureEvent.postValue(outputUri to isVideo)
    }

    fun onCaptureResult(success: Boolean, isVideo: Boolean) {
        if (!success) {
            onResultFail()
            return
        }

        val mimeType = if (isVideo) "video/mp4" else "image/jpeg"
        val file = outputFile
        val uri = outputUri
        val displayName = file.name
        val dateAdded = System.currentTimeMillis()
        val folderId = file.parentFile?.absolutePath ?: ""
        val folderName = file.parentFile?.name ?: ""
        val duration = if (isVideo) getVideoDuration(context, uri) else 0L

        val mediaItem = MediaItem(
            uri = uri,
            mimeType = mimeType,
            displayName = displayName,
            dateAdded = dateAdded,
            folderId = folderId,
            folderName = folderName,
            duration = duration,
            finalPath = ""
        )

        onResult(mediaItem)
    }

    private fun getVideoDuration(context: Context, uri: Uri): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    private fun onResult(mediaItem: MediaItem) {

        loading.value = true

        val cacheDir = context.externalCacheDir?.absoluteFile ?: return
        val targetDir = File(cacheDir, Constants.cacheDirName)

        viewModelScope.launch {
            val result =
                MediaFileUtil.copyAndCompressMediaItems(context, listOf(mediaItem), targetDir)
            loading.value = false
            this@CameraCaptureViewModel.result.value = result.first()

        }


    }

    private fun onResultFail() {
        //用户取消
        cancel.value = true
    }
}
