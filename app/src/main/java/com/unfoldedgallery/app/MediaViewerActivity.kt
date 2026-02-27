package com.unfoldedgallery.app

import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.unfoldedgallery.app.databinding.ActivityMediaViewerBinding

class MediaViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = MediaListHolder.items
        if (items.isEmpty()) { finish(); return }

        val startPosition = intent.getIntExtra("position", 0)

        val pagerAdapter = MediaPagerAdapter(items)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.setCurrentItem(startPosition, false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                stopAllVideosExcept(position)
            }
        })

        binding.backButton.setOnClickListener { finish() }
    }

    private fun stopAllVideosExcept(currentPosition: Int) {
        val recyclerView = binding.viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView ?: return
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val holder = recyclerView.getChildViewHolder(child)
            if (holder.bindingAdapterPosition != currentPosition) {
                val videoView = child.findViewById<VideoView>(R.id.videoView)
                if (videoView != null && videoView.visibility == View.VISIBLE) {
                    videoView.stopPlayback()
                    videoView.visibility = View.GONE
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val recyclerView = binding.viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView ?: return
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val videoView = child.findViewById<VideoView>(R.id.videoView)
            if (videoView != null && videoView.isPlaying) {
                videoView.pause()
            }
        }
    }
}
