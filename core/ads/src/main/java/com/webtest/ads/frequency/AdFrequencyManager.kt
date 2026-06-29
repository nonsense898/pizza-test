package com.webtest.ads.frequency


import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdFrequencyManager @Inject constructor() {
    companion object {
        private const val SHOW_EVERY = 3
    }

    private var counter = 0

    fun shouldShow(): Boolean {
        counter++
        return counter % SHOW_EVERY == 0
    }
}