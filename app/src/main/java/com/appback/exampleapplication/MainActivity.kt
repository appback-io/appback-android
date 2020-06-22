package com.appback.exampleapplication

import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appback.appbacksdk.AppBack
import com.appback.appbacksdk.callbacks.*
import com.appback.appbacksdk.poko.toggle.Toggle
import com.appback.appbacksdk.poko.transalation.LanguageItem
import com.appback.appbacksdk.poko.transalation.Translation


class MainActivity : AppCompatActivity(), OnTranslationSearched,
    OnTranslationsSearched, OnAppBackInitialization {

    val tvTranslation by lazy<TextView?> {
        findViewById(R.id.tv_transalation_example)
    }

    val tvAllLanguages by lazy<TextView?> {
        findViewById(R.id.tv_all_languages)
    }

    val tvAllTranslations by lazy<TextView?> {
        findViewById(R.id.tv_all_translations)
    }
    val tvAllToggles by lazy<TextView?> {
        findViewById(R.id.tv_all_toggles)
    }

    val tvToggle by lazy<TextView?> {
        findViewById(R.id.tv_toggle_example)
    }

    val btnSendEvent by lazy<Button?> {
        findViewById(R.id.btn_send_event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // AppBack initialization
        AppBack.getInstance(baseContext).configure(apiKey = "RIvzTBbH7LnHsxBmomzogxov0B4lW7tumhho5jaTxLMjfAPv4t1589904786", toggleRouter = "toggles_prod", callback = this)
    }

    override fun onInitialization(success: Boolean) {

        if (success) {

            //////////// Toggles ///////////////////
            /// Get all toggles example
            AppBack.getInstance(baseContext).getToggles(router = "toggles_prod", callback = {
                runOnUiThread {
                    var values = "All Toogle values: \n"
                    if (it != null) {
                        for (toggle: Toggle in it) {
                            values += "${toggle.key}: ${toggle.value} \n"
                        }
                    }
                    tvAllToggles?.text = values
                }
            })

            /// String Example
            AppBack.getInstance(baseContext).getToggle(key = "string_test", router = "toggles_prod", callback = {
                runOnUiThread {
                    tvToggle?.text = "Toogle example:\n ${it.toString()}"
                }
            })

            /// Boolean Example
            AppBack.getInstance(baseContext).getBoolToggle("bool_test", router = "toggles_prod", callback =  {
                runOnUiThread {
                    tvToggle?.text = "Toggle example:\n ${it.toString()}"
                }
            })
            //////////// End Toggles ///////////////////

            //////////// Event Logs ///////////////////
            var hashItems = arrayListOf<HashMap<String, Any>>()
            hashItems.add(hashMapOf("user" to "1234"))
            hashItems.add(hashMapOf("price" to 30500))

            btnSendEvent?.setOnClickListener {
                AppBack.getInstance(baseContext).addEventLog(context = baseContext ,router = "events_prod", eventName = "prueba3", parameters = hashItems, deviceInformation = true)
            }
            //////////// End Event Logs ///////////////////

            //////////// Translations ///////////////////
            AppBack.getInstance(baseContext).getTranslations(router = "translations_jutilities", languageIdentifier = "ES", callback = this)
            AppBack.getInstance(baseContext).getLanguages("translations_jutilities", callback = {
                it.let {
                    runOnUiThread {
                        if (it != null) {
                            var values = "All languages: \n"
                            for (language: LanguageItem in it) {
                                values += "${language.key}: ${language.value} \n"
                            }
                            tvAllLanguages?.text = values
                        }
                    }
                }
            })
            //////////// End Translations ///////////////////
        }
    }


    override fun onTranslationFound(translation: Translation) {
        runOnUiThread {
            tvTranslation?.text = "Translation example:\n ${translation.value}"
        }
    }

    override fun onTranslationNotFount(key: String) {
        runOnUiThread {
            tvTranslation?.text = "Error translation"
        }
    }

    override fun onTranslationsFound(translations: List<Translation>) {
        var result = "All translations: \n"
        for (translation in translations) {
            result += "${translation.key}: ${translation.value}\n"
        }
        runOnUiThread {
            tvAllTranslations?.text = result
        }
        AppBack.getInstance(baseContext).getTranslation(router = "translations_jutilities", key = "general.greeting", callback = this)
    }

}
