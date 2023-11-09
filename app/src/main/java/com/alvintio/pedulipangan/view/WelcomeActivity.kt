package com.alvintio.pedulipangan.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipangan.databinding.ActivityWelcomeBinding
import com.alvintio.pedulipangan.util.ViewUtils

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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