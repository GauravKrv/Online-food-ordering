package com.internshala.foodapp2.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodapp2.R
import com.internshala.foodapp2.model.FoodItem

class DetailsRecyclerAdapter(val context: Context, private val menuList:ArrayList<FoodItem>,
                             private val listener : OnItemClickListener
) :
    RecyclerView.Adapter<DetailsRecyclerAdapter.DetailsViewHolder>()
{
    companion object {
        var isCartEmpty = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
     val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_custom_row, parent, false)

       return DetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
    interface OnItemClickListener {
        fun onAddItemClick(foodItem: FoodItem)
        fun onRemoveItemClick(foodItem: FoodItem)
    }

    override fun onBindViewHolder(parent : DetailsViewHolder, viewType: Int) {
        val menuObject = menuList[viewType]

        parent.foodItemName.text = menuObject.name
        val cost = "Rs. ${menuObject.cost?.toString()}"
        parent.foodItemCost.text = cost
        parent.sno.text = (viewType + 1).toString()
        parent.addToCart.setOnClickListener {
            parent.addToCart.visibility = View.GONE
            parent.removeFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(menuObject)
    }

   /* class DetailsViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val txtfoodName: TextView = view.findViewById(R.id.txtfoodName)
        val txtfoodPrice: TextView = view.findViewById(R.id.txtfookPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }*/
        parent.removeFromCart.setOnClickListener {
            parent.removeFromCart.visibility = View.GONE
            parent.addToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(menuObject)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    class DetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodItemName: TextView = view.findViewById(R.id.txtItemName)
        val foodItemCost: TextView = view.findViewById(R.id.txtItemCost)
        val sno: TextView = view.findViewById(R.id.txtSNo)
        val addToCart: Button = view.findViewById(R.id.btnAddToCart)
        val removeFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }
}