package com.sendbird.jerry.dx.chatbot_turorial

import android.app.Application
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.internal.SB
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.SendbirdUIKit.LogLevel
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.interfaces.UserInfo
import com.sendbird.uikit.interfaces.providers.MessageListAdapterProvider
import com.sendbird.uikit.providers.AdapterProviders
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

val APP_ID: String = TODO("Enter your app id")
val BOT_ID: String = TODO("Enter your bot id")
val USER_ID: String = TODO("Enter your user id")
class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        SendbirdUIKit.init(object : SendbirdUIKitAdapter {
            override fun getAppId(): String {
                return APP_ID
            }

            override fun getAccessToken(): String {
                return ""
            }

            override fun getUserInfo(): UserInfo {
                return object : UserInfo {
                    override fun getUserId(): String {
                        return USER_ID
                    }

                    override fun getNickname(): String {
                        return USER_ID
                    }

                    override fun getProfileUrl(): String {
                        return ""
                    }
                }
            }

            override fun getInitResultHandler(): InitResultHandler {
                return object : InitResultHandler {
                    override fun onMigrationStarted() {
                        // DB migration has started.
                    }

                    override fun onInitFailed(e: SendbirdException) {
                        // If DB migration fails, this method is called.
                    }

                    override fun onInitSucceed() {
                        // If DB migration is successful, this method is called and you can proceed to the next step.
                        // In the sample app, the `LiveData` class notifies you on the initialization progress
                        // And observes the `MutableLiveData<InitState> initState` value in `SplashActivity()`.
                        // If successful, the `LoginActivity` screen
                        // Or the `HomeActivity` screen will show.
                    }
                }
            }
        }, this)

        AdapterProviders.messageList = MessageListAdapterProvider { channel, uiParams ->
            CardViewAdapter(channel, uiParams)
        }
    }
}
