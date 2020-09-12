package skyrim.requiem

import skyproc.SPGlobal
import skyproc.gui.SUMGUI
import skyrim.requiem.fptools.None
import skyrim.requiem.fptools.Option
import skyrim.requiem.fptools.Some
import skyrim.requiem.gui.PopupTools
import skyrim.requiem.localization.TextFormatter
import skyrim.requiem.localization.TextReference
import java.util.Locale

fun main(args: Array<String>) {
    when (val locale = getLocale()) {
        is Some -> {
            val req = Reqtificator(locale.value)
            SPGlobal.language = SPGlobal.getLanguageFromSkyrimIni()
            if (args.contains("-OLDLOGGING")) {
                SPGlobal.createGlobalLog()
                SPGlobal.logging(true)
            }
            SUMGUI.open(req, args)
        }
        is None -> {
            PopupTools(TextFormatter(Locale("en"))).showPopupMessage(
                TextReference("error_handling.popup_title"),
                TextReference("setup.language_unknown"),
                PopupTools.Companion.PopupType.Error)
            return
        }
    }
}

private fun getLocale(): Option<Locale> {
    return Option(SPGlobal.getLanguageFromSkyrimIni()).map {
        when (it) {
            SPGlobal.Language.Czech -> Locale("cz")
            SPGlobal.Language.English -> Locale("en")
            SPGlobal.Language.French -> Locale("fr")
            SPGlobal.Language.German -> Locale("de")
            SPGlobal.Language.Italian -> Locale("it")
            SPGlobal.Language.Japanese -> Locale("ja")
            SPGlobal.Language.Polish -> Locale("pl")
            SPGlobal.Language.Russian -> Locale("ru")
            SPGlobal.Language.Spanish -> Locale("es")
        }
    }
}