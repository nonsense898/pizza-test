package com.cpunks.pizzacatalog

import android.app.Application
import com.webtest.ads.initializer.AdsInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PizzaApp : Application(){
    override fun onCreate() {

        super.onCreate()

        AdsInitializer.initialize(this)
    }
}
