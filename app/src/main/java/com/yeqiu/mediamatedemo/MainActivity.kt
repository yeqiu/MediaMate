package com.yeqiu.mediamatedemo

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yeqiu.mediamate.MediaMateManager
import com.yeqiu.mediamate.model.MediaActionType
import com.yeqiu.mediamate.utils.logInfo

class MainActivity : AppCompatActivity() {


    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }


        permissionLauncher.launch(permission)

        findViewById<TextView>(R.id.tv_select).setOnClickListener {

            select()
        }

        findViewById<TextView>(R.id.tv_take_image).setOnClickListener {

            MediaMateManager.take(this, MediaActionType.TAKE_IMAGE) {


                logInfo("TAKE_IMAGE")

                tvResult.text = ""

                tvResult.append("文件名 = ${it.displayName} , 路径=${it.finalPath}")
                tvResult.append("\n")
                tvResult.append("\n")
            }
        }

        findViewById<TextView>(R.id.tv_take_video).setOnClickListener {

            MediaMateManager.take(this, MediaActionType.TAKE_VIDEO) {


                tvResult.text = ""

                tvResult.append("文件名 = ${it.displayName} , 路径=${it.finalPath}")
                tvResult.append("\n")
                tvResult.append("\n")
            }
        }



        tvResult = findViewById<TextView>(R.id.tv_result)


    }


    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 判断是否所有权限都被授予
        val granted = permissions.all { it.value }
        if (granted) {
            onGrantedAction?.invoke()
        } else {

        }
    }


    private var onGrantedAction: (() -> Unit)? = null

    private fun select() {


        tvResult.text = ""



        MediaMateManager.select(this, MediaActionType.SELECT_ALL, 6) {

            it.forEach {
                tvResult.append("文件名 = ${it.displayName} , 路径=${it.finalPath}")
                tvResult.append("\n")
                tvResult.append("\n")

            }

        }

    }
}