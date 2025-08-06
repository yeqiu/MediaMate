package com.yeqiu.mediamate.model

import java.io.Serializable

/**
 * @project：AchillesArmory
 * @author：小卷子
 * @date 2025/7/27
 * @describe：
 * @fix：
 */
data class MediaData(
    val mediaItemList: List<MediaItem>,
    val mediaFolderList: List<MediaFolder>
):Serializable
