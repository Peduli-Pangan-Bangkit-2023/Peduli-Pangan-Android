package com.alvintio.pedulipangan.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipangan.MainActivity
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.ActivityLoginBinding
import com.alvintio.pedulipangan.util.ViewUtils
import com.alvintio.pedulipangan.viewmodel.AuthenticationViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var viewModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        ViewUtils.setupFullScreen(this)

        setupLogin()

        viewModel.loginState.observe(this) { loginState ->
            when (loginState) {
                is AuthenticationViewModel.LoginState.Success -> {
                    moveToMainActivity()
                }

                is AuthenticationViewModel.LoginState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    handleError(Exception(loginState.message))
                }
            }
        }
    }

    private fun setupLogin() {
        binding.progressBar.visibility = View.GONE

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isEmpty()) {
                binding.edLoginEmail.error = getString(R.string.input_email)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edLoginPassword.error = getString(R.string.input_password)
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            viewModel.login(email, password)
        }
    }

    private fun moveToMainActivity() {
        ViewUtils.moveActivityNoHistory(this@LoginActivity, MainActivity::class.java)
        finish()
    }

    private fun handleError(exception: Exception?) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.error))
            setMessage(exception?.localizedMessage ?: getString(R.string.error))
            setPositiveButton(getString(R.string.continue_on)) { _, _ -> }
            create()
            show()
        }
    }

}