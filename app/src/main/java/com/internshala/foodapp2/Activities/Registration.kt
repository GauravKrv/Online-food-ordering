package com.internshala.foodapp2.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodapp2.R
import com.internshala.foodapp2.util.ConnectionManager
import org.json.JSONObject

class Registration : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etPhone: EditText
    lateinit var etEmail: EditText
lateinit var btnRegister: Button
   lateinit var etAddress: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var etPassword: EditText
    lateinit var sharedPreferences: SharedPreferences

    var userId : String? = "31"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)


        if (intent != null){
            userId = intent.getStringExtra("user_id")
        }else {
            finish()
            Toast.makeText(this@Registration, "Some unexpected error occured", Toast.LENGTH_SHORT).show()
        }

        if (userId == "31"){
            finish()
            Toast.makeText(this@Registration, "Some unexpected error occured!", Toast.LENGTH_SHORT).show()
        }
        btnRegister.setOnClickListener {
            Toast.makeText(this@Registration, "it works", Toast.LENGTH_SHORT).show()

            var  name = etName.text.toString()
            var phone = etPhone.text.toString()
            var password = etPassword.text.toString()
            var address = etAddress.text.toString()
            var email = etEmail.text.toString()

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/register/fetch_result"  // ..check the slash here 19.4 DONE

           val jasonParams = JSONObject()
            jasonParams.put("user_id",userId)
            jasonParams.put("name",name)
            jasonParams.put("mobile_number",phone)
            jasonParams.put("password",password)
            jasonParams.put("address",address)
            jasonParams.put("email",email)

            if (ConnectionManager().checkConnectivity(this@Registration)){
                Toast.makeText(this@Registration, "it works again", Toast.LENGTH_SHORT).show()

                val jsonRequest = object: JsonObjectRequest(Request.Method.POST, url,jasonParams, Response.Listener {

                    println("Response is $it")
                    try {  //problem is here 18.4.20.7pm  CHECK ON 19
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success){
                            Toast.makeText(this@Registration, "it works aggainn", Toast.LENGTH_SHORT).show()

                            val restaurantJsonObject = data.getJSONObject("data")
                           userId = restaurantJsonObject.getString("user_id")
                            Toast.makeText(this@Registration, "it works aggainn2", Toast.LENGTH_SHORT).show()

                           name = restaurantJsonObject.getString("name")
                        email= restaurantJsonObject.getString("email")
                            phone = restaurantJsonObject.getString("mobile_number")
                           // password= restaurantJsonObject.getString("password")
                           address = restaurantJsonObject.getString("address")

                            Toast.makeText(this@Registration, "it works aggainn3", Toast.LENGTH_SHORT).show()



                            val intent = Intent(this@Registration, LoginActivity::class.java)
                            startActivity(intent)
                        }


                         else {
                            Toast.makeText(this@Registration, "Some Error Ocurred!", Toast.LENGTH_SHORT).show()

                        }
                    }  catch (e: Exception){
                        Toast.makeText(this@Registration, "Some error ocurred!", Toast.LENGTH_SHORT).show()
                    }


                },Response.ErrorListener {
                    Toast.makeText(this@Registration, "Volley Error $it", Toast.LENGTH_SHORT).show()

                } ){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "1d06da9695d555"
                        return headers
                    }
                }

                queue.add(jsonRequest)
            }else{
                Toast.makeText(this@Registration, "Open settings", Toast.LENGTH_SHORT).show()

                /* val dialog = AlertDialog.Builder(this@Registration)
                 dialog.setTitle("Error")
                 dialog.setMessage("Internet Connection Not Found")
                 dialog.setPositiveButton("Open Settings"){text, listener -> //Do nothing //
                     val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                     startActivity(settingsIntent)
                     finish()
                 }

                 dialog.setNegativeButton("Exit"){text, listener -> //Do nothing //
                     ActivityCompat.finishAffinity(this@Registration)

                 }
                 dialog.create()
                 dialog.show()*/
            }
        }
    }
}
