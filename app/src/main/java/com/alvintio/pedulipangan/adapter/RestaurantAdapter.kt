package com.alvintio.pedulipangan.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipangan.R

class RestaurantAdapter(private var restaurantList: List<String>) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    fun updateData(newList: List<String>) {
        restaurantList = newList
        notifyDataSetChanged()
    }

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.tv_restaurant)
        val restaurantPhoto: ImageView = itemView.findViewById(R.id.iv_restaurant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_list, parent, false)
        return RestaurantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val currentRestaurant = restaurantList[position]
        holder.restaurantName.text = currentRestaurant
        holder.restaurantPhoto.setImageResource(R.drawable.ic_launcher_background)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

}

