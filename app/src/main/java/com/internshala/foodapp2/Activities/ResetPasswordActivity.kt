package com.internshala.foodapp2.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodapp2.R
import com.internshala.foodapp2.util.ConnectionManager
import com.internshala.foodrunner.util.RESET_PASSWORD
import com.internshala.foodrunner.util.Validations
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.json.JSONObject
import java.lang.StringBuilder

class ResetPasswordActivity : AppCompatActivity() {
 //USAGE1-WGY PRIVATE-MAY12
    private lateinit var etOTP: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnSubmitOTP: Button
    private lateinit var rlOTP: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnSubmitOTP = findViewById(R.id.btnSubmitOTP)
        rlOTP = findViewById(R.id.rlOTP)
        progressBar = findViewById(R.id.progressBar)

        rlOTP.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        if(intent != null){
            mobileNumber = intent.getStringExtra("user_mobile") as String
            //USAGE2--WHY ABOVE LINE IS TYPCASTED ->MAY12
        }

        btnSubmitOTP.setOnClickListener{
            rlOTP.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            if (ConnectionManager().isNetworkAvailable(this@ResetPasswordActivity)) {
                if (etOTP.text.length == 4) {
                    if (Validations.validatePasswordLength(etNewPassword.text.toString())) {
                        if (Validations.matchPassword(
                                etNewPassword.text.toString(), etConfirmNewPassword.text.toString()
                            )) {
                            resetPassword(
                                mobileNumber,
                                etNewPassword.text.toString(),
                                etConfirmNewPassword.text.toString()
                            )
                        }
                        else {
                            rlOTP.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                "Passwords do not match",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }else {
                        rlOTP.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Invalid Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else {
                    rlOTP.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ResetPasswordActivity, "Incorrect OTP", Toast.LENGTH_SHORT)
                        .show()
                }
            }else {
                rlOTP.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "No Internet Connection!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
    private fun resetPassword(mobileNumber : String, otp: String, password: String){
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_Number",mobileNumber)
        jsonParams.put("password", password)
        jsonParams.put("otp", otp)
        Toast.makeText(this@ResetPasswordActivity, "IT WORKED3", Toast.LENGTH_SHORT).show()


        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, RESET_PASSWORD, jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    Toast.makeText(this@ResetPasswordActivity, "IT WORKED1", Toast.LENGTH_SHORT).show()

                    if (success){
                        Toast.makeText(this@ResetPasswordActivity, "IT WORKED2", Toast.LENGTH_SHORT).show()

                        progressBar.visibility = View.INVISIBLE
                            val builder = AlertDialog.Builder(this@ResetPasswordActivity)
                            builder.setTitle("Confirmation")
                            builder.setMessage("Your password has been successfully changed")
                            builder.setIcon(R.drawable.ic_action_success)
                            builder.setCancelable(false)
                        builder.setPositiveButton("OK") { _, _ ->
                            startActivity(
                                Intent(
                                    this@ResetPasswordActivity,
                                    LoginActivity::class.java
                                )
                            )
                            ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                        }
                        builder.create().show()

                        }
                    else {
                        rlOTP.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        //USAGE 3 WHY NOT USE TOAST  MAY12
                        val error = data.getString("errorMessage")
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Toast.makeText(this@ResetPasswordActivity, "IT WORKED4", Toast.LENGTH_SHORT).show()

                }
                catch (e: Exception) {
                    e.printStackTrace()
                    rlOTP.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Incorrect Response!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                rlOTP.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                //usage4 why volley with log ->may12
                VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                Toast.makeText(this@ResetPasswordActivity, it.message, Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {

                    val headers = HashMap<String, String>()
                    headers ["Content-type"] = "application/json"
                    headers["token"] = "1d06da9695d555"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }
}
