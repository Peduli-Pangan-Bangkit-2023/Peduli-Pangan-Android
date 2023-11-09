package com.alvintio.pedulipangan.viewmodel

import androidx.lifecycle.ViewModel
import com.alvintio.pedulipangan.data.repo.PanganRepository

class RegisterViewModel(private val panganRepository: PanganRepository) : ViewModel() {
    fun registerUser(nameInput: String, emailInput: String, passwordInput: String) =
        panganRepository.registerUser(nameInput, emailInput, passwordInput)
}