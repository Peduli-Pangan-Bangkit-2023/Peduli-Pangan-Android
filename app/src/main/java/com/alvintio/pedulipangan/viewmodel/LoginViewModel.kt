package com.alvintio.pedulipangan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvintio.pedulipangan.data.repo.PanganRepository
import com.alvintio.pedulipangan.data.repo.UserPreferences
import kotlinx.coroutines.launch

class LoginViewModel(
    private val panganRepository: PanganRepository,
    private val pref: UserPreferences
) : ViewModel() {
    fun loginUser(emailInput: String, passwordInput: String) =
        panganRepository.loginUser(emailInput, passwordInput)

    fun saveLoginState(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
            pref.login()
        }
    }
}