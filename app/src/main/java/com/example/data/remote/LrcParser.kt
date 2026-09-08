package com.example.data.remote

import com.example.data.model.LyricLine
import java.util.regex.Pattern

object LrcParser {
    // Regex for [mm:ss.xx] or [mm:ss.xxx] or [mm:ss]
    private val TIME_TAG_REGEX = Pattern.compile("\\[(\\d{1,2}):(\\d{2})(?:\\.(\\d{1,3}))?\\]")

    fun parse(lrcContent: String?): List<LyricLine> {
        if (lrcContent.isNullOrBlank()) return emptyList()

        val lines = mutableListOf<LyricLine>()
        val stringLines = lrcContent.lines()

        for (rawLine in stringLines) {
            val trimmed = rawLine.trim()
            if (trimmed.isEmpty()) continue

            val matcher = TIME_TAG_REGEX.matcher(trimmed)
            val timestamps = mutableListOf<Long>()
            var lastMatchEnd = 0

            while (matcher.find()) {
                val minutes = matcher.group(1)?.toLongOrNull() ?: 0L
                val seconds = matcher.group(2)?.toLongOrNull() ?: 0L
                val fractionStr = matcher.group(3)
                val millis = when {
                    fractionStr == null -> 0L
                    fractionStr.length == 1 -> fractionStr.toLong() * 100L
                    fractionStr.length == 2 -> fractionStr.toLong() * 10L
                    fractionStr.length >= 3 -> fractionStr.take(3).toLong()
                    else -> 0L
                }

                val totalMs = (minutes * 60 * 1000) + (seconds * 1000) + millis
                timestamps.add(totalMs)
                lastMatchEnd = matcher.end()
            }

            if (timestamps.isNotEmpty()) {
                val lyricText = trimmed.substring(lastMatchEnd).trim()
                if (lyricText.isNotEmpty() || timestamps.isNotEmpty()) {
                    for (ts in timestamps) {
                        lines.add(LyricLine(timestampMs = ts, text = lyricText))
                    }
                }
            }
        }

        return lines.sortedBy { it.timestampMs }
    }

    /**
     * Finds the index of the currently active lyric line for the given audio position in milliseconds.
     */
    fun findActiveLyricIndex(lyrics: List<LyricLine>, currentPositionMs: Long): Int {
        if (lyrics.isEmpty()) return -1
        if (currentPositionMs < lyrics.first().timestampMs) return 0

        var low = 0
        var high = lyrics.size - 1
        var candidate = 0

        while (low <= high) {
            val mid = (low + high) / 2
            if (lyrics[mid].timestampMs <= currentPositionMs) {
                candidate = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return candidate
    }
}
