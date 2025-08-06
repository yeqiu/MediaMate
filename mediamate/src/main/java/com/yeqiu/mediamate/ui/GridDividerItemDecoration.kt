package com.yeqiu.mediamate.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView 网格布局分割线
 * @param dividerWidth 分割线宽度（dp），默认 2dp
 * @param dividerColor 分割线颜色，默认白色（Color.WHITE）
 */
class GridDividerItemDecoration(
    private val dividerWidth: Int = 2, // 默认 2dp
    @ColorInt private val dividerColor: Int = Color.WHITE // 默认白色
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        color = dividerColor
        style = Paint.Style.FILL
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontalDividers(c, parent)
        drawVerticalDividers(c, parent)
    }

    /** 绘制水平分割线（底部） */
    private fun drawHorizontalDividers(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        val columnCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = child.right + params.rightMargin
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerWidth

            // 跳过最后一行的分割线
            if ((i + columnCount) >= childCount) continue

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    /** 绘制垂直分割线（右侧） */
    private fun drawVerticalDividers(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        val columnCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + dividerWidth
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin

            // 跳过每行最后一个 item 的右侧分割线
            if ((i + 1) % columnCount == 0) continue

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    /** 设置 item 的间距（避免分割线被遮挡） */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(dividerWidth, dividerWidth, dividerWidth, dividerWidth)
    }
}