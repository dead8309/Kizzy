package com.android.girish.vlog

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.android.girish.vlog.utils.drawableToBitmap
import com.android.girish.vlog.utils.getOverlayFlag
import com.android.girish.vlog.utils.getScreenSize
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringListener
import com.facebook.rebound.SpringSystem
import kotlin.math.pow

internal class ChatHead(var chatHeads: ChatHeads, mContentViewModel: ContentViewModel) :
    FrameLayout(chatHeads.context),
    View.OnTouchListener,
    SpringListener {
    var params: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        getOverlayFlag(),
        0,
        PixelFormat.TRANSLUCENT
    )

    var springSystem: SpringSystem = SpringSystem.create()

    var springX: Spring = springSystem.createSpring()
    var springY: Spring = springSystem.createSpring()

    private val paint = Paint()

    private var initialX = 0.0f
    private var initialY = 0.0f

    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f

    private var moving = false

    private var notificationsTextView: TextView
    var notificationsView: LinearLayout

    var notifications = 0
        set(value) {
            if (value >= 0) field = value

            if (value == 0) {
                notificationsView.visibility = GONE
            } else if (value > 0) {
                notificationsView.visibility = VISIBLE
                notificationsTextView.text = "$value"
            }
        }

    override fun onSpringEndStateChange(spring: Spring?) = Unit
    override fun onSpringAtRest(spring: Spring?) = Unit
    override fun onSpringActivate(spring: Spring?) = Unit

    init {
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0
        params.width = ChatHeads.CHAT_HEAD_SIZE + 15
        params.height = ChatHeads.CHAT_HEAD_SIZE + 30

        val view = inflate(context, R.layout.bubble, this)

        val imageView: ImageView = view.findViewById(R.id.bubble_avatar)
        notificationsTextView = view.findViewById(R.id.bubble_notifications_text)
        notificationsView = view.findViewById(R.id.bubble_notifications)

        springX.addListener(
            object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring) {
                    x = spring.currentValue.toFloat()
                }
            }
        )

        springX.springConfig = SpringConfigs.NOT_DRAGGING
        springX.addListener(this)

        springY.addListener(
            object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring) {
                    y = spring.currentValue.toFloat()
                }
            }
        )
        springY.springConfig = SpringConfigs.NOT_DRAGGING
        springY.addListener(this)

        this.setLayerType(View.LAYER_TYPE_HARDWARE, paint)

        chatHeads.addView(this, params)

        this.setOnTouchListener(this)

        // placeholder (appropriate bitmap for chat head)
        /*var chatHeadBitmap: Bitmap = Bitmap.createBitmap(ChatHeads.CHAT_HEAD_SIZE, ChatHeads.CHAT_HEAD_SIZE, Bitmap.Config.ARGB_8888)
            .addBackground(R.drawable.teams_icon)
            .makeCircular()
            .scaleToSize(ChatHeads.CHAT_HEAD_SIZE)
            .addShadow()*/

        // TODO: @girish unable to add background color for the custom png bitmap (figure it out!)
        /*val localBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.teams_icon2)
        localBitmap
            .addBackground(Color.BLACK)
            .makeCircular()
            .scaleToSize(ChatHeads.CHAT_HEAD_SIZE)
            .addShadow()*/

        // getting the application icon to display on the bubble
        var imageBitmap: Bitmap
        try {
            val drawableIcon = context.packageManager.getApplicationIcon(context.applicationContext.packageName)
            imageBitmap = drawableToBitmap(drawableIcon)
        } catch (e: PackageManager.NameNotFoundException) {
            imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)
        }

        // Rendering the icon
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, imageBitmap)
        roundedBitmapDrawable.setCornerRadius(50.0f)
        roundedBitmapDrawable.setAntiAlias(true)
        imageView.setImageDrawable(roundedBitmapDrawable)

        mContentViewModel.resultObserver.observeForever {
            updateNotifications(it.count())
        }
    }

    fun updateNotifications(value: Int) {
        notifications = value
    }

    override fun onSpringUpdate(spring: Spring) {
        if (spring !== this.springX && spring !== this.springY) return
        val totalVelocity = Math.hypot(springX.velocity, springY.velocity).toInt()

        chatHeads.onChatHeadSpringUpdate(this, spring, totalVelocity)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val currentChatHead = chatHeads.chatHeads.find { it == v }!!

        val metrics = getScreenSize()

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = x
                initialY = y
                initialTouchX = event.rawX
                initialTouchY = event.rawY

                scaleX = 0.92f
                scaleY = 0.92f
            }
            MotionEvent.ACTION_UP -> {
                if (!moving) {
                    if (currentChatHead == chatHeads.activeChatHead) {
                        chatHeads.collapse()
                    } else {
                        chatHeads.activeChatHead = currentChatHead
                    }
                } else {
                    springX.endValue = metrics.widthPixels - width - chatHeads.chatHeads.indexOf(this) * (width + ChatHeads.CHAT_HEAD_EXPANDED_PADDING).toDouble()
                    springY.endValue = ChatHeads.CHAT_HEAD_EXPANDED_MARGIN_TOP.toDouble()

                    if (this == chatHeads.activeChatHead) {
                        chatHeads.content.showContent()
                    }
                }

                scaleX = 1f
                scaleY = 1f

                moving = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (ChatHeads.distance(initialTouchX, event.rawX, initialTouchY, event.rawY) > ChatHeads.CHAT_HEAD_DRAG_TOLERANCE.pow(2) && !moving) {
                    moving = true

                    if (this == chatHeads.activeChatHead) {
                        chatHeads.content.hideContent()
                    }
                }

                if (moving) {
                    springX.currentValue = initialX + (event.rawX - initialTouchX).toDouble()
                    springY.currentValue = initialY + (event.rawY - initialTouchY).toDouble()
                }
            }
        }

        return true
    }
}
