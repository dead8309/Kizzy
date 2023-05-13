/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * MetadataResolver.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.get_current_data.media

import android.graphics.Bitmap
import android.media.MediaMetadata
import javax.inject.Inject

class MetadataResolver @Inject constructor() {
    fun getCoverArt(metadata: MediaMetadata): Bitmap? {
        return if (metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART) != null) metadata.getBitmap(
            MediaMetadata.METADATA_KEY_ALBUM_ART
        )
        else metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
    }

    fun getArtistOrAuthor(metadata: MediaMetadata): String? {
        return if (metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) != null) "by " + metadata.getString(
            MediaMetadata.METADATA_KEY_ARTIST
        ) else if (metadata.getString(MediaMetadata.METADATA_KEY_AUTHOR) != null) "by " + metadata.getString(
            MediaMetadata.METADATA_KEY_AUTHOR
        ) else null
    }
}