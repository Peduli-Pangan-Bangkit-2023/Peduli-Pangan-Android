package com.alvintio.pedulipangan.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.alvintio.pedulipangan.data.remote.ApiConfig
import com.alvintio.pedulipangan.data.repo.PanganRepository
import com.alvintio.pedulipangan.data.repo.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object Injection {
    fun provideRepository(context: Context): PanganRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val token = runBlocking {
            pref.getToken().first()
        }
        val apiService = ApiConfig.getApiService(token.toString())
        return PanganRepository.getInstance(apiService, pref)
    }
}