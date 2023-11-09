package com.alvintio.pedulipangan.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.alvintio.pedulipangan.MainActivity
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.data.repo.Result
import com.alvintio.pedulipangan.data.repo.UserPreferences
import com.alvintio.pedulipangan.databinding.ActivityLoginBinding
import com.alvintio.pedulipangan.util.ViewUtils
import com.alvintio.pedulipangan.viewmodel.LoginViewModel
import com.alvintio.pedulipangan.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewUtils.setupFullScreen(this)
        setupLogin()
    }

    private fun setupLogin() {
        binding.progressBar.visibility = View.GONE

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(
                this,
                UserPreferences.getInstance(dataStore)
            )
        val viewModel: LoginViewModel by viewModels {
            factory
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.input_email)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.input_password)
                }
                else -> {
                    viewModel.loginUser(email, password).observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is Result.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    val response = result.data
                                    viewModel.saveLoginState(response.token.toString())
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.success))
                                        setMessage(getString(R.string.welcome_back) +" " + "${response.name}")
                                        setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                            ViewUtils.moveActivityNoHistory(this@LoginActivity, MainActivity::class.java)
                                        }
                                        create()
                                        show()
                                    }.apply {
                                        setOnCancelListener {// Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                            ViewUtils.moveActivityNoHistory(this@LoginActivity, MainActivity::class.java)
                                        }
                                        show()
                                    }
                                }
                                is Result.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.error))
                                        setMessage(result.error)
                                        setPositiveButton(getString(R.string.continue_on)) { _, _ -> }
                                        create()
                                        show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}