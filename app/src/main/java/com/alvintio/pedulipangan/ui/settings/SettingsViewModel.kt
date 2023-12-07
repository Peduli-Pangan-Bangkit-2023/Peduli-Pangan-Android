package com.alvintio.pedulipangan.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alvintio.pedulipangan.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class SettingsViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    private var userDataListener: ListenerRegistration? = null

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
    fun getUserData(uid: String) {
        val userRef: DocumentReference = firestore.collection("users").document(uid)

        userDataListener?.remove()

        userDataListener = userRef.addSnapshotListener { documentSnapshot, _ ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)

                if (user != null) {
                    _userData.value = user
                } else {
                }
            }
        }
    }

    override fun onCleared() {
        userDataListener?.remove()
        super.onCleared()
    }
}