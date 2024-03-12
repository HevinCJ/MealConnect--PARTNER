package com.example.mealconnect.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mealconnect.Orders
import com.example.mealconnect.Profile
import com.example.mealconnect.R
import com.example.mealconnect.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding?=null
    private lateinit var navController:NavController

    private var auth:FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth= FirebaseAuth.getInstance()

        replaceFragment(dashboard())


        binding?.bottomNavigationView?.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Homem-> {
                    replaceFragment(dashboard())
                    true
                }
                R.id.Ordersm->{
                    replaceFragment(Orders())
                    true
                }
                R.id.Profilem-> {
                    replaceFragment(Profile())
                    true
                }

                else -> {false}
            }


        }

    }




    private fun replaceFragment(fragment:Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container,fragment)
        fragmentTransaction.commit()
    }


}

