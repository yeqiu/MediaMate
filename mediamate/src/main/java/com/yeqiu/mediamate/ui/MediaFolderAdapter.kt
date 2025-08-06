package com.yeqiu.mediamate.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yeqiu.mediamate.databinding.ItemMediaFolderBinding
import com.yeqiu.mediamate.model.MediaFolder
import com.yeqiu.mediamate.utils.Util

/**
 * @project：achilles-armory
 * @author：小卷子
 * @date 2025/8/3
 * @describe：
 * @fix：
 */
class MediaFolderAdapter(private val onItemClickListener: (MediaFolder) -> Unit) :
    RecyclerView.Adapter<MediaFolderAdapter.MediaFolderAdapterViewHolder>() {


    private val data = mutableListOf<MediaFolder>()

    fun setList(list: List<MediaFolder>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaFolderAdapterViewHolder {

        val binding = ItemMediaFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaFolderAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MediaFolderAdapterViewHolder, position: Int) {
        holder.binding.item = data.get(position)
        holder.binding.onClickListener = this

        Util.loadImage(holder.binding.ivFolder, data.get(position).coverUri)
    }


    fun clickSelect(item: MediaFolder) {
        onItemClickListener.invoke(item)
    }


    inner class MediaFolderAdapterViewHolder(val binding: ItemMediaFolderBinding) :
        RecyclerView.ViewHolder(binding.root)
}