package com.webtest.ads.banner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.webtest.ads.model.AdMobTestAdUnits

@Composable
fun BannerAd(
    modifier: Modifier = Modifier
) {
    val adRequest = remember {
        AdRequest.Builder().build()
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdMobTestAdUnits.BANNER
                loadAd(adRequest)
            }
        }
    )
}