package com.internshala.foodapp2.util

import android.content.Context

class SessionManager (context: Context){ //CHECK USAGE 1->10MAY ->session management class is used to manage the sessions or data of a user for logging in
    // using shared preferences(in form of key value pairs),shared pref is called useing get shared preferences method
    var PRIVATE_MODE = 0
    val PREF_NAME = "FoodApp"

    val KEY_IS_LOGGEDIN = "isLoggedIn"
    var pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    var editor = pref.edit()

    fun setLogin(isLoggedIn: Boolean){
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn)
        editor.apply()
    }


    fun isLoggedIn(): Boolean {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false)
    }
}