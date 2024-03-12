package com.example.mealconnect

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mealconnect.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference

class Profile : Fragment() {
    private var profile:FragmentProfileBinding?=null
    private val binding get() = profile!!

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var storageref: StorageReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       profile = FragmentProfileBinding.inflate(layoutInflater,container,false)

        auth = FirebaseAuth.getInstance()
        databaseref = FirebaseDatabase.getInstance().getReference("Users")


        binding.logoutbtn.setOnClickListener{
           if (auth.currentUser!=null){
               showAlertDialogue()
           }
        }


        return binding.root
    }

    private fun showAlertDialogue() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Are You Sure To Log Out")
            .setPositiveButton("Yes"){dialogue,which->
                auth.signOut()
                val intent = Intent(requireActivity(),StartActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("No"){dialogue,which->
                dialogue.cancel()
            }

        val alertdialog = builder.create()
        alertdialog.show()
    }


}