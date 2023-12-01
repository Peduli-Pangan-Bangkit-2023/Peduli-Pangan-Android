package com.alvintio.pedulipangan.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.ActivityWelcomeBinding
import com.alvintio.pedulipangan.util.ViewUtils

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var appIcon: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appIcon = findViewById(R.id.iv_welcome_image)
        appIcon.setAnimation(R.raw.app_logo)
        appIcon.playAnimation()

        ViewUtils.setupFullScreen(this)
        setupAction()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            ViewUtils.moveActivity(this, LoginActivity::class.java)
        }
        binding.btnRegister.setOnClickListener {
            ViewUtils.moveActivity(this, RegisterActivity::class.java)
        }
    }
}