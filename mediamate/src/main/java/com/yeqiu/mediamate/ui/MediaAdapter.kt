package com.yeqiu.mediamate.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yeqiu.mediamate.databinding.ItemMediaBinding
import com.yeqiu.mediamate.model.MediaItemSelect
import com.yeqiu.mediamate.utils.Util

/**
 * @project：AchillesArmory
 * @author：小卷子
 * @date 2025/7/27
 * @describe：
 * @fix：
 */
class MediaAdapter(private val onSelectListener:(MediaItemSelect)->Unit) : RecyclerView.Adapter<MediaAdapter.MediaAdapterViewHolder>() {

    private val data = mutableListOf<MediaItemSelect>()

    fun setList(list: List<MediaItemSelect>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaAdapterViewHolder {


        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MediaAdapterViewHolder, position: Int) {
        holder.binding.item = data.get(position)
        holder.binding.onClickListener = this

        Util.loadImage(holder.binding.ivImg, data.get(position).mediaItem.uri)

    }


    fun clickSelect(item: MediaItemSelect) {
        onSelectListener.invoke(item)
    }

    inner class MediaAdapterViewHolder(val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root)
}