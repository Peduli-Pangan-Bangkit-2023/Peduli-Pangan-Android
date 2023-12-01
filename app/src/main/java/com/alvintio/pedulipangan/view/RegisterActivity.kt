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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

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

            if (name.isEmpty()) {
                binding.edRegisterName.error = getString(R.string.input_name)
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.edRegisterEmail.error = getString(R.string.input_email)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edRegisterPassword.error = getString(R.string.input_password)
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { result ->
                    binding.progressBar.visibility = View.GONE
                    if (result.isSuccessful) {
                        showSuccessDialog()
                    } else {
                        handleError(result.exception)
                    }
                }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(getString(R.string.success))
            setPositiveButton(getString(R.string.continue_on)) { _, _ ->
                ViewUtils.moveActivityNoHistory(this@RegisterActivity, WelcomeActivity::class.java)
            }
            setOnCancelListener {
                ViewUtils.moveActivityNoHistory(this@RegisterActivity, WelcomeActivity::class.java)
            }
            create()
            show()
        }
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