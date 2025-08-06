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
    //文件名
    val displayName: String,
    //创建日期
    val dateAdded: Long,
    val folderId: String,
    //所在的目录名称
    val folderName: String,
    // 视频时长（单位毫秒）
    val duration: Long = 0L,
    //最终路径，file的绝对路径
    val finalPath: String = ""
) : Serializable{

    fun isVideo():Boolean{
        return mimeType.startsWith("video/")
    }
}



