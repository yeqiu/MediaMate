package com.yeqiu.mediamate.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide

/**
 * @project：achilles-armory
 * @author：小卷子
 * @date 2025/8/4
 * @describe：
 * @fix：
 */
internal object Util {

     fun showToast(context: Context,message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    fun loadImage(imageView: ImageView,uri: Uri){

        Glide.with(imageView)
            .load(uri)
            .centerCrop()
            .into(imageView)

    }


}



fun logInfo(log:String){

    Log.i("ValkyrieLog", "ValkyrieLog.logInfo: $log")
}