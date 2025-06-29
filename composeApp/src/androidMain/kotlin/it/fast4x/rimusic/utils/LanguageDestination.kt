package it.fast4x.rimusic.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import app.kreate.android.Preferences
import it.fast4x.rimusic.enums.Languages
import me.bush.translator.Language


@Composable
fun languageDestination (
    language: Languages? = null,
): Language {
    //val languageApp  by rememberPreference(languageAppKey, Languages.English)
    val otherLanguageApp  by Preferences.OTHER_APP_LANGUAGE
    //Timber.d("LanguageDestination: language $language otherLanguageApp $otherLanguageApp")

    return when (language ?: otherLanguageApp) {
        Languages.Afrikaans -> Language.AFRIKAANS
        Languages.Arabic -> Language.ARABIC
        Languages.Azerbaijani -> Language.AZERBAIJANI
        Languages.Bashkir -> Language.BASQUE
        Languages.Bengali -> Language.BENGALI
        Languages.Catalan -> Language.CATALAN
        Languages.ChineseSimplified -> Language.CHINESE_SIMPLIFIED
        Languages.ChineseTraditional -> Language.CHINESE_TRADITIONAL
        Languages.Czech -> Language.CZECH
        Languages.Danish -> Language.DANISH
        Languages.Dutch -> Language.DUTCH
        Languages.English -> Language.ENGLISH
        Languages.Esperanto -> Language.ESPERANTO
        Languages.Estonian -> Language.ESTONIAN
        Languages.Filipino -> Language.FILIPINO
        Languages.Finnish -> Language.FINNISH
        Languages.French -> Language.FRENCH
        Languages.Galician -> Language.GALICIAN
        Languages.German -> Language.GERMAN
        Languages.Greek -> Language.GREEK
        Languages.Hebrew -> Language.HEBREW_HE
        Languages.Hindi -> Language.HINDI
        Languages.Hungarian -> Language.HUNGARIAN
        Languages.Indonesian -> Language.INDONESIAN
        Languages.Interlingua -> Language.LATIN
        Languages.Irish -> Language.IRISH
        Languages.Japanese -> Language.JAPANESE
        Languages.Korean -> Language.KOREAN
        Languages.Italian -> Language.ITALIAN
        Languages.Malayalam -> Language.MALAYALAM
        Languages.Norwegian -> Language.NORWEGIAN
        Languages.Odia -> Language.ODIA
        //Languages.Persian -> Language.PERSIAN
        Languages.Polish -> Language.POLISH
        Languages.PortugueseBrazilian -> Language.PORTUGUESE
        Languages.Portuguese -> Language.PORTUGUESE
        Languages.Romanian -> Language.ROMANIAN
        Languages.Russian -> Language.RUSSIAN
        Languages.SerbianCyrillic, Languages.SerbianLatin -> Language.SERBIAN
        Languages.Sinhala -> Language.SINHALA
        Languages.Spanish -> Language.SPANISH
        Languages.Swedish -> Language.SWEDISH
        Languages.Tamil -> Language.TAMIL
        Languages.Telugu -> Language.TELUGU
        Languages.Turkish -> Language.TURKISH
        Languages.Ukrainian -> Language.UKRAINIAN
        Languages.Vietnamese -> Language.VIETNAMESE
        else -> Language.ENGLISH
    }
}
