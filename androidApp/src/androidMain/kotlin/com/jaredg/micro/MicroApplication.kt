package com.jaredg.micro

import android.app.Application
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import leakcanary.LeakCanary

class MicroApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        NotifierManager.initialize(
            NotificationPlatformConfiguration.Android(
                notificationIconResId = androidx.core.R.drawable.ic_call_answer,
                notificationIconColorResId = androidx.appcompat.R.color.material_blue_grey_800,
                notificationChannelData = NotificationPlatformConfiguration.Android.NotificationChannelData(
                    id = "CHANNEL_ID_GENERAL",
                    name = "General"
                )
            )
        )
    }
}