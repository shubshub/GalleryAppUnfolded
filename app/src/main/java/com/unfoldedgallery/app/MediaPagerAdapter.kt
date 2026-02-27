package com.unfoldedgallery.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unfoldedgallery.app.databinding.ItemMediaViewerBinding

class MediaPagerAdapter(
    private val items: List<MediaItem>
) : RecyclerView.Adapter<MediaPagerAdapter.PageViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemMediaViewerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: PageViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    class PageViewHolder(
        private val binding: ItemMediaViewerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MediaItem) {
            if (item.isVideo) {
                binding.imageView.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE

                val mediaController = MediaController(binding.root.context)
                mediaController.setAnchorView(binding.videoView)
                binding.videoView.setMediaController(mediaController)
                binding.videoView.setVideoURI(item.uri)
                binding.videoView.setOnPreparedListener { it.start() }
            } else {
                binding.imageView.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE

                Glide.with(binding.imageView)
                    .load(item.uri)
                    .into(binding.imageView)
            }
        }

        fun recycle() {
            if (binding.videoView.isPlaying) {
                binding.videoView.stopPlayback()
            }
            binding.videoView.visibility = View.GONE
            Glide.with(binding.imageView).clear(binding.imageView)
        }
    }
}
