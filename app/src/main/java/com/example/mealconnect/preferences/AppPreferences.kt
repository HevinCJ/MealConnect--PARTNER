package com.example.mealconnectuser.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.mealconnect.utils.Constants

class AppPreferences(context: Context):PreferenceHelper {

    override var profileimage: String?
        get() = customPreferences.getString(Constants.PROFILE_IMAGE,null)
        set(value) = customPreferences.edit().putString(Constants.PROFILE_IMAGE,value).apply()

    val customPreferences:SharedPreferences = context.getSharedPreferences("custom_preferences",MODE_PRIVATE)

    override var username: String?
        get() = customPreferences.getString(Constants.CUSTOMER_NAME,null)
        set(value) = customPreferences.edit().putString(Constants.CUSTOMER_NAME,value).apply()

    override var email: String?
        get() = customPreferences.getString(Constants.CUSTOMEER_EMAIL,null)
        set(value) = customPreferences.edit().putString(Constants.CUSTOMEER_EMAIL,value).apply()

    override var password: String?
        get() = customPreferences.getString(Constants.PASSWORD,null)
        set(value) = customPreferences.edit().putString(Constants.PASSWORD,value).apply()

    override var phoneno: String?
        get() = customPreferences.getString(Constants.CUSTOMER_PHONE,null)
        set(value) = customPreferences.edit().putString(Constants.CUSTOMER_PHONE,value).apply()


    override var cutofftime: Long?
        get() = customPreferences.getLong(Constants.CUTT_OFF_TIME, 0L)
        set(value) {
            customPreferences.edit().putLong(Constants.CUTT_OFF_TIME, value ?: 0L).apply()
        }


    override fun saveUser(username: String, email: String, password: String, phoneno: String) {

    }

    @SuppressLint("CommitPrefEdits")
    override fun clearAllPreferences() {
        customPreferences.edit().clear().apply()
    }
}