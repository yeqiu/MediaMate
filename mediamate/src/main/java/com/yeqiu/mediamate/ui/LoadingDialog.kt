package com.yeqiu.mediamate.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.yeqiu.mediamate.R

class LoadingDialog : DialogFragment() {

    private lateinit var loadingImageView: ImageView
    private var rotationAnimator: ObjectAnimator? = null

    companion object {
        private val TAG = LoadingDialog::class.java.simpleName

        fun show(fragmentManager: FragmentManager): LoadingDialog {
            val dialog = LoadingDialog()
            dialog.show(fragmentManager, TAG)
            return dialog
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as? LoadingDialog)?.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_loading_media_mate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingImageView = view.findViewById(R.id.iv_loading)

        startRotationAnimation()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 停止动画
        stopRotationAnimation()
    }

    private fun startRotationAnimation() {
        rotationAnimator = ObjectAnimator.ofFloat(loadingImageView, "rotation", 0f, 360f).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }

    private fun stopRotationAnimation() {
        rotationAnimator?.cancel()
        rotationAnimator = null
    }

    override fun dismiss() {
        // 先停止动画再关闭对话框
        stopRotationAnimation()
        super.dismiss()
    }
}