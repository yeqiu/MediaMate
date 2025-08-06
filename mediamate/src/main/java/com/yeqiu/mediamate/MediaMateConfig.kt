package com.yeqiu.mediamate

import com.yeqiu.mediamate.model.MediaActionType
import com.yeqiu.mediamate.model.MediaItem

/**
 * @project：media-mate
 * @author：小卷子
 * @date 2025/8/5
 * @describe：
 * @fix：
 */
internal class MediaMateConfig private constructor() {


    companion object {
        fun build(): MediaMateConfig {
            return MediaMateConfig()
        }
    }


    var mediaType: MediaActionType = MediaActionType.SELECT_IMAGE
    var maxSize: Int = 1
    var onSelectResult: ((List<MediaItem>) -> Unit)? = null
    var onTakeResult: ((MediaItem) -> Unit)? = null


    fun config(config:(MediaMateConfig)->Unit):MediaMateConfig{
        config.invoke(this)
        return this
    }


    fun get(): MediaMateConfig {
        return this
    }


}