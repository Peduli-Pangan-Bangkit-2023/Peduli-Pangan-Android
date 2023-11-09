package com.alvintio.pedulipangan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvintio.pedulipangan.data.repo.PanganRepository
import com.alvintio.pedulipangan.data.repo.UserPreferences
import kotlinx.coroutines.launch

class MainViewModel(
    private val panganRepository: PanganRepository,
    private val pref: UserPreferences
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}