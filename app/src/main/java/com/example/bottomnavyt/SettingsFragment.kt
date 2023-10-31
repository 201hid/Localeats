package com.example.bottomnavyt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.app.AppCompatDelegate
import com.example.bottomnavyt.R

class SettingsFragment : Fragment() {

    private lateinit var switchLightMode: SwitchCompat
    private lateinit var switchDarkMode: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        switchLightMode = view.findViewById(R.id.switch_light_mode)
        switchDarkMode = view.findViewById(R.id.switch_dark_mode)

        // Check and set the initial state of the toggle buttons
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        switchLightMode.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_NO
        switchDarkMode.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES

        // Set click listeners for the toggle buttons
        switchLightMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Set the app's theme to Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Set the app's theme to Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        return view
    }
}

