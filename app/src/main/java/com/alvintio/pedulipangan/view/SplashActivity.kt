package com.alvintio.pedulipangan.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.alvintio.pedulipangan.MainActivity
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.data.repo.UserPreferences
import com.alvintio.pedulipangan.util.ViewUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ViewUtils.setupFullScreen(this)

        val content = findViewById<View>(android.R.id.content)
        @Suppress("UNUSED_EXPRESSION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnDrawListener { false } //prevents double splash screens on android 12 or higher
        }

        lifecycleScope.launch {
            delay(500.toLong())

            val userLogin = runBlocking {
                UserPreferences.getInstance(this@SplashActivity.dataStore).getLoginStatus().first()
            }

            if (userLogin == true)
                ViewUtils.moveActivityNoHistory(this@SplashActivity, MainActivity::class.java)
            else
                ViewUtils.moveActivityNoHistory(this@SplashActivity, OnboardingActivity::class.java)
        }
    }
}