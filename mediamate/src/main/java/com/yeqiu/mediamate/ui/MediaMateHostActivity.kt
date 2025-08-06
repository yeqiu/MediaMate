package com.yeqiu.mediamate.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.yeqiu.mediamate.MediaMateManager
import com.yeqiu.mediamate.R
import com.yeqiu.mediamate.model.Constants
import com.yeqiu.mediamate.model.MediaActionType


/**
 * @project：achilles-armory
 * @author：小卷子
 * @date 2025/8/5
 * @describe：
 * @fix：
 */
class MediaMateHostActivity : AppCompatActivity() {


    companion object {

        fun startWithSelect(
            activity: FragmentActivity,
            mediaType: MediaActionType,
            maxSize: Int,
        ) {
            val intent = Intent(activity, MediaMateHostActivity::class.java).apply {
                putExtra(Constants.mediaType, mediaType.name)
                putExtra(Constants.maxSize, maxSize)
            }
            activity.startActivity(intent)
        }

        fun startWithTake(
            activity: FragmentActivity,
            mediaType: MediaActionType,
        ) {
            val intent = Intent(activity, MediaMateHostActivity::class.java).apply {
                putExtra(Constants.mediaType, mediaType.name)
            }
            activity.startActivity(intent)
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatus()
        setContentView(R.layout.activity_media_mate_host_media_mate)

       val  mediaType =
            intent.getStringExtra(Constants.mediaType)?.run { MediaActionType.valueOf(this) }
                ?: MediaActionType.SELECT_IMAGE

        val isSelect = mediaType == MediaActionType.SELECT_IMAGE || mediaType == MediaActionType.SELECT_VIDEO || mediaType == MediaActionType.SELECT_ALL

        initFragment(isSelect)

    }


    private fun initStatus() {

        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT

        setLightStatusBar()

    }

    //文字浅色
    private fun setDarkStatusBar() {
        val flags = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    //文字深色
    private fun setLightStatusBar() {
        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }


    //加载默认的Fragment
    private fun initFragment(isSelect: Boolean) {

        val fragment = if (isSelect) {
            MediaSelectFragment.getInstance()
        } else {
            CameraCaptureFragment.getInstance()
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_container, fragment)
            .commit()

    }


    override fun onDestroy() {
        super.onDestroy()
        MediaMateManager.resetConfig()
    }



}

