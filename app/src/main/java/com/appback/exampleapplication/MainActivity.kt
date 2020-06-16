package com.appback.exampleapplication

import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appback.appbacksdk.AppBack
import com.appback.appbacksdk.callbacks.OnToggleSearched
import com.appback.appbacksdk.callbacks.OnTogglesSearched
import com.appback.appbacksdk.callbacks.OnTranslationSearched
import com.appback.appbacksdk.callbacks.OnTranslationsSearched
import com.appback.appbacksdk.poko.toggle.Toggle
import com.appback.appbacksdk.poko.transalation.Translation


class MainActivity : AppCompatActivity(), OnTranslationSearched, OnToggleSearched,
    OnTranslationsSearched, OnTogglesSearched {

    val tvTranslation by lazy<TextView?> {
        findViewById(R.id.tv_transalation_example)
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

        AppBack.getInstance(baseContext).configure(apiKey = "RIvzTBbH7LnHsxBmomzogxov0B4lW7tumhho5jaTxLMjfAPv4t1589904786", toggleRouter = "toggles_prod")

        /// String Example
        AppBack.getInstance(baseContext).getToggle(key = "string_test", router = "toggles_prod", callback = {
            runOnUiThread {
                tvToggle?.text = it
            }
        })

        /// Boolean Example
        /*AppBack.getInstance(baseContext).getBoolToggle("bool_test", router = "toggles_prod", callback =  {
            runOnUiThread {
                tvToggle?.text = it.toString()
            }
        })*/

        // Event parameters example
        var hashItems = arrayListOf<HashMap<String, Any>>()
        hashItems.add(hashMapOf("user" to "1234"))
        hashItems.add(hashMapOf("price" to 30500))

        btnSendEvent?.setOnClickListener {
            AppBack.getInstance(baseContext).addEventLog(context = baseContext ,router = "events_prod", eventName = "prueba3", parameters = hashItems, deviceInformation = true)
        }

        // No working yet
        AppBack.getInstance(baseContext).getTranslation("appname", this)
        AppBack.getInstance(baseContext).getTranslations(this)

    }

    override fun onTranslationFound(translation: Translation) {
        runOnUiThread {
            tvTranslation?.text = translation.value
        }
    }

    override fun onTranslationNotFount(key: String) {
        runOnUiThread {
            tvTranslation?.text = "Error translation"
        }
    }

    override fun onToggleFound(toggle: Toggle) {
        runOnUiThread {
            //tvToggle?.text = toggle.value
        }
    }

    override fun onToggleNotFound(key: String) {
        runOnUiThread {
            tvToggle?.text = "Error toggle"
        }
    }

    override fun onTranslationsFound(translations: List<Translation>) {
        var result = "All translations: "
        for (translation in translations) {
            result += "${translation.value},"
        }
        runOnUiThread {
            tvAllTranslations?.text = result
        }
    }

    override fun onTogglesFound(toggles: List<Toggle>) {
        var result = "All toggles: "
        for (toggle in toggles) {
            result += "${toggle.value},"
        }
        runOnUiThread {
            tvAllToggles?.text = result
        }
    }
}
