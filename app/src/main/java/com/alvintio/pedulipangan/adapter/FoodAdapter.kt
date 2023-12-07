package com.alvintio.pedulipangan.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.model.Food
import com.alvintio.pedulipangan.view.DetailListActivity
import com.bumptech.glide.Glide

class FoodAdapter(private var foodList: List<Food>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    private var userLatitude: Double? = null
    private var userLongitude: Double? = null

    fun updateData(newFoodList: List<Food>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }

    fun setUserLocation(latitude: Double, longitude: Double) {
        userLatitude = latitude
        userLongitude = longitude
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.tv_restaurant_food)
        val foodPrice: TextView = itemView.findViewById(R.id.tv_price)
        val foodImage: ImageView = itemView.findViewById(R.id.iv_restaurant_food)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedFood = foodList[position]

                    val context = itemView.context

                    val intent = Intent(context, DetailListActivity::class.java).apply {
                        putExtra(DetailListActivity.EXTRA_PRODUCT_ID, selectedFood.id)
                        putExtra(DetailListActivity.EXTRA_PRODUCT_NAME, selectedFood.name)
                        putExtra(DetailListActivity.EXTRA_PRODUCT_PRICE, selectedFood.price)
                        putExtra(DetailListActivity.EXTRA_PRODUCT_DESCRIPTION, selectedFood.detail)
                        putExtra(DetailListActivity.EXTRA_PRODUCT_IMAGE, selectedFood.attachment)
                    }

                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFood = foodList[position]

        holder.foodName.text = currentFood.name
        holder.foodPrice.text = "Rp${String.format("%,.0f", currentFood.price)}"

        Glide.with(holder.foodImage.context)
            .load(currentFood.attachment)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.foodImage)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}
