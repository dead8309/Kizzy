/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * MediaMetadataSanitizer.kt is part of Kizzy
 *  *  * and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.get_current_data.media

object MediaMetadataSanitizer {
    private val streamingBrowserPackages = setOf(
        "com.android.chrome",
        "com.chrome.beta",
        "com.chrome.dev",
        "com.chrome.canary",
        "org.mozilla.firefox",
        "com.microsoft.emmx",
        "com.opera.browser",
        "com.brave.browser",
        "com.duckduckgo.mobile.android",
        "com.sec.android.app.sbrowser",
        "com.vivaldi.browser"
    )

    private val streamingAppPackages = streamingBrowserPackages + "com.netflix.mediaclient"

    private val streamingCleanupPatterns = listOf(
        Regex("(?i)\\s*[\\|\\-–—]\\s*Netflix.*$"),
        Regex("(?i)\\s*[\\|\\-–—]\\s*Chrome.*$"),
        Regex("(?i)\\s*[\\|\\-–—]\\s*Google.*$"),
        Regex("(?i)\\s*[\\|\\-–—]\\s*Firefox.*$"),
        Regex("(?i)\\s*[\\|\\-–—]\\s*Edge.*$"),
        Regex("(?i)\\s*[\\|\\-–—]\\s*Opera.*$"),
        Regex("(?i)\\s*[\\|\\-–—]\\s*Brave.*$"),
        Regex("(?i)\\s*[\\|\\-–—]\\s*DuckDuckGo.*$"),
        Regex("(?i)\\s*[\\|\\-–—]\\s*Samsung Internet.*$"),
        Regex("(?i)https?://.*$")
    )

    fun sanitizeMediaText(packageName: String?, text: String?): String? {
        val pkg = packageName ?: ""
        val value = text?.trim().orEmpty()
        if (value.isEmpty()) return null
        if (pkg in streamingAppPackages) {
            return streamingCleanupPatterns
                .fold(value) { acc, pattern -> acc.replace(pattern, "") }
                .trim()
        }
        return value
    }

    private fun extractEpisodeInfo(value: String?): String? {
        val text = value?.replace("—", "-")?.replace("–", "-")?.trim() ?: return null
        if (text.isEmpty()) return null

        Regex("(?i)s\\s*(\\d+)\\s*e\\s*(\\d+)").find(text)?.let {
            return "Season ${it.groupValues[1]} • Episode ${it.groupValues[2]}"
        }

        Regex("(?i)season\\s*(\\d+).*?episode\\s*(\\d+)").find(text)?.let {
            return "Season ${it.groupValues[1]} • Episode ${it.groupValues[2]}"
        }

        Regex("(?i)episode\\s*(\\d+)\\s*(?:of|/)\\s*(\\d+)").find(text)?.let {
            return "Episode ${it.groupValues[1]} of ${it.groupValues[2]}"
        }

        Regex("(?i)episode\\s*(\\d+)\\b").find(text)?.let {
            return "Episode ${it.groupValues[1]}"
        }

        return null
    }

    private fun cleanStreamingTitle(packageName: String, value: String?): String? {
        val v = value?.trim().orEmpty()
        if (v.isEmpty()) return null
        // For browser-based players the metadata often contains the site name or other
        // prefixes like "Aniwave - Show Name (2021)". Strip those leading prefixes.
        if (packageName in streamingBrowserPackages) {
            // Remove one or more leading segments like "Site - " or "Site: "
            val cleaned = Regex("^(?:[^\\-–—:|]+[\\-–—:|]\\s*)+").replace(v, "").trim()
            return cleaned.ifEmpty { null }
        }
        return v
    }

    fun summarizeStreamingDetails(packageName: String?, title: String?, album: String?, author: String?): String? {
        val pkg = packageName ?: ""
        if (pkg !in streamingAppPackages) {
            return sanitizeMediaText(pkg, album) ?: sanitizeMediaText(pkg, author)
        }

        val cleanedAlbum = sanitizeMediaText(pkg, album)
        val cleanedAuthor = sanitizeMediaText(pkg, author)
        val cleanedTitle = sanitizeMediaText(pkg, title)?.let { cleanStreamingTitle(pkg, it) } ?: sanitizeMediaText(pkg, title)

        // Try to extract episode info from album/author/title
        val episodeInfo = extractEpisodeInfo(cleanedAlbum) ?: extractEpisodeInfo(cleanedAuthor) ?: extractEpisodeInfo(cleanedTitle) ?: extractEpisodeInfo(title)

        // Derive a show name from the title by removing obvious episode markers and year suffixes
        val showName = cleanedTitle?.replace(Regex("(?i)\\s*(?:episode|ep|ep\\.|#)\\s*\\d+.*$"), "")
            ?.replace(Regex("\\(\\d{4}\\)"), "")
            ?.trim()

        return listOfNotNull(cleanedAlbum, cleanedAuthor, showName, episodeInfo)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .joinToString(" • ")
            .ifEmpty { null }
    }
}
