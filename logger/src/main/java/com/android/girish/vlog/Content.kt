package com.android.girish.vlog

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.girish.vlog.VlogModel.Companion.DEBUG
import com.android.girish.vlog.VlogModel.Companion.ERROR
import com.android.girish.vlog.VlogModel.Companion.INFO
import com.android.girish.vlog.VlogModel.Companion.VERBOSE
import com.android.girish.vlog.VlogModel.Companion.WARN
import com.android.girish.vlog.VlogModel.LogPriority
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem

internal class Content(context: Context, val mContentViewModel: ContentViewModel) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()

    var messagesView: RecyclerView
    var layoutManager = LinearLayoutManager(context)
    val mVlogAdapter: VlogAdapter = VlogAdapter()

    init {
        inflate(context, R.layout.log_content_view, this)

        messagesView = findViewById(R.id.events)
        messagesView.layoutManager = layoutManager
        messagesView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        messagesView.adapter = mVlogAdapter

        val logPriorityTxtVw: TextView = findViewById(R.id.log_priority_txtvw)
        logPriorityTxtVw.setOnClickListener {
            showPriorityOptions(context, logPriorityTxtVw, mVlogAdapter)
        }

        val editText: EditText = findViewById(R.id.editText)
        val clearButton: View = findViewById(R.id.clear_logs)

        editText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(constraint: CharSequence?, start: Int, before: Int, count: Int) {
                    mContentViewModel.onKeywordEnter(constraint.toString())
                }
            }
        )

        scaleSpring.addListener(
            object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring) {
                    scaleX = spring.currentValue.toFloat()
                    scaleY = spring.currentValue.toFloat()
                }
            }
        )
        scaleSpring.springConfig = SpringConfigs.CONTENT_SCALE

        scaleSpring.currentValue = 0.0

        clearButton.setOnClickListener {
            mContentViewModel.onClearLogs()
        }

        mContentViewModel.resultObserver.observeForever {
            mVlogAdapter.addLogs(it)
        }
    }

    private fun showPriorityOptions(context: Context, logPriorityTxtVw: TextView, vlogAdapter: VlogAdapter) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Select Log priority")
        val priorityList: List<String> = resources.getStringArray(R.array.log_priority_names).toMutableList()
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            android.R.layout.simple_list_item_1,
            priorityList
        )
        builder.setAdapter(arrayAdapter) { _, selectedIndex ->
            logPriorityTxtVw.text = priorityList[selectedIndex]
            mContentViewModel.onPrioritySet(getLogPriority(selectedIndex))
        }
        builder.setPositiveButton(
            "Cancel"
        ) { dialog, _ -> dialog?.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        dialog.show()
    }

    private fun getLogPriority(selectedIndex: Int): Int {
        @LogPriority var priority: Int = VERBOSE

        when (selectedIndex) {
            0 -> priority = VERBOSE
            1 -> priority = DEBUG
            2 -> priority = INFO
            3 -> priority = WARN
            4 -> priority = ERROR
        }

        return priority
    }

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (ignored: PackageManager.NameNotFoundException) {
        }

        return false
    }

    private fun isAppEnabled(context: Context, packageName: String): Boolean {
        var appStatus = false
        try {
            val ai = context.packageManager.getApplicationInfo(packageName, 0)
            if (ai != null) {
                appStatus = ai.enabled
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return appStatus
    }

    fun hideContent() {
        VlogService.sInstance.chatHeads.showContentRunnable?.let {
            VlogService.sInstance.chatHeads.handler.removeCallbacks(
                it
            )
        }

        scaleSpring.endValue = 0.0

        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }

    fun showContent() {
        scaleSpring.endValue = 1.0

        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }
}
