package de.nif.utils.admanager

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import de.nif.utils.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


//Helper class to show AdMob ads and handle GDPR and CCPA consent
//has to be injected as singleton
//also: set app id in manifest
class AdManager {

    val TAG = "AdManager"

    private lateinit var consentInformation: ConsentInformation

    //check if consent is required
    //if yes, show button to change options in ui (ie settings) and call showConsentOptions()
    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED


    private val _isConsentInformationInitialized = MutableStateFlow(false)
    val isConsentInformationInitialized = _isConsentInformationInitialized.asStateFlow()

    //init consent information only once (ie MainActivity onCreate)
    fun initConsentInformation(
        context: Context,
    ) {
        consentInformation = UserMessagingPlatform.getConsentInformation(context)
    }

    //calls consent form if required
    fun updateConsentInformation(activity: ComponentActivity) {

        Log.d(TAG, "updateConsentInformation: ")

        //call only when ads not initialized. To change consent options, call showConsentOptions()
        if (isConsentInformationInitialized.value)
            return

        val params = if (BuildConfig.DEBUG) {

            //get test device id from logcat
            val testDeviceId = "TEST_DEVICE_ID"

            val debugSettings = ConsentDebugSettings.Builder(activity)
                .addTestDeviceHashedId(testDeviceId)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .build()

            ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)      //true for mixed targets
                .setConsentDebugSettings(debugSettings)
                .build()

        } else {
            ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(true)      //true for mixed targets
                .build()
        }

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {

                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.e(TAG,"error receiving consent form: ${formError.errorCode}: ${formError.message}")
                    }

                    if (consentInformation.canRequestAds()) {
                        initMobileAds(activity)
                    } else {
                        Log.d(TAG, "updateConsentInformation: NO CONSENT")
                    }

                }
            },
            { formError ->
                    Log.e(TAG,"error receiving consent form: ${formError.errorCode}: ${formError.message}")
            },
        )

        if (consentInformation.canRequestAds()) {
            initMobileAds(activity)
        }


    }


    //initialize mobile ads
    //if NOT initialized here, ads are implicitly initialized when ads are loaded!
    //use isConsentInformationInitialized to wait for initialization
    fun initMobileAds(context: Context) {

        //only initialize once
        if (isConsentInformationInitialized.value)
            return

        _isConsentInformationInitialized.update { true }

        val requestConfiguration = MobileAds.getRequestConfiguration()
            .toBuilder()
            .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        //initialize apps
        MobileAds.initialize(context) { }



    }

    //change consent / privacy options (ie in settings)
    fun showConsentOptions(
        activity: ComponentActivity,
        onError: (errorCode: Int, message: String) -> Unit = { _, _ -> }
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            if (it != null) {
                Log.e(TAG, "showConsentOptions: ${it.errorCode}: ${it.message}")
                //show error message ie as toast
                onError(it.errorCode, it.message)
            }
        }

    }


}