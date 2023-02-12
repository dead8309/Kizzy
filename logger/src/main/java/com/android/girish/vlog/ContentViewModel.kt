package com.android.girish.vlog

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.girish.vlog.VlogModel.LogPriority

internal class ContentViewModel(private val mVlogRepository: VlogRepository) : ViewModel(), VlogRepository.ResultListener {

    val resultObserver = MutableLiveData<List<VlogModel>>()

    init {
        mVlogRepository.setResultListener(this)
    }

    override fun onFilterResults(filterResults: List<VlogModel>) {
        resultObserver.setValue(filterResults)
    }

    /**
     * This method is called by the view when user enters filter keyword
     *
     * @param keyword
     */
    fun onKeywordEnter(keyword: String) {
        mVlogRepository.configureKeywordFilter(keyword)
    }

    /**
     * This method is called by the view when user sets the log priority
     *
     * @param priority
     */
    fun onPrioritySet(@LogPriority priority: Int) {
        mVlogRepository.configureLogPriority(priority)
    }

    fun onClearLogs() {
        mVlogRepository.clearLogs()
    }

    fun onBubbleRemoved(context: Context) {
        Vlog.getInstance(context).stop()
    }
}
