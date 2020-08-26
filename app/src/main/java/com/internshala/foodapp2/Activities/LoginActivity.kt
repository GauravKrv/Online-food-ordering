package com.internshala.foodapp2.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodapp2.R
import com.internshala.foodapp2.util.ConnectionManager
import com.internshala.foodapp2.util.SessionManager
import com.internshala.foodrunner.util.LOGIN
import com.internshala.foodrunner.util.Validations
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
   lateinit var btnLogin: Button
    lateinit var txtForgotPassword:TextView
    lateinit var btnRegister: Button
    lateinit var etPhone: EditText
    lateinit var etPassword: EditText

    lateinit var sessionManager: SessionManager
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        btnRegister = findViewById(R.id.btnRegister)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)

        sessionManager = SessionManager(this)  //USER WILL HAVE TO PASS HIS CONTEXT
        sharedPreferences =  //BELOW first parameter is used for refering to userinfo and seond for making the mode as private-refer video
            this.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)  //HERE WE ARE ALSO RECIEVING THW CNTEXT USING "THIS."
        //FIND ABOVE USAGES-2->10MAY    ->useful when we want to store user data globally,it can be done in two ways-:
        //--1.storing them in a global variable which causes data dletion once user closes the app
                //--2.storing data in shared preferences,which causes the data to be persistence eve wen the user cloases the app

        btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, Registration::class.java)
            startActivity(intent)
        }
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener {
           btnLogin.visibility = View.INVISIBLE

            //FIND USAGE -3 BELOW LINE VALIDATIONS OBJECT -10MAY->here we are just preparing for validating user info before sending the request queue
            if (Validations.validateMobile(etPhone.text.toString())&& Validations.validatePasswordLength(etPassword.text.toString())){
                if (ConnectionManager().isNetworkAvailable(this@LoginActivity)) {
                   //FIND USAGE ABOVE FUNCTION OF ISNETWORKAVAILABLE - 5->10MAY-its just a replacement of the function connectivity anager and no
                    //thing new is inside it.

                    val queue = Volley.newRequestQueue(this@LoginActivity)

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etPhone.text.toString())  //here we are sending the prepared infos as parameters
                    jsonParams.put("password", etPassword.text.toString())

                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST, LOGIN, jsonParams,
                        Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if(success){
                                    val response = data.getJSONObject("data")
                                    //USAGE -8 OF USING SHAREDPREFERENCES LIKE THIS-10MAY-sh pref is storing the recieved response in terms of key
                                    //value pairs,, maybe for further usage
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "user_mobile_number",
                                            response.getString("mobile_number")
                                        )
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_address", response.getString("address"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_email", response.getString("email")).apply()
                                    sessionManager.setLogin(true)
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finish()
                          }else{
                                    btnLogin.visibility = View.VISIBLE
                                    txtForgotPassword.visibility = View.VISIBLE
                                    btnLogin.visibility = View.VISIBLE
                                    val errorMessage = data.getString("errorMessage")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                btnLogin.visibility = View.VISIBLE
                                txtForgotPassword.visibility = View.VISIBLE
                                btnRegister.visibility = View.VISIBLE
                                e.printStackTrace()//USAGE-6->10MAY-this method is used to handle exception by telling the line number wher the exceptio
                                //occured.It is used for diaganosing the exceptions
                            }
                        },
                        Response.ErrorListener {
                           btnLogin.visibility = View.VISIBLE
                            txtForgotPassword.visibility = View.VISIBLE
                            btnRegister.visibility = View.VISIBLE
                            //USAGE 7-LOG USE ->10MAY - used to identify source of a log message or end am error log message,where e means error
                            //tag--it usually identifies the class or activity where the log call occurs.this value may be null
                            //msg--the message which we would like logged and its vale cannot be null
                            Log.e("Error::::", "/post request fail! Error: ${it.message}")
                        }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "1d06da9695d555"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                }
                else{
                    btnLogin.visibility = View.VISIBLE
                    txtForgotPassword.visibility = View.VISIBLE
                    btnRegister.visibility = View.VISIBLE
                    Toast.makeText(this@LoginActivity, "No internet Connection", Toast.LENGTH_SHORT)
                        .show()
                }
            } else{
                btnLogin.visibility = View.VISIBLE
                txtForgotPassword.visibility = View.VISIBLE
                btnRegister.visibility = View.VISIBLE
                Toast.makeText(this@LoginActivity, "Invalid Phone or Password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }
}
