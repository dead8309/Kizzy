package com.android.girish.vlog

import android.os.Handler
import android.os.Looper
import android.widget.Filter
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import com.android.girish.vlog.VlogModel.LogPriority
import com.android.girish.vlog.filter.Criteria
import com.android.girish.vlog.filter.KeywordFilter
import com.android.girish.vlog.filter.PriorityFilter

/**
 * Filter manager
 *
 * @property mFilterDelay The amount of delay (in ms) before the filter process starts
 * @constructor Create empty Filter manager
 */
internal class VlogRepository(private val mFilterDelay: Long = 100) : Filter() {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val mKeywordFilter = KeywordFilter()
    private val mPriorityFilter = PriorityFilter()
    private val mFilters: List<Criteria<VlogModel>>
    private val mVlogs: MutableList<VlogModel>
    private var mResultListener: ResultListener? = null

    init {
        mFilters = ArrayList()
        mFilters.add(mKeywordFilter)
        mFilters.add(mPriorityFilter)
        mVlogs = mutableListOf()
    }

    /**
     * Initiate filter process
     *
     */
    private fun initiateFilter() {

        // remove all callbacks and messages
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(
            object : Runnable {
                override fun run() {
                    this@VlogRepository.filter(null)
                }
            },
            mFilterDelay
        )
    }

    /**
     * For listening the filtered results
     *
     * @param resultListener
     */
    fun setResultListener(resultListener: ResultListener) {
        mResultListener = resultListener
        initiateFilter()
    }

    /**
     * pre-configures the keyword
     *
     * @param keyword
     */
    fun configureKeywordFilter(keyword: String) {
        mKeywordFilter.setKeyword(keyword)
        initiateFilter()
    }

    /**
     * pre-configures the log priority
     *
     * @param priority
     */
    fun configureLogPriority(@LogPriority priority: Int) {
        mPriorityFilter.setPriority(priority)
        initiateFilter()
    }

    /**
     * Result listener
     *
     * @constructor Create empty Result listener
     */
    interface ResultListener {
        /**
         * On filter results
         *
         * @param filterResults
         */
        fun onFilterResults(filterResults: List<VlogModel>)
    }

    @WorkerThread
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var filteredList: List<VlogModel> = mVlogs
        for (filter in mFilters) {
            filteredList = filter.meetCriteria(filteredList)
        }

        val filterResult = FilterResults()
        filterResult.values = filteredList
        filterResult.count = filterResult.count
        return filterResult
    }

    @UiThread
    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        mResultListener?.onFilterResults(results?.values as List<VlogModel>)
    }

    /**
     * Add log to the existing log repository
     *
     * @param model
     */
    fun feedLog(model: VlogModel) {
        mVlogs.add(model)
        initiateFilter()
    }

    /**
     * Clear logs
     *
     */
    fun clearLogs() {
        mVlogs.clear()
        initiateFilter()
    }

    fun reset() {
        mVlogs.clear()
        for (filter in mFilters) {
            filter.reset()
        }
        initiateFilter()
    }
}
