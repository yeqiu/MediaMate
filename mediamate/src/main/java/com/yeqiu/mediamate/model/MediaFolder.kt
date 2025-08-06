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
data class MediaFolder(
    val folderId: String,
    val folderName: String,
    val coverUri: Uri,   // 用于封面展示
    val itemCount: Int   // 文件夹下图片+视频的数量
):Serializable
