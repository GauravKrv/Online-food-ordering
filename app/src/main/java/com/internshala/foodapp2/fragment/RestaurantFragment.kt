package com.internshala.foodapp2.fragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodapp2.Adapter.DetailsRecyclerAdapter

import com.internshala.foodapp2.R
import com.internshala.foodapp2.model.FoodItem
import com.internshala.foodapp2.util.ConnectionManager
import com.internshala.foodrunner.util.DrawerLocker
import com.internshala.foodrunner.util.FETCH_RESTAURANTS

/**
 * A simple [Fragment] subclass.
 */
class RestaurantFragment : Fragment() {
    private lateinit var recyclerMenu: RecyclerView
    private lateinit var restaurantMenuAdapter: DetailsRecyclerAdapter
    private var menuList = arrayListOf<FoodItem>()
    private lateinit var rlLoading: RelativeLayout
    private var orderList = arrayListOf<FoodItem>()
    lateinit var sharedPreferences: SharedPreferences

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var goToCart: Button
        var resId: Int? = 0
        var resName: String? = ""
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)
        sharedPreferences =
            activity?.getSharedPreferences("FoodApp2", Context.MODE_PRIVATE) as SharedPreferences
        rlLoading = view?.findViewById(R.id.rlLoading) as RelativeLayout
        rlLoading.visibility = View.VISIBLE
        resId = arguments?.getInt("id", 0)
        resName = arguments?.getString("name", "")
        (activity as DrawerLocker).setDrawerEnabled(false)
        setHasOptionsMenu(true)
        goToCart = view.findViewById(R.id.btnGoToCart) as Button
        goToCart.visibility = View.GONE
        goToCart.setOnClickListener {
           // proceedToCart()//CHECK FROM HERE DATE 23.6.20
        }
        setUpRestaurantMenu(view)
        Toast.makeText(activity as Context, "it works 1", Toast.LENGTH_SHORT).show()

        return view
    }

    private fun setUpRestaurantMenu(view: View) {
        Toast.makeText(activity as Context, "it works 2", Toast.LENGTH_SHORT).show()

        recyclerMenu = view.findViewById(R.id.recyclerMenuItems)
        if (ConnectionManager().isNetworkAvailable(activity as Context)) {
            Toast.makeText(activity as Context, "it works 1", Toast.LENGTH_SHORT).show()

            val queue = Volley.newRequestQueue(activity as Context)

            val jsonObjectRequest = object :
                JsonObjectRequest(Method.GET, FETCH_RESTAURANTS + resId, null, Response.Listener {
                    rlLoading.visibility = View.GONE

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            Toast.makeText(activity as Context, "it works 3", Toast.LENGTH_SHORT).show()

                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val menuObject = resArray.getJSONObject(i)
                                val foodItem = FoodItem(
                                    menuObject.getString("id"),
                                    menuObject.getString("name"),
                                    menuObject.getString("cost_for_one").toInt()
                                )
                                menuList.add(foodItem)
                                restaurantMenuAdapter = DetailsRecyclerAdapter(
                                    activity as Context,
                                    menuList,
                                    object : DetailsRecyclerAdapter.OnItemClickListener {
                                        override fun onAddItemClick(foodItem: FoodItem) {
                                            orderList.add(foodItem)
                                            if (orderList.size > 0) {
                                                goToCart.visibility = View.VISIBLE
                                                DetailsRecyclerAdapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(foodItem: FoodItem) {
                                            orderList.remove(foodItem)
                                            if (orderList.isEmpty()) {
                                                goToCart.visibility = View.GONE
                                                DetailsRecyclerAdapter.isCartEmpty = true
                                            }
                                        }
                                    })
                                val mLayoutManager = LinearLayoutManager(activity)
                                recyclerMenu.layoutManager = mLayoutManager
                                recyclerMenu.itemAnimator = DefaultItemAnimator()
                                recyclerMenu.adapter = restaurantMenuAdapter
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"

                    /*The below used token will not work, kindly use the token provided to you in the training*/
                    headers["token"] = "1d06da9695d555"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            Toast.makeText(activity as Context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }


}
