package com.unfoldedgallery.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.unfoldedgallery.app.databinding.ActivityMediaViewerBinding

class MediaViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaViewerBinding
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = MediaListHolder.items
        if (items.isEmpty()) { finish(); return }

        val startPosition = intent.getIntExtra("position", 0)
        currentPosition = startPosition

        val pagerAdapter = MediaPagerAdapter(items)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.setCurrentItem(startPosition, false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                stopVideoAtPosition(currentPosition)
                currentPosition = position
                if (items[position].isVideo) {
                    binding.viewPager.post { playVideoAtPosition(position) }
                }
            }
        })

        // Auto-play if initial page is a video (deferred until layout completes)
        if (items[startPosition].isVideo) {
            binding.viewPager.post { playVideoAtPosition(startPosition) }
        }

        binding.backButton.setOnClickListener { finish() }
    }

    private fun playVideoAtPosition(position: Int) {
        val recyclerView = binding.viewPager.getChildAt(0) as? RecyclerView ?: return
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as? MediaPagerAdapter.PageViewHolder
        holder?.playVideo()
    }

    private fun stopVideoAtPosition(position: Int) {
        val recyclerView = binding.viewPager.getChildAt(0) as? RecyclerView ?: return
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as? MediaPagerAdapter.PageViewHolder
        holder?.stopVideo()
    }

    override fun onPause() {
        super.onPause()
        stopVideoAtPosition(currentPosition)
    }
}
