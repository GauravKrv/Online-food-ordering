package com.internshala.foodapp2.Adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodapp2.R
import com.internshala.foodapp2.fragment.RestaurantFragment
import com.internshala.foodapp2.model.Restaurant
import com.internshala.foodrunner.database.RestaurantDatabase
import com.internshala.foodrunner.database.RestaurantEntity
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, private var restaurants : ArrayList<Restaurant> ) : RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(p0: DashboardViewHolder, p1: Int) {
        //picasso library load later and class prblm
        val resObject = restaurants.get(p1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            p0.resThumbnail.clipToOutline = true
        }
        p0.restaurantName.text = resObject.name
        p0.rating.text = resObject.rating
        val costForTwo = "${resObject.costforTwo.toString()}/person"
        p0.cost.text = costForTwo
        Picasso.get().load(resObject.imageUrl).error(R.drawable.default_book_cover)
            .into(p0.resThumbnail)

        p0.cardRestaurant.setOnClickListener {
            val fragment = RestaurantFragment()
            val args = Bundle()
            args.putInt("id", resObject.id as Int)
            args.putString("name", resObject.name)
            fragment.arguments = args
            val transaction =
                (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, fragment)
            transaction.commit()
            (context as AppCompatActivity).supportActionBar?.title =
                p0.restaurantName.text.toString()
        }
    }


    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resThumbnail = view.findViewById(R.id.imgRestaurantImage) as ImageView
        val restaurantName = view.findViewById(R.id.txtRestaurantName) as TextView
        val rating = view.findViewById(R.id.txtRestaurantsRating) as TextView
        val cost = view.findViewById(R.id.txtCostForOne) as TextView
        val cardRestaurant = view.findViewById(R.id.cardRestaurant) as CardView
        //val favImage = view.findViewById(R.id.imgIsFav) as ImageView
    }
    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            /*
            Mode 1 -> Check DB if the book is favourite or not
            Mode 2 -> Save the book into DB as favourite
            Mode 3 -> Remove the favourite book
            */

            when (mode) {

                1 -> {
                    val res: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }

}
