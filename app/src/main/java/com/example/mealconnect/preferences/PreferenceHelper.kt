package com.example.mealconnectuser.preferences

import com.google.android.gms.auth.api.identity.SignInPassword

interface PreferenceHelper {
    var username:String?
    var email:String?
    var password:String?
    var phoneno:String?
    var profileimage:String?
    var cutofftime:Long?


    fun saveUser(username:String,email:String,password:String,phoneno:String)

    fun clearAllPreferences()


}