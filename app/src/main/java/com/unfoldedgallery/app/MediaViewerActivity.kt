package com.unfoldedgallery.app

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.unfoldedgallery.app.databinding.ActivityMediaViewerBinding

class MediaViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriString = intent.getStringExtra("uri") ?: run { finish(); return }
        val uri = Uri.parse(uriString)
        val isVideo = intent.getBooleanExtra("isVideo", false)

        binding.backButton.setOnClickListener { finish() }

        if (isVideo) {
            binding.imageView.visibility = android.view.View.GONE
            binding.videoView.visibility = android.view.View.VISIBLE

            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.videoView)
            binding.videoView.setMediaController(mediaController)
            binding.videoView.setVideoURI(uri)
            binding.videoView.setOnPreparedListener { it.start() }
        } else {
            binding.imageView.visibility = android.view.View.VISIBLE
            binding.videoView.visibility = android.view.View.GONE

            Glide.with(this)
                .load(uri)
                .into(binding.imageView)
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoView.isPlaying) {
            binding.videoView.pause()
        }
    }
}
