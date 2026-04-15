package de.nif.utils.admanager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/9214589741", //always use banner test unit id in tests
    load: Boolean = false,      // check if ad should be loaded (AdManager.isConsentInformationInitialized)
    isConnected: () -> Boolean  // check if internet connection is available
) {

    val context = LocalContext.current

    val adView = remember { AdView(context) }

    val placeholderColor = Color.Gray


    if (load) {

        adView.adUnitId = adUnitId
        adView.setAdSize(AdSize.BANNER)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
            }

            override fun onAdClosed() {
                super.onAdClosed()
            }
        }
    }


    Box(
        modifier = modifier
            .height(AdSize.BANNER.height.dp)
            .background(placeholderColor),
        contentAlignment = Alignment.Center
    ) {

        if (LocalInspectionMode.current || !load || !isConnected()) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background( placeholderColor )
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onPrimary)
            )
        } else {
            adView.loadAd( AdRequest.Builder().build())
            AndroidView(
                modifier = Modifier.background(placeholderColor),
                factory = { adView }
            )

        }

    }

    DisposableEffect(Unit) {
        onDispose { adView.destroy() }
    }


}