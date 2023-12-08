package com.alvintio.pedulipangan.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipangan.MainActivity
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.ActivityReserveBinding
import com.alvintio.pedulipangan.model.Notification
import com.alvintio.pedulipangan.util.ViewUtils
import com.alvintio.pedulipangan.viewmodel.NotificationsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ReserveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReserveBinding
    private val notificationsViewModel by viewModels<NotificationsViewModel>()
    private val firestore = FirebaseFirestore.getInstance()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReserveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productPrice = intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0)
        val productQuantity = intent.getIntExtra(EXTRA_PRODUCT_QUANTITY, 0)


        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        var timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val currentDateTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).apply {
            timeZone = timeZone
        }.format(Date())

        binding.dateReserve.text = getString(R.string.date_reserve, currentDateTime)

        val totalPrice = productPrice * productQuantity

        val formattedPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalPrice)
        binding.priceReserve.text = getString(R.string.price_reserve, formattedPrice)

        val notificationMessage = "Selamat, pembelian anda sebesar $formattedPrice telah berhasil"
        val notification = Notification(currentUser.toString(), currentDateTime, notificationMessage)

        if (currentUser != null) {
            val uid = currentUser.uid
            val notification = Notification(uid, currentDateTime, notificationMessage)

            firestore.collection("notifications")
                .add(notification.toMap())
                .addOnSuccessListener {
                    notificationsViewModel.addNotification(notification)
                }
                .addOnFailureListener { e ->
                }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewUtils.setupFullScreen(this)
    }

    companion object {
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_QUANTITY = "extra_product_quantity"
    }
}
