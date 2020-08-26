package com.internshala.foodapp2.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodapp2.R
import com.internshala.foodapp2.util.ConnectionManager
import com.internshala.foodrunner.util.FORGOT_PASSWORD
import com.internshala.foodrunner.util.Validations
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.json.JSONObject
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etForgotMobile: EditText
    lateinit var etForgotEmail: EditText
    lateinit var btnForgotNext: Button
    lateinit var progressBar: ProgressBar
    lateinit var rlContentMain: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etForgotMobile = findViewById(R.id.etForgotMobile)
        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnForgotNext = findViewById(R.id.btnForgotNext)
        rlContentMain = findViewById(R.id.r1ContentMain)
        progressBar = findViewById(R.id.progressBar)
        rlContentMain.visibility = View.VISIBLE
        progressBar.visibility = View.GONE //SEARCH 1 :WHY GONE ->MAY11 DONE

        btnForgotNext.setOnClickListener{

            val forgotMobileNumber = etForgotMobile.text.toString()
            if (Validations.validateMobile(forgotMobileNumber)){
                etForgotMobile.error = null
            if (Validations.validateEmail(etForgotEmail.text.toString())){
                if (ConnectionManager().isNetworkAvailable(this@ForgotPasswordActivity)){
                    rlContentMain.visibility = View.GONE //CHECK1->DOUBT MAY 11
                    progressBar.visibility = View.VISIBLE
                    sendOTP(etForgotMobile.text.toString(), etForgotEmail.text.toString())
                }else{
                    rlContentMain.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "NEHA IS BANDAR No internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else
            {
                rlContentMain.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                etForgotEmail.error = "Invalid Email"
            }

        }else{
                rlContentMain.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                etForgotMobile.error = "Invalid Mobile Number"
            }
        }
    }
    private fun sendOTP(mobileNumber: String, email: String){
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("email", email)

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, FORGOT_PASSWORD, jsonParams,
            Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    Toast.makeText(this@ForgotPasswordActivity, "IT WORKED2", Toast.LENGTH_SHORT).show()

                    val success = data.getBoolean("success")
                    Toast.makeText(this@ForgotPasswordActivity, "IT WORKED4", Toast.LENGTH_SHORT).show()

                    if (success){
                        val firstTry = data.getBoolean("first_try") //USAGE 2 OF FIRSTTRY MAY11
                        Toast.makeText(this@ForgotPasswordActivity, "IT WORKED3", Toast.LENGTH_SHORT).show()

                        if(firstTry){
                           // USAGE OF BUILDERS 3 ->MAY 11

                            Toast.makeText(this@ForgotPasswordActivity, "IT WORKED1", Toast.LENGTH_SHORT).show()
                            val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                            builder.setTitle("Information")
                            builder.setMessage("Please Check Your Registered Email ID for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("OK") { _, _ ->
                                val intent = Intent(
                                   this@ForgotPasswordActivity,
                                    ResetPasswordActivity::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        }else{
                            val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                            builder.setTitle("Information")
                            builder.setMessage("Please refer to the previous email for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgotPasswordActivity,
                                    ResetPasswordActivity::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        }
                    }else{
                        rlContentMain.visibility = View.VISIBLE
                        r1ForgotPass.visibility = View.GONE
                        Toast.makeText(
                            this@ForgotPasswordActivity,"Mobile Number NOT registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception){
                    e.printStackTrace()
                    rlContentMain.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Incorrect response error!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },Response.ErrorListener {
                rlContentMain.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                Toast.makeText(this@ForgotPasswordActivity, it.message, Toast.LENGTH_SHORT).show()

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
}
