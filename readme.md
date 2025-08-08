## MediaMate



## 🚀 快速接入

```kotlin
implementation 'com.github.yeqiu:MediaMate:1.0.0'
```


### MediaMate

图片选择器

本库不处理权限，在调用前请确保已经申请相关权限。主要是这三个权限

```xml
    <!--13以下-->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!--读取图片-->
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <!--读取视频-->
    <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
```

调用前根据选择的类型申请对应的权限，无权限调用会抛出异常，库内部会提示缺失的权限



### 文件处理逻辑说明

选择图片和视频，内部会将选择的文件复制到私有目录中。

拍照和拍视频会调用系统的相机，完成后复制到私有目录。

图片会经过压缩，视频不会压缩。 图片文件使用 [Luban](https://github.com/Curzibn/Luban) 压缩

⚠️ 私有目录文件不受系统相册管理，可避免用户误删或数据被清理，如需清除需要手动调用`MediaMateManager.clearCache`



返回的数据

```kotlin
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
    // 视频时长（单位毫秒）选择图片时本字段为0
    val duration: Long = 0L,
    //最终路径，file的绝对路径
    val finalPath: String = ""
)
```



### 调用方式

```kotlin
        MediaMateManager.select(this, MediaActionType.SELECT_ALL, 6) {
            it.forEach {
                tvResult.append("文件名 = ${it.displayName} , 路径=${it.finalPath}")
                tvResult.append("\n")
                tvResult.append("\n")
            }
        }
        
        //拍照
        MediaMateManager.take(this, MediaActionType.TAKE_IMAGE) {
            tvResult.append("文件名 = ${it.displayName} , 路径=${it.finalPath}")
            tvResult.append("\n")
            tvResult.append("\n")
        }
        //拍视频
        MediaMateManager.take(this, MediaActionType.TAKE_VIDEO) {
            tvResult.append("文件名 = ${it.displayName} , 路径=${it.finalPath}")
            tvResult.append("\n")
            tvResult.append("\n")
        }
```



### UI调整

请下载源码自行修改，页面相关的代码参见 `com.yeqiu.mediamate.ui`包下



### 仅使用扫描

com.yeqiu.mediamate.utils.MediaScanner 提供扫描图片，视频以及所在文件的功能。主要函数

~~~kotlin
    /**
     * 同时扫描媒体文件和文件夹
     */
    suspend fun scanMediaData(
        context: Context,
        mediaType: MediaActionType,
    ): MediaData = withContext(Dispatchers.IO)


    /**
	 * 扫描系统中所有媒体项（图片/视频），
	 */
	suspend fun scanMedia(
	    context: Context,
	    mediaType: MediaActionType
	): List<MediaItem> = withContext(Dispatchers.IO)


	/**
	 * 扫描媒体目录，按文件夹返回封面和数量，
	 */
	suspend fun scanMediaFolders(
	    context: Context,
	    mediaType: MediaActionType,
	    onRequiredPermission: ((missingPermission: String, resume: () -> Unit) -> Unit)? = null,
	): List<MediaFolder> = withContext(Dispatchers.IO)


~~~



### 使用到其他库

~~~kotlin
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.7.1")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("top.zibin:Luban:1.1.8")
~~~





### 📄 License

MIT License © 2025 YeQiu