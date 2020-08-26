package com.internshala.foodapp2.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.internshala.foodapp2.Adapter.DashboardRecyclerAdapter
import com.android.volley.toolbox.Volley
import com.internshala.foodapp2.R
import com.internshala.foodapp2.model.Restaurant
import com.internshala.foodapp2.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    val restaurantInfoList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)


      /*  recyclerDashboard.addItemDecoration(
            DividerItemDecoration(
            recyclerDashboard.context,
            (layoutManager as LinearLayoutManager).orientation
        )
        )*/

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)){

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                try {
                    val data = it.getJSONObject("data")

                    progressLayout.visibility = View.GONE
                    val success = data.getBoolean("success")
                    //Toast.makeText(activity as Context, "Some unexpected error occurredhere44!", Toast.LENGTH_SHORT).show()

                    if (success){
                       // Toast.makeText(activity as Context, "Some unexpected error occurred here33!", Toast.LENGTH_SHORT).show()
                        Toast.makeText(activity as Context, "it works 1", Toast.LENGTH_SHORT).show()

                        val dataArray = data.getJSONArray("data")
                       // Toast.makeText(activity as Context, "Some unexpected error occurredhere1!", Toast.LENGTH_SHORT).show()

                        for (i in 0 until dataArray.length()){
                           // Toast.makeText(activity as Context, "Some unexpected error occurred here22!", Toast.LENGTH_SHORT).show()

                            val resJsonObject = dataArray.getJSONObject(i)


                            val restaurantObject = Restaurant(
                                resJsonObject.getString("id").toInt(),
                                resJsonObject.getString("name"),
                                resJsonObject.getString("rating"),
                                resJsonObject.getString("cost_for_one"),
                                resJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantObject)
                            recyclerAdapter = DashboardRecyclerAdapter(activity as Context, restaurantInfoList)

                            recyclerDashboard.adapter = recyclerAdapter

                            recyclerDashboard.layoutManager = layoutManager
                        }

                    } else {
                        Toast.makeText(activity as Context, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    progressLayout.visibility = View.GONE
                    Toast.makeText(activity as Context, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                }


        }, Response.ErrorListener {

                if (activity != null){
                    Toast.makeText(activity as Context, "Volley error occurred!", Toast.LENGTH_SHORT).show()
                }

        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "1d06da9695d555"
                return headers
            }
        }
        queue.add(jsonObjectRequest)

        } else {
           val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
            Toast.makeText(activity as Context, " error occurred!", Toast.LENGTH_SHORT).show()

        }


        return view
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId


        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }
}

