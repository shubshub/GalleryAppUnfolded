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

        private var currentItem: MediaItem? = null
        private var videoActive = false

        fun bind(item: MediaItem) {
            currentItem = item
            videoActive = false

            // Reset state
            binding.videoView.setOnPreparedListener(null)
            binding.videoView.visibility = View.GONE
            binding.imageView.visibility = View.VISIBLE

            // Load poster frame via Glide (works for both images and videos)
            Glide.with(binding.imageView)
                .load(item.uri)
                .into(binding.imageView)

            if (item.isVideo) {
                binding.playButton.visibility = View.VISIBLE
                binding.playButton.setOnClickListener { playVideo() }
            } else {
                binding.playButton.visibility = View.GONE
                binding.playButton.setOnClickListener(null)
            }
        }

        fun playVideo() {
            val item = currentItem ?: return
            if (!item.isVideo || videoActive) return
            videoActive = true

            binding.playButton.visibility = View.GONE
            binding.videoView.visibility = View.VISIBLE

            val mediaController = MediaController(binding.root.context)
            mediaController.setAnchorView(binding.videoView)
            binding.videoView.setMediaController(mediaController)
            binding.videoView.setOnPreparedListener { mp ->
                binding.imageView.visibility = View.GONE
                mp.start()
            }
            binding.videoView.setVideoURI(item.uri)
        }

        fun stopVideo() {
            if (!videoActive) return
            videoActive = false

            binding.videoView.setOnPreparedListener(null)
            binding.videoView.stopPlayback()
            binding.videoView.visibility = View.GONE
            binding.imageView.visibility = View.VISIBLE

            // Reload poster frame
            currentItem?.let { item ->
                Glide.with(binding.imageView)
                    .load(item.uri)
                    .into(binding.imageView)
            }

            binding.playButton.visibility = if (currentItem?.isVideo == true) View.VISIBLE else View.GONE
        }

        fun recycle() {
            videoActive = false
            binding.videoView.setOnPreparedListener(null)
            binding.videoView.stopPlayback()
            binding.videoView.visibility = View.GONE
            binding.playButton.visibility = View.GONE
            binding.playButton.setOnClickListener(null)
            Glide.with(binding.imageView).clear(binding.imageView)
            currentItem = null
        }
    }
}
