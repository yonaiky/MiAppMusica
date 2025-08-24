package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import app.kreate.android.R
import it.fast4x.rimusic.appContext
import me.knighthat.enums.TextView
import me.knighthat.innertube.Localized
import java.util.Locale

/**
 * A list of supported languages, each [code]
 * must correspond to a valid `res/values-*` directory.
 *
 * [code] but adhere to [BCP 47](https://tools.ietf.org/html/bcp47) -
 * IETF standard for language tags. TL;DR `language[-script][-region][-variant]`
 *
 *
 * @param code 2 (or 3) letters language code
 */
enum class Languages(
    val code: String
): TextView {

    System( "system" ),
    Afrikaans( "af" ),
    Arabic( "az" ),
    Azerbaijani( "ar" ),
    Bashkir( "ba" ),
    Bengali( "bn" ),
    Catalan( "ca" ),
    ChineseSimplified( "zh-CN" ),
    ChineseTraditional( "zh-TW" ),
    Czech( "cs" ),
    Danish( "da" ),
    Dutch( "nl" ),
    English( "en" ),
    Esperanto( "eo" ),
    Estonian( "et" ),
    Filipino( "fil" ),
    Finnish( "fi" ),
    French( "fr" ),
    Galician( "gl" ),
    German( "de" ),
    Greek( "el" ),
    Hebrew( "he" ),
    Hindi( "hi" ),
    Hungarian( "hu" ),
    Indonesian( "id" ),
    Interlingua( "ia" ),
    Irish( "ga" ),
    Italian( "it" ),
    Japanese( "ja" ),
    Korean( "ko" ),
    Malayalam( "ml" ),
    Norwegian( "no" ),
    Odia( "or" ),
    Persian( "fa" ),
    Polish( "pl" ),
    Portuguese( "pt" ),
    PortugueseBrazilian( "pt-BR" ),
    Romanian( "ro" ),
    Russian( "ru" ),
    SerbianCyrillic( "sr-Cyrl" ),
    SerbianLatin( "sr-Latn" ),
    Sinhala( "si" ),
    Spanish( "es" ),
    Swedish( "sv" ),
    Tamil( "ta" ),
    Telugu( "te" ),
    Turkish( "tr" ),
    Ukrainian( "uk" ),
    Vietnamese( "vi" );

    @get:Composable
    @get: Localized
    override val text: String
        get() {
            if( this === System )
                return appContext().getString( R.string.system_language )

            val parts = code.split( "-", "_" )
            check( parts.isNotEmpty() && parts.size <= 2 ) {
                "Invalid parts size: $parts"
            }

            val builder = Locale.Builder().setLanguage( parts[0] )
            if( parts.size == 2 )
                if( parts[0] == "sr" )
                    builder.setScript( parts[1] )
                else
                    builder.setRegion( parts[1] )

            val locale = builder.build()
            return locale.getDisplayName( locale )
        }
}
