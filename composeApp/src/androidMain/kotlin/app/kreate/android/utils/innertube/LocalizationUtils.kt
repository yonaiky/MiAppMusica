package app.kreate.android.utils.innertube

import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import app.kreate.android.Preferences
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.enums.Languages
import me.knighthat.innertube.request.Localization
import java.util.Locale


val CURRENT_LOCALE: Localization by lazy {
    val locale = Locale.getDefault()

    val languageCode = when( Preferences.APP_LANGUAGE.value ) {
        Languages.System ->
            try {
                enumValueOf<Languages>( locale.language ).code
            } catch (_: IllegalArgumentException) {
                "en"
            }

        else -> Preferences.APP_LANGUAGE.value.code
    }

    var countryCode = locale.country
    if( !countryCode.matches( Regex("^[A-Z]{2}$") ) ) {
        val telephonyManager = getSystemService(
            appContext(), TelephonyManager::class.java
        )

        // Some older Android returns blank even at this point
        countryCode = telephonyManager?.networkCountryIso
                                      ?.uppercase()
                                      .orEmpty()
                                      .ifBlank { "US" }
    }

    Localization(languageCode, countryCode)
}