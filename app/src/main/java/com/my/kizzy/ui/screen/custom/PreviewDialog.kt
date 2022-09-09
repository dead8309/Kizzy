package com.my.kizzy.ui.screen.custom

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.my.kizzy.R
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.USER_AVATAR
import com.my.kizzy.utils.Prefs.USER_DATA
import com.my.kizzy.utils.Prefs.USER_ID
import com.my.kizzy.utils.Prefs.USER_NAME


object PreviewDialog {

     fun showPreview(
        context: Context,
        rpc: Rpc
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.rpc_preview, null)

         rpc.apply {
             if (largeImg.isNotEmpty()) {
                 if (largeImg.startsWith("attachments")) {
                     Glide.with(context)
                         .load("https://media.discordapp.net/$largeImg")
                         .placeholder(R.drawable.ic_rpc_placeholder)
                         .error(R.drawable.ic_rpc_placeholder)
                         .into(view.findViewById(R.id.largeimg))
                 } else {
                     Glide.with(context)
                         .load(largeImg)
                         .placeholder(R.drawable.ic_rpc_placeholder)
                         .error(R.drawable.ic_rpc_placeholder)
                         .into(view.findViewById(R.id.largeimg))
                 }
             } else {
                 view.findViewById<ImageView>(R.id.largeimg).visibility = View.GONE
             }

             if (smallImg.isNotEmpty()) {
                 if (smallImg.startsWith("attachments")) {
                     Glide.with(context)
                         .load("https://media.discordapp.net/$smallImg")
                         .placeholder(R.drawable.ic_rpc_placeholder)
                         .error(R.drawable.ic_rpc_placeholder)
                         .into(view.findViewById(R.id.smallimg))
                 } else {
                     Glide.with(context)
                         .load(smallImg)
                         .placeholder(R.drawable.ic_rpc_placeholder)
                         .error(R.drawable.ic_rpc_placeholder)
                         .into(view.findViewById(R.id.smallimg))
                 }
             } else {
                 view.findViewById<ImageView>(R.id.smallimg).visibility = View.GONE
             }

             view.findViewById<TextView?>(R.id.activity_status).text = type.getType()
             view.findViewById<TextView?>(R.id.activity_name).text = name
             view.findViewById<TextView?>(R.id.activity_details).text = details
             view.findViewById<TextView?>(R.id.activity_state).text = state

             if (button1.isNotEmpty()) {
                 val button = view.findViewById<TextView?>(R.id.activity_button1)
                 button.text = button2
             } else
                 view.findViewById<TextView?>(R.id.activity_button1).visibility = View.GONE


             if (button2.isNotEmpty()) {
                 val button = view.findViewById<TextView?>(R.id.activity_button2)
                 button.text = button2
             } else
                 view.findViewById<TextView?>(R.id.activity_button2).visibility = View.GONE
         }


         val user: HashMap<String, String> = Gson().fromJson(
            Prefs[USER_DATA, "{}"],
            object : TypeToken<HashMap<String?, String?>?>() {}.type
        )
        view.findViewById<TextView>(R.id.user_name).text =
            when (user[USER_NAME]?.isNotEmpty()) {
                true -> user[USER_NAME]
                else -> "User"
            }
        view.findViewById<TextView>(R.id.user_hash).text =
            user[USER_ID]?.let { user[USER_ID]?.substring(it.indexOf("#")) } ?: "#0000"

        Glide
            .with(context)
            .load(user[USER_AVATAR])
            .error(R.drawable.error_avatar).into(view.findViewById(R.id.user_profile))

        val builder =  AlertDialog.Builder(context)
            .setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun String.getType(): String {
        val type:Int = try {
            if (this.isNotEmpty()) this.toDouble().toInt()
            else 0
        } catch (ex:NumberFormatException){
            0
        }
        return when (type) {
            1 -> "Streaming on $this"
            2 -> "Listening $this"
            3 -> "Watching $this"
            4 -> ""
            5 -> "Competing in $this"
            else -> "Playing a game"
        }
    }
}