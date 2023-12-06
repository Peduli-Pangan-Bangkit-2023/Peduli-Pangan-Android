package com.alvintio.pedulipangan.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.alvintio.pedulipangan.MainActivity
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.ActivityRegisterBinding
import com.alvintio.pedulipangan.util.ViewUtils
import com.alvintio.pedulipangan.viewmodel.AuthenticationViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var viewModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        viewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)

        ViewUtils.setupFullScreen(this)
        setupRegister()
    }

    private fun setupRegister() {
        binding.progressBar.visibility = View.GONE

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

            viewModel.register(name, email, password)

            registerWithEmailAndPassword(name, email, password)
        }
    }


    private fun saveUserDataToFirestore(userId: String, name: String, email: String) {
        val db = Firebase.firestore
        val usersCollection = db.collection("users")

        val user = com.alvintio.pedulipangan.model.User(name, email, userId)

        usersCollection.document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Data telah tersimpan di Firestore!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Data belum tersimpan di Firestore!", e)
            }
    }

    private fun registerWithEmailAndPassword(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Mengatur ProgressBar kembali ke GONE setelah operasi selesai
                binding.progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        Log.d("Register", "User created successfully")
                        saveUserDataToFirestore(user.uid, name, email)

                        Toast.makeText(
                            this,
                            "Registrasi berhasil!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Registrasi gagal, tolong ulang kembali!!",
                        Toast.LENGTH_SHORT
                    ).show()
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