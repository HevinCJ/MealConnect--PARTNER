package com.example.mealconnect

import com.example.mealconnect.notifications.entity.PushNotification
import com.example.mealconnect.notifications.network.ApiManager

class Repostiory {
    private val apiManager: ApiManager = ApiManager()

    suspend fun sendNotification(notification: PushNotification) {
        apiManager.postNotification(notification)
    }
}