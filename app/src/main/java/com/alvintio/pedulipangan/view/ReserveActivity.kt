package com.alvintio.pedulipangan.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipangan.MainActivity
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.ActivityReserveBinding
import com.alvintio.pedulipangan.ui.home.HomeFragment
import com.alvintio.pedulipangan.util.ViewUtils
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ReserveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReserveBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReserveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productPrice = intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0)
        val productQuantity = intent.getIntExtra(EXTRA_PRODUCT_QUANTITY, 0)

        var timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val currentDateTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).apply {
            timeZone = timeZone
        }.format(Date())

        binding.dateReserve.text = getString(R.string.date_reserve, currentDateTime)

        val totalPrice = productPrice * productQuantity

        val formattedPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalPrice)
        binding.priceReserve.text = getString(R.string.price_reserve, formattedPrice)

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
