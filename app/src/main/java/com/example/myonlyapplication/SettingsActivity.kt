package com.example.myonlyapplication

import android.os.Bundle
import android.preference.PreferenceActivity
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)
    }
}
