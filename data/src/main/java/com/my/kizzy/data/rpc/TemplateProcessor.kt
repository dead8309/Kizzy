/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * TemplateProcessor.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc

import android.media.MediaMetadata

class TemplateKeys {
    companion object {
        const val MEDIA_TITLE = "{{media_title}}"
        const val MEDIA_ARTIST = "{{media_artist}}"
        const val MEDIA_AUTHOR = "{{media_author}}"
        const val APP_NAME = "{{app_name}}"
    }
}

class TemplateProcessor(
    private val mediaMetadata: MediaMetadata? = null,
    private val mediaPlayerAppName: String? = null,
    private val mediaPlayerPackageName: String? = null,
    private val detectedAppInfo: CommonRpc? = null,
) {
    fun process(template: String?): String? {
        if (template.isNullOrBlank()) return null

        var result = template

        if (mediaMetadata != null && mediaPlayerAppName != null && mediaPlayerPackageName != null) {
            result = result
                .replace(
                    TemplateKeys.MEDIA_TITLE,
                    mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
                )
                .replace(
                    TemplateKeys.MEDIA_ARTIST,
                    mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
                )
                .replace(
                    TemplateKeys.MEDIA_AUTHOR,
                    mediaMetadata.getString(MediaMetadata.METADATA_KEY_AUTHOR) ?: ""
                )
                .replace(TemplateKeys.APP_NAME, mediaPlayerAppName)
        } else if (detectedAppInfo != null) {
            result = result.replace(TemplateKeys.APP_NAME, detectedAppInfo.name)
        }

        // NOTE: remove unreplaced placeholders
        result = result.replace(
            Regex.fromLiteral("\\{\\{(media|app)_[^}]+\\}\\}"), ""
        )

        return result
    }
}
