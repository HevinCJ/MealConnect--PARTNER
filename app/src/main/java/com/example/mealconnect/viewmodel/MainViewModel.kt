package com.example.mealconnect.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealconnect.Repostiory
import com.example.mealconnect.notifications.entity.PushNotification
import com.example.mealconnect.services.DeleteExpiredFirebase
import com.example.mealconnect.services.FirebaseServiceViewModel
import com.example.mealconnect.utils.NetworkResult
import com.example.mealconnect.utils.PartnerData
import com.example.mealconnectuser.preferences.AppPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application):AndroidViewModel(application) {

    private var databaseref:DatabaseReference?=null
    private var auth:FirebaseAuth?=null
    private var id:String?=null
    private val firebaseServiceViewModel  = FirebaseServiceViewModel(application)
    private val preferences:AppPreferences
    val getAllData = MutableLiveData<List<PartnerData>>()
    var currentLocation = MutableLiveData<LatLng>()
    private val repostiory:Repostiory = Repostiory()
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    init {
        auth = FirebaseAuth.getInstance()
        databaseref=FirebaseDatabase.getInstance().getReference("Partner").child(auth?.currentUser?.uid.orEmpty())
        id=databaseref?.push()?.key
        preferences = AppPreferences(application.applicationContext)
        firebaseServiceViewModel.startDeleteExpiredItemsWorker()
        getCurrentTimestampMinutes()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(application.applicationContext)
        getLastLocation()
    }


    fun getCurrentTimestampMinutes(): Long {
        val now = Date().time
        val oneHourInMillis = TimeUnit.HOURS.toMillis(1)
        val cutoff = now + oneHourInMillis
        return cutoff
    }


    fun sendNotification(notification: PushNotification){
        viewModelScope.launch(Dispatchers.IO) {
            repostiory.sendNotification(notification)
        }
    }

    fun getAllDataFromFirebase(resultcallback:(NetworkResult<List<PartnerData>>) -> Unit){

        resultcallback(NetworkResult.Loading())

        viewModelScope.launch(Dispatchers.IO){
            databaseref?.addValueEventListener(object:ValueEventListener{


                override fun onDataChange(snapshot: DataSnapshot) {
                      val user = snapshot.children.mapNotNull { it.getValue(PartnerData::class.java) }
                    resultcallback(NetworkResult.Success(user))
                }


                override fun onCancelled(error: DatabaseError) {
                    resultcallback(NetworkResult.Error(message = error.message))
                }

            })
        }
        }


    fun getAllOrdersFromFirebase(OrdersCallback:(NetworkResult<List<PartnerData>>) -> Unit){
        OrdersCallback(NetworkResult.Loading())

        databaseref?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.children.mapNotNull { it.getValue(PartnerData::class.java) }
                val filteredList = user.filter { it.isorderplaced }

                OrdersCallback(NetworkResult.Success(filteredList))
            }

            override fun onCancelled(error: DatabaseError) {
                OrdersCallback(NetworkResult.Error(message = error.message))
            }

        })

    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient?.lastLocation?.addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                val location: Location? = task.result
                if (location != null) {
                    currentLocation.value = LatLng(location.latitude, location.longitude)
                } else {
                    requestNewLocationData()
                }
            } else {
                Toast.makeText(getApplication(),"${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
            .setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        mFusedLocationClient?.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            if (mLastLocation != null) {
                currentLocation.value = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            }
        }
    }

}