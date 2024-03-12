package com.example.mealconnect.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.mealconnect.R
import com.example.mealconnect.StartActivity
import com.example.mealconnect.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class login : Fragment() {
    private var login:FragmentLoginBinding?=null
    val binding get() = login!!

    private  var auth:FirebaseAuth ?=null
    private  var databaseref:DatabaseReference ?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        login = FragmentLoginBinding.inflate(layoutInflater,container,false)

        auth = FirebaseAuth.getInstance()
        databaseref = FirebaseDatabase.getInstance().getReference("Users")

        if (auth?.currentUser!=null){
            val intent = Intent(requireActivity(),MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


        binding.loginbutton.setOnClickListener {
            loginUserToFirebase()
        }

        return binding.root
    }

    private fun loginUserToFirebase() {
        val email = binding.edttextemail.text.toString()
        val password = binding.edttextpassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            auth?.signInWithEmailAndPassword(email,password)?.addOnCompleteListener {
                if (it.isSuccessful){
                    val intent = Intent(requireActivity(),MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(requireContext(),"Logged In",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}