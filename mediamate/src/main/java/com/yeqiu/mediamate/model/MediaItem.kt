package com.yeqiu.mediamate.model

import android.net.Uri
import java.io.Serializable

/**
 * @project：AchillesArmory
 * @author：小卷子
 * @date 2025/7/27
 * @describe：
 * @fix：
 */
data class MediaItem(
    val uri: Uri,
    val mimeType: String,
    val displayName: String,
    val dateAdded: Long,
    val folderId: String,
    val folderName: String,
    val duration: Long = 0L,
    val finalPath: String = ""
) : Serializable{

    fun isVideo():Boolean{
        return mimeType.startsWith("video/")
    }
}