package com.kizzy.bubble_logger.screen

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kizzy.bubble_logger.viewholder.LogViewHolder
import com.kizzy.bubble_logger.BubbleDataHelper

internal class BubbleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_LOG = 0
    }

    sealed class Item {

        data class Log(
            val log: BubbleDataHelper.Log
        ) : Item() {
            var filterStrings: List<String>? = null
        }
    }

    var items: List<Item>? = null
        set(value) {
            field = value
            updateDisplayItems()
        }

    var displayItems: List<Item>? = null
        private set(newItems) {
            val oldItems = field
            field = newItems
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {

                override fun getOldListSize() = oldItems?.size ?: 0

                override fun getNewListSize() = newItems?.size ?: 0

                override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldItem = oldItems!![oldItemPosition]
                    val newItem = newItems!![newItemPosition]
                    if (oldItem::class != newItem::class) return false
                    return when (newItem) {
                        is Item.Log -> newItem.log.id == (oldItem as Item.Log).log.id
                    }
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldItem = oldItems!![oldItemPosition]
                    return when (val newItem = newItems!![newItemPosition]) {
                        is Item.Log -> {
                            oldItem as Item.Log
                            newItem.filterStrings == filterStrings &&
                                newItem.log.title == oldItem.log.title &&
                                newItem.log.message == oldItem.log.message &&
                                newItem.log.timestamp == oldItem.log.timestamp
                        }
                    }
                }
            }).dispatchUpdatesTo(this)
        }
    var filterStrings: List<String> = emptyList()
        set(value) {
            field = value
            updateDisplayItems()
        }

    private fun updateDisplayItems() {
        val filterText = filterStrings
        displayItems = if (filterText.isEmpty()) items
        else {
            val regex = filterStrings.joinToString(
                separator = "",
                prefix = ".*",
                postfix = ".*"
            ) {
                "(?=.*$it)"
            }.toRegex()
            items?.filter {
                when (it) {
                    is Item.Log -> {
                        val log = it.log
                        "${log.title?.toString()?.plus(" ") ?: ""}${log.message}".matches(regex)
                    }
                }
            }
        }
    }

    override fun getItemCount() = displayItems?.size ?: 0

    override fun getItemViewType(position: Int) = when (displayItems!![position]) {
        is Item.Log -> TYPE_LOG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_LOG -> LogViewHolder.create(parent)
        else -> throw IllegalStateException("Unknown view holder for type $viewType.")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = displayItems!![position]) {
            is Item.Log -> {
                (holder as LogViewHolder).bind(
                    title = item.log.title,
                    content = item.log.message,
                    timestamp = item.log.timestamp,
                    filterStrings = filterStrings,
                    logType = item.log.type
                )
                item.filterStrings = filterStrings
            }
        }
    }
}