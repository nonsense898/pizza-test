package com.webtest.ads.interstitial

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.webtest.ads.model.AdMobTestAdUnits
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialAdManager @Inject constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var isLoading: Boolean = false
    fun canShow(): Boolean = interstitialAd != null

    private fun canLoad(): Boolean = !isLoading && interstitialAd == null


    fun load(context: Context) {
        if (!canLoad()) return

        isLoading = true

        InterstitialAd.load(
            context.applicationContext,
            AdMobTestAdUnits.INTERSTITIAL,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: InterstitialAd) {
                    isLoading = false
                    interstitialAd = ad

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            load(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            interstitialAd = null
                            load(context)
                        }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    interstitialAd = null
                }
            }
        )
    }

    fun show(activity: Activity) {
        interstitialAd?.show(activity)
    }
}