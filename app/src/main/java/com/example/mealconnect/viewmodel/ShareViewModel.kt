package com.example.mealconnect.viewmodel

import LocationData
import android.app.Application
import android.text.TextUtils

import androidx.lifecycle.AndroidViewModel



class ShareViewModel(application: Application):AndroidViewModel(application) {


    fun UserDataValidation(
        mealname: String,
        number: String,
        amount: String,
        descp: String,
        image: String,
        location:LocationData
    ):Boolean{
        return if(TextUtils.isEmpty(mealname)||TextUtils.isEmpty(number)||TextUtils.isEmpty(amount)||TextUtils.isEmpty(descp)||TextUtils.isEmpty(image)||TextUtils.isEmpty(location.toString())){
            false
        }else{
            true
        }
    }







}