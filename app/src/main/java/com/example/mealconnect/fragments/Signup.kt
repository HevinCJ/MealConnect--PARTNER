package com.example.mealconnect.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.mealconnect.R
import com.example.mealconnect.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class signup : Fragment() {
   private var signup:FragmentSignupBinding?=null
    val binding get() = signup!!

    private lateinit var auth:FirebaseAuth
    private lateinit var databasereference:DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signup = FragmentSignupBinding.inflate(layoutInflater,container,false)

        auth = FirebaseAuth.getInstance()
        databasereference = FirebaseDatabase.getInstance().getReference("Users")

        binding.txtviewloginclicker.setOnClickListener {

        }



      binding.signupbtn.setOnClickListener {
          signUpUserToFirebase()
      }
        return(binding.root)
    }

    private fun signUpUserToFirebase() {
        val email = binding.edttextemail.text.toString()
        val password = binding.edttextpassword.text.toString()
        val confirmpassword = binding.edttextpasswordconfirm.text.toString()

        if (email.isEmpty()) binding.edttextemail.error="please fill email"
        if (password.isEmpty()) binding.edttextpassword.error="please fill password"
        if (confirmpassword.isEmpty()) binding.edttextpasswordconfirm.error="please fill confirm password"

        if (password == confirmpassword){

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(requireContext(),"Logged In",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(),it.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(requireContext(),"Please Check Fields",Toast.LENGTH_SHORT).show()
        }

    }

}