package com.yeqiu.mediamate.viewmodel

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yeqiu.mediamate.MediaMateManager
import com.yeqiu.mediamate.utils.MediaScanner
import com.yeqiu.mediamate.model.Constants
import com.yeqiu.mediamate.model.MediaActionType
import com.yeqiu.mediamate.model.MediaData
import com.yeqiu.mediamate.model.MediaFolder
import com.yeqiu.mediamate.model.MediaItem
import com.yeqiu.mediamate.model.MediaItemSelect
import com.yeqiu.mediamate.utils.MediaFileUtil
import com.yeqiu.mediamate.utils.Util
import kotlinx.coroutines.launch
import java.io.File

/**
 * @project：achilles-armory
 * @author：小卷子
 * @date 2025/8/3
 * @describe：
 * @fix：
 */
class MediaSelectViewModel(application: Application) : AndroidViewModel(application) {


    val currentFolderName = MutableLiveData<String>()
    val mediaList = MutableLiveData<List<MediaItemSelect>>()
    val folderList = MutableLiveData<List<MediaFolder>>()
    val selectSize = MutableLiveData(0)
    val resultList = MutableLiveData<List<MediaItem>>()
    val loading = MutableLiveData<Boolean>()

    private var mediaType: MediaActionType = MediaActionType.SELECT_IMAGE
    private var maxSize: Int = 1
    private lateinit var mediaData: MediaData
    private val context by lazy {
        getApplication<Application>().applicationContext
    }


    fun initConfigArgument() {

        MediaMateManager.getMediaMateConfig().let {
            this@MediaSelectViewModel.mediaType = it.mediaType
            this@MediaSelectViewModel.maxSize = it.maxSize

            if (mediaType != MediaActionType.SELECT_IMAGE && mediaType != MediaActionType.SELECT_VIDEO && mediaType != MediaActionType.SELECT_ALL) {
                throw IllegalArgumentException("传入的mediaType不合法,仅支持 SELECT_IMAGE,SELECT_VIDEO,SELECT_ALL")
            }
        }

        getMediaList()

    }


    private fun getMediaList() {

        viewModelScope.launch {

            val mediaData = MediaScanner.scanMediaData(context, mediaType)

            val mediaList = mediaData.mediaItemList.map { MediaItemSelect(it) }
            val mediaFolderList = mediaData.mediaFolderList

            val allFolderList = mutableListOf(
                MediaFolder(
                    "all", "全部",
                    coverUri = if (mediaList.isNotEmpty()) mediaList.first().mediaItem.uri else "".toUri(),
                    itemCount = mediaList.size,
                )
            ).apply {
                addAll(mediaFolderList)
            }



            this@MediaSelectViewModel.mediaList.value = mediaList
            folderList.value = allFolderList
            currentFolderName.value = allFolderList.first().folderName

            this@MediaSelectViewModel.mediaData =
                MediaData(mediaData.mediaItemList, allFolderList)
        }
    }


    fun selectFolder(mediaFolder: MediaFolder) {

        getMediaListByFolder(mediaFolder)
    }

    private fun getMediaListByFolder(mediaFolder: MediaFolder) {

        if (!::mediaData.isInitialized) {
            Util.showToast(context, "未获取相册数据")
            return
        }


        currentFolderName.value = mediaFolder.folderName

        mediaList.value = if (mediaFolder.folderId == "all") {
            mediaData.mediaItemList.map { MediaItemSelect(it) }
        } else {
            mediaData.mediaItemList.filter { it.folderId == mediaFolder.folderId }
                .map { MediaItemSelect(it) }
        }


    }

    fun updateSelectSize() {
        val mediaList = mediaList.value ?: return
        selectSize.value = mediaList.count { it.isSelect.get() }
    }

    fun getMaxSize() = maxSize

    fun canSelect(): Boolean {

        val selectSize = selectSize.value ?: 0
        return selectSize < maxSize

    }

    fun doNext() {


        val mediaList =
            mediaList.value?.filter { it.isSelect.get() }?.map { it.mediaItem } ?: return

        if (mediaList.isEmpty()){
            Util.showToast(context,"请至少选择一项")
            return
        }


        loading.value = true

        val cacheDir = context.externalCacheDir?.absoluteFile ?: return
        val targetDir = File(cacheDir, Constants.cacheDirName)

        viewModelScope.launch {
            val result =
                MediaFileUtil.copyAndCompressMediaItems(context, mediaList, targetDir)
            loading.value = false
            resultList.value = result
        }
    }

    fun clearResult() {
        resultList.value = emptyList()
    }


}