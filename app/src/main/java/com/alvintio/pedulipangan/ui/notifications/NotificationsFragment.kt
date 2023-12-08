package com.alvintio.pedulipangan.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipangan.adapter.NotificationsAdapter
import com.alvintio.pedulipangan.databinding.FragmentNotificationsBinding
import com.alvintio.pedulipangan.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val notificationsRecyclerView: RecyclerView = binding.recyclerView
        notificationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val currentUserUid = auth.currentUser?.uid

        firestore.collection("notifications")
            .get()
            .addOnSuccessListener { result ->
                val notifications = mutableListOf<Notification>()
                for (document in result) {
                    val uid = document.getString("uid")
                    val date = document.getString("date")
                    val message = document.getString("message")

                    uid?.let {
                        if (it == currentUserUid && date != null && message != null) {
                            notifications.add(Notification(it, date, message))
                        }
                    }
                }

                val adapter = NotificationsAdapter(notifications)
                notificationsRecyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
            }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
