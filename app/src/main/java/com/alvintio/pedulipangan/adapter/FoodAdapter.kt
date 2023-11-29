// FoodAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.adapter.RestaurantAdapter

class FoodAdapter(
    private var foodList: List<String>,
    private var restaurantAdapter: RestaurantAdapter
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    fun updateData(newFoodList: List<String>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.tv_restaurant_food)
        val restaurantPhoto: ImageView = itemView.findViewById(R.id.iv_restaurant_food)
        val foodName: TextView = itemView.findViewById(R.id.tv_food)
        val foodPrice: TextView = itemView.findViewById(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_list, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentFood = foodList[position]
        val currentRestaurant = restaurantAdapter.getRestaurantName(position)

        holder.foodName.text = currentFood
        holder.restaurantName.text = "Nasi Goreng"
        holder.restaurantPhoto.setImageResource(R.drawable.ic_launcher_background)
        holder.foodPrice.text = "$10.00"
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}
