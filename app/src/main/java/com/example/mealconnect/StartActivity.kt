package com.example.mealconnect

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Database
import com.example.mealconnect.databinding.ActivityStartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class StartActivity : AppCompatActivity() {
    private var binding:ActivityStartBinding?=null

    private var databasereference:DatabaseReference?=null
    private var auth:FirebaseAuth?=null

    private var navcontroller:NavController?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        val navhostfragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        navcontroller=navhostfragment.navController


    }

    override fun onSupportNavigateUp(): Boolean {
        return navcontroller!!.navigateUp()||super.onSupportNavigateUp()
    }
}