package com.alvintio.pedulipangan.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alvintio.pedulipangan.databinding.FragmentSettingsBinding
import com.alvintio.pedulipangan.view.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogout.setOnClickListener {
            settingsViewModel.logout()
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            settingsViewModel.getUserData(currentUser.uid)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.userData.observe(viewLifecycleOwner) { userData ->
            binding.tvUserName.text = userData.name
            binding.tvUserEmail.text = userData.email
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

