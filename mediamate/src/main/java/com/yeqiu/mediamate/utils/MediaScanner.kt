package com.yeqiu.mediamate.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.yeqiu.mediamate.MediaPermissionChecker
import com.yeqiu.mediamate.model.MediaActionType
import com.yeqiu.mediamate.model.MediaData
import com.yeqiu.mediamate.model.MediaFolder
import com.yeqiu.mediamate.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MediaScanner {

    /**
     * 同时扫描媒体文件和文件夹
     */
    suspend fun scanMediaData(
        context: Context,
        mediaType: MediaActionType,
    ): MediaData = withContext(Dispatchers.IO) {

        val mediaList = mutableListOf<MediaItem>()

        if (mediaType == MediaActionType.SELECT_IMAGE || mediaType == MediaActionType.SELECT_ALL) {
            mediaList += loadImages(context)
        }
        if (mediaType == MediaActionType.SELECT_VIDEO || mediaType == MediaActionType.SELECT_ALL) {
            mediaList += loadVideos(context)
        }

        val folderMap = mutableMapOf<String, MutableList<MediaItem>>()
        for (item in mediaList) {
            folderMap.getOrPut(item.folderId) { mutableListOf() }.add(item)
        }

        val folderList = folderMap.map { (folderId, items) ->
            val latest = items.maxByOrNull { it.dateAdded } ?: items.first()
            MediaFolder(
                folderId = folderId,
                folderName = latest.folderName,
                coverUri = latest.uri,
                itemCount = items.size
            )
        }.sortedByDescending { it.itemCount }

        MediaData(
            mediaItemList = mediaList.sortedByDescending { it.dateAdded },
            mediaFolderList = folderList
        )
    }
}


private fun loadImages(context: Context): List<MediaItem> {

    val missingPermission =
        MediaPermissionChecker.getMissingPermission(context, MediaActionType.SELECT_IMAGE)
    if (missingPermission.isNotEmpty()){
        val missingPermissionStr = missingPermission.joinToString()
        throw SecurityException("未获取到权限： $missingPermissionStr")
    }

    val images = mutableListOf<MediaItem>()
    val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    context.contentResolver.query(
        collection, projection, null, null, null
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
        val folderIdCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
        val folderNameCol =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val uri = Uri.withAppendedPath(collection, id.toString())
            images.add(
                MediaItem(
                    uri = uri,
                    mimeType = cursor.getString(mimeCol)?:"",
                    displayName = cursor.getString(nameCol)?:"",
                    dateAdded = cursor.getLong(dateCol)?:0,
                    folderId = cursor.getString(folderIdCol)?:"0",
                    folderName = cursor.getString(folderNameCol)?:"",
                    duration = 0L
                )
            )
        }
    }

    return images
}

private fun loadVideos(context: Context): List<MediaItem> {

    val missingPermission =
        MediaPermissionChecker.getMissingPermission(context, MediaActionType.SELECT_VIDEO)
    if (missingPermission.isNotEmpty()){
        val missingPermissionStr = missingPermission.joinToString()
        throw SecurityException("未获取到权限： $missingPermissionStr")
    }

    val videos = mutableListOf<MediaItem>()
    val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.MIME_TYPE,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DATE_ADDED,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.Media.DURATION
    )

    context.contentResolver.query(
        collection, projection, null, null, null
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
        val folderIdCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
        val folderNameCol =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val uri = Uri.withAppendedPath(collection, id.toString())
            videos.add(
                MediaItem(
                    uri = uri,
                    mimeType = cursor.getString(mimeCol),
                    displayName = cursor.getString(nameCol),
                    dateAdded = cursor.getLong(dateCol),
                    folderId = cursor.getString(folderIdCol),
                    folderName = cursor.getString(folderNameCol),
                    duration = cursor.getLong(durationCol)
                )
            )
        }
    }

    return videos
}

private fun loadImageFolders(context: Context): Map<String, MediaFolder> {
    val folders = mutableMapOf<String, MediaFolder>()

    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(
        collection, projection, null, null, sortOrder
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val folderIdCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
        val folderNameCol =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val folderId = cursor.getString(folderIdCol)
            val folderName = cursor.getString(folderNameCol)

            if (folders.containsKey(folderId)) {
                val existing = folders[folderId]!!
                folders[folderId] = existing.copy(itemCount = existing.itemCount + 1)
            } else {
                val mediaId = cursor.getLong(idCol)
                val uri = Uri.withAppendedPath(collection, mediaId.toString())
                folders[folderId] = MediaFolder(
                    folderId = folderId,
                    folderName = folderName,
                    coverUri = uri,
                    itemCount = 1
                )
            }
        }
    }

    return folders
}

private fun loadVideoFolders(context: Context): Map<String, MediaFolder> {
    val folders = mutableMapOf<String, MediaFolder>()

    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME
    )

    val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

    context.contentResolver.query(
        collection, projection, null, null, sortOrder
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val folderIdCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
        val folderNameCol =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val folderId = cursor.getString(folderIdCol)
            val folderName = cursor.getString(folderNameCol)

            if (folders.containsKey(folderId)) {
                val existing = folders[folderId]!!
                folders[folderId] = existing.copy(itemCount = existing.itemCount + 1)
            } else {
                val mediaId = cursor.getLong(idCol)
                val uri = Uri.withAppendedPath(collection, mediaId.toString())
                folders[folderId] = MediaFolder(
                    folderId = folderId,
                    folderName = folderName,
                    coverUri = uri,
                    itemCount = 1
                )
            }
        }
    }

    return folders
}






/**
 * 扫描系统中所有媒体项（图片/视频），
 */
suspend fun scanMedia(
    context: Context,
    mediaType: MediaActionType
): List<MediaItem> = withContext(Dispatchers.IO) {
    val result = mutableListOf<MediaItem>()

    if (mediaType == MediaActionType.SELECT_IMAGE || mediaType == MediaActionType.SELECT_ALL) {
        result += loadImages(context)
    }
    if (mediaType == MediaActionType.SELECT_VIDEO || mediaType == MediaActionType.SELECT_ALL) {
        result += loadVideos(context)
    }

    result.sortedByDescending { it.dateAdded }
}

/**
 * 扫描媒体目录，按文件夹返回封面和数量，
 */
suspend fun scanMediaFolders(
    context: Context,
    mediaType: MediaActionType,
    onRequiredPermission: ((missingPermission: String, resume: () -> Unit) -> Unit)? = null,
): List<MediaFolder> = withContext(Dispatchers.IO) {

    val folderMap = mutableMapOf<String, MediaFolder>()

    if (mediaType == MediaActionType.SELECT_IMAGE || mediaType == MediaActionType.SELECT_ALL) {
        folderMap.putAll(loadImageFolders(context))
    }
    if (mediaType == MediaActionType.SELECT_VIDEO || mediaType == MediaActionType.SELECT_ALL) {
        folderMap.putAll(loadVideoFolders(context))
    }

    folderMap.values.sortedByDescending { it.itemCount }

}







