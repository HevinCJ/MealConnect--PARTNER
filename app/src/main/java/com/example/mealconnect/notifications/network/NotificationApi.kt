package com.example.mealconnect.notifications.network

import com.example.mealconnect.notifications.entity.PushNotification
import com.squareup.okhttp.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    companion object{
        const val CONTENT_TYPE = "application/json"
        const val API_KEY = "AAAAQ2MrSVw:APA91bEUGSLsoFhUTUiYIWRf20alZDi5-rgJr99-SrOJnw8GkNdpCnMDVGW9Xa7rmfyXEAGNMgshgWeMXUAcE6taUyIhprM_cdQhWAYEwQ8Sqhbx9Si6G2FQrMj5SIV0b0hn997rkWpe"
    }

    @Headers("Authorization: key=${API_KEY}", "Content-Type: $CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}