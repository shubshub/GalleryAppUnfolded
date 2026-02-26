package com.unfoldedgallery.app

import android.net.Uri

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val dateAdded: Long,
    val mimeType: String,
    val duration: Long = 0
) {
    val isVideo: Boolean get() = mimeType.startsWith("video/")
}
