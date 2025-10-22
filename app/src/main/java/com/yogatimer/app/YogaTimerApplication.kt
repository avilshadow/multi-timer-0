package com.yogatimer.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YogaTimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
