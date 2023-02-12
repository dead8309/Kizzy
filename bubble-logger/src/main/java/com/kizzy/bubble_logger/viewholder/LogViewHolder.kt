package com.kizzy.bubble_logger.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kizzy.bubble_logger.LogType
import com.kizzy.bubble_logger.databinding.ListItemLogBinding
import java.text.DateFormat
import java.util.*

/**
 * [RecyclerView.ViewHolder] for a log in the bubble.
 */
class LogViewHolder(itemLogBinding: ListItemLogBinding) : RecyclerView.ViewHolder(itemLogBinding.root) {

    companion object {

        private val dateFormat = DateFormat.getDateTimeInstance()

        /**
         * Create an instance of [LogViewHolder].
         */
        fun create(parent: ViewGroup): LogViewHolder {
            val itemBinding = ListItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LogViewHolder(itemBinding)
        }
    }

    private val titleTextView = itemLogBinding.titleTextView
    private val contentTextView = itemLogBinding.contentTextView
    private val timeTextView = itemLogBinding.timeTextView

    /**
     * Bind data to this [LogViewHolder].
     * @param title Title of the log.
     * @param content Content of the log.
     * @param timestamp Timestamp of the log.
     * @param filterStrings Filter strings for text highlighting.
     * @param logType for showing log colors
     */
    @SuppressLint("SetTextI18n")
    fun bind(
        title: CharSequence?,
        content: CharSequence,
        timestamp: Long,
        filterStrings: List<String>,
        logType: LogType
    ) {
        val errorColor = Color.parseColor("#F44336")
        val infoColor = Color.parseColor("#4CAF50")
        val warnColor = Color.parseColor("#FFC107")
        val debugColor = Color.parseColor("#2196F3")
        val defaultColor = Color.parseColor("#9C27B0")

        when (logType) {
            LogType.VERBOSE -> {
                titleTextView.setTextColor(defaultColor)
                contentTextView.setTextColor(defaultColor)
            }
            LogType.DEBUG -> {
                titleTextView.setTextColor(debugColor)
                contentTextView.setTextColor(debugColor)
            }
            LogType.INFO -> {
                titleTextView.setTextColor(infoColor)
                contentTextView.setTextColor(infoColor)
            }
            LogType.WARN -> {
                titleTextView.setTextColor(warnColor)
                contentTextView.setTextColor(warnColor)
            }
            LogType.ERROR -> {
                titleTextView.setTextColor(errorColor)
                contentTextView.setTextColor(errorColor)
            }
        }

        titleTextView.apply {
            visibility = if (title == null) View.GONE else View.VISIBLE
            text = buildString {
                append(getLogPriorityInitials(logType))
                append("/")
                append(title.highlightedWith(filterStrings))
            }
        }
        contentTextView.text = content.highlightedWith(filterStrings)
        timeTextView.text = dateFormat.format(Date(timestamp))
    }

    private fun getLogPriorityInitials(logType: LogType): String {
        return when (logType) {
            LogType.DEBUG -> "D"
            LogType.ERROR -> "E"
            LogType.INFO -> "I"
            LogType.VERBOSE -> "V"
            LogType.WARN -> "W"
        }
    }

    private fun CharSequence?.highlightedWith(
        filterStrings: List<String>
    ) = SpannableStringBuilder(this).apply {
        for (filterString in filterStrings) {
            val startIndex = indexOf(filterString).let { if (it == -1) null else it } ?: continue
            val endIndex = startIndex + filterString.length
            setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                UnderlineSpan(),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}
