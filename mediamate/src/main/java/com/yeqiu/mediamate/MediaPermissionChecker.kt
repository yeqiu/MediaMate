package com.yeqiu.mediamate


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.yeqiu.mediamate.model.MediaActionType

object MediaPermissionChecker {

    private const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
    private const val READ_MEDIA_VIDEO = Manifest.permission.READ_MEDIA_VIDEO
    private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE


    fun getMissingPermission(
        context: Context,
        type: MediaActionType
    ):List<String> {

        val missingPermission = mutableListOf<String>()

        when (type) {
            MediaActionType.SELECT_IMAGE -> {
                getMissingImagePermission(context)?.let {
                    missingPermission.add(it)
                }
            }

            MediaActionType.SELECT_VIDEO -> {
                getMissingVideoPermission(context)?.let {
                    missingPermission.add(it)
                }
            }

            MediaActionType.SELECT_ALL -> {
                getMissingImagePermission(context)?.let {
                    missingPermission.add(it)
                }
                getMissingVideoPermission(context)?.let {
                    missingPermission.add(it)
                }
            }

            else -> {}
        }

        return missingPermission
    }


    private fun getMissingImagePermission(context: Context): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isGranted(context, READ_MEDIA_IMAGES)) READ_MEDIA_IMAGES else null
        } else {
            if (!isGranted(context, READ_EXTERNAL_STORAGE)) READ_EXTERNAL_STORAGE else null
        }
    }

    private fun getMissingVideoPermission(context: Context): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isGranted(context, READ_MEDIA_VIDEO)) READ_MEDIA_VIDEO else null
        } else {
            if (!isGranted(context, READ_EXTERNAL_STORAGE)) READ_EXTERNAL_STORAGE else null
        }
    }


    private fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

