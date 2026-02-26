package com.unfoldedgallery.app

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

object MediaLoader {

    fun loadAllMedia(context: Context): List<MediaItem> {
        val items = mutableListOf<MediaItem>()
        items.addAll(queryMedia(context, isVideo = false))
        items.addAll(queryMedia(context, isVideo = true))
        items.sortByDescending { it.dateAdded }
        return items
    }

    private fun queryMedia(context: Context, isVideo: Boolean): List<MediaItem> {
        val items = mutableListOf<MediaItem>()

        val collection = if (isVideo) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = mutableListOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.MIME_TYPE
        )

        if (isVideo) {
            projection.add(MediaStore.Video.VideoColumns.DURATION)
        }

        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection.toTypedArray(),
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val durationCol = if (isVideo) {
                cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)
            } else -1

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(collection, id)
                val name = cursor.getString(nameCol) ?: ""
                val date = cursor.getLong(dateCol)
                val mime = cursor.getString(mimeCol) ?: ""
                val duration = if (isVideo && durationCol >= 0) cursor.getLong(durationCol) else 0

                items.add(MediaItem(id, uri, name, date, mime, duration))
            }
        }

        return items
    }
}
