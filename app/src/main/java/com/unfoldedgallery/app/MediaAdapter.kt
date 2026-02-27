package com.unfoldedgallery.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.unfoldedgallery.app.databinding.ItemMediaBinding

class MediaAdapter(
    private val onClick: (MediaItem, Int) -> Unit
) : ListAdapter<MediaItem, MediaAdapter.ViewHolder>(DIFF) {

    private var thumbnailSize = 154

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        thumbnailSize = parent.context.resources.displayMetrics.widthPixels / 7
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemMediaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MediaItem) {
            val request = Glide.with(binding.thumbnail)
                .load(item.uri)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(MediaStoreSignature(item.mimeType, item.dateAdded, 0))
                .override(thumbnailSize, thumbnailSize)
                .format(DecodeFormat.PREFER_RGB_565)
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .centerCrop()

            if (item.isVideo) {
                request.disallowHardwareConfig()
            } else {
                request.thumbnail(0.25f)
            }

            request.into(binding.thumbnail)

            binding.videoIcon.visibility = if (item.isVideo) View.VISIBLE else View.GONE
            binding.durationText.visibility = if (item.isVideo) View.VISIBLE else View.GONE

            if (item.isVideo) {
                binding.durationText.text = formatDuration(item.duration)
            }

            binding.root.setOnClickListener { onClick(item, bindingAdapterPosition) }
        }

        private fun formatDuration(millis: Long): String {
            val totalSeconds = millis / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%d:%02d", minutes, seconds)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<MediaItem>() {
            override fun areItemsTheSame(a: MediaItem, b: MediaItem) = a.id == b.id
            override fun areContentsTheSame(a: MediaItem, b: MediaItem) = a == b
        }
    }
}
