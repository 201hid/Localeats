package com.example.bottomnavyt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.app.AppCompatDelegate

class SettingsFragment : Fragment() {

    private lateinit var switchTheme: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        switchTheme = view.findViewById(R.id.switch_theme)

        // Check and set the initial state of the toggle button
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        switchTheme.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES
        updateSwitchText()

        // Set click listener for the toggle button
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            updateSwitchText()
        }

        return view
    }

    private fun updateSwitchText() {
        if (switchTheme.isChecked) {
            switchTheme.text = "Dark Mode"
        } else {
            switchTheme.text = "Light Mode"
        }
    }
}
