package com.alvintio.pedulipangan.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.data.repo.UserPreferences
import com.alvintio.pedulipangan.databinding.FragmentSettingsBinding
import com.alvintio.pedulipangan.view.LoginActivity
import com.alvintio.pedulipangan.view.OnboardingActivity
import com.alvintio.pedulipangan.view.WelcomeActivity
import java.util.prefs.Preferences

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreferences: UserPreferences
    private val Context.dataStore by preferencesDataStore("user_preferences")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userPreferences = UserPreferences.getInstance(requireContext().dataStore)

        val viewModelFactory = SettingsViewModelFactory(userPreferences)
        val settingsViewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

        settingsViewModel.logoutComplete.observe(viewLifecycleOwner) { logoutComplete ->
            if (logoutComplete) {
                startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                requireActivity().finish()
            }
        }

        val textView: TextView = binding.textSettings
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.btnLogout.setOnClickListener {
            settingsViewModel.userLogout()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
