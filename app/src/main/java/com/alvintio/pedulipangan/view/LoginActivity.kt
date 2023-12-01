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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        ViewUtils.setupFullScreen(this)

        setupLogin()
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
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { result ->
                    binding.progressBar.visibility = View.GONE
                    if (result.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            moveToMainActivity()
                        }
                    } else {
                        handleError(result.exception)
                    }
                }
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