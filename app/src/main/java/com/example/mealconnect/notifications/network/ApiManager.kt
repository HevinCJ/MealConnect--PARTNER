package com.example.mealconnect.notifications.network

import android.util.Log
import com.example.mealconnect.notifications.entity.PushNotification

class ApiManager {

    suspend fun postNotification(notification: PushNotification) {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful){
                Log.d("notification",notification.to)
                println("Sending data was successful - notification recipient: ${notification.to}")
            }else{
                println("Error sending the data")
            }
        } catch (e: Exception) {
            println(e.message.toString())
        }
    }
}