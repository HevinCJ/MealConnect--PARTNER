package com.example.mealconnect.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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

        val navhostfragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController=navhostfragment.navController


        binding?.bottomNavigationView?.setupWithNavController(navController)


        binding?.bottomNavigationView?.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Homem-> {
                   navController.navigate(R.id.dashboard)
                    true
                }
                R.id.Ordersm->{
                    navController.navigate(R.id.orders)
                    true
                }
                R.id.settingsm-> {
                    navController.navigate(R.id.settings)
                    true
                }

                else -> {false}
            }


        }

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()||super.onSupportNavigateUp()
    }


}

