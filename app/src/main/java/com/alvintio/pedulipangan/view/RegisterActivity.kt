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
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.data.repo.Result
import com.alvintio.pedulipangan.data.repo.UserPreferences
import com.alvintio.pedulipangan.databinding.ActivityRegisterBinding
import com.alvintio.pedulipangan.util.ViewUtils
import com.alvintio.pedulipangan.viewmodel.RegisterViewModel
import com.alvintio.pedulipangan.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewUtils.setupFullScreen(this)
        setupRegister()
    }

    private fun setupRegister() {
        binding.progressBar.visibility = View.GONE

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(
                this@RegisterActivity,
                UserPreferences.getInstance(dataStore)
            )
        val viewModel: RegisterViewModel by viewModels {
            factory
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.input_name)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.input_email)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.input_password)
                }
                else -> {
                    viewModel.registerUser(name, email, password).observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is Result.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    val response = result.data
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.success))
                                        setMessage(response.message)
                                        setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                                            ViewUtils.moveActivityNoHistory(this@RegisterActivity, WelcomeActivity::class.java)
                                        }
                                        create()
                                        show()
                                    }.apply {
                                        setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                            ViewUtils.moveActivityNoHistory(this@RegisterActivity, WelcomeActivity::class.java)
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