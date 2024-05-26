package com.example.mealconnect.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class FirebaseServiceViewModel(application: Application):AndroidViewModel(application) {


    fun startDeleteExpiredItemsWorker() {
        val workRequest = PeriodicWorkRequest.Builder(
            DeleteExpiredItemsWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance().enqueue(workRequest)
    }

}