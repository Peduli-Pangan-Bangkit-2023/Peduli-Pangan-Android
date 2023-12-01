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
import com.airbnb.lottie.LottieAnimationView
import com.alvintio.pedulipangan.MainActivity
import com.alvintio.pedulipangan.R
import com.google.firebase.auth.FirebaseAuth
import com.alvintio.pedulipangan.util.ViewUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var appIcon: LottieAnimationView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        appIcon = findViewById(R.id.img_icon)
        appIcon.setAnimation(R.raw.app_logo)
        appIcon.playAnimation()

        ViewUtils.setupFullScreen(this)

        val content = findViewById<View>(android.R.id.content)
        @Suppress("UNUSED_EXPRESSION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnDrawListener { false }
        }

        auth = FirebaseAuth.getInstance()

        lifecycleScope.launch {
            delay(6000.toLong())

            val user = auth.currentUser

            if (user != null) {
                ViewUtils.moveActivityNoHistory(this@SplashActivity, MainActivity::class.java)
            } else {
                ViewUtils.moveActivityNoHistory(this@SplashActivity, OnboardingActivity::class.java)
            }
        }
    }
}
