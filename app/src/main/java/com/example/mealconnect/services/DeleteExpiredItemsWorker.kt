package com.example.mealconnect.services

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class DeleteExpiredItemsWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        private const val TAG = "DeleteExpiredItemsWorker"
    }

    override fun doWork(): Result {
        try {
            val service = DeleteExpiredFirebase(applicationContext)
            service.deleteExpiredItems()
            Log.d(TAG, "Expired items deleted successfully.")
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting expired items: ${e.message}", e)
            return Result.failure()
        }
    }

}
