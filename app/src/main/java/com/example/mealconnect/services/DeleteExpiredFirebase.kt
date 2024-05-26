package com.example.mealconnect.services

import android.content.Context
import android.util.Log
import com.example.mealconnect.utils.PartnerData
import com.example.mealconnect.viewmodel.MainViewModel
import com.example.mealconnectuser.preferences.AppPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DeleteExpiredFirebase(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val preferences = AppPreferences(context)

    companion object {
        private const val TAG = "DeleteExpiredItems"
        private const val THRESHOLD_HOURS = 3600000L
    }

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("Partner").child(auth.currentUser.uid)
    }

    fun deleteExpiredItems() {
        val now = Date().time

        val oldItemsQuery = database.orderByChild("timestamp").endAt(now.toDouble())

        oldItemsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updates = HashMap<String, Any?>()
                snapshot.children.forEach { child ->
                    updates[child.key!!] = null
                    Log.d(TAG,"Adding key: ${child.key}")
                }
                database.updateChildren(updates)
                    .addOnSuccessListener {
                        println("Old items deleted successfully")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to delete old items: $e")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database query cancelled: ${error.message}")
            }
        })
    }





    private fun calculateTimeDifference(): Boolean {
        val now = Date().time
       val cutoff = now + THRESHOLD_HOURS
        return true
    }
}
