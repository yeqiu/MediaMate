package com.yeqiu.mediamate.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * @project：media-mate
 * @author：小卷子
 * @date 2025/8/5
 * @describe：
 * @fix：
 */
internal object AnimatorUtil {


    fun expandedWithAnimator(
        view: View,
        isExpanded: Boolean,
        onAnimationEnd: ((Boolean) -> Unit)? = null
    ) {

        val startAngle = if (isExpanded) 0f else 180f
        val endAngle = if (isExpanded) 180f else 0f

        val animator = ObjectAnimator.ofFloat(view, "rotation", startAngle, endAngle)
        animator.duration = 300L
        animator.interpolator = DecelerateInterpolator()

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd?.invoke(!isExpanded)
            }
        })

        animator.start()
    }

    fun showWhitAnim(view: View, isShow: Boolean) {


        // 初始位置（隐藏时在顶部之外，显示时在原始位置）
        val startY = if (isShow) -view.height.toFloat() else 0f
        val endY = if (isShow) 0f else -view.height.toFloat()

        // 设置初始位置（避免动画开始前的闪烁）
        view.translationY = startY

        // 创建位移动画
        val animator = ObjectAnimator.ofFloat(view, "translationY", startY, endY)
        animator.duration = 300L
        animator.interpolator = DecelerateInterpolator()

        // 动画监听
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                if (isShow) {
                    view.visibility = View.VISIBLE // 显示时确保可见
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    view.visibility = View.GONE
                }
            }
        })

        animator.start()
    }


}