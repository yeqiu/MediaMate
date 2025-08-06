package com.yeqiu.mediamate.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yeqiu.mediamate.MediaMateManager
import com.yeqiu.mediamate.R
import com.yeqiu.mediamate.viewmodel.CameraCaptureViewModel

/**
 * @project：achilles-armory
 * @author：小卷子
 * @date 2025/8/5
 * @describe：
 * @fix：
 */
internal class CameraCaptureFragment private constructor() : Fragment() {

    companion object {
        fun getInstance(): CameraCaptureFragment = CameraCaptureFragment()
    }

    private val viewModel by viewModels<CameraCaptureViewModel>()

    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>
    private lateinit var takeVideoLauncher: ActivityResultLauncher<Uri>

    private val mediaType by lazy {
        MediaMateManager.getMediaMateConfig().mediaType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_camera_capture, null)
        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLauncher()
        viewModel.capture(mediaType)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                LoadingDialog.show(childFragmentManager)
            } else {
                LoadingDialog.dismiss(childFragmentManager)
            }
        }

        viewModel.result.observe(viewLifecycleOwner) {
            MediaMateManager.onTakeResult(it)
            requireActivity().finish()
        }

        viewModel.cancel.observe(viewLifecycleOwner){
            if (it){
                requireActivity().finish()
            }

        }


    }



    private fun initLauncher() {
        takePhotoLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                viewModel.onCaptureResult(success, isVideo = false)
            }

        takeVideoLauncher =
            registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
                viewModel.onCaptureResult(success, isVideo = true)
            }

        // 监听 outputUri 准备完成后启动相机
        viewModel.launchCaptureEvent.observe(this) { (uri, isVideo) ->
            if (isVideo) takeVideoLauncher.launch(uri)
            else takePhotoLauncher.launch(uri)
        }
    }

}


