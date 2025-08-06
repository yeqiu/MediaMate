package com.yeqiu.mediamate.utils

import android.content.Context
import android.webkit.MimeTypeMap
import com.yeqiu.mediamate.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.zibin.luban.Luban
import java.io.File
import java.io.FileOutputStream

/**
 * @project：AchillesArmory
 * @author：小卷子
 * @date 2025/7/27
 * @describe：
 * @fix：
 */
internal object MediaFileUtil {

    /**
     * 将媒体文件复制到私有目录，并对图片进行压缩。视频文件仅复制不压缩。
     * 返回更新后的 MediaItem 列表，包含压缩或复制后的 finalPath。
     */
    suspend fun copyAndCompressMediaItems(
        context: Context,
        sourceList: List<MediaItem>,
        targetDir: File
    ): List<MediaItem> = withContext(Dispatchers.IO) {
        if (!targetDir.exists()) targetDir.mkdirs()

        val updatedList = mutableListOf<MediaItem>()

        for (item in sourceList) {

            val inputStream = context.contentResolver.openInputStream(item.uri) ?: continue
            val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(item.mimeType) ?: "jpg"

            val originalName = item.displayName.substringBeforeLast(".")

            val fileName = System.currentTimeMillis().toString()
            val targetFile = getUniqueFile(targetDir, fileName, extension)

            inputStream.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            val finalFile = if (item.mimeType.startsWith("image/")) {
                // 使用 Luban 压缩
                val file = Luban.with(context)
                    .load(targetFile)
                    .ignoreBy(100) // 忽略小于 100KB 的图片
                    .setTargetDir(targetDir.absolutePath)
                    .get()
                    .first()
                targetFile.delete()
                file

            } else {
                // 视频直接复制
                targetFile
            }

            updatedList += item.copy(finalPath = finalFile.absolutePath)
        }

        updatedList
    }


    /**
     * 如果文件已存在，自动加后缀避免覆盖，如 xxx.jpg -> xxx(1).jpg
     */
    private fun getUniqueFile(dir: File, baseName: String, ext: String): File {
        var index = 0
        var file: File
        do {
            val name = if (index == 0) "$baseName.$ext" else "$baseName($index).$ext"
            file = File(dir, name)
            index++
        } while (file.exists())
        return file
    }



    /**
     * 异步删除整个目录及其内容，包括目录本身。
     *
     * @param dir 要删除的目录 File 实例。
     * @return 删除是否成功。
     */
    suspend fun delete(dir: File): Boolean = withContext(Dispatchers.IO) {
        deleteRecursively(dir)
    }

    /**
     * 递归删除逻辑（私有函数）。
     */
    private fun deleteRecursively(file: File): Boolean {
        if (!file.exists()) return true
        if (file.isFile) return file.delete()

        val children = file.listFiles() ?: return false
        for (child in children) {
            val success = deleteRecursively(child)
            if (!success) return false
        }
        return file.delete()
    }



}
