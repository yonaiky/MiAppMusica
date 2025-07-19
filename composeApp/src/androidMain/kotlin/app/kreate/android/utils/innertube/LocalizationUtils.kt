package app.kreate.android.utils.innertube

import android.telephony.TelephonyManager
import androidx.core.content.getSystemService
import app.kreate.android.Preferences
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.enums.Languages
import me.knighthat.innertube.request.Localization
import java.util.Locale


val CURRENT_LOCALE: Localization by lazy {
    Localization(getAppLanguageCode(), getAppCountryCode())
}

fun getAppLanguageCode(): String = when ( Preferences.APP_LANGUAGE.value ) {
    Languages.System ->
        try {
            enumValueOf<Languages>(Locale.getDefault().language).code
        } catch (_: IllegalArgumentException) {
            "en"
        }

    else -> Preferences.APP_LANGUAGE.value.code
}

fun getSystemCountryCode(): String {
    var countryCode = Locale.getDefault().country
    if( countryCode !in Locale.getISOCountries() )
        countryCode = appContext().getSystemService<TelephonyManager>()
                                  ?.networkCountryIso
                                  ?.uppercase()
                                  .orEmpty()

    return countryCode.ifBlank { "US" }
}

fun getAppCountryCode(): String {
    var countryCode = Preferences.APP_REGION.value
    if( countryCode.isBlank() || countryCode !in Locale.getISOCountries() )
        countryCode = getSystemCountryCode()

    return countryCode
}