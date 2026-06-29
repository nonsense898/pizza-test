package com.webtest.ads.rewarded

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.webtest.ads.model.AdMobTestAdUnits
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdManager @Inject constructor() {

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    fun canShow() = rewardedAd != null

    private fun canLoad(): Boolean = !isLoading && rewardedAd == null


    fun load(context: Context) {
        if (!canLoad()) return

        isLoading = true

        RewardedAd.load(
            context.applicationContext,
            AdMobTestAdUnits.REWARDED,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {

                override fun onAdLoaded(ad: RewardedAd) {
                    isLoading = false
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    rewardedAd = null
                }
            }
        )
    }

    fun show(
        activity: Activity,
        onReward: () -> Unit,
        onDismissed: () -> Unit = {}
    ): Boolean {
        val ad = rewardedAd ?: return false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                load(activity.applicationContext)
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                load(activity.applicationContext)
            }
        }

        ad.show(activity) { onReward() }

        return true
    }
}