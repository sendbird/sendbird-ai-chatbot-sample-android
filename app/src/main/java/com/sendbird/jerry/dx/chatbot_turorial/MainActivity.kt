package com.sendbird.jerry.dx.chatbot_turorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.activities.ChannelActivity
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.utils.ContextUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            SendbirdUIKit.connect { _, e ->
                if (e != null) {
                    ContextUtils.toastError(this, "Connection must be made. ${e.message}")
                    return@connect
                }
                SendbirdUIKit.startChatWithAiBot(this, BOT_ID, true) { error ->
                    if (error != null) {
                        ContextUtils.toastError(this, "Failed to start chat with ai bot. ${error.message}")
                    }
                }
            }
        }
    }
}
