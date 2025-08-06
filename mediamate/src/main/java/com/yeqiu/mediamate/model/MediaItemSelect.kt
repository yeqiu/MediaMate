package com.yeqiu.mediamate.model

import androidx.databinding.ObservableBoolean
import java.io.Serializable

/**
 * @project：achilles-armory
 * @author：小卷子
 * @date 2025/8/3
 * @describe：
 * @fix：
 */
class MediaItemSelect(
    val mediaItem: MediaItem,
    val isSelect: ObservableBoolean = ObservableBoolean(false)
) : Serializable