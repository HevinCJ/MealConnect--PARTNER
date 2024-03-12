package com.example.mealconnect.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealconnect.fragments.Adapter.MealAdapter
import com.example.mealconnect.utils.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application):AndroidViewModel(application) {

    private var databaseref:DatabaseReference?=null
    private var auth:FirebaseAuth?=null
    private var id:String?=null
    val getAllData = MutableLiveData<List<UserData>>()

    init {
        databaseref=FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        id=databaseref?.push()?.key
        getAllDataFromFirebase()
    }

    fun getAllDataFromFirebase(){
        viewModelScope.launch(Dispatchers.IO){
            databaseref?.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userlist = snapshot.children.mapNotNull { it.getValue<UserData>() }
                    getAllData.value=userlist
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
        }
}