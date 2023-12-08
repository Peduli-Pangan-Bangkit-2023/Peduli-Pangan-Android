package com.alvintio.pedulipangan.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.ActivityDetailListBinding
import com.alvintio.pedulipangan.model.Food
import com.alvintio.pedulipangan.util.ViewUtils
import com.bumptech.glide.Glide

class DetailListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailListBinding

    private var quantity = 1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productName = intent.getStringExtra(EXTRA_PRODUCT_NAME)
        val productPrice = intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0)
        val productDescription = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION)
        val productImage = intent.getStringExtra(EXTRA_PRODUCT_IMAGE)

        binding.tvProductNameDetail.text = productName ?: ""
        binding.tvProductPriceDetail.text = "Rp${String.format("%,.0f", productPrice)}"
        binding.tvProductDescriptionDetail.text = productDescription ?: ""

        binding.btnDecreaseQuantity.setOnClickListener {
            updateQuantity(-1)
        }

        binding.btnIncreaseQuantity.setOnClickListener {
            updateQuantity(1)
        }

        binding.btnReserveProducts.setOnClickListener {
            val intent = Intent(this, ReserveActivity::class.java).apply {
                putExtra(ReserveActivity.EXTRA_PRODUCT_NAME, productName)
                putExtra(ReserveActivity.EXTRA_PRODUCT_PRICE, productPrice)
                putExtra(ReserveActivity.EXTRA_PRODUCT_QUANTITY, quantity)
            }
            startActivity(intent)
        }

        Glide.with(this)
            .load(productImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivDetailList)

        ViewUtils.setupFullScreen(this)
    }

    private fun updateQuantity(change: Int) {
        val newQuantity = quantity + change

        if (newQuantity >= 1) {
            quantity = newQuantity
            binding.tvQuantity.text = quantity.toString()
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
        const val EXTRA_PRODUCT_IMAGE = "extra_product_image"
    }
}


