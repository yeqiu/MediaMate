package com.yeqiu.mediamate

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.yeqiu.mediamate.model.Constants
import com.yeqiu.mediamate.model.MediaActionType
import com.yeqiu.mediamate.model.MediaItem
import com.yeqiu.mediamate.ui.MediaMateHostActivity
import com.yeqiu.mediamate.utils.MediaFileUtil
import java.io.File

/**
 * @project：AchillesArmory
 * @author：小卷子
 * @date 2025/7/27
 * @describe：
 * @fix：
 */
object MediaMateManager {


    private var mediaMateConfig: MediaMateConfig? = null

    /**
     * 选择图片，视频
     * @param activity FragmentActivity
     * @param mediaType MediaSelectType 图片，视频，图片和视频
     * @param maxSize Int 最多选择的数量
     * @param onResult Function1<List<MediaItem>, Unit>
     */
    fun select(
        activity: FragmentActivity,
        mediaType: MediaActionType,
        maxSize: Int = 1,
        onResult: (List<MediaItem>) -> Unit
    ) {

        if (mediaType == MediaActionType.TAKE_IMAGE || mediaType == MediaActionType.TAKE_VIDEO) {
            throw IllegalArgumentException("传入的mediaType不合法,仅支持 SELECT_IMAGE,SELECT_VIDEO,SELECT_ALL")
        }

        mediaMateConfig = getMediaMateConfig().config { config ->
            config.mediaType = mediaType
            config.maxSize = maxSize
            config.onSelectResult = onResult
        }.get()


        MediaMateHostActivity.startWithSelect(activity, mediaType, maxSize)
    }


    /**
     * 拍照，拍视频 返回值为空时表示获取文件失败
     * @param activity FragmentActivity
     * @param mediaType MediaActionType
     * @param onResult Function1<MediaItem, Unit>
     */
    fun take(
        activity: FragmentActivity,
        mediaType: MediaActionType,
        onResult: (MediaItem) -> Unit
    ) {

        if (mediaType == MediaActionType.SELECT_IMAGE || mediaType == MediaActionType.SELECT_VIDEO || mediaType == MediaActionType.SELECT_ALL) {
            throw IllegalArgumentException("传入的mediaType不合法,仅支持 TAKE_IMAGE,TAKE_VIDEO")
        }

        mediaMateConfig = getMediaMateConfig().config { config ->
            config.mediaType = mediaType
            config.onTakeResult = onResult
        }.get()


        MediaMateHostActivity.startWithTake(activity, mediaType)
    }


    /**
     * 清除私有目录媒体缓存，！！！【调用此方法会直接删除缓存文件】
     * @param context Context
     */
    suspend fun clearCache(context: Context) {
        val cacheDir = context.externalCacheDir?.absoluteFile ?: return
        val targetDir = File(cacheDir, Constants.cacheDirName)
        MediaFileUtil.delete(targetDir)
    }




    internal fun onSelectResult(result: List<MediaItem>) {

        getMediaMateConfig().onSelectResult?.invoke(result)
    }


    internal fun onTakeResult(result: MediaItem) {
        getMediaMateConfig().onTakeResult?.invoke(result)
    }


    internal fun getMediaMateConfig() = mediaMateConfig ?: MediaMateConfig.build()


    internal fun resetConfig() {
        this.mediaMateConfig = null
    }


}